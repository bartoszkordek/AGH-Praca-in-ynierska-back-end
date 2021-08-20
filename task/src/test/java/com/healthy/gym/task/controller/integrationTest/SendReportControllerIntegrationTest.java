package com.healthy.gym.task.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.task.configuration.FixedClockConfig;
import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
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
public class SendReportControllerIntegrationTest {

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
    private String taskId;
    private String declinedByEmployeeTaskId;

    private String requestContent;
    private String emptyRequestContent;
    private String invalidRequestContent;
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

        taskId = UUID.randomUUID().toString();

        //request
        objectMapper = new ObjectMapper();

        String report = "Done!";
        EmployeeReportRequest employeeReportRequest = new EmployeeReportRequest();
        employeeReportRequest.setResult(report);

        requestContent = objectMapper.writeValueAsString(employeeReportRequest);

        EmployeeReportRequest emptyEmployeeReportRequest = new EmployeeReportRequest();
        emptyRequestContent = objectMapper.writeValueAsString(emptyEmployeeReportRequest);

        EmployeeReportRequest invalidEmployeeReportRequest = new EmployeeReportRequest();
        invalidEmployeeReportRequest.setResult("R");
        invalidRequestContent = objectMapper.writeValueAsString(invalidEmployeeReportRequest);

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
        taskDocument.setLastOrderUpdateDate(LocalDate.now());
        taskDocument.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);

        mongoTemplate.save(taskDocument);

        declinedByEmployeeTaskId = UUID.randomUUID().toString();
        TaskDocument declinedByEmployeeTaskDocument = new TaskDocument();
        declinedByEmployeeTaskDocument.setTaskId(declinedByEmployeeTaskId);
        declinedByEmployeeTaskDocument.setManager(managerDocument);
        declinedByEmployeeTaskDocument.setEmployee(employeeDocument);
        declinedByEmployeeTaskDocument.setTitle("Title 1");
        declinedByEmployeeTaskDocument.setDescription("Description 1");
        declinedByEmployeeTaskDocument.setDueDate(LocalDate.now().plusMonths(1));
        declinedByEmployeeTaskDocument.setLastOrderUpdateDate(LocalDate.now());
        declinedByEmployeeTaskDocument.setEmployeeAccept(AcceptanceStatus.NOT_ACCEPTED);
        declinedByEmployeeTaskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);

        mongoTemplate.save(declinedByEmployeeTaskDocument);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(TaskDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldSendReport_whenValidTaskIdAndUserAndRequest(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/"+ taskId+"/employee/"+employeeId+"/report");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
        String expectedMessage = messages.get("report.sent");

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
        assertThat(responseEntity.getBody().get("task").get("lastOrderUpdateDate").textValue())
                .isEqualTo(LocalDate.now().toString());
        assertThat(responseEntity.getBody().get("task").get("dueDate").textValue())
                .isEqualTo(LocalDate.now().plusMonths(1).toString());
        assertThat(responseEntity.getBody().get("task").get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get("task").get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get("task").get("report").textValue())
                .isEqualTo("Done!");
        assertThat(responseEntity.getBody().get("task").get("reportDate").textValue())
                .isEqualTo(LocalDate.now().toString());
    }


    @Nested
    class ShouldNotSendReportWhenInvalidRequest{

        @Nested
        class BindException{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotSendReport_whenEmptyRequestContent(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/"+ taskId+"/employee/"+employeeId+"/report");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", employeeToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(emptyRequestContent, headers);
                String expectedMessage = messages.get("request.bind.exception");

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
                assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                        .isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
                assertThat(responseEntity.getBody().get("errors")).isNotNull();
                assertThat(responseEntity.getBody().get("errors").get("result").textValue())
                        .isEqualTo(messages.get("field.required"));
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotSendReport_whenInvalidRequestContent(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/"+ taskId+"/employee/"+employeeId+"/report");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", employeeToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(invalidRequestContent, headers);
                String expectedMessage = messages.get("request.bind.exception");

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
                assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                        .isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
                assertThat(responseEntity.getBody().get("errors")).isNotNull();
                assertThat(responseEntity.getBody().get("errors").get("result").textValue())
                        .isEqualTo(messages.get("field.result.failure"));
            }
        }


        @Nested
        class ShouldNotSendReportTaskWhenNotAuthorized{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotSendReportWhenNoToken(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/"+ taskId+"/employee/"+employeeId+"/report");

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
            void shouldNotSendReportWhenLoggedAsUser(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/"+ taskId+"/employee/"+employeeId+"/report");

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
            void shouldNotSendReportWhenLoggedAsManager(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/"+ taskId+"/employee/"+employeeId+"/report");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", managerToken);
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
        void shouldNotSendReport_whenInvalidTaskId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String notFoundTaskId = UUID.randomUUID().toString();

            URI uri = new URI("http://localhost:" + port + "/"+ notFoundTaskId+"/employee/"+employeeId+"/report");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
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
        void shouldNotSendReport_whenTaskAlreadyDeclinedByEmployee(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + declinedByEmployeeTaskId + "/employee/" + employeeId + "/report");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
            String expectedMessage = messages.get("exception.declined.employee");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

    }

}
