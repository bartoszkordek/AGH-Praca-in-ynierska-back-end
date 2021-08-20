package com.healthy.gym.task.service;

import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.data.repository.TaskDAO;
import com.healthy.gym.task.data.repository.UserDAO;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.exception.*;
import com.healthy.gym.task.pojo.request.ManagerReportVerificationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class VerifyReportServiceTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskDAO taskDAO;

    @MockBean
    private UserDAO userDAO;

    private String employeeId;
    private String managerId;
    private String taskId;

    @Test
    void shouldAcceptTask() throws TaskNotFoundException, ReportNotSentException, InvalidStatusException, InvalidMarkException, TaskDeclinedByEmployeeException {

        employeeId = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();
        taskId = UUID.randomUUID().toString();

        //request
        ManagerReportVerificationRequest managerReportVerificationRequest = new ManagerReportVerificationRequest();
        managerReportVerificationRequest.setApprovalStatus("APPROVE");
        managerReportVerificationRequest.setMark(5);

        String title = "Przykładowe zadanie";
        String description = "Opis przykładowego zadania";
        var now = LocalDate.now();
        LocalDate taskCreationDate = now.minusMonths(1);
        LocalDate dueDate = now.plusMonths(1);
        String report = "Przykładowy raport";
        LocalDate reportDate = now.minusDays(5);
        String employeeComment = "Przykładowy komentarz";

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

        TaskDocument taskDocumentToAccept = new TaskDocument();
        taskDocumentToAccept.setTaskId(taskId);
        taskDocumentToAccept.setManager(managerDocument);
        taskDocumentToAccept.setEmployee(employeeDocument);
        taskDocumentToAccept.setTitle(title);
        taskDocumentToAccept.setDescription(description);
        taskDocumentToAccept.setTaskCreationDate(taskCreationDate);
        taskDocumentToAccept.setLastTaskUpdateDate(reportDate);
        taskDocumentToAccept.setDueDate(dueDate);
        taskDocumentToAccept.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocumentToAccept.setManagerAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentToAccept.setReport(report);
        taskDocumentToAccept.setReportDate(reportDate);
        taskDocumentToAccept.setEmployeeComment(employeeComment);

        //response
        TaskDTO taskResponse = new TaskDTO(
                taskId,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                title,
                description,
                report,
                taskCreationDate,
                now,
                dueDate,
                null,
                reportDate,
                null,
                0,
                AcceptanceStatus.ACCEPTED,
                AcceptanceStatus.ACCEPTED,
                employeeComment
        );

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocumentToAccept);
        when(taskDAO.save(any())).thenReturn(taskDocumentToAccept);

        //then
        assertThat(taskService.verifyReport(taskId, managerReportVerificationRequest)).isEqualTo(taskResponse);
    }

    @Test
    void shouldDeclineReport() throws TaskNotFoundException, ReportNotSentException, InvalidStatusException, InvalidMarkException, TaskDeclinedByEmployeeException {
        employeeId = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();
        taskId = UUID.randomUUID().toString();

        //request
        ManagerReportVerificationRequest managerReportVerificationRequest = new ManagerReportVerificationRequest();
        managerReportVerificationRequest.setApprovalStatus("DECLINE");
        managerReportVerificationRequest.setMark(5);

        String title = "Przykładowe zadanie";
        String description = "Opis przykładowego zadania";
        var now = LocalDate.now();
        LocalDate taskCreationDate = now.minusMonths(1);
        LocalDate dueDate = now.plusMonths(1);
        String report = "Przykładowy raport";
        LocalDate reportDate = now.minusDays(5);
        String employeeComment = "Przykładowy komentarz";

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

        TaskDocument taskDocumentToDecline = new TaskDocument();
        taskDocumentToDecline.setTaskId(taskId);
        taskDocumentToDecline.setManager(managerDocument);
        taskDocumentToDecline.setEmployee(employeeDocument);
        taskDocumentToDecline.setTitle(title);
        taskDocumentToDecline.setDescription(description);
        taskDocumentToDecline.setTaskCreationDate(taskCreationDate);
        taskDocumentToDecline.setLastTaskUpdateDate(reportDate);
        taskDocumentToDecline.setDueDate(dueDate);
        taskDocumentToDecline.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocumentToDecline.setManagerAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentToDecline.setReport(report);
        taskDocumentToDecline.setReportDate(reportDate);
        taskDocumentToDecline.setEmployeeComment(employeeComment);

        //response
        TaskDTO taskResponse = new TaskDTO(
                taskId,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                title,
                description,
                report,
                taskCreationDate,
                now,
                dueDate,
                null,
                reportDate,
                null,
                0,
                AcceptanceStatus.ACCEPTED,
                AcceptanceStatus.NOT_ACCEPTED,
                employeeComment
        );

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocumentToDecline);
        when(taskDAO.save(any())).thenReturn(taskDocumentToDecline);

        //then
        assertThat(taskService.verifyReport(taskId, managerReportVerificationRequest)).isEqualTo(taskResponse);
    }

    @Test
    void shouldNotVerifyReport_whenTaskIdNotExist(){
        //before
        String notFoundTaskId = UUID.randomUUID().toString();

        //when
        when(taskDAO.findByTaskId(notFoundTaskId)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.verifyReport(notFoundTaskId, any())
        ).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void shouldNotVerifyReport_whenTaskDeclinedByEmployee(){
        //before
        String declinedByEmployeeTaskId = UUID.randomUUID().toString();

        employeeId = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();

        //request
        ManagerReportVerificationRequest managerReportVerificationRequest = new ManagerReportVerificationRequest();
        managerReportVerificationRequest.setApprovalStatus("DECLINE");
        managerReportVerificationRequest.setMark(5);

        String title = "Przykładowe zadanie";
        String description = "Opis przykładowego zadania";
        var now = LocalDate.now();
        LocalDate taskCreationDate = now.minusMonths(1);
        LocalDate dueDate = now.plusMonths(1);
        String report = "Przykładowy raport";
        LocalDate reportDate = now.minusDays(5);
        String employeeComment = "Przykładowy komentarz";

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

        TaskDocument declinedTaskDocument = new TaskDocument();
        declinedTaskDocument.setTaskId(declinedByEmployeeTaskId);
        declinedTaskDocument.setManager(managerDocument);
        declinedTaskDocument.setEmployee(employeeDocument);
        declinedTaskDocument.setTitle(title);
        declinedTaskDocument.setDescription(description);
        declinedTaskDocument.setTaskCreationDate(taskCreationDate);
        declinedTaskDocument.setLastTaskUpdateDate(reportDate);
        declinedTaskDocument.setDueDate(dueDate);
        declinedTaskDocument.setEmployeeAccept(AcceptanceStatus.NOT_ACCEPTED);
        declinedTaskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);
        declinedTaskDocument.setReport(report);
        declinedTaskDocument.setReportDate(reportDate);
        declinedTaskDocument.setEmployeeComment(employeeComment);


        //when
        when(taskDAO.findByTaskId(declinedByEmployeeTaskId)).thenReturn(declinedTaskDocument);

        //then
        assertThatThrownBy(() ->
                taskService.verifyReport(declinedByEmployeeTaskId, managerReportVerificationRequest)
        ).isInstanceOf(TaskDeclinedByEmployeeException.class);
    }


    @Test
    void shouldNotVerifyReport_whenInvalidMark(){
        //before
        taskId = UUID.randomUUID().toString();
        employeeId = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();

        //request: mark < 1
        ManagerReportVerificationRequest managerReportVerificationRequestLessThan1 = new ManagerReportVerificationRequest();
        managerReportVerificationRequestLessThan1.setApprovalStatus("DECLINE");
        managerReportVerificationRequestLessThan1.setMark(0);

        //request: mark > 5
        ManagerReportVerificationRequest managerReportVerificationRequestGreaterThan5 = new ManagerReportVerificationRequest();
        managerReportVerificationRequestGreaterThan5.setApprovalStatus("APPROVE");
        managerReportVerificationRequestGreaterThan5.setMark(6);

        String title = "Przykładowe zadanie";
        String description = "Opis przykładowego zadania";
        var now = LocalDate.now();
        LocalDate taskCreationDate = now.minusMonths(1);
        LocalDate dueDate = now.plusMonths(1);
        String report = "Przykładowy raport";
        LocalDate reportDate = now.minusDays(5);
        String employeeComment = "Przykładowy komentarz";

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

        TaskDocument taskDocument = new TaskDocument();
        taskDocument.setTaskId(taskId);
        taskDocument.setManager(managerDocument);
        taskDocument.setEmployee(employeeDocument);
        taskDocument.setTitle(title);
        taskDocument.setDescription(description);
        taskDocument.setTaskCreationDate(taskCreationDate);
        taskDocument.setLastTaskUpdateDate(reportDate);
        taskDocument.setDueDate(dueDate);
        taskDocument.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocument.setManagerAccept(AcceptanceStatus.NO_ACTION);
        taskDocument.setReport(report);
        taskDocument.setReportDate(reportDate);
        taskDocument.setEmployeeComment(employeeComment);


        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocument);

        //then
        assertThatThrownBy(() ->
                taskService.verifyReport(taskId, managerReportVerificationRequestLessThan1)
        ).isInstanceOf(InvalidMarkException.class);

        assertThatThrownBy(() ->
                taskService.verifyReport(taskId, managerReportVerificationRequestGreaterThan5)
        ).isInstanceOf(InvalidMarkException.class);

    }
}
