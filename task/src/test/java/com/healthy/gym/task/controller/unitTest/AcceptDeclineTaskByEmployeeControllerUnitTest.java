package com.healthy.gym.task.controller.unitTest;

import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.controller.TaskController;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.exception.EmployeeNotFoundException;
import com.healthy.gym.task.exception.InvalidStatusException;
import com.healthy.gym.task.exception.TaskNotFoundException;
import com.healthy.gym.task.pojo.request.EmployeeAcceptDeclineTaskRequest;
import com.healthy.gym.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
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
import java.util.Objects;
import java.util.UUID;

import static com.healthy.gym.task.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.task.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles(value = "test")
@WebMvcTest(TaskController.class)
public class AcceptDeclineTaskByEmployeeControllerUnitTest {

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

    private String requestAcceptanceStatusAccept;
    private String requestEmployeeCommentAccept;
    private String requestAcceptanceStatusDecline;
    private String requestEmployeeCommentDecline;
    private String requestInvalidAcceptanceStatus;
    private String requestInvalidEmployeeComment;

    private ObjectMapper objectMapper;

    private String validAcceptedTaskRequestContent;
    private String validDeclinedTaskRequestContent;
    private String invalidTaskRequestInvalidEmployeeCommentRequestContent;
    private String invalidTaskRequestMissingRequestDataRequestContent;
    private String invalidTaskRequestInvalidAcceptanceStatusRequestContent;
    private String invalidStateExceptionRequestContent;

    private EmployeeAcceptDeclineTaskRequest validAcceptTaskRequest;
    private EmployeeAcceptDeclineTaskRequest validDeclineTaskRequest;
    private EmployeeAcceptDeclineTaskRequest invalidTaskRequestInvalidEmployeeComment;
    private EmployeeAcceptDeclineTaskRequest invalidTaskRequestMissingRequestData;
    private EmployeeAcceptDeclineTaskRequest invalidTaskRequestInvalidAcceptanceStatus;
    private EmployeeAcceptDeclineTaskRequest invalidStateExceptionRequest;

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

        requestAcceptanceStatusAccept = "approve";
        requestEmployeeCommentAccept = "I accept this task";

        requestAcceptanceStatusDecline = "DECLINE";
        requestEmployeeCommentDecline = "I decline this task";

        requestInvalidAcceptanceStatus = "INVALID_ACCEPTANCE_STATUS";
        requestInvalidEmployeeComment = "C";

        //valid request - accepted task
        validAcceptTaskRequest = new EmployeeAcceptDeclineTaskRequest();
        validAcceptTaskRequest.setAcceptanceStatus(requestAcceptanceStatusAccept);
        validAcceptTaskRequest.setEmployeeComment(requestEmployeeCommentAccept);
        validAcceptedTaskRequestContent = objectMapper.writeValueAsString(validAcceptTaskRequest);

        //valid request - declined task
        validDeclineTaskRequest = new EmployeeAcceptDeclineTaskRequest();
        validDeclineTaskRequest.setAcceptanceStatus(requestAcceptanceStatusDecline);
        validDeclineTaskRequest.setEmployeeComment(requestEmployeeCommentDecline);
        validDeclinedTaskRequestContent = objectMapper.writeValueAsString(validDeclineTaskRequest);

        //invalid request - invalid employee comment
        invalidTaskRequestInvalidEmployeeComment = new EmployeeAcceptDeclineTaskRequest();
        invalidTaskRequestInvalidEmployeeComment.setAcceptanceStatus(requestAcceptanceStatusAccept);
        invalidTaskRequestInvalidEmployeeComment.setEmployeeComment(requestInvalidEmployeeComment);
        invalidTaskRequestInvalidEmployeeCommentRequestContent = objectMapper.writeValueAsString(invalidTaskRequestInvalidEmployeeComment);

        //invalid request - invalid acceptance status
        invalidTaskRequestInvalidAcceptanceStatus = new EmployeeAcceptDeclineTaskRequest();
        invalidTaskRequestInvalidAcceptanceStatus.setAcceptanceStatus(requestInvalidAcceptanceStatus);
        invalidTaskRequestInvalidAcceptanceStatus.setEmployeeComment(requestEmployeeCommentAccept);
        invalidTaskRequestInvalidAcceptanceStatusRequestContent = objectMapper.writeValueAsString(invalidTaskRequestInvalidAcceptanceStatus);

        //invalid request - missing required data
        invalidTaskRequestMissingRequestData = new EmployeeAcceptDeclineTaskRequest();
        invalidTaskRequestMissingRequestDataRequestContent = objectMapper.writeValueAsString(invalidTaskRequestMissingRequestData);

        //request for InvalidStateExceptionTest
        invalidStateExceptionRequest = new EmployeeAcceptDeclineTaskRequest();
        invalidStateExceptionRequest.setAcceptanceStatus("InvalidStateException");
        invalidStateExceptionRequest.setEmployeeComment("InvalidStateException");
        invalidStateExceptionRequestContent = objectMapper.writeValueAsString(invalidStateExceptionRequest);

