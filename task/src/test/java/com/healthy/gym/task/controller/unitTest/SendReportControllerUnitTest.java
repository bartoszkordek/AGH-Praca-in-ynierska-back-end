package com.healthy.gym.task.controller.unitTest;

import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.controller.TaskController;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.exception.TaskDeclinedByEmployeeException;
import com.healthy.gym.task.exception.TaskNotFoundException;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
import com.healthy.gym.task.service.TaskService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
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
import java.util.Objects;
import java.util.UUID;

import static com.healthy.gym.task.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.task.configuration.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TaskController.class)
public class SendReportControllerUnitTest {

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

    private String requestContent;
    private String emptyEmployeeReportRequestContent;
    private String invalidEmployeeReportRequestContent;
    private String requestResult;
    private EmployeeReportRequest employeeReportRequest;
    private EmployeeReportRequest emptyEmployeeReportRequest;
    private EmployeeReportRequest invalidEmployeeReportRequest;

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

        requestResult = "Done!";
        employeeReportRequest = new EmployeeReportRequest();
        employeeReportRequest.setResult(requestResult);

        requestContent = objectMapper.writeValueAsString(employeeReportRequest);

        emptyEmployeeReportRequest = new EmployeeReportRequest();
        emptyEmployeeReportRequestContent = objectMapper.writeValueAsString(emptyEmployeeReportRequest);

        invalidEmployeeReportRequest = new EmployeeReportRequest();
        invalidEmployeeReportRequest.setResult("R");
        invalidEmployeeReportRequestContent = objectMapper.writeValueAsString(invalidEmployeeReportRequest);

        uri = new URI("/");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldSendReport(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri+taskId+"/employee/"+employeeId+"/report")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", employeeToken)
                .content(requestContent)
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
        LocalDate lastOrderUpdateDate = now.minusMonths(1);
        LocalDate dueDate = now.plusMonths(1);
        LocalDate reportDate = now;
        AcceptanceStatus employeeAccept = AcceptanceStatus.ACCEPTED;
        AcceptanceStatus managerAccept = AcceptanceStatus.NO_ACTION;
        String report = "Done!";

        TaskDTO taskResponse = new TaskDTO(
                taskId,
                manager,
                employee,
                title,
                description,
                report,
                null,
                lastOrderUpdateDate,
                dueDate,
                null,
                reportDate,
                null,
                0,
                employeeAccept,
                managerAccept,
                null
        );

        when(taskService.sendReport(taskId, employeeId, employeeReportRequest))
                .thenReturn(taskResponse);

        String expectedMessage = messages.get("report.sent");

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
                        jsonPath("$.task.orderDate").doesNotExist(),
                        jsonPath("$.task.lastOrderUpdateDate").value(is(lastOrderUpdateDate.toString())),
                        jsonPath("$.task.dueDate").value(is(dueDate.toString())),
                        jsonPath("$.task.reportDate").value(is(reportDate.toString())),
                        jsonPath("$.task.employeeAccept").value(is(employeeAccept.toString())),
                        jsonPath("$.task.managerAccept").value(is(managerAccept.toString()))
                ));

    }

    @Nested
    class ShouldNotSendReportWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/report")
                    .header("Accept-Language", testedLocale.toString())
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

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
                    .put(uri+taskId+"/employee/"+employeeId+"/report")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .content(requestContent)
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
        void whenUserIsNotLogInAsManager(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/report")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(requestContent)
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
        void whenUserIsNotLogInAsAdmin(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/report")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(requestContent)
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

    @Nested
    class ShouldNotSendReportWhenInvalidRequest{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException_whenEmptyResultField(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/report")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(emptyEmployeeReportRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("request.bind.exception");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.errors").value(is(notNullValue())),
                            jsonPath("$.errors.result")
                                    .value(is(messages.get("field.required")))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException_whenInvalidResultField(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/report")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(invalidEmployeeReportRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("request.bind.exception");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.errors").value(is(notNullValue())),
                            jsonPath("$.errors.result")
                                    .value(is(messages.get("field.result.failure")))
                    ));
        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldTaskDeclinedByEmployeeException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String notExistingTaskId = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+notExistingTaskId+"/employee/"+employeeId+"/report")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.task.not.found");

            doThrow(TaskNotFoundException.class)
                    .when(taskService)
                    .sendReport(notExistingTaskId, employeeId, employeeReportRequest);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(TaskNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTaskNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String declinedTaskId = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+declinedTaskId+"/employee/"+employeeId+"/report")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.declined.employee");

            doThrow(TaskDeclinedByEmployeeException.class)
                    .when(taskService)
                    .sendReport(declinedTaskId, employeeId, employeeReportRequest);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            Assertions.assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(TaskDeclinedByEmployeeException.class)
                    );
        }
    }
}
