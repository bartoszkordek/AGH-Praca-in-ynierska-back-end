package com.healthy.gym.task.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.task.configuration.FixedClockConfig;
import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.pojo.request.ManagerTaskCreationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.healthy.gym.task.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.task.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@ActiveProfiles(value = "test")
@Tag("integration")
public class UpdateTaskControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private Integer port;

    private String userId;
    private String employeeId1;
    private String employeeId2;
    private String managerId;
    private String otherManagerId;
    private String adminId;
    private String userToken;
    private String employeeToken;
    private String managerToken;
    private String adminToken;

    private String taskId;

    private String requestContent;
    private String requestContentWithOptionalParams;
    private String requestTitle;
    private String requestDescription;
    private String requestDueDate;
    private String requestReminderDate;
    private String priority;
    private ManagerTaskCreationRequest managerTaskCreationRequest;
    private ManagerTaskCreationRequest managerTaskCreationRequestContentWithOptionalParams;

    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {

        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        employeeId1 = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getUserToken(employeeId1);

        employeeId2 = UUID.randomUUID().toString();

        managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        taskId = UUID.randomUUID().toString();

        objectMapper = new ObjectMapper();

        requestTitle = "Updated test task 1";
        requestDescription = "Updated description for task 1";
        requestDueDate = LocalDateTime.now().plusMonths(2).format(DateTimeFormatter.ISO_LOCAL_DATE);
        requestReminderDate = LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE);
        priority = "HIGH";
        managerTaskCreationRequest = new ManagerTaskCreationRequest();
        managerTaskCreationRequest.setTitle(requestTitle);
        managerTaskCreationRequest.setDescription(requestDescription);
        managerTaskCreationRequest.setEmployeeId(employeeId2);
        managerTaskCreationRequest.setDueDate(requestDueDate);

        requestContent = objectMapper.writeValueAsString(managerTaskCreationRequest);

        managerTaskCreationRequestContentWithOptionalParams = new ManagerTaskCreationRequest();
        managerTaskCreationRequestContentWithOptionalParams.setTitle(requestTitle);
        managerTaskCreationRequestContentWithOptionalParams.setDescription(requestDescription);
        managerTaskCreationRequestContentWithOptionalParams.setEmployeeId(employeeId2);
        managerTaskCreationRequestContentWithOptionalParams.setDueDate(requestDueDate);
        managerTaskCreationRequestContentWithOptionalParams.setReminderDate(requestReminderDate);
        managerTaskCreationRequestContentWithOptionalParams.setPriority(priority);

        requestContentWithOptionalParams = objectMapper.writeValueAsString(managerTaskCreationRequestContentWithOptionalParams);


        //existing DB docs
        String employeeName1 = "Jan";
        String employeeSurname1 = "Kowalski";
        UserDocument employeeDocument1 = new UserDocument();
        employeeDocument1.setName(employeeName1);
        employeeDocument1.setSurname(employeeSurname1);
        employeeDocument1.setUserId(employeeId1);
        employeeDocument1.setGymRoles(List.of(GymRole.EMPLOYEE));

        mongoTemplate.save(employeeDocument1);

        String employeeName2 = "Paweł";
        String employeeSurname2 = "Walczak";
        UserDocument employeeDocument2 = new UserDocument();
        employeeDocument2.setName(employeeName2);
        employeeDocument2.setSurname(employeeSurname2);
        employeeDocument2.setUserId(employeeId2);
        employeeDocument2.setGymRoles(List.of(GymRole.EMPLOYEE));

        mongoTemplate.save(employeeDocument2);

        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));

        mongoTemplate.save(managerDocument);

        otherManagerId = UUID.randomUUID().toString();
        String otherManagerName = "Adam";
        String otherManagerSurname = "Nowak";
        UserDocument otherManagerDocument = new UserDocument();
        otherManagerDocument.setName(otherManagerName);
        otherManagerDocument.setSurname(otherManagerSurname);
        otherManagerDocument.setUserId(otherManagerId);
        otherManagerDocument.setGymRoles(List.of(GymRole.MANAGER));

        mongoTemplate.save(otherManagerDocument);

        TaskDocument taskDocument = new TaskDocument();
        taskDocument.setTaskId(taskId);
        taskDocument.setManager(managerDocument);
        taskDocument.setEmployee(employeeDocument1);
        taskDocument.setTitle("Title 1");
        taskDocument.setDescription("Description 1");
        taskDocument.setTaskCreationDate(LocalDateTime.now().minusMonths(1));
        taskDocument.setDueDate(LocalDateTime.now().plusMonths(1));
        taskDocument.setLastTaskUpdateDate(LocalDateTime.now());
        taskDocument.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);

        mongoTemplate.save(taskDocument);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(TaskDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldUpdateTask_whenValidRequestAndTaskIdWithoutOptionalParams(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port +"/" + taskId + "/manager/" + managerId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
        String expectedMessage = messages.get("task.updated");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("task").get("id")).isNotNull();
        assertThat(responseEntity.getBody().get("task").get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get("task").get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get("task").get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get("task").get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get("task").get("employee").get("userId").textValue())
                .isEqualTo(employeeId2);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Paweł");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Walczak");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Updated test task 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Updated description for task 1");
        assertThat(responseEntity.getBody().get("task").get("taskCreationDate").textValue())
                .isEqualTo(LocalDate.now().minusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(2).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("reminderDate")).isNull();
        assertThat(responseEntity.getBody().get("task").get("mark").intValue()).isZero();
        assertThat(responseEntity.getBody().get("task").get("employeeComment")).isNull();
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldUpdateTask_whenValidRequestAndTaskIdWithOptionalParams(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port +"/" + taskId + "/manager/" + managerId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContentWithOptionalParams, headers);
        String expectedMessage = messages.get("task.updated");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("task").get("id")).isNotNull();
        assertThat(responseEntity.getBody().get("task").get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get("task").get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get("task").get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get("task").get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get("task").get("employee").get("userId").textValue())
                .isEqualTo(employeeId2);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Paweł");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Walczak");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Updated test task 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Updated description for task 1");
        assertThat(responseEntity.getBody().get("task").get("taskCreationDate").textValue())
                .isEqualTo(LocalDate.now().minusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(2).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("reminderDate").textValue())
                .isEqualTo(LocalDate.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertThat(responseEntity.getBody().get("task").get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get("task").get("employeeComment")).isNull();
    }


    @Nested
    class ShouldNotUpdateTaskWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotUpdateTaskWhenNoToken(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + taskId + "/manager/" + managerId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo("Access Denied");
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotUpdateTaskWhenLoggedAsUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + taskId + "/manager/" + managerId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            String expectedMessage = messages.get("exception.access.denied");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotUpdateTaskWhenLoggedAsEmployee(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + taskId + "/manager/" + managerId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            String expectedMessage = messages.get("exception.access.denied");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotUpdateTaskWhenLoggedAsOtherManager(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String otherManagerToken = tokenFactory.getMangerToken(otherManagerId);

            URI uri = new URI("http://localhost:" + port + "/" + taskId + "/manager/" + managerId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", otherManagerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            String expectedMessage = messages.get("exception.access.denied");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotUpdateTask_whenInvalidTaskId(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String invalidTaskId = UUID.randomUUID().toString();

        ManagerTaskCreationRequest invalidEmployeeManagerTaskCreationRequest = new ManagerTaskCreationRequest();
        invalidEmployeeManagerTaskCreationRequest.setTitle(requestTitle);
        invalidEmployeeManagerTaskCreationRequest.setDescription(requestDescription);
        invalidEmployeeManagerTaskCreationRequest.setEmployeeId(employeeId2);
        invalidEmployeeManagerTaskCreationRequest.setDueDate(requestDueDate);

        String invalidEmployeeRequestContent = objectMapper.writeValueAsString(invalidEmployeeManagerTaskCreationRequest);

        URI uri = new URI("http://localhost:" + port + "/" + invalidTaskId + "/manager/" + managerId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidEmployeeRequestContent, headers);
        String expectedMessage = messages.get("exception.task.not.found");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotUpdateTask_whenInvalidEmployeeId(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String invalidEmployeeId = UUID.randomUUID().toString();

        ManagerTaskCreationRequest invalidEmployeeManagerTaskCreationRequest = new ManagerTaskCreationRequest();
        invalidEmployeeManagerTaskCreationRequest.setTitle(requestTitle);
        invalidEmployeeManagerTaskCreationRequest.setDescription(requestDescription);
        invalidEmployeeManagerTaskCreationRequest.setEmployeeId(invalidEmployeeId);
        invalidEmployeeManagerTaskCreationRequest.setDueDate(requestDueDate);

        String invalidEmployeeRequestContent = objectMapper.writeValueAsString(invalidEmployeeManagerTaskCreationRequest);

        URI uri = new URI("http://localhost:" + port + "/" + taskId + "/manager/" + managerId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidEmployeeRequestContent, headers);
        String expectedMessage = messages.get("exception.employee.not.found");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotUpdateTask_whenRetroDueDate(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        ManagerTaskCreationRequest invalidDueDateManagerTaskCreationRequest = new ManagerTaskCreationRequest();
        invalidDueDateManagerTaskCreationRequest.setTitle(requestTitle);
        invalidDueDateManagerTaskCreationRequest.setDescription(requestDescription);
        invalidDueDateManagerTaskCreationRequest.setEmployeeId(employeeId2);
        invalidDueDateManagerTaskCreationRequest.setDueDate(LocalDate.now().minusDays(1).toString());

        String invalidDueDateRequestContent = objectMapper.writeValueAsString(invalidDueDateManagerTaskCreationRequest);

        URI uri = new URI("http://localhost:" + port + "/" + taskId + "/manager/" + managerId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidDueDateRequestContent, headers);
        String expectedMessage = messages.get("exception.retro.due.date");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotUpdateTask_whenInvalidPriority(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        ManagerTaskCreationRequest invalidPriorityManagerTaskCreationRequest = new ManagerTaskCreationRequest();
        invalidPriorityManagerTaskCreationRequest.setTitle(requestTitle);
        invalidPriorityManagerTaskCreationRequest.setDescription(requestDescription);
        invalidPriorityManagerTaskCreationRequest.setEmployeeId(employeeId2);
        invalidPriorityManagerTaskCreationRequest.setDueDate(LocalDate.now().toString());
        invalidPriorityManagerTaskCreationRequest.setPriority("INVALID_PRIORITY");

        String invalidDueDateRequestContent = objectMapper.writeValueAsString(invalidPriorityManagerTaskCreationRequest);

        URI uri = new URI("http://localhost:" + port + "/" + taskId + "/manager/" + managerId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidDueDateRequestContent, headers);
        String expectedMessage = messages.get("exception.invalid.priority");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }
}