        uri = new URI("/");
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptTask(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", employeeToken)
                .content(validAcceptedTaskRequestContent)
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
        AcceptanceStatus employeeAccept = AcceptanceStatus.ACCEPTED;
        AcceptanceStatus managerAccept = AcceptanceStatus.NO_ACTION;
        String employeeComment = "I accept this task";

        TaskDTO taskResponse = new TaskDTO(
                taskId,
                manager,
                employee,
                title,
                description,
                null,
                taskCreationDate,
                lastTaskUpdateDate,
                dueDate,
                null,
                null,
                null,
                0,
                employeeAccept,
                managerAccept,
                employeeComment
        );

        when(taskService.acceptDeclineTaskByEmployee(taskId, employeeId, validAcceptTaskRequest))
                .thenReturn(taskResponse);

        String expectedMessage = messages.get("task.approved.employee");

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
                        jsonPath("$.task.report").doesNotExist(),
                        jsonPath("$.task.taskCreationDate").value(is(taskCreationDate.toString())),
                        jsonPath("$.task.lastTaskUpdateDate").value(is(lastTaskUpdateDate.toString())),
                        jsonPath("$.task.dueDate").value(is(dueDate.toString())),
                        jsonPath("$.task.reportDate").doesNotExist(),
                        jsonPath("$.task.employeeAccept").value(is(employeeAccept.toString())),
                        jsonPath("$.task.managerAccept").value(is(managerAccept.toString())),
                        jsonPath("$.task.employeeComment").value(is(employeeComment))
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldDeclineTask(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", employeeToken)
                .content(validDeclinedTaskRequestContent)
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
        LocalDate lastOrderUpdateDate = now;
        LocalDate dueDate = now.plusMonths(1);
        AcceptanceStatus employeeAccept = AcceptanceStatus.NOT_ACCEPTED;
        AcceptanceStatus managerAccept = AcceptanceStatus.NO_ACTION;

        TaskDTO taskResponse = new TaskDTO(
                taskId,
                manager,
                employee,
                title,
                description,
                null,
                taskCreationDate,
                lastOrderUpdateDate,
                dueDate,
                null,
                null,
                null,
                0,
                employeeAccept,
                managerAccept,
                null
        );

        when(taskService.acceptDeclineTaskByEmployee(taskId, employeeId, validDeclineTaskRequest))
                .thenReturn(taskResponse);

        String expectedMessage = messages.get("task.declined.employee");

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
                        jsonPath("$.task.report").doesNotExist(),
                        jsonPath("$.task.taskCreationDate").value(is(taskCreationDate.toString())),
                        jsonPath("$.task.lastTaskUpdateDate").value(is(lastOrderUpdateDate.toString())),
                        jsonPath("$.task.dueDate").value(is(dueDate.toString())),
                        jsonPath("$.task.reportDate").doesNotExist(),
                        jsonPath("$.task.employeeAccept").value(is(employeeAccept.toString())),
                        jsonPath("$.task.managerAccept").value(is(managerAccept.toString()))
                ));
    }

    @Nested
    class ShouldNotApproveTaskWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
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
                    .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .content(validAcceptedTaskRequestContent)
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
                    .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(validAcceptedTaskRequestContent)
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
                    .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(validAcceptedTaskRequestContent)
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
    class ShouldNotUpdateTaskWhenInvalidRequest {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException_whenInvalidComment(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(invalidTaskRequestInvalidEmployeeCommentRequestContent)
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
                            jsonPath("$.errors.employeeComment")
                                    .value(is(messages.get("field.employee.comment")))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException_whenMissingRequiredData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(invalidTaskRequestMissingRequestDataRequestContent)
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
                            jsonPath("$.errors.acceptanceStatus")
                                    .value(is(messages.get("field.required"))),
                            jsonPath("$.errors.employeeComment")
                                    .value(is(messages.get("field.required")))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTaskNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String notExistingTaskId = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+notExistingTaskId+"/employee/"+employeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(validAcceptedTaskRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.task.not.found");

            doThrow(TaskNotFoundException.class)
                    .when(taskService)
                    .acceptDeclineTaskByEmployee(notExistingTaskId, employeeId, validAcceptTaskRequest);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(TaskNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowEmployeeNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String notExistingEmployeeId = UUID.randomUUID().toString();
            String notExistingEmployeeToken = tokenFactory.getUserToken(notExistingEmployeeId);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+notExistingEmployeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", notExistingEmployeeToken)
                    .content(validAcceptedTaskRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.employee.not.found");

            doThrow(EmployeeNotFoundException.class)
                    .when(taskService)
                    .acceptDeclineTaskByEmployee(taskId, notExistingEmployeeId, validAcceptTaskRequest);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(EmployeeNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInvalidStatusException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId+"/employee/"+employeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(invalidTaskRequestInvalidAcceptanceStatusRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.invalid.status");

            doThrow(InvalidStatusException.class)
                    .when(taskService)
                    .acceptDeclineTaskByEmployee(taskId, employeeId, invalidTaskRequestInvalidAcceptanceStatus);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(InvalidStatusException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String taskIdForIllegalStateException = UUID.randomUUID().toString();
            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskIdForIllegalStateException+"/employee/"+employeeId+"/approvalStatus")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(invalidStateExceptionRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(IllegalStateException.class)
                    .when(taskService)
                    .acceptDeclineTaskByEmployee(taskIdForIllegalStateException, employeeId, invalidStateExceptionRequest);

            String expectedMessage = messages.get("exception.internal.error");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(IllegalStateException.class)
                    );
        }
    }
}
