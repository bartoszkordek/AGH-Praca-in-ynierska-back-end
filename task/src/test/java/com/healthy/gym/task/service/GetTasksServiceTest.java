package com.healthy.gym.task.service;

import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.data.repository.TaskDAO;
import com.healthy.gym.task.data.repository.UserDAO;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.enums.Priority;
import com.healthy.gym.task.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
class GetTasksServiceTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskDAO taskDAO;

    @MockBean
    private UserDAO userDAO;

    private String employeeId;
    private String managerId;
    private String taskId1;
    private String taskId2;
    private String taskId3;

    private int page;
    private int size;
    private Pageable paging;

    private List<TaskDocument> dbAll;
    private List<TaskDocument> dbBeforeLastMonth;
    private List<TaskDocument> dbBetweenLastAndTwoFutureMonths;
    private List<TaskDTO> responseAll;
    private List<TaskDTO> responseBeforeLastMonth;
    private List<TaskDTO> responseBetweenLastAndTwoFutureMonths;


    @BeforeEach
    void setUp() {

        page = 0;
        size = 10;
        paging = PageRequest.of(page, size);

        employeeId = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();

        var now = LocalDate.now();

        //DB documents
        String employeeName = "Jan";
        String employeeSurname = "Kowalski";
        UserDocument employeeDocument = new UserDocument();
        employeeDocument.setName(employeeName);
        employeeDocument.setSurname(employeeSurname);
        employeeDocument.setUserId(employeeId);
        employeeDocument.setGymRoles(List.of(GymRole.EMPLOYEE));
        employeeDocument.setId("507f1f77bcf86cd799435213");

        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        taskId1 = UUID.randomUUID().toString();
        TaskDocument taskDocument1 = new TaskDocument();
        taskDocument1.setTaskId(taskId1);
        taskDocument1.setManager(managerDocument);
        taskDocument1.setEmployee(employeeDocument);
        taskDocument1.setTitle("Title 1");
        taskDocument1.setDescription("Description 1");
        taskDocument1.setTaskCreationDate(now.minusMonths(1));
        taskDocument1.setLastTaskUpdateDate(now.minusMonths(1));
        taskDocument1.setDueDate(now.plusMonths(2));
        taskDocument1.setReminderDate(now.plusMonths(1));
        taskDocument1.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocument1.setManagerAccept(AcceptanceStatus.NO_ACTION);

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

        taskId3 = UUID.randomUUID().toString();
        TaskDocument taskDocument3 = new TaskDocument();
        taskDocument3.setTaskId(taskId3);
        taskDocument3.setManager(managerDocument);
        taskDocument3.setEmployee(employeeDocument);
        taskDocument3.setTitle("Title 3");
        taskDocument3.setDescription("Description 3");
        taskDocument3.setTaskCreationDate(now.minusMonths(5));
        taskDocument3.setLastTaskUpdateDate(now.minusMonths(2));
        taskDocument3.setDueDate(now.minusMonths(1).minusDays(1));
        taskDocument3.setPriority(Priority.MEDIUM);
        taskDocument3.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocument3.setManagerAccept(AcceptanceStatus.ACCEPTED);
        taskDocument3.setMark(5);
        taskDocument3.setReport("Report 3");
        taskDocument3.setReportDate(now.minusMonths(2));
        taskDocument3.setEmployeeComment("Employee Comment 3");

        dbAll = List.of(taskDocument1, taskDocument2, taskDocument3);
        dbBeforeLastMonth = List.of(taskDocument3);
        dbBetweenLastAndTwoFutureMonths = List.of(taskDocument1, taskDocument2);


        //DTOs
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


        TaskDTO taskDTO3 = new TaskDTO(
                taskId3,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                "Title 3",
                "Description 3",
                "Report 3",
                now.minusMonths(5),
                now.minusMonths(2),
                now.minusMonths(1).minusDays(1),
                null,
                now.minusMonths(2),
                Priority.MEDIUM,
                5,
                AcceptanceStatus.ACCEPTED,
                AcceptanceStatus.ACCEPTED,
                "Employee Comment 3"
        );

        responseAll = List.of(taskDTO1, taskDTO2, taskDTO3);
        responseBeforeLastMonth = List.of(taskDTO3);
        responseBetweenLastAndTwoFutureMonths = List.of(taskDTO1, taskDTO2);
    }

    @Test
    void shouldGetTask1And2Tasks_whenNotProvidedDateRange() throws StartDateAfterEndDateException, NoTasksException {
        LocalDate defaultStartDate = LocalDate.now().minusMonths(1).minusDays(1);
        LocalDate defaultEndDate = LocalDate.now().plusMonths(2).plusDays(1);
        Page<TaskDocument> taskDocumentPage = new PageImpl<>(dbBetweenLastAndTwoFutureMonths);

        //when
        when(taskDAO.findAllByDueDateBetween(
                defaultStartDate,
                defaultEndDate,
                paging
        )).thenReturn(taskDocumentPage);

        //then
        assertThat(taskService.getTasks(null, null, paging).get(0))
                .isEqualTo(responseBetweenLastAndTwoFutureMonths.get(0));
        assertThat(taskService.getTasks(null, null, paging).get(1))
                .isEqualTo(responseBetweenLastAndTwoFutureMonths.get(1));
    }

    @Test
    void shouldGetTask3_whenDateRangeBeforeLastMonth() throws StartDateAfterEndDateException, NoTasksException {
        var now = LocalDate.now();
        String requestStartDate = now.minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String requestEndDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        Page<TaskDocument> taskDocumentPage = new PageImpl<>(dbBeforeLastMonth);

        //when
        when(taskDAO.findAllByDueDateBetween(
                now.minusYears(1).minusDays(1),
                now.plusDays(1),
                paging
        )).thenReturn(taskDocumentPage);

        //then
        assertThat(taskService.getTasks(requestStartDate, requestEndDate, paging).get(0))
                .isEqualTo(responseBeforeLastMonth.get(0));
    }


    @Test
    void shouldGetAllTasks() throws StartDateAfterEndDateException, NoTasksException {
        var now = LocalDate.now();
        String requestStartDate = now.minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String requestEndDate = now.plusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Page<TaskDocument> taskDocumentPage = new PageImpl<>(dbAll);

        //when
        when(taskDAO.findAllByDueDateBetween(
                now.minusYears(1).minusDays(1),
                now.plusYears(1).plusDays(1),
                paging
        )).thenReturn(taskDocumentPage);

        //then
        assertThat(taskService.getTasks(requestStartDate, requestEndDate, paging).get(0))
                .isEqualTo(responseAll.get(0));
        assertThat(taskService.getTasks(requestStartDate, requestEndDate, paging).get(1))
                .isEqualTo(responseAll.get(1));
        assertThat(taskService.getTasks(requestStartDate, requestEndDate, paging).get(2))
                .isEqualTo(responseAll.get(2));
    }

    @Test
    void shouldNotGetTasks_whenStartDateAfterEndDate() {
        assertThatThrownBy(() ->
                taskService.getTasks("2030-12-31", "2000-01-01", paging)
        ).isInstanceOf(StartDateAfterEndDateException.class);
    }

    @Test
    void shouldNotGetTasks_whenNoContent() {
        var now = LocalDate.now();
        String requestStartDate = now.plusYears(100).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String requestEndDate = now.plusYears(200).format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<TaskDocument> taskDocuments = new ArrayList<>();
        Page<TaskDocument> taskDocumentPage = new PageImpl<>(taskDocuments);
        //when
        when(taskDAO.findAllByDueDateBetween(
                now.plusYears(100).minusDays(1),
                now.plusYears(200).plusDays(1),
                paging
        )).thenReturn(taskDocumentPage);

        assertThatThrownBy(() ->
                taskService.getTasks(requestStartDate, requestEndDate, paging)
        ).isInstanceOf(NoTasksException.class);
    }
}
