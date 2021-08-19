package com.healthy.gym.task.service;

import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.data.repository.TaskDAO;
import com.healthy.gym.task.data.repository.UserDAO;
import com.healthy.gym.task.dto.BasicUserInfoDTO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.exception.EmployeeNotFoundException;
import com.healthy.gym.task.exception.InvalidStatusException;
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
public class AcceptDeclineTaskByEmployeeServiceTest {

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
    void shouldAcceptTask_whenValidRequest() throws TaskNotFoundException, InvalidStatusException, EmployeeNotFoundException {

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

        TaskDocument taskDocumentToBeUpdated = new TaskDocument();
        taskDocumentToBeUpdated.setTaskId(taskId);
        taskDocumentToBeUpdated.setManager(managerDocument);
        taskDocumentToBeUpdated.setEmployee(employeeDocument);
        taskDocumentToBeUpdated.setTitle(title);
        taskDocumentToBeUpdated.setDescription(description);
        taskDocumentToBeUpdated.setLastOrderUpdateDate(now);
        taskDocumentToBeUpdated.setDueDate(dueDate);
        taskDocumentToBeUpdated.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentToBeUpdated.setManagerAccept(AcceptanceStatus.NO_ACTION);

        TaskDocument taskDocumentUpdated = new TaskDocument();
        taskDocumentUpdated.setTaskId(taskId);
        taskDocumentUpdated.setManager(managerDocument);
        taskDocumentUpdated.setEmployee(employeeDocument);
        taskDocumentUpdated.setTitle(title);
        taskDocumentUpdated.setDescription(description);
        taskDocumentUpdated.setLastOrderUpdateDate(now);
        taskDocumentUpdated.setDueDate(dueDate);
        taskDocumentUpdated.setEmployeeAccept(AcceptanceStatus.ACCEPTED);
        taskDocumentUpdated.setManagerAccept(AcceptanceStatus.NO_ACTION);


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
                AcceptanceStatus.ACCEPTED,
                AcceptanceStatus.NO_ACTION,
                null
        );

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocumentToBeUpdated);
        when(userDAO.findByUserId(employeeId)).thenReturn(employeeDocument);
        when(taskDAO.save(taskDocumentUpdated)).thenReturn(taskDocumentUpdated);

        //then
        assertThat(taskService.acceptDeclineTaskByEmployee(taskId, employeeId, "APPROVE")).isEqualTo(taskResponse);
    }

    @Test
    void shouldNotAcceptTask_whenTaskIdNotExist(){
        //before
        String notFoundTaskId = UUID.randomUUID().toString();
        String status = "APPROVE";

        //when
        when(taskDAO.findByTaskId(notFoundTaskId)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.acceptDeclineTaskByEmployee(notFoundTaskId, employeeId, status)
        ).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void shouldNotAcceptTask_whenEmployeeNotExist(){
        //before
        String notFoundEmployeeId = UUID.randomUUID().toString();
        String status = "APPROVE";

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(new TaskDocument());
        when(userDAO.findByUserId(notFoundEmployeeId)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.acceptDeclineTaskByEmployee(taskId, notFoundEmployeeId, status)
        ).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void shouldNotAcceptTask_whenUserIsNotEmployeeNotExist(){
        //before
        String notEmployeeId = UUID.randomUUID().toString();
        String status = "APPROVE";

        String managerName = "Piotr";
        String managerSurname = "Kowalski";
        UserDocument notEmployeeDocument = new UserDocument();
        notEmployeeDocument.setName(managerName);
        notEmployeeDocument.setSurname(managerSurname);
        notEmployeeDocument.setUserId(notEmployeeId);
        notEmployeeDocument.setGymRoles(List.of(GymRole.MANAGER));
        notEmployeeDocument.setId("507f1f77bcf86cd799434821");

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(new TaskDocument());
        when(userDAO.findByUserId(notEmployeeId)).thenReturn(notEmployeeDocument);

        //then
        assertThatThrownBy(() ->
                taskService.acceptDeclineTaskByEmployee(taskId, notEmployeeId, status)
        ).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void shouldNotAcceptTask_whenInvalidStatus(){
        //before
        String status = "INVALID_STATUS";
        String employeeName = "Jan";
        String employeeSurname = "Kowalski";
        UserDocument employeeDocument = new UserDocument();
        employeeDocument.setName(employeeName);
        employeeDocument.setSurname(employeeSurname);
        employeeDocument.setUserId(employeeId);
        employeeDocument.setGymRoles(List.of(GymRole.EMPLOYEE));
        employeeDocument.setId("507f1f77bcf86cd799435213");

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(new TaskDocument());
        when(userDAO.findByUserId(employeeId)).thenReturn(employeeDocument);

        //then
        assertThatThrownBy(() ->
                taskService.acceptDeclineTaskByEmployee(taskId, employeeId, status)
        ).isInstanceOf(InvalidStatusException.class);
    }
}
