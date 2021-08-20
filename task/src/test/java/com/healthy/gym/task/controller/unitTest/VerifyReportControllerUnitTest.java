package com.healthy.gym.task.controller.unitTest;

import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.controller.TaskController;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
import com.healthy.gym.task.pojo.request.ManagerReportVerificationRequest;
import com.healthy.gym.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.task.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.task.configuration.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TaskController.class)
@ActiveProfiles(value = "test")
public class VerifyReportControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private TaskService taskService;

    private String managerToken;
    private String adminToken;
    private String userToken;
    private String employeeToken;

    private String taskId;
    private String employeeId;

    private ObjectMapper objectMapper;

    private String validRequestContentApproved;
    private String validRequestContentDeclined;

    private ManagerReportVerificationRequest managerReportVerificationRequestApproved;
    private ManagerReportVerificationRequest managerReportVerificationRequestDeclined;

    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException, JsonProcessingException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getUserToken(employeeId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        taskId = UUID.randomUUID().toString();

        objectMapper = new ObjectMapper();

        managerReportVerificationRequestApproved = new ManagerReportVerificationRequest();
        managerReportVerificationRequestApproved.setMark(5);
        managerReportVerificationRequestApproved.setApprovalStatus("APPROVE");

        validRequestContentApproved = objectMapper.writeValueAsString(managerReportVerificationRequestApproved);

        managerReportVerificationRequestDeclined = new ManagerReportVerificationRequest();
        managerReportVerificationRequestDeclined.setMark(1);
        managerReportVerificationRequestDeclined.setApprovalStatus("APPROVE");

        validRequestContentDeclined = objectMapper.writeValueAsString(managerReportVerificationRequestDeclined);

        uri = new URI("/");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldApproveReport(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri+taskId+"/reportVerification")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .content(validRequestContentApproved)
                .contentType(MediaType.APPLICATION_JSON);

        var now = LocalDate.now();
        String managerId = UUID.randomUUID().toString();
        String managerName = "Martin";
        String managerSurname = "Manager";
        BasicUserInfoDTO manager = new BasicUserInfoDTO(managerId, managerName, managerSurname);
        String employeeName = "Eric";
        String employeeSurname = "Employee";
        BasicUserInfoDTO employee = new BasicUserInfoDTO(employeeId, employeeName, employeeSurname);
        String title = "Test task 1";
        String description = "Description for task 1";
        LocalDate taskCreationDate = now.minusMonths(1);
        LocalDate lastTaskUpdateDate = now;
        LocalDate dueDate = now.plusMonths(1);
        LocalDate reportDate = now.minusDays(2);
        int mark = 5;
        AcceptanceStatus employeeAccept = AcceptanceStatus.ACCEPTED;
        AcceptanceStatus managerAccept = AcceptanceStatus.ACCEPTED;
        String report = "Done!";
        String employeeComment = "I approve this task.";

        TaskDTO taskResponse = new TaskDTO(
                taskId,
                manager,
                employee,
                title,
                description,
                report,
                taskCreationDate,
                lastTaskUpdateDate,
                dueDate,
                null,
                reportDate,
                null,
                mark,
                employeeAccept,
                managerAccept,
                employeeComment
        );

        when(taskService.verifyReport(taskId, managerReportVerificationRequestApproved))
                .thenReturn(taskResponse);

        String expectedMessage = messages.get("report.approved.manager");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(is(expectedMessage)),
                        jsonPath("$.task").exists(),
                        jsonPath("$.task.id").exists(),
                        jsonPath("$.task.id").value(is(taskId)),
                        jsonPath("$.task.manager").exists(),
                        jsonPath("$.task.manager.userId").value(is(managerId)),
                        jsonPath("$.task.manager.name").value(is(managerName)),
                        jsonPath("$.task.manager.surname").value(is(managerSurname)),
                        jsonPath("$.task.employee").exists(),
                        jsonPath("$.task.employee.userId").exists(),
                        jsonPath("$.task.employee.name").value(is(employeeName)),
                        jsonPath("$.task.employee.surname").value(is(employeeSurname)),
                        jsonPath("$.task.title").value(is(title)),
                        jsonPath("$.task.description").value(is(description)),
                        jsonPath("$.task.report").value(is(report)),
                        jsonPath("$.task.taskCreationDate").value(is(taskCreationDate.toString())),
                        jsonPath("$.task.lastTaskUpdateDate").value(is(lastTaskUpdateDate.toString())),
                        jsonPath("$.task.dueDate").value(is(dueDate.toString())),
                        jsonPath("$.task.reportDate").value(is(reportDate.toString())),
                        jsonPath("$.task.mark").value(is(mark)),
                        jsonPath("$.task.employeeAccept").value(is(employeeAccept.toString())),
                        jsonPath("$.task.managerAccept").value(is(managerAccept.toString())),
                        jsonPath("$.task.employeeComment").value(is(employeeComment))
                ));
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldDeclineReport(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri+taskId+"/reportVerification")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .content(validRequestContentDeclined)
                .contentType(MediaType.APPLICATION_JSON);

        var now = LocalDate.now();
        String managerId = UUID.randomUUID().toString();
        String managerName = "Martin";
        String managerSurname = "Manager";
        BasicUserInfoDTO manager = new BasicUserInfoDTO(managerId, managerName, managerSurname);
        String employeeName = "Eric";
        String employeeSurname = "Employee";
        BasicUserInfoDTO employee = new BasicUserInfoDTO(employeeId, employeeName, employeeSurname);
        String title = "Test task 1";
        String description = "Description for task 1";
        LocalDate taskCreationDate = now.minusMonths(1);
        LocalDate lastTaskUpdateDate = now;
        LocalDate dueDate = now.plusMonths(1);
        LocalDate reportDate = now.minusDays(2);
        int mark = 1;
        AcceptanceStatus employeeAccept = AcceptanceStatus.ACCEPTED;
        AcceptanceStatus managerAccept = AcceptanceStatus.NOT_ACCEPTED;
        String report = "Done!";
        String employeeComment = "I approve this task.";

        TaskDTO taskResponse = new TaskDTO(
                taskId,
                manager,
                employee,
                title,
                description,
                report,
                taskCreationDate,
                lastTaskUpdateDate,
                dueDate,
                null,
                reportDate,
                null,
                mark,
                employeeAccept,
                managerAccept,
                employeeComment
        );

        when(taskService.verifyReport(taskId, managerReportVerificationRequestDeclined))
                .thenReturn(taskResponse);

        String expectedMessage = messages.get("report.declined.manager");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(is(expectedMessage)),
                        jsonPath("$.task").exists(),
                        jsonPath("$.task.id").exists(),
                        jsonPath("$.task.id").value(is(taskId)),
                        jsonPath("$.task.manager").exists(),
                        jsonPath("$.task.manager.userId").value(is(managerId)),
                        jsonPath("$.task.manager.name").value(is(managerName)),
                        jsonPath("$.task.manager.surname").value(is(managerSurname)),
                        jsonPath("$.task.employee").exists(),
                        jsonPath("$.task.employee.userId").exists(),
                        jsonPath("$.task.employee.name").value(is(employeeName)),
                        jsonPath("$.task.employee.surname").value(is(employeeSurname)),
                        jsonPath("$.task.title").value(is(title)),
                        jsonPath("$.task.description").value(is(description)),
                        jsonPath("$.task.report").value(is(report)),
                        jsonPath("$.task.taskCreationDate").value(is(taskCreationDate.toString())),
                        jsonPath("$.task.lastTaskUpdateDate").value(is(lastTaskUpdateDate.toString())),
                        jsonPath("$.task.dueDate").value(is(dueDate.toString())),
                        jsonPath("$.task.reportDate").value(is(reportDate.toString())),
                        jsonPath("$.task.mark").value(is(mark)),
                        jsonPath("$.task.employeeAccept").value(is(employeeAccept.toString())),
                        jsonPath("$.task.managerAccept").value(is(managerAccept.toString())),
                        jsonPath("$.task.employeeComment").value(is(employeeComment))
                ));
    }

    @Nested
    class ShouldNotVerifyReportWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/reportVerification")
                    .header("Accept-Language", testedLocale.toString());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogInAsUsualUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/reportVerification")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .content(validRequestContentApproved)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.access.denied");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                    .andExpect(jsonPath("$.error").value(is("Forbidden")))
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogInAsEmployee(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/reportVerification")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(validRequestContentApproved)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.access.denied");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                    .andExpect(jsonPath("$.error").value(is("Forbidden")))
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

    }
}
