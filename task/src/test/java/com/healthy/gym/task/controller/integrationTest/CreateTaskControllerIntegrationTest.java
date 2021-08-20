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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.time.LocalDate;
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
@Tag("integration")
public class CreateTaskControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private Integer port;

    private String userId;
    private String employeeId;
    private String managerId;
    private String adminId;
    private String userToken;
    private String employeeToken;
    private String managerToken;
    private String adminToken;

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
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {

        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getUserToken(employeeId);

        managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        objectMapper = new ObjectMapper();

        requestTitle = "Test task 1";
        requestDescription = "Description for task 1";
        requestDueDate = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        requestReminderDate = LocalDate.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE);
        priority = "HIGH";
        managerTaskCreationRequest = new ManagerTaskCreationRequest();
        managerTaskCreationRequest.setTitle(requestTitle);
        managerTaskCreationRequest.setDescription(requestDescription);
        managerTaskCreationRequest.setEmployeeId(employeeId);
        managerTaskCreationRequest.setDueDate(requestDueDate);

        requestContent = objectMapper.writeValueAsString(managerTaskCreationRequest);

        managerTaskCreationRequestContentWithOptionalParams = new ManagerTaskCreationRequest();
        managerTaskCreationRequestContentWithOptionalParams.setTitle(requestTitle);
        managerTaskCreationRequestContentWithOptionalParams.setDescription(requestDescription);
        managerTaskCreationRequestContentWithOptionalParams.setEmployeeId(employeeId);
        managerTaskCreationRequestContentWithOptionalParams.setDueDate(requestDueDate);
        managerTaskCreationRequestContentWithOptionalParams.setReminderDate(requestReminderDate);
        managerTaskCreationRequestContentWithOptionalParams.setPriority(priority);

        requestContentWithOptionalParams = objectMapper.writeValueAsString(managerTaskCreationRequestContentWithOptionalParams);

        //existing DB docs
        String employeeName = "Jan";
        String employeeSurname = "Kowalski";
        UserDocument employeeDocument = new UserDocument();
        employeeDocument.setName(employeeName);
        employeeDocument.setSurname(employeeSurname);
        employeeDocument.setUserId(employeeId);
        employeeDocument.setGymRoles(List.of(GymRole.EMPLOYEE));
        employeeDocument.setId("507f1f77bcf86cd799435213");

        mongoTemplate.save(employeeDocument);

        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        mongoTemplate.save(managerDocument);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(TaskDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldCreateTask_whenValidRequestWithoutOptionalParams(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
        String expectedMessage = messages.get("task.created");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Test task 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Description for task 1");
        assertThat(responseEntity.getBody().get("task").get("lastOrderUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("reminderDate")).isNull();
        assertThat(responseEntity.getBody().get("task").get("priority")).isNull();
        assertThat(responseEntity.getBody().get("task").get("mark").intValue()).isZero();
        assertThat(responseEntity.getBody().get("task").get("employeeComment")).isNull();
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldCreateTask_whenValidRequestWithOptionalParams(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContentWithOptionalParams, headers);
        String expectedMessage = messages.get("task.created");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, request, JsonNode.class);

        System.out.println(responseEntity.getBody());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Test task 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Description for task 1");
        assertThat(responseEntity.getBody().get("task").get("lastOrderUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("reminderDate").textValue())
                .isEqualTo(LocalDate.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertThat(responseEntity.getBody().get("task").get("priority").textValue())
                .isEqualTo("HIGH");
        assertThat(responseEntity.getBody().get("task").get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get("task").get("employeeComment")).isNull();
    }

    @Nested
    class ShouldNotCreateTaskWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotCreateTaskWhenNoToken(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.POST, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo("Access Denied");
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotCreateTaskWhenLoggedAsUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.POST, request, JsonNode.class);

            String expectedMessage = messages.get("exception.access.denied");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotCreateTaskWhenLoggedAsEmployee(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.POST, request, JsonNode.class);

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
    void shouldNotCreateTask_whenInvalidEmployeeId(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String invalidEmployeeId = UUID.randomUUID().toString();

        ManagerTaskCreationRequest invalidEmployeeManagerTaskCreationRequest = new ManagerTaskCreationRequest();
        invalidEmployeeManagerTaskCreationRequest.setTitle(requestTitle);
        invalidEmployeeManagerTaskCreationRequest.setDescription(requestDescription);
        invalidEmployeeManagerTaskCreationRequest.setEmployeeId(invalidEmployeeId);
        invalidEmployeeManagerTaskCreationRequest.setDueDate(requestDueDate);

        String invalidEmployeeRequestContent = objectMapper.writeValueAsString(invalidEmployeeManagerTaskCreationRequest);

        URI uri = new URI("http://localhost:" + port );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidEmployeeRequestContent, headers);
        String expectedMessage = messages.get("exception.employee.not.found");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotCreateTask_whenRetroDueDate(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        ManagerTaskCreationRequest invalidDueDateManagerTaskCreationRequest = new ManagerTaskCreationRequest();
        invalidDueDateManagerTaskCreationRequest.setTitle(requestTitle);
        invalidDueDateManagerTaskCreationRequest.setDescription(requestDescription);
        invalidDueDateManagerTaskCreationRequest.setEmployeeId(employeeId);
        invalidDueDateManagerTaskCreationRequest.setDueDate(LocalDate.now().minusDays(1).toString());

        String invalidDueDateRequestContent = objectMapper.writeValueAsString(invalidDueDateManagerTaskCreationRequest);

        URI uri = new URI("http://localhost:" + port );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidDueDateRequestContent, headers);
        String expectedMessage = messages.get("exception.retro.due.date");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }
}
