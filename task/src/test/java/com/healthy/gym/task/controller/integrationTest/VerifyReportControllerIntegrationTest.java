package com.healthy.gym.task.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.task.configuration.FixedClockConfig;
import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.pojo.request.ManagerReportVerificationRequest;
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
public class VerifyReportControllerIntegrationTest {

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
    private String employeeId;
    private String managerId;
    private String adminId;
    private String userToken;
    private String employeeToken;
    private String managerToken;
    private String adminToken;

    private String validTaskId;
    private String declinedByEmployeeTaskId;
    private String reportNotSentTaskId;

    private ObjectMapper objectMapper;

    private String validRequestContentApproved;
    private String validRequestContentDeclined;
    private String invalidRequestContentMissingValues;
    private String invalidRequestContentInvalidMark;
    private String invalidRequestContentInvalidStatus;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
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

        //request
        objectMapper = new ObjectMapper();

        ManagerReportVerificationRequest managerReportVerificationRequestApproved = new ManagerReportVerificationRequest();
        managerReportVerificationRequestApproved.setApprovalStatus("APPROVE");
        managerReportVerificationRequestApproved.setMark(4);

        validRequestContentApproved = objectMapper.writeValueAsString(managerReportVerificationRequestApproved);

        ManagerReportVerificationRequest managerReportVerificationRequestDeclined = new ManagerReportVerificationRequest();
        managerReportVerificationRequestDeclined.setApprovalStatus("DECLINE");
        managerReportVerificationRequestDeclined.setMark(2);

        validRequestContentDeclined = objectMapper.writeValueAsString(managerReportVerificationRequestDeclined);

        ManagerReportVerificationRequest managerReportVerificationRequestMissingValues
                = new ManagerReportVerificationRequest();
        invalidRequestContentMissingValues = objectMapper
                .writeValueAsString(managerReportVerificationRequestMissingValues);

        ManagerReportVerificationRequest managerReportVerificationRequestInvalidMark
                = new ManagerReportVerificationRequest();
        managerReportVerificationRequestInvalidMark.setApprovalStatus("APPROVE");
        managerReportVerificationRequestInvalidMark.setMark(6);
        invalidRequestContentInvalidMark = objectMapper
                .writeValueAsString(managerReportVerificationRequestInvalidMark);

        ManagerReportVerificationRequest managerReportVerificationRequestInvalidStatus
                = new ManagerReportVerificationRequest();
        managerReportVerificationRequestInvalidStatus.setApprovalStatus("INVALID");
        managerReportVerificationRequestInvalidStatus.setMark(3);
        invalidRequestContentInvalidStatus = objectMapper
                .writeValueAsString(managerReportVerificationRequestInvalidStatus);

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

