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
}
