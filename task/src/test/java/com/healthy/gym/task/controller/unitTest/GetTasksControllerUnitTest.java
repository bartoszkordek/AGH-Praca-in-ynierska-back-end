package com.healthy.gym.task.controller.unitTest;

import com.healthy.gym.task.configuration.TestCountry;
import com.healthy.gym.task.configuration.TestRoleTokenFactory;
import com.healthy.gym.task.controller.TaskController;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.Priority;
import com.healthy.gym.task.exception.EmployeeNotFoundException;
import com.healthy.gym.task.exception.InvalidPriorityException;
import com.healthy.gym.task.exception.NoTasksException;
import com.healthy.gym.task.exception.StartDateAfterEndDateException;
import com.healthy.gym.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.healthy.gym.task.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.task.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TaskController.class)
@ActiveProfiles(value = "test")
class GetTasksControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private TaskService taskService;

    private String employeeId;
    private String managerToken;
    private String adminToken;
    private String userToken;
    private String employeeToken;

    private String taskId1;
    private String taskId2;
    private String taskId3;

    private int page;
    private int size;
    private Pageable paging;

    private List responseBeforeLastMonth;
    private List responseBetweenLastAndTwoFutureMonths;

    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getUserToken(employeeId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        page = 0;
        size = 10;
        paging = PageRequest.of(page, size);
        var now = LocalDate.now();
        String employeeName = "Jan";
        String employeeSurname = "Kowalski";
        String managerName = "Adam";
        String managerSurname = "Nowak";

        //DTOs
        taskId1 = UUID.randomUUID().toString();
        TaskDTO taskDTO1 = new TaskDTO(
                taskId1,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                "Title 1",
                "Description 1",
                null,
                now.minusMonths(1),
                now.minusMonths(1),
                now.plusMonths(2),
                now.plusMonths(1),
                null,
                null,
                0,
                AcceptanceStatus.NO_ACTION,
                AcceptanceStatus.NO_ACTION,
                null
        );

        taskId2 = UUID.randomUUID().toString();
        TaskDTO taskDTO2 = new TaskDTO(
                taskId2,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                "Title 2",
                "Description 2",
                null,
                now.minusDays(10),
                now.minusDays(3),
                now.plusDays(20),
                null,
                null,
                Priority.HIGH,
                0,
                AcceptanceStatus.ACCEPTED,
                AcceptanceStatus.NO_ACTION,
                "Employee Comment 2"
        );

        taskId3 = UUID.randomUUID().toString();
        TaskDTO taskDTO3 = new TaskDTO(
                taskId3,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                "Title 3",
                "Description 3",
                "Report 3",
                now.minusMonths(5),
                now.minusMonths(2).plusDays(1),
                now.minusMonths(1).minusDays(1),
                null,
                now.minusMonths(2),
                Priority.MEDIUM,
                5,
                AcceptanceStatus.ACCEPTED,
                AcceptanceStatus.ACCEPTED,
                "Employee Comment 3"
        );

        responseBeforeLastMonth = List.of(taskDTO3);
        responseBetweenLastAndTwoFutureMonths = List.of(taskDTO1, taskDTO2);

        uri = new URI("/page/");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenProvidedDateRange(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        var now = LocalDate.now();
        String startDueDate = now.minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDueDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page)+"?startDueDate="+startDueDate+"&endDueDate="+endDueDate)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);


        when(taskService.getTasks(
                now.minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                now.format(DateTimeFormatter.ISO_LOCAL_DATE),
                null,
                null,
                paging)
        ).thenReturn(responseBeforeLastMonth);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.[0].id").exists(),
                        jsonPath("$.[0].id").value(is(taskId3)),
                        jsonPath("$.[0].manager").exists(),
                        jsonPath("$.[0].manager.userId").exists(),
                        jsonPath("$.[0].manager.name").value(is("Adam")),
                        jsonPath("$.[0].manager.surname").value(is("Nowak")),
                        jsonPath("$.[0].employee").exists(),
                        jsonPath("$.[0].employee.userId").exists(),
                        jsonPath("$.[0].employee.name").value(is("Jan")),
                        jsonPath("$.[0].employee.surname").value(is("Kowalski")),
                        jsonPath("$.[0].title").value(is("Title 3")),
                        jsonPath("$.[0].description").value(is("Description 3")),
                        jsonPath("$.[0].report").value(is("Report 3")),
                        jsonPath("$.[0].taskCreationDate").value(is(now.minusMonths(5).toString())),
                        jsonPath("$.[0].lastTaskUpdateDate").value(is(now.minusMonths(2).plusDays(1).toString())),
                        jsonPath("$.[0].dueDate").value(is(now.minusMonths(1).minusDays(1).toString())),
                        jsonPath("$.[0].reminderDate").doesNotExist(),
                        jsonPath("$.[0].priority").value(is(Priority.MEDIUM.toString())),
                        jsonPath("$.[0].mark").value(is(5)),
                        jsonPath("$.[0].reportDate").value(is(now.minusMonths(2).toString())),
                        jsonPath("$.[0].employeeAccept").value(is(AcceptanceStatus.ACCEPTED.toString())),
                        jsonPath("$.[0].managerAccept").value(is(AcceptanceStatus.ACCEPTED.toString())),
                        jsonPath("$.[0].employeeComment").value(is("Employee Comment 3"))
                ));
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenProvidedDateRangeAndPriority(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        var now = LocalDate.now();
        String startDueDate = now.minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDueDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String priority = "MEDIUM";

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page)+"?startDueDate="+startDueDate+"&endDueDate="+endDueDate
                        + "&priority=" + priority)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);


        when(taskService.getTasks(
                now.minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                now.format(DateTimeFormatter.ISO_LOCAL_DATE),
                null,
                priority,
                paging)
        ).thenReturn(responseBeforeLastMonth);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.[0].id").exists(),
                        jsonPath("$.[0].id").value(is(taskId3)),
                        jsonPath("$.[0].manager").exists(),
                        jsonPath("$.[0].manager.userId").exists(),
                        jsonPath("$.[0].manager.name").value(is("Adam")),
                        jsonPath("$.[0].manager.surname").value(is("Nowak")),
                        jsonPath("$.[0].employee").exists(),
                        jsonPath("$.[0].employee.userId").exists(),
                        jsonPath("$.[0].employee.name").value(is("Jan")),
                        jsonPath("$.[0].employee.surname").value(is("Kowalski")),
                        jsonPath("$.[0].title").value(is("Title 3")),
                        jsonPath("$.[0].description").value(is("Description 3")),
                        jsonPath("$.[0].report").value(is("Report 3")),
                        jsonPath("$.[0].taskCreationDate").value(is(now.minusMonths(5).toString())),
                        jsonPath("$.[0].lastTaskUpdateDate").value(is(now.minusMonths(2).plusDays(1).toString())),
                        jsonPath("$.[0].dueDate").value(is(now.minusMonths(1).minusDays(1).toString())),
                        jsonPath("$.[0].reminderDate").doesNotExist(),
                        jsonPath("$.[0].priority").value(is(Priority.MEDIUM.toString())),
                        jsonPath("$.[0].mark").value(is(5)),
                        jsonPath("$.[0].reportDate").value(is(now.minusMonths(2).toString())),
                        jsonPath("$.[0].employeeAccept").value(is(AcceptanceStatus.ACCEPTED.toString())),
                        jsonPath("$.[0].managerAccept").value(is(AcceptanceStatus.ACCEPTED.toString())),
                        jsonPath("$.[0].employeeComment").value(is("Employee Comment 3"))
                ));
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenProvidedDateRangeAndUserIdAndPriority(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        var now = LocalDate.now();
        String startDueDate = now.minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDueDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String priority = "MEDIUM";

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page)+"?startDueDate="+startDueDate+"&endDueDate="+endDueDate
                        + "&userId=" + employeeId + "&priority=" + priority)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);


        when(taskService.getTasks(
                now.minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                now.format(DateTimeFormatter.ISO_LOCAL_DATE),
                employeeId,
                priority,
                paging)
        ).thenReturn(responseBeforeLastMonth);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.[0].id").exists(),
                        jsonPath("$.[0].id").value(is(taskId3)),
                        jsonPath("$.[0].manager").exists(),
                        jsonPath("$.[0].manager.userId").exists(),
                        jsonPath("$.[0].manager.name").value(is("Adam")),
                        jsonPath("$.[0].manager.surname").value(is("Nowak")),
                        jsonPath("$.[0].employee").exists(),
                        jsonPath("$.[0].employee.userId").exists(),
                        jsonPath("$.[0].employee.name").value(is("Jan")),
                        jsonPath("$.[0].employee.surname").value(is("Kowalski")),
                        jsonPath("$.[0].title").value(is("Title 3")),
                        jsonPath("$.[0].description").value(is("Description 3")),
                        jsonPath("$.[0].report").value(is("Report 3")),
                        jsonPath("$.[0].taskCreationDate").value(is(now.minusMonths(5).toString())),
                        jsonPath("$.[0].lastTaskUpdateDate").value(is(now.minusMonths(2).plusDays(1).toString())),
                        jsonPath("$.[0].dueDate").value(is(now.minusMonths(1).minusDays(1).toString())),
                        jsonPath("$.[0].reminderDate").doesNotExist(),
                        jsonPath("$.[0].priority").value(is(Priority.MEDIUM.toString())),
                        jsonPath("$.[0].mark").value(is(5)),
                        jsonPath("$.[0].reportDate").value(is(now.minusMonths(2).toString())),
                        jsonPath("$.[0].employeeAccept").value(is(AcceptanceStatus.ACCEPTED.toString())),
                        jsonPath("$.[0].managerAccept").value(is(AcceptanceStatus.ACCEPTED.toString())),
                        jsonPath("$.[0].employeeComment").value(is("Employee Comment 3"))
                ));
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetTasks_whenNotProvidedDateRange(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page))
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON);

        var now = LocalDate.now();


        when(taskService.getTasks(null, null, null, null, paging))
                .thenReturn(responseBetweenLastAndTwoFutureMonths);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),

                        jsonPath("$.[0].id").exists(),
                        jsonPath("$.[0].id").value(is(taskId1)),
                        jsonPath("$.[0].manager").exists(),
                        jsonPath("$.[0].manager.userId").exists(),
                        jsonPath("$.[0].manager.name").value(is("Adam")),
                        jsonPath("$.[0].manager.surname").value(is("Nowak")),
                        jsonPath("$.[0].employee").exists(),
                        jsonPath("$.[0].employee.userId").exists(),
                        jsonPath("$.[0].employee.name").value(is("Jan")),
                        jsonPath("$.[0].employee.surname").value(is("Kowalski")),
                        jsonPath("$.[0].title").value(is("Title 1")),
                        jsonPath("$.[0].description").value(is("Description 1")),
                        jsonPath("$.[0].report").doesNotExist(),
                        jsonPath("$.[0].taskCreationDate").value(is(now.minusMonths(1).toString())),
                        jsonPath("$.[0].lastTaskUpdateDate").value(is(now.minusMonths(1).toString())),
                        jsonPath("$.[0].dueDate").value(is(now.plusMonths(2).toString())),
                        jsonPath("$.[0].reminderDate").value(is(now.plusMonths(1).toString())),
                        jsonPath("$.[0].priority").doesNotExist(),
                        jsonPath("$.[0].mark").value(is(0)),
                        jsonPath("$.[0].reportDate").doesNotExist(),
                        jsonPath("$.[0].employeeAccept").value(is(AcceptanceStatus.NO_ACTION.toString())),
                        jsonPath("$.[0].managerAccept").value(is(AcceptanceStatus.NO_ACTION.toString())),
                        jsonPath("$.[0].employeeComment").doesNotExist(),

                        jsonPath("$.[1].id").exists(),
                        jsonPath("$.[1].id").value(is(taskId2)),
                        jsonPath("$.[1].manager").exists(),
                        jsonPath("$.[1].manager.userId").exists(),
                        jsonPath("$.[1].manager.name").value(is("Adam")),
                        jsonPath("$.[1].manager.surname").value(is("Nowak")),
                        jsonPath("$.[1].employee").exists(),
                        jsonPath("$.[1].employee.userId").exists(),
                        jsonPath("$.[1].employee.name").value(is("Jan")),
                        jsonPath("$.[1].employee.surname").value(is("Kowalski")),
                        jsonPath("$.[1].title").value(is("Title 2")),
                        jsonPath("$.[1].description").value(is("Description 2")),
                        jsonPath("$.[1].report").doesNotExist(),
                        jsonPath("$.[1].taskCreationDate").value(is(now.minusDays(10).toString())),
                        jsonPath("$.[1].lastTaskUpdateDate").value(is(now.minusDays(3).toString())),
                        jsonPath("$.[1].dueDate").value(is(now.plusDays(20).toString())),
                        jsonPath("$.[1].reminderDate").doesNotExist(),
                        jsonPath("$.[1].priority").value(is("HIGH")),
                        jsonPath("$.[1].mark").value(is(0)),
                        jsonPath("$.[1].reportDate").doesNotExist(),
                        jsonPath("$.[1].employeeAccept").value(is(AcceptanceStatus.ACCEPTED.toString())),
                        jsonPath("$.[1].managerAccept").value(is(AcceptanceStatus.NO_ACTION.toString())),
                        jsonPath("$.[1].employeeComment").value(is("Employee Comment 2"))
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetTasksWhenStartDateAfterEndDate(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String startDueDate= "2100-01-01";
        String endDueDate= "2000-01-01";

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page)+"?startDueDate="+startDueDate+"&endDueDate="+endDueDate)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        doThrow(StartDateAfterEndDateException.class)
                .when(taskService)
                .getTasks(any(), any(), any(), any(), any());

        String expectedMessage = messages.get( "exception.start.after.end");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(StartDateAfterEndDateException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetTasksWhenEmptyList(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String startDueDate= "2100-01-01";
        String endDueDate= "2100-02-01";

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page)+"?startDueDate="+startDueDate+"&endDueDate="+endDueDate)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        doThrow(NoTasksException.class)
                .when(taskService)
                .getTasks(any(), any(), any(), any(), any());

        String expectedMessage = messages.get( "exception.no.tasks");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(NoTasksException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetTasksWhenInvalidUserId(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String userNotFoundId = UUID.randomUUID().toString();

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page)+"?userId="+userNotFoundId)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        doThrow(EmployeeNotFoundException.class)
                .when(taskService)
                .getTasks(any(), any(), any(), any(), any());

        String expectedMessage = messages.get( "exception.employee.not.found");

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
    void shouldNotGetTasksWhenInvalidPriority(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String priority = "INVALID";

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page)+"?priority="+priority)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        doThrow(InvalidPriorityException.class)
                .when(taskService)
                .getTasks(any(), any(), any(), any(), any());

        String expectedMessage = messages.get( "exception.invalid.priority");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(InvalidPriorityException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
            throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+String.valueOf(page))
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        doThrow(IllegalStateException.class)
                .when(taskService)
                .getTasks(any(), any(), any(), any(), any());

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

    @Nested
    class ShouldNotGetTasksWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+String.valueOf(page))
                    .header("Accept-Language", testedLocale.toString());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogInAsRandomUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+String.valueOf(page))
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
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