        validTaskId = UUID.randomUUID().toString();
        TaskDocument taskDocument = new TaskDocument();
        taskDocument.setTaskId(validTaskId);
        taskDocument.setManager(managerDocument);
        taskDocument.setEmployee(employeeDocument);
        taskDocument.setTitle("Title 1");
        taskDocument.setDescription("Description 1");
        taskDocument.setTaskCreationDate(LocalDateTime.now().minusMonths(1));
        taskDocument.setDueDate(LocalDateTime.now().plusMonths(1));
        taskDocument.setLastTaskUpdateDate(LocalDateTime.now().minusDays(5));
        taskDocument.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);
        taskDocument.setEmployeeComment("I approve this task");
        taskDocument.setReport("Sample report");
        taskDocument.setReportDate(LocalDateTime.now().minusDays(5));

        mongoTemplate.save(taskDocument);

        declinedByEmployeeTaskId = UUID.randomUUID().toString();
        TaskDocument declinedByEmployeeTaskDocument = new TaskDocument();
        declinedByEmployeeTaskDocument.setTaskId(declinedByEmployeeTaskId);
        declinedByEmployeeTaskDocument.setManager(managerDocument);
        declinedByEmployeeTaskDocument.setEmployee(employeeDocument);
        declinedByEmployeeTaskDocument.setTitle("Title 1");
        declinedByEmployeeTaskDocument.setDescription("Description 1");
        declinedByEmployeeTaskDocument.setTaskCreationDate(LocalDateTime.now().minusMonths(1));
        declinedByEmployeeTaskDocument.setDueDate(LocalDateTime.now().plusMonths(1));
        declinedByEmployeeTaskDocument.setLastTaskUpdateDate(LocalDateTime.now().minusDays(5));
        declinedByEmployeeTaskDocument.setEmployeeAccept(AcceptanceStatus.NOT_ACCEPTED);
        declinedByEmployeeTaskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);
        declinedByEmployeeTaskDocument.setEmployeeComment("I decline this task");

        mongoTemplate.save(declinedByEmployeeTaskDocument);

        reportNotSentTaskId = UUID.randomUUID().toString();
        TaskDocument reportNotSentEmployeeTaskDocument = new TaskDocument();
        reportNotSentEmployeeTaskDocument.setTaskId(reportNotSentTaskId);
        reportNotSentEmployeeTaskDocument.setManager(managerDocument);
        reportNotSentEmployeeTaskDocument.setEmployee(employeeDocument);
        reportNotSentEmployeeTaskDocument.setTitle("Title 1");
        reportNotSentEmployeeTaskDocument.setDescription("Description 1");
        reportNotSentEmployeeTaskDocument.setTaskCreationDate(LocalDateTime.now().minusMonths(1));
        reportNotSentEmployeeTaskDocument.setDueDate(LocalDateTime.now().plusMonths(1));
        reportNotSentEmployeeTaskDocument.setLastTaskUpdateDate(LocalDateTime.now().minusDays(5));
        reportNotSentEmployeeTaskDocument.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        reportNotSentEmployeeTaskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);
        reportNotSentEmployeeTaskDocument.setEmployeeComment("I approve this task");

        mongoTemplate.save(reportNotSentEmployeeTaskDocument);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(TaskDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptReport(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + validTaskId + "/reportVerification");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validRequestContentApproved, headers);
        String expectedMessage = messages.get("report.approved.manager");

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
        assertThat(responseEntity.getBody().get("task").get("taskCreationDate").textValue())
                .isEqualTo(LocalDate.now().minusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("employeeComment").textValue())
                .isEqualTo("I approve this task");
        assertThat(responseEntity.getBody().get("task").get("report").textValue())
                .isEqualTo("Sample report");
        assertThat(responseEntity.getBody().get("task").get("reportDate").textValue())
                .isEqualTo(LocalDate.now().minusDays(5).toString());
        assertThat(responseEntity.getBody().get("task").get("mark").intValue())
                .isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldDeclineReport(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + validTaskId + "/reportVerification");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validRequestContentDeclined, headers);
        String expectedMessage = messages.get("report.declined.manager");

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
        assertThat(responseEntity.getBody().get("task").get("taskCreationDate").textValue())
                .isEqualTo(LocalDate.now().minusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("lastTaskUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NOT_ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("employeeComment").textValue())
                .isEqualTo("I approve this task");
        assertThat(responseEntity.getBody().get("task").get("report").textValue())
                .isEqualTo("Sample report");
        assertThat(responseEntity.getBody().get("task").get("reportDate").textValue())
                .isEqualTo(LocalDate.now().minusDays(5).toString());
        assertThat(responseEntity.getBody().get("task").get("mark").intValue())
                .isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotSendReport_whenMissingRequiredValues(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + validTaskId + "/reportVerification");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidRequestContentMissingValues, headers);
        String expectedMessage = messages.get("request.bind.exception");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        assertThat(responseEntity.getBody().get("errors")).isNotNull();
        assertThat(responseEntity.getBody().get("errors").get("approvalStatus").textValue())
                .isEqualTo(messages.get("field.required"));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotSendReport_whenNotFoundTaskId(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String notExistingTaskId = UUID.randomUUID().toString();

        URI uri = new URI("http://localhost:" + port + "/" + notExistingTaskId + "/reportVerification");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(validRequestContentApproved, headers);
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
    void shouldNotSendReport_whenInvalidRequestMark(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + validTaskId + "/reportVerification");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidRequestContentInvalidMark, headers);
        String expectedMessage = messages.get("exception.invalid.mark");

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
    void shouldNotSendReport_whenInvalidRequestStatus(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + validTaskId + "/reportVerification");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidRequestContentInvalidStatus, headers);
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
    void shouldNotSendReport_whenDeclinedByEmployee(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + declinedByEmployeeTaskId + "/reportVerification");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidRequestContentInvalidStatus, headers);
        String expectedMessage = messages.get("exception.declined.employee");

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
    void shouldNotSendReport_whenReportNotSent(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + reportNotSentTaskId + "/reportVerification");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(invalidRequestContentInvalidStatus, headers);
        String expectedMessage = messages.get("exception.report.not.sent");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }


    @Nested
    class ShouldNotVerifyReportWhenNotAuthorized {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotVerifyReportWhenNoToken(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + validTaskId + "/reportVerification");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(validRequestContentApproved, headers);

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
        void shouldNotVerifyReportWhenLoggedAsUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + validTaskId + "/reportVerification");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(validRequestContentApproved, headers);

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
        void shouldNotVerifyReportWhenLoggedAsEmployee(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + validTaskId + "/reportVerification");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(validRequestContentApproved, headers);

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

}
