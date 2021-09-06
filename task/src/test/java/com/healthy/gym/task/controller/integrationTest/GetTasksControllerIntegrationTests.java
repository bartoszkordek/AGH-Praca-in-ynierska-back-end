package com.healthy.gym.task.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.task.component.TokenManager;
import com.healthy.gym.task.configuration.FixedClockConfig;
import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.enums.Priority;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class GetTasksControllerIntegrationTests {

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
    private String taskId1;
    private String taskId2;

    private int page;
    private int size;
    private Pageable paging;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }
    private LocalDateTime now;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        now = LocalDateTime.now();
        page = 0;
        size = 10;
        paging = PageRequest.of(page, size);

        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getUserToken(employeeId);

        managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

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

        taskId1 = UUID.randomUUID().toString();
        TaskDocument taskDocument1 = new TaskDocument();
        taskDocument1.setTaskId(taskId1);
        taskDocument1.setManager(managerDocument);
        taskDocument1.setEmployee(employeeDocument);
        taskDocument1.setTitle("Title 1");
        taskDocument1.setDescription("Description 1");
        taskDocument1.setTaskCreationDate(now.minusMonths(1));
        taskDocument1.setLastTaskUpdateDate(now.minusMonths(1));
        taskDocument1.setDueDate(now.plusMonths(3));
        taskDocument1.setReminderDate(now.plusMonths(2));
        taskDocument1.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocument1.setManagerAccept(AcceptanceStatus.NO_ACTION);

        mongoTemplate.save(taskDocument1);

        taskId2 = UUID.randomUUID().toString();
        TaskDocument taskDocument2 = new TaskDocument();
        taskDocument2.setTaskId(taskId2);
        taskDocument2.setManager(managerDocument);
        taskDocument2.setEmployee(employeeDocument);
        taskDocument2.setTitle("Title 2");
        taskDocument2.setDescription("Description 2");
        taskDocument2.setTaskCreationDate(now.minusDays(10));
        taskDocument2.setLastTaskUpdateDate(now.minusDays(3));
        taskDocument2.setDueDate(now.plusDays(20));
        taskDocument2.setPriority(Priority.HIGH);
        taskDocument2.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocument2.setManagerAccept(AcceptanceStatus.NO_ACTION);
        taskDocument2.setEmployeeComment("Employee Comment 2");

        mongoTemplate.save(taskDocument2);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(TaskDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenProvidedDates(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        String startDueDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDueDate = now.plusMonths(3).format(DateTimeFormatter.ISO_LOCAL_DATE);

        URI uri = new URI("http://localhost:" + port + "/page/" + page
                + "?startDueDate="+startDueDate +"&endDueDate=" + endDueDate);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get(0).get("id")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("id").textValue())
                .isEqualTo(taskId1);
        assertThat(responseEntity.getBody().get(0).get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get(0).get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get(0).get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get(0).get("employee").get("userId").textValue())
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get(0).get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get(0).get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get(0).get("title").textValue())
                .isEqualTo("Title 1");
        assertThat(responseEntity.getBody().get(0).get("description").textValue())
                .isEqualTo("Description 1");
        assertThat(responseEntity.getBody().get(0).get("report"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("taskCreationDate").textValue())
                .isEqualTo(now.minusMonths(1).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("lastTaskUpdateDate").textValue())
                .isEqualTo(now.minusMonths(1).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("dueDate").textValue())
                .isEqualTo(now.plusMonths(3).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("reminderDate").textValue())
                .isEqualTo(now.plusMonths(2).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("priority"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get(0).get("reportDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get(0).get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get(0).get("employeeComment"))
                .isNull();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get(1).get("id")).isNotNull();
        assertThat(responseEntity.getBody().get(1).get("id").textValue())
                .isEqualTo(taskId2);
        assertThat(responseEntity.getBody().get(1).get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get(1).get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get(1).get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get(1).get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get(1).get("employee").get("userId").textValue())
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get(1).get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get(1).get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get(1).get("title").textValue())
                .isEqualTo("Title 2");
        assertThat(responseEntity.getBody().get(1).get("description").textValue())
                .isEqualTo("Description 2");
        assertThat(responseEntity.getBody().get(1).get("report"))
                .isNull();
        assertThat(responseEntity.getBody().get(1).get("taskCreationDate").textValue())
                .isEqualTo(now.minusDays(10).format(formatter));
        assertThat(responseEntity.getBody().get(1).get("lastTaskUpdateDate").textValue())
                .isEqualTo(now.minusDays(3).format(formatter));
        assertThat(responseEntity.getBody().get(1).get("dueDate").textValue())
                .isEqualTo(now.plusDays(20).format(formatter));
        assertThat(responseEntity.getBody().get(1).get("reminderDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(1).get("priority").textValue())
                .isEqualTo("HIGH");
        assertThat(responseEntity.getBody().get(1).get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get(1).get("reportDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(1).get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get(1).get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get(1).get("employeeComment").textValue())
                .isEqualTo("Employee Comment 2");

        List<TaskDocument> gymPassDocumentList = mongoTemplate.findAll(TaskDocument.class);
        assertThat(gymPassDocumentList.size()).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenNotProvidedDates(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/page/" + page);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get(0).get("id")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("id").textValue())
                .isEqualTo(taskId2);
        assertThat(responseEntity.getBody().get(0).get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get(0).get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get(0).get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get(0).get("employee").get("userId").textValue())
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get(0).get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get(0).get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get(0).get("title").textValue())
                .isEqualTo("Title 2");
        assertThat(responseEntity.getBody().get(0).get("description").textValue())
                .isEqualTo("Description 2");
        assertThat(responseEntity.getBody().get(0).get("report"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("taskCreationDate").textValue())
                .isEqualTo(now.minusDays(10).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("lastTaskUpdateDate").textValue())
                .isEqualTo(now.minusDays(3).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("dueDate").textValue())
                .isEqualTo(now.plusDays(20).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("reminderDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("priority").textValue())
                .isEqualTo("HIGH");
        assertThat(responseEntity.getBody().get(0).get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get(0).get("reportDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get(0).get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get(0).get("employeeComment").textValue())
                .isEqualTo("Employee Comment 2");

        List<TaskDocument> gymPassDocumentList = mongoTemplate.findAll(TaskDocument.class);
        assertThat(gymPassDocumentList.size()).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenNotProvidedDatesAndUserId_employeeToken(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/page/" + page + "?userId=" + employeeId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get(0).get("id")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("id").textValue())
                .isEqualTo(taskId2);
        assertThat(responseEntity.getBody().get(0).get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get(0).get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get(0).get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get(0).get("employee").get("userId").textValue())
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get(0).get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get(0).get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get(0).get("title").textValue())
                .isEqualTo("Title 2");
        assertThat(responseEntity.getBody().get(0).get("description").textValue())
                .isEqualTo("Description 2");
        assertThat(responseEntity.getBody().get(0).get("report"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("taskCreationDate").textValue())
                .isEqualTo(now.minusDays(10).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("lastTaskUpdateDate").textValue())
                .isEqualTo(now.minusDays(3).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("dueDate").textValue())
                .isEqualTo(now.plusDays(20).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("reminderDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("priority").textValue())
                .isEqualTo("HIGH");
        assertThat(responseEntity.getBody().get(0).get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get(0).get("reportDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get(0).get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get(0).get("employeeComment").textValue())
                .isEqualTo("Employee Comment 2");

        List<TaskDocument> gymPassDocumentList = mongoTemplate.findAll(TaskDocument.class);
        assertThat(gymPassDocumentList.size()).isEqualTo(2);
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenNotProvidedDatesProvidedValidUserIdAndPriority(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        String priority = "HIGH";

        URI uri = new URI("http://localhost:" + port + "/page/" + page
                + "?userId=" + employeeId + "&priority=" + priority);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get(0).get("id")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("id").textValue())
                .isEqualTo(taskId2);
        assertThat(responseEntity.getBody().get(0).get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get(0).get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get(0).get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get(0).get("employee").get("userId").textValue())
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get(0).get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get(0).get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get(0).get("title").textValue())
                .isEqualTo("Title 2");
        assertThat(responseEntity.getBody().get(0).get("description").textValue())
                .isEqualTo("Description 2");
        assertThat(responseEntity.getBody().get(0).get("report"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("taskCreationDate").textValue())
                .isEqualTo(now.minusDays(10).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("lastTaskUpdateDate").textValue())
                .isEqualTo(now.minusDays(3).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("dueDate").textValue())
                .isEqualTo(now.plusDays(20).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("reminderDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("priority").textValue())
                .isEqualTo("HIGH");
        assertThat(responseEntity.getBody().get(0).get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get(0).get("reportDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get(0).get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get(0).get("employeeComment").textValue())
                .isEqualTo("Employee Comment 2");

        List<TaskDocument> gymPassDocumentList = mongoTemplate.findAll(TaskDocument.class);
        assertThat(gymPassDocumentList.size()).isEqualTo(2);
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenNotProvidedDatesProvidedValidUserIdNotProvidedPriority(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/page/" + page
                + "?userId=" + employeeId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get(0).get("id")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("id").textValue())
                .isEqualTo(taskId2);
        assertThat(responseEntity.getBody().get(0).get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get(0).get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get(0).get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get(0).get("employee").get("userId").textValue())
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get(0).get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get(0).get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get(0).get("title").textValue())
                .isEqualTo("Title 2");
        assertThat(responseEntity.getBody().get(0).get("description").textValue())
                .isEqualTo("Description 2");
        assertThat(responseEntity.getBody().get(0).get("report"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("taskCreationDate").textValue())
                .isEqualTo(now.minusDays(10).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("lastTaskUpdateDate").textValue())
                .isEqualTo(now.minusDays(3).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("dueDate").textValue())
                .isEqualTo(now.plusDays(20).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("reminderDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("priority").textValue())
                .isEqualTo("HIGH");
        assertThat(responseEntity.getBody().get(0).get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get(0).get("reportDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get(0).get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get(0).get("employeeComment").textValue())
                .isEqualTo("Employee Comment 2");

        List<TaskDocument> gymPassDocumentList = mongoTemplate.findAll(TaskDocument.class);
        assertThat(gymPassDocumentList.size()).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenNotProvidedDatesNotProvidedUserIdProvidedPriority(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        String priority = "HIGH";

        URI uri = new URI("http://localhost:" + port + "/page/" + page
                + "?priority=" + priority);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get(0).get("id")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("id").textValue())
                .isEqualTo(taskId2);
        assertThat(responseEntity.getBody().get(0).get("manager")).isNotNull();
        assertThat(responseEntity.getBody().get(0).get("manager").get("userId").textValue())
                .isEqualTo(managerId);
        assertThat(responseEntity.getBody().get(0).get("manager").get("name").textValue())
                .isEqualTo("Adam");
        assertThat(responseEntity.getBody().get(0).get("manager").get("surname").textValue())
                .isEqualTo("Nowak");
        assertThat(responseEntity.getBody().get(0).get("employee").get("userId").textValue())
                .isEqualTo(employeeId);
        assertThat(responseEntity.getBody().get(0).get("employee").get("name").textValue())
                .isEqualTo("Jan");
        assertThat(responseEntity.getBody().get(0).get("employee").get("surname").textValue())
                .isEqualTo("Kowalski");
        assertThat(responseEntity.getBody().get(0).get("title").textValue())
                .isEqualTo("Title 2");
        assertThat(responseEntity.getBody().get(0).get("description").textValue())
                .isEqualTo("Description 2");
        assertThat(responseEntity.getBody().get(0).get("report"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("taskCreationDate").textValue())
                .isEqualTo(now.minusDays(10).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("lastTaskUpdateDate").textValue())
                .isEqualTo(now.minusDays(3).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("dueDate").textValue())
                .isEqualTo(now.plusDays(20).format(formatter));
        assertThat(responseEntity.getBody().get(0).get("reminderDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("priority").textValue())
                .isEqualTo("HIGH");
        assertThat(responseEntity.getBody().get(0).get("mark").intValue())
                .isZero();
        assertThat(responseEntity.getBody().get(0).get("reportDate"))
                .isNull();
        assertThat(responseEntity.getBody().get(0).get("employeeAccept").textValue())
                .isEqualTo(AcceptanceStatus.ACCEPTED.toString());
        assertThat(responseEntity.getBody().get(0).get("managerAccept").textValue())
                .isEqualTo(AcceptanceStatus.NO_ACTION.toString());
        assertThat(responseEntity.getBody().get(0).get("employeeComment").textValue())
                .isEqualTo("Employee Comment 2");

        List<TaskDocument> gymPassDocumentList = mongoTemplate.findAll(TaskDocument.class);
        assertThat(gymPassDocumentList.size()).isEqualTo(2);
    }


    @Nested
    class ShouldNotGetTasksWhenNotAuthorized {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetTasksWhenNoToken(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/page/" + page);


            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo("Access Denied");
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetTasksWhenLoggedAsUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/page/" + page);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            String expectedMessage = messages.get("exception.access.denied");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetTasksWhenLoggedAsEmployeeNotProvidedUserId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/page/" + page);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            String expectedMessage = messages.get("exception.access.denied");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetTasksWhenLoggedAsOtherEmployee(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String otherEmployeeId = UUID.randomUUID().toString();
            String otherEmployeeToken = tokenFactory.getEmployeeToken(otherEmployeeId);

            URI uri = new URI("http://localhost:" + port + "/page/" + page + "?userId=" + employeeId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", otherEmployeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

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
    void shouldNotGetTasks_whenStartDateAfterEndDate(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String startDueDate = "2100-01-01";
        String endDueDate = "2000-01-01";

        URI uri = new URI("http://localhost:" + port + "/page/" + page
                + "?startDueDate="+startDueDate +"&endDueDate=" + endDueDate);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        String expectedMessage = messages.get("exception.start.after.end");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetTasks_whenEmployeeNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String employeeNotFoundId = UUID.randomUUID().toString();

        URI uri = new URI("http://localhost:" + port + "/page/" + page
                + "?userId="+employeeNotFoundId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        String expectedMessage = messages.get("exception.employee.not.found");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetTasks_whenInvalidPriority(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String priority = "INVALID";

        URI uri = new URI("http://localhost:" + port + "/page/" + page
                + "?priority="+priority);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        String expectedMessage = messages.get("exception.invalid.priority");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetTasks_whenEmptyList(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String startDueDate = "2100-01-01";
        String endDueDate = "2100-02-01";

        URI uri = new URI("http://localhost:" + port + "/page/" + page
                + "?startDueDate="+startDueDate +"&endDueDate=" + endDueDate);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        String expectedMessage = messages.get("exception.no.tasks");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                .isEqualTo(expectedMessage);
    }
}
