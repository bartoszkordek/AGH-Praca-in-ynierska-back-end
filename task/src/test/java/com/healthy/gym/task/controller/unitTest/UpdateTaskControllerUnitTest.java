package com.healthy.gym.task.controller.unitTest;

import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.controller.TaskController;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.exception.EmployeeNotFoundException;
import com.healthy.gym.task.exception.ManagerNotFoundException;
import com.healthy.gym.task.exception.RetroDueDateException;
import com.healthy.gym.task.pojo.request.ManagerOrderRequest;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.healthy.gym.task.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.task.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TaskController.class)
public class UpdateTaskControllerUnitTest {

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

    private String requestContent;
    private String requestTitle;
    private String requestDescription;
    private String requestDueDate;
    private ManagerOrderRequest managerOrderRequest;

    private ObjectMapper objectMapper;

    private URI uri;

    @BeforeEach
    void setUp() throws JsonProcessingException, URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getUserToken(employeeId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        taskId = UUID.randomUUID().toString();

        objectMapper = new ObjectMapper();

        requestTitle = "Test task 1";
        requestDescription = "Description for task 1";
        requestDueDate = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        managerOrderRequest = new ManagerOrderRequest();
        managerOrderRequest.setTitle(requestTitle);
        managerOrderRequest.setDescription(requestDescription);
        managerOrderRequest.setEmployeeId(employeeId);
        managerOrderRequest.setDueDate(requestDueDate);

        requestContent = objectMapper.writeValueAsString(managerOrderRequest);

        uri = new URI("/");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldUpdateTask(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri+taskId)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .content(requestContent)
                .contentType(MediaType.APPLICATION_JSON);

        var now = LocalDate.now();
        String managerId = UUID.randomUUID().toString();
        String managerName = "Martin";
        String managerSurname = "Manager";
        BasicUserInfoDTO manager = new BasicUserInfoDTO(managerId, managerName, managerSurname);
        String employeeId = UUID.randomUUID().toString();
        String employeeName = "Eric";
        String employeeSurname = "Employee";
        BasicUserInfoDTO employee = new BasicUserInfoDTO(employeeId, employeeName, employeeSurname);
        String title = "Test task 1";
        String description = "Description for task 1";
        LocalDate lastOrderUpdateDate = now;
        LocalDate dueDate = now.plusMonths(1);
        AcceptanceStatus employeeAccept = AcceptanceStatus.NO_ACTION;
        AcceptanceStatus managerAccept = AcceptanceStatus.NO_ACTION;

        TaskDTO taskResponse = new TaskDTO(
                taskId,
                manager,
                employee,
                title,
                description,
                null,
                null,
                lastOrderUpdateDate,
                dueDate,
                null,
                employeeAccept,
                managerAccept
        );

        when(taskService.updateTask(taskId, managerOrderRequest))
                .thenReturn(taskResponse);

        String expectedMessage = messages.get("task.updated");

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
                        jsonPath("$.task.orderDate").doesNotExist(),
                        jsonPath("$.task.lastOrderUpdateDate").value(is(lastOrderUpdateDate.toString())),
                        jsonPath("$.task.dueDate").value(is(dueDate.toString())),
                        jsonPath("$.task.reportDate").doesNotExist(),
                        jsonPath("$.task.employeeAccept").value(is(employeeAccept.toString())),
                        jsonPath("$.task.managerAccept").value(is(managerAccept.toString()))
                ));

    }


    @Nested
    class ShouldNotUpdateTaskWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId)
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
                    .put(uri+taskId)
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
        void whenUserIsNotLogInAsEmployee(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
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
    class ShouldNotUpdateTaskWhenInvalidRequest {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException_whenInvalidRequestBodyValues(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            //before
            String invalidRequestTitle = "T";
            String invalidRequestDescription = "D";
            String invalidEmployeeId = "invalidEmployeeId";
            String invalidRequestDueDate = "Invalid Date";
            ManagerOrderRequest invalidManagerOrderRequest = new ManagerOrderRequest();
            invalidManagerOrderRequest.setTitle(invalidRequestTitle);
            invalidManagerOrderRequest.setDescription(invalidRequestDescription);
            invalidManagerOrderRequest.setEmployeeId(invalidEmployeeId);
            invalidManagerOrderRequest.setDueDate(invalidRequestDueDate);

            String invalidTitleRequestContent = objectMapper.writeValueAsString(invalidManagerOrderRequest);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(invalidTitleRequestContent)
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
                            jsonPath("$.errors.title")
                                    .value(is(messages.get("field.title.failure"))),
                            jsonPath("$.errors.description")
                                    .value(is(messages.get("field.description.failure"))),
                            jsonPath("$.errors.employeeId")
                                    .value(is(messages.get("exception.invalid.id.format"))),
                            jsonPath("$.errors.dueDate")
                                    .value(is(messages.get("exception.invalid.date.format")))
                    ));
        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException_whenInvalidEmptyBodyValues(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            //before
            ManagerOrderRequest invalidManagerOrderRequest = new ManagerOrderRequest();
            invalidManagerOrderRequest.setEmployeeId(UUID.randomUUID().toString());
            invalidManagerOrderRequest.setDueDate("2030-12-31");

            String invalidTitleRequestContent = objectMapper.writeValueAsString(invalidManagerOrderRequest);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(invalidTitleRequestContent)
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
                            jsonPath("$.errors.title")
                                    .value(is(messages.get("field.required"))),
                            jsonPath("$.errors.description")
                                    .value(is(messages.get("field.required")))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowManagerNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.manager.not.found");

            doThrow(ManagerNotFoundException.class)
                    .when(taskService)
                    .updateTask(any(),any());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(ManagerNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowEmployeeNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.employee.not.found");

            doThrow(EmployeeNotFoundException.class)
                    .when(taskService)
                    .updateTask(any(),any());

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
        void shouldThrowRetroDueDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+taskId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.retro.due.date");

            doThrow(RetroDueDateException.class)
                    .when(taskService)
                    .updateTask(any(),any());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(RetroDueDateException.class)
                    );
        }
    }

}
