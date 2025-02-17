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
import com.healthy.gym.task.exception.DueDateExceedException;
import com.healthy.gym.task.exception.ReportAlreadySentException;
import com.healthy.gym.task.exception.TaskDeclinedByEmployeeException;
import com.healthy.gym.task.exception.TaskNotFoundException;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SendReportServiceUnitTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskDAO taskDAO;

    @MockBean
    private UserDAO userDAO;

    private String employeeId;
    private String managerId;
    private String taskId;
    private String taskIdWithExceededDueDate;
    private String taskIdWithAlreadySentReport;

    private EmployeeReportRequest reportRequest;
    private TaskDocument taskDocumentReportToSend;
    private TaskDocument taskDocumentWithReport;
    private TaskDocument declinedTaskDocument;
    private TaskDocument taskDocumentWithExceededDueDate;
    private TaskDocument taskDocumentWithAlreadySentReport;
    private TaskDTO taskResponse;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        employeeId = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();
        taskId = UUID.randomUUID().toString();

        String title = "Przykładowe zadanie";
        String description = "Opis przykładowego zadania";
        var now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusMonths(1);
        LocalDateTime lastTaskUpdateDate = now.minusMonths(1);
        String report = "Done!";

        //request
        reportRequest = new EmployeeReportRequest();
        reportRequest.setResult(report);

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

        taskDocumentReportToSend = new TaskDocument();
        taskDocumentReportToSend.setTaskId(taskId);
        taskDocumentReportToSend.setManager(managerDocument);
        taskDocumentReportToSend.setEmployee(employeeDocument);
        taskDocumentReportToSend.setTitle(title);
        taskDocumentReportToSend.setDescription(description);
        taskDocumentReportToSend.setTaskCreationDate(lastTaskUpdateDate);
        taskDocumentReportToSend.setLastTaskUpdateDate(lastTaskUpdateDate);
        taskDocumentReportToSend.setDueDate(dueDate);
        taskDocumentReportToSend.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentReportToSend.setManagerAccept(AcceptanceStatus.NO_ACTION);

        declinedTaskDocument = new TaskDocument();
        declinedTaskDocument.setTaskId(taskId);
        declinedTaskDocument.setManager(managerDocument);
        declinedTaskDocument.setEmployee(employeeDocument);
        declinedTaskDocument.setTitle(title);
        declinedTaskDocument.setDescription(description);
        declinedTaskDocument.setTaskCreationDate(lastTaskUpdateDate);
        declinedTaskDocument.setLastTaskUpdateDate(lastTaskUpdateDate);
        declinedTaskDocument.setDueDate(dueDate);
        declinedTaskDocument.setEmployeeAccept(AcceptanceStatus.NOT_ACCEPTED);
        declinedTaskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);

        taskDocumentWithReport = new TaskDocument();
        taskDocumentWithReport.setTaskId(taskId);
        taskDocumentWithReport.setManager(managerDocument);
        taskDocumentWithReport.setEmployee(employeeDocument);
        taskDocumentWithReport.setTitle(title);
        taskDocumentWithReport.setDescription(description);
        taskDocumentWithReport.setTaskCreationDate(lastTaskUpdateDate);
        taskDocumentWithReport.setLastTaskUpdateDate(now);
        taskDocumentWithReport.setDueDate(dueDate);
        taskDocumentWithReport.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocumentWithReport.setManagerAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentWithReport.setReport(report);
        taskDocumentWithReport.setReportDate(now);

        taskIdWithExceededDueDate = UUID.randomUUID().toString();
        taskDocumentWithExceededDueDate = new TaskDocument();
        taskDocumentWithExceededDueDate.setTaskId(taskIdWithExceededDueDate);
        taskDocumentWithExceededDueDate.setManager(managerDocument);
        taskDocumentWithExceededDueDate.setEmployee(employeeDocument);
        taskDocumentWithExceededDueDate.setTitle(title);
        taskDocumentWithExceededDueDate.setDescription(description);
        taskDocumentWithExceededDueDate.setTaskCreationDate(lastTaskUpdateDate);
        taskDocumentWithExceededDueDate.setLastTaskUpdateDate(now.minusDays(5));
        taskDocumentWithExceededDueDate.setDueDate(now.minusDays(1));
        taskDocumentWithExceededDueDate.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocumentWithExceededDueDate.setManagerAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentWithExceededDueDate.setReport(report);
        taskDocumentWithExceededDueDate.setReportDate(now);

        taskIdWithAlreadySentReport = UUID.randomUUID().toString();
        taskDocumentWithAlreadySentReport = new TaskDocument();
        taskDocumentWithAlreadySentReport.setTaskId(taskIdWithAlreadySentReport);
        taskDocumentWithAlreadySentReport.setManager(managerDocument);
        taskDocumentWithAlreadySentReport.setEmployee(employeeDocument);
        taskDocumentWithAlreadySentReport.setTitle(title);
        taskDocumentWithAlreadySentReport.setDescription(description);
        taskDocumentWithAlreadySentReport.setTaskCreationDate(lastTaskUpdateDate);
        taskDocumentWithAlreadySentReport.setLastTaskUpdateDate(now.minusDays(5));
        taskDocumentWithAlreadySentReport.setDueDate(now.plusMonths(1));
        taskDocumentWithAlreadySentReport.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocumentWithAlreadySentReport.setManagerAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentWithAlreadySentReport.setReport(report);
        taskDocumentWithAlreadySentReport.setReportDate(now.minusDays(5));

        //response
        taskResponse = new TaskDTO(
                taskId,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                title,
                description,
                report,
                now.minusMonths(1),
                now,
                dueDate,
                null,
                now,
                null,
                0,
                AcceptanceStatus.ACCEPTED,
                AcceptanceStatus.NO_ACTION,
                null
        );
    }


    @Test
    void shouldSendReport_whenValidRequest() throws TaskNotFoundException, TaskDeclinedByEmployeeException,
            DueDateExceedException, ReportAlreadySentException {
        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocumentReportToSend);
        when(taskDAO.save(any())).thenReturn(taskDocumentWithReport);

        //then
        assertThat(taskService.sendReport(taskId, employeeId, reportRequest)).isEqualTo(taskResponse);
    }

    @Test
    void shouldNotSendReport_whenTaskIdNotExist() {
        //when
        when(taskDAO.findByTaskId(any())).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.sendReport(taskId, employeeId, reportRequest)
        ).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void shouldNotSendReport_whenTaskDeclinedByEmployee() {
        //when
        when(taskDAO.findByTaskId(any())).thenReturn(declinedTaskDocument);

        //then
        assertThatThrownBy(() ->
                taskService.sendReport(taskId, employeeId, reportRequest)
        ).isInstanceOf(TaskDeclinedByEmployeeException.class);
    }

    @Test
    void shouldNotSendReport_whenTaskExceededDueDate() {
        //when
        when(taskDAO.findByTaskId(any())).thenReturn(taskDocumentWithExceededDueDate);

        //then
        assertThatThrownBy(() ->
                taskService.sendReport(taskIdWithExceededDueDate, employeeId, reportRequest)
        ).isInstanceOf(DueDateExceedException.class);
    }

    @Test
    void shouldNotSendReport_whenReportAlreadySent() {
        //when
        when(taskDAO.findByTaskId(any())).thenReturn(taskDocumentWithAlreadySentReport);

        //then
        assertThatThrownBy(() ->
                taskService.sendReport(taskIdWithAlreadySentReport, employeeId, reportRequest)
        ).isInstanceOf(ReportAlreadySentException.class);
    }
}
