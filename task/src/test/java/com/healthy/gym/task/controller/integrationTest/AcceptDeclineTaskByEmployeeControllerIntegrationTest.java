package com.healthy.gym.task.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.task.configuration.FixedClockConfig;
import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.pojo.request.EmployeeAcceptDeclineTaskRequest;
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
public class AcceptDeclineTaskByEmployeeControllerIntegrationTest {

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
    private String trainerId;
    private String employeeId;
    private String managerId;
    private String adminId;
    private String userToken;
    private String trainerToken;
    private String employeeToken;
    private String managerToken;
    private String adminToken;
    private String taskId;
    private String approvedTaskDocumentId;
    private String taskForTrainerDocumentId;

    private ObjectMapper objectMapper;

    private String validAcceptedTaskRequestContent;
    private String validDeclinedTaskRequestContent;
    private String invalidTaskRequestInvalidEmployeeCommentRequestContent;
    private String invalidTaskRequestInvalidAcceptanceStatusRequestContent;
    private String invalidTaskRequestMissingRequestDataRequestContent;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {

        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        trainerId = UUID.randomUUID().toString();
        trainerToken = tokenFactory.getTrainerToken(trainerId);

        employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getUserToken(employeeId);

        managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        taskId = UUID.randomUUID().toString();

        //requests
        objectMapper = new ObjectMapper();

        String requestAcceptanceStatusAccept = "approve";
        String requestEmployeeCommentAccept = "I accept this task";

        String requestAcceptanceStatusDecline = "DECLINE";
        String requestEmployeeCommentDecline = "I decline this task";

        String requestInvalidAcceptanceStatus = "INVALID_ACCEPTANCE_STATUS";
        String requestInvalidEmployeeComment = "C";

        //valid request - accepted task
        EmployeeAcceptDeclineTaskRequest validAcceptTaskRequest = new EmployeeAcceptDeclineTaskRequest();
        validAcceptTaskRequest.setAcceptanceStatus(requestAcceptanceStatusAccept);
        validAcceptTaskRequest.setEmployeeComment(requestEmployeeCommentAccept);
        validAcceptedTaskRequestContent = objectMapper.writeValueAsString(validAcceptTaskRequest);

        //valid request - declined task
        EmployeeAcceptDeclineTaskRequest validDeclineTaskRequest = new EmployeeAcceptDeclineTaskRequest();
        validDeclineTaskRequest.setAcceptanceStatus(requestAcceptanceStatusDecline);
        validDeclineTaskRequest.setEmployeeComment(requestEmployeeCommentDecline);
        validDeclinedTaskRequestContent = objectMapper.writeValueAsString(validDeclineTaskRequest);

        //invalid request - invalid employee comment
        EmployeeAcceptDeclineTaskRequest invalidTaskRequestInvalidEmployeeComment = new EmployeeAcceptDeclineTaskRequest();
        invalidTaskRequestInvalidEmployeeComment.setAcceptanceStatus(requestAcceptanceStatusAccept);
        invalidTaskRequestInvalidEmployeeComment.setEmployeeComment(requestInvalidEmployeeComment);
        invalidTaskRequestInvalidEmployeeCommentRequestContent = objectMapper
                .writeValueAsString(invalidTaskRequestInvalidEmployeeComment);

        //invalid request - invalid acceptance status
        EmployeeAcceptDeclineTaskRequest invalidTaskRequestInvalidAcceptanceStatus = new EmployeeAcceptDeclineTaskRequest();
        invalidTaskRequestInvalidAcceptanceStatus.setAcceptanceStatus(requestInvalidAcceptanceStatus);
        invalidTaskRequestInvalidAcceptanceStatus.setEmployeeComment(requestEmployeeCommentAccept);
        invalidTaskRequestInvalidAcceptanceStatusRequestContent = objectMapper
                .writeValueAsString(invalidTaskRequestInvalidAcceptanceStatus);

        //invalid request - missing required data
        EmployeeAcceptDeclineTaskRequest invalidTaskRequestMissingRequestData = new EmployeeAcceptDeclineTaskRequest();
        invalidTaskRequestMissingRequestDataRequestContent = objectMapper
                .writeValueAsString(invalidTaskRequestMissingRequestData);

        //existing DB docs
        String employeeName = "Jan";
        String employeeSurname = "Kowalski";
        UserDocument employeeDocument = new UserDocument();
        employeeDocument.setName(employeeName);
        employeeDocument.setSurname(employeeSurname);
        employeeDocument.setUserId(employeeId);
        employeeDocument.setGymRoles(List.of(GymRole.EMPLOYEE));

        mongoTemplate.save(employeeDocument);

        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));

        mongoTemplate.save(managerDocument);

        TaskDocument taskDocument = new TaskDocument();
        taskDocument.setTaskId(taskId);
        taskDocument.setManager(managerDocument);
        taskDocument.setEmployee(employeeDocument);
        taskDocument.setTitle("Title 1");
        taskDocument.setDescription("Description 1");
        taskDocument.setDueDate(LocalDate.now().plusMonths(1));
        taskDocument.setLastTaskUpdateDate(LocalDate.now());
        taskDocument.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);

        mongoTemplate.save(taskDocument);

        approvedTaskDocumentId = UUID.randomUUID().toString();
        TaskDocument approvedTaskDocument = new TaskDocument();
        approvedTaskDocument.setTaskId(approvedTaskDocumentId);
        approvedTaskDocument.setManager(managerDocument);
        approvedTaskDocument.setEmployee(employeeDocument);
        approvedTaskDocument.setTitle("Title 1");
        approvedTaskDocument.setDescription("Description 1");
        approvedTaskDocument.setDueDate(LocalDate.now().plusMonths(1));
        approvedTaskDocument.setLastTaskUpdateDate(LocalDate.now());
        approvedTaskDocument.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        approvedTaskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);

        mongoTemplate.save(approvedTaskDocument);

        String trainerName = "Anna";
        String trainerSurname = "Kwiatkowska";
        UserDocument trainerDocument = new UserDocument();
        trainerDocument.setName(trainerName);
        trainerDocument.setSurname(trainerSurname);
        trainerDocument.setUserId(trainerId);
        trainerDocument.setGymRoles(List.of(GymRole.TRAINER));

        mongoTemplate.save(trainerDocument);

        taskForTrainerDocumentId = UUID.randomUUID().toString();
        TaskDocument taskForTrainerDocument = new TaskDocument();
        taskForTrainerDocument.setTaskId(taskForTrainerDocumentId);
        taskForTrainerDocument.setManager(managerDocument);
        taskForTrainerDocument.setEmployee(trainerDocument);
        taskForTrainerDocument.setTitle("Title 1");
        taskForTrainerDocument.setDescription("Description 1");
        taskForTrainerDocument.setDueDate(LocalDate.now().plusMonths(1));
        taskForTrainerDocument.setLastTaskUpdateDate(LocalDate.now());
        taskForTrainerDocument.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskForTrainerDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);

        mongoTemplate.save(taskForTrainerDocument);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(TaskDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptTask_whenValidTaskIdEmployeeIdRequestBody(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ taskId + "/employee/" + employeeId + "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validAcceptedTaskRequestContent, headers);
        String expectedMessage = messages.get("task.approved.employee");

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
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Title 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Description 1");
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("employeeComment").textValue())
                .isEqualTo("I accept this task");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptTask_whenValidTaskIdTrainerIdRequestBody(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ taskForTrainerDocumentId + "/employee/" + trainerId +
                "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", trainerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validAcceptedTaskRequestContent, headers);
        String expectedMessage = messages.get("task.approved.employee");

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
                .isEqualTo(trainerId);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Anna");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Kwiatkowska");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Title 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Description 1");
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("employeeComment").textValue())
                .isEqualTo("I accept this task");
    }



    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldDeclineTask_whenValidTaskIdEmployeeIdStatus(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ taskId + "/employee/" + employeeId + "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validDeclinedTaskRequestContent, headers);
        String expectedMessage = messages.get("task.declined.employee");

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
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Title 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Description 1");
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.NOT_ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("employeeComment").textValue())
                .isEqualTo("I decline this task");

    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptTask_whenValidTaskIdEmployeeIdAndApprovedStatus(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ approvedTaskDocumentId + "/employee/" + employeeId
                + "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validAcceptedTaskRequestContent, headers);
        String expectedMessage = messages.get("task.approved.employee");

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
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Title 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Description 1");
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("employeeComment").textValue())
                .isEqualTo("I accept this task");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldDeclineTask_whenValidTaskIdEmployeeIdAndAlreadyApproved(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ approvedTaskDocumentId + "/employee/" + employeeId
                + "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validDeclinedTaskRequestContent, headers);
        String expectedMessage = messages.get("task.declined.employee");

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
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get("task").get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get("task").get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get("task").get("title").textValue())
                .isEqualTo("Title 1");
        assertThat(responseEntity.getBody().get("task").get("description").textValue())
                .isEqualTo("Description 1");
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.NOT_ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("employeeComment").textValue())
                .isEqualTo("I decline this task");
    }

    @Nested
    class ShouldNotAcceptTaskWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotAcceptTaskWhenNoToken(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/"+ taskId + "/employee/" + employeeId + "/approvalStatus");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(validAcceptedTaskRequestContent, headers);

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
        void shouldNotAcceptTaskWhenLoggedAsUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/"+ taskId + "/employee/" + employeeId + "/approvalStatus");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(validAcceptedTaskRequestContent, headers);

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
        void shouldNotAcceptTaskWhenLoggedAsManager(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/"+ taskId + "/employee/" + employeeId + "/approvalStatus");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(validAcceptedTaskRequestContent, headers);

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
    void shouldNotAcceptTask_whenInvalidTaskId(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String invalidTaskId = UUID.randomUUID().toString();

        URI uri = new URI("http://localhost:" + port + "/"+ invalidTaskId + "/employee/" + employeeId
                + "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validAcceptedTaskRequestContent, headers);
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
    void shouldNotAcceptTask_whenInvalidStatus(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ taskId + "/employee/" + employeeId + "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidTaskRequestInvalidAcceptanceStatusRequestContent, headers);
        String expectedMessage = messages.get("exception.invalid.status");

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
    void shouldNotAcceptTask_whenInvalidComment(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ taskId + "/employee/" + employeeId + "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidTaskRequestInvalidEmployeeCommentRequestContent, headers);
        String expectedMessage = messages.get("request.bind.exception");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        assertThat(responseEntity.getBody().get("errors")).isNotNull();
        assertThat(responseEntity.getBody().get("errors").get("employeeComment").textValue())
                .isEqualTo(messages.get("field.employee.comment"));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotAcceptTask_whenMissingRequiredData(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ taskId + "/employee/" + employeeId + "/approvalStatus");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidTaskRequestMissingRequestDataRequestContent, headers);
        String expectedMessage = messages.get("request.bind.exception");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        assertThat(responseEntity.getBody().get("errors")).isNotNull();
        assertThat(responseEntity.getBody().get("errors").get("acceptanceStatus").textValue())
                .isEqualTo(messages.get("field.required"));
        assertThat(responseEntity.getBody().get("errors").get("employeeComment").textValue())
                .isEqualTo(messages.get("field.required"));
    }
}
