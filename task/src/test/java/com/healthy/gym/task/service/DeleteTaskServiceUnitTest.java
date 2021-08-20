package com.healthy.gym.task.service;

import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.data.repository.TaskDAO;
import com.healthy.gym.task.data.repository.UserDAO;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.exception.TaskNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
class DeleteTaskServiceUnitTest {

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
    void shouldUpdateTask_whenValidRequest() throws TaskNotFoundException {

        employeeId = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();
        taskId = UUID.randomUUID().toString();

        String title = "Przykładowe zadanie";
        String description = "Opis przykładowego zadania";
        var now = LocalDate.now();
        LocalDate dueDate = now.plusMonths(1);

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

        TaskDocument taskDocumentToRemove = new TaskDocument();
        taskDocumentToRemove.setTaskId(taskId);
        taskDocumentToRemove.setManager(managerDocument);
        taskDocumentToRemove.setEmployee(employeeDocument);
        taskDocumentToRemove.setTitle(title);
        taskDocumentToRemove.setDescription(description);
        taskDocumentToRemove.setLastTaskUpdateDate(now);
        taskDocumentToRemove.setDueDate(dueDate);
        taskDocumentToRemove.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentToRemove.setManagerAccept(AcceptanceStatus.NO_ACTION);


        //response
        TaskDTO taskResponse = new TaskDTO(
                taskId,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeId, employeeName, employeeSurname),
                title,
                description,
                null,
                null,
                now,
                dueDate,
                null,
                null,
                null,
                0,
                AcceptanceStatus.NO_ACTION,
                AcceptanceStatus.NO_ACTION,
                null
        );

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocumentToRemove);

        //then
        assertThat(taskService.deleteTask(taskId)).isEqualTo(taskResponse);
    }

    @Test
    void shouldNotDeleteTask_whenTaskIdNotExist(){
        //before
        String notFoundTaskId = UUID.randomUUID().toString();

        //when
        when(taskDAO.findByTaskId(notFoundTaskId)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.deleteTask(notFoundTaskId)
        ).isInstanceOf(TaskNotFoundException.class);
    }
}
