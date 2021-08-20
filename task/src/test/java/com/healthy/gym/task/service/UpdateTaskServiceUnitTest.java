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
import com.healthy.gym.task.exception.ManagerNotFoundException;
import com.healthy.gym.task.exception.RetroDueDateException;
import com.healthy.gym.task.exception.TaskNotFoundException;
import com.healthy.gym.task.pojo.request.ManagerTaskCreationRequest;
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
public class UpdateTaskServiceUnitTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskDAO taskDAO;

    @MockBean
    private UserDAO userDAO;

    private String employeeIdToUpdate;
    private String employeeIdUpdated;
    private String managerId;
    private String taskId;

    @Test
    void shouldUpdateTask_whenValidRequest() throws ManagerNotFoundException, EmployeeNotFoundException,
            RetroDueDateException, TaskNotFoundException {

        employeeIdToUpdate = UUID.randomUUID().toString();
        employeeIdUpdated = UUID.randomUUID().toString();
        managerId = UUID.randomUUID().toString();
        taskId = UUID.randomUUID().toString();

        String titleToUpdate = "Przykładowe zadanie";
        String titleUpdated = "Zaktualizowane przykładowe zadanie";
        String descriptionToUpdate = "Opis przykładowego zadania";
        String descriptionUpdated = "Zaktualizowany opis przykładowego zadania";
        var now = LocalDate.now();
        LocalDate dueDateToUpdate = now.plusMonths(1);
        LocalDate dueDateUpdated = now.plusMonths(2);

        //request
        ManagerTaskCreationRequest managerTaskCreationRequest = new ManagerTaskCreationRequest();
        managerTaskCreationRequest.setEmployeeId(employeeIdUpdated);
        managerTaskCreationRequest.setTitle(titleUpdated);
        managerTaskCreationRequest.setDescription(descriptionUpdated);
        managerTaskCreationRequest.setDueDate(dueDateUpdated.toString());

        //DB documents
        String employeeNameToUpdate = "Jan";
        String employeeSurnameToUpdate = "Kowalski";
        UserDocument employeeDocumentToUpdate = new UserDocument();
        employeeDocumentToUpdate.setName(employeeNameToUpdate);
        employeeDocumentToUpdate.setSurname(employeeSurnameToUpdate);
        employeeDocumentToUpdate.setUserId(employeeIdToUpdate);
        employeeDocumentToUpdate.setGymRoles(List.of(GymRole.EMPLOYEE));
        employeeDocumentToUpdate.setId("507f1f77bcf86cd799435213");

        String employeeNameUpdated = "Piotr";
        String employeeSurnameUpdated = "Baran";
        UserDocument employeeDocumentUpdated = new UserDocument();
        employeeDocumentUpdated.setName(employeeNameUpdated);
        employeeDocumentUpdated.setSurname(employeeSurnameUpdated);
        employeeDocumentUpdated.setUserId(employeeIdUpdated);
        employeeDocumentUpdated.setGymRoles(List.of(GymRole.EMPLOYEE));
        employeeDocumentUpdated.setId("507f1f77bcf86cd799435214");

        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        TaskDocument taskDocumentToUpdate = new TaskDocument();
        taskDocumentToUpdate.setTaskId(taskId);
        taskDocumentToUpdate.setManager(managerDocument);
        taskDocumentToUpdate.setEmployee(employeeDocumentToUpdate);
        taskDocumentToUpdate.setTitle(titleToUpdate);
        taskDocumentToUpdate.setDescription(descriptionToUpdate);
        taskDocumentToUpdate.setTaskCreationDate(now.minusMonths(1));
        taskDocumentToUpdate.setLastTaskUpdateDate(now.minusMonths(1));
        taskDocumentToUpdate.setDueDate(dueDateToUpdate);
        taskDocumentToUpdate.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentToUpdate.setManagerAccept(AcceptanceStatus.NO_ACTION);

        TaskDocument taskDocumentUpdated = new TaskDocument();
        taskDocumentUpdated.setTaskId(taskId);
        taskDocumentUpdated.setManager(managerDocument);
        taskDocumentUpdated.setEmployee(employeeDocumentUpdated);
        taskDocumentUpdated.setTitle(titleUpdated);
        taskDocumentUpdated.setDescription(descriptionUpdated);
        taskDocumentUpdated.setTaskCreationDate(now.minusMonths(1));
        taskDocumentUpdated.setLastTaskUpdateDate(now);
        taskDocumentUpdated.setDueDate(dueDateUpdated);
        taskDocumentUpdated.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentUpdated.setManagerAccept(AcceptanceStatus.NO_ACTION);


        //response
        TaskDTO taskResponse = new TaskDTO(
                taskId,
                new BasicUserInfoDTO(managerId, managerName, managerSurname),
                new BasicUserInfoDTO(employeeIdUpdated, employeeNameUpdated, employeeSurnameUpdated),
                titleUpdated,
                descriptionUpdated,
                null,
                now.minusMonths(1),
                now,
                dueDateUpdated,
                null,
                null,
                null,
                0,
                AcceptanceStatus.NO_ACTION,
                AcceptanceStatus.NO_ACTION,
                null
        );

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocumentToUpdate);
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(managerDocument);
        when(userDAO.findByUserId(employeeIdUpdated)).thenReturn(employeeDocumentUpdated);
        when(taskDAO.save(any())).thenReturn(taskDocumentUpdated);

        //then
        assertThat(taskService.updateTask(taskId, managerTaskCreationRequest)).isEqualTo(taskResponse);
    }

    @Test
    void shouldNotCreateTask_whenTaskIdNotExist(){
        //before
        String notFoundTaskId = UUID.randomUUID().toString();
        //request
        ManagerTaskCreationRequest managerTaskCreationRequest = new ManagerTaskCreationRequest();
        managerTaskCreationRequest.setEmployeeId(notFoundTaskId);
        managerTaskCreationRequest.setTitle("Sample title");
        managerTaskCreationRequest.setDescription("Sample description");
        managerTaskCreationRequest.setDueDate(LocalDate.now().plusMonths(1).toString());

        //when
        when(taskDAO.findByTaskId(notFoundTaskId)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.updateTask(notFoundTaskId, managerTaskCreationRequest)
        ).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void shouldNotUpdateTask_whenManagerNotExist(){
        //before
        //request
        ManagerTaskCreationRequest managerTaskCreationRequest = new ManagerTaskCreationRequest();
        managerTaskCreationRequest.setEmployeeId(employeeIdToUpdate);
        managerTaskCreationRequest.setTitle("Sample title");
        managerTaskCreationRequest.setDescription("Sample description");
        managerTaskCreationRequest.setDueDate(LocalDate.now().plusMonths(1).toString());

        String taskIdToRequest = UUID.randomUUID().toString();
        TaskDocument taskDocumentToUpdate = new TaskDocument();

        //when
        when(taskDAO.findByTaskId(taskIdToRequest)).thenReturn(taskDocumentToUpdate);
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.updateTask(taskIdToRequest, managerTaskCreationRequest)
        ).isInstanceOf(ManagerNotFoundException.class);
    }

    @Test
    void shouldNotUpdateTask_whenEmployeeNotExist(){
        //before
        //request
        String invalidEmployeeId = UUID.randomUUID().toString();
        ManagerTaskCreationRequest managerTaskCreationRequest = new ManagerTaskCreationRequest();
        managerTaskCreationRequest.setEmployeeId(invalidEmployeeId);
        managerTaskCreationRequest.setTitle("Sample title");
        managerTaskCreationRequest.setDescription("Sample description");
        managerTaskCreationRequest.setDueDate(LocalDate.now().plusMonths(1).toString());


        //DB documents
        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        String taskIdToRequest = UUID.randomUUID().toString();
        TaskDocument taskDocumentToUpdate = new TaskDocument();

        //when
        when(taskDAO.findByTaskId(taskIdToRequest)).thenReturn(taskDocumentToUpdate);
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(managerDocument);
        when(userDAO.findByUserId(employeeIdUpdated)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                taskService.updateTask(taskIdToRequest, managerTaskCreationRequest)
        ).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void shouldNotUpdateTask_whenUserNotEmployee(){
        //before
        String taskId = UUID.randomUUID().toString();
        TaskDocument taskDocumentToUpdate = new TaskDocument();
        String employeeId = UUID.randomUUID().toString();

        //request
        String invalidEmployeeId = UUID.randomUUID().toString();
        ManagerTaskCreationRequest managerTaskCreationRequest = new ManagerTaskCreationRequest();
        managerTaskCreationRequest.setEmployeeId(invalidEmployeeId);
        managerTaskCreationRequest.setTitle("Sample title");
        managerTaskCreationRequest.setDescription("Sample description");
        managerTaskCreationRequest.setDueDate(LocalDate.now().plusMonths(1).toString());

        //DB documents
        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        String notEmployeeName = "Jan";
        String notEmployeeSurname = "Kowalski";
        UserDocument notEmployeeDocument = new UserDocument();
        notEmployeeDocument.setName(notEmployeeName);
        notEmployeeDocument.setSurname(notEmployeeSurname);
        notEmployeeDocument.setUserId(employeeId);
        notEmployeeDocument.setGymRoles(List.of(GymRole.USER));
        notEmployeeDocument.setId("507f1f77bcf86cd799435213");

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocumentToUpdate);
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(managerDocument);
        when(userDAO.findByUserId(employeeId)).thenReturn(notEmployeeDocument);

        //then
        assertThatThrownBy(() ->
                taskService.updateTask(taskId, managerTaskCreationRequest)
        ).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void shouldNotUpdateTask_whenRetroDueDate(){
        //before
        String taskId = UUID.randomUUID().toString();
        String employeeId = UUID.randomUUID().toString();
        TaskDocument taskDocumentToUpdate = new TaskDocument();

        //request
        ManagerTaskCreationRequest managerTaskCreationRequest = new ManagerTaskCreationRequest();
        managerTaskCreationRequest.setEmployeeId(employeeId);
        managerTaskCreationRequest.setTitle("Sample title");
        managerTaskCreationRequest.setDescription("Sample description");
        managerTaskCreationRequest.setDueDate(LocalDate.now().minusDays(1).toString());

        //DB documents
        String managerName = "Adam";
        String managerSurname = "Nowak";
        UserDocument managerDocument = new UserDocument();
        managerDocument.setName(managerName);
        managerDocument.setSurname(managerSurname);
        managerDocument.setUserId(managerId);
        managerDocument.setGymRoles(List.of(GymRole.MANAGER));
        managerDocument.setId("507f1f77bcf86cd799435002");

        String employeeName = "Jan";
        String employeeSurname = "Kowalski";
        UserDocument employeeDocument = new UserDocument();
        employeeDocument.setName(employeeName);
        employeeDocument.setSurname(employeeSurname);
        employeeDocument.setUserId(employeeId);
        employeeDocument.setGymRoles(List.of(GymRole.EMPLOYEE));
        employeeDocument.setId("507f1f77bcf86cd799435213");

        //when
        when(taskDAO.findByTaskId(taskId)).thenReturn(taskDocumentToUpdate);
        when(userDAO.findByGymRolesContaining(GymRole.MANAGER)).thenReturn(managerDocument);
        when(userDAO.findByUserId(employeeId)).thenReturn(employeeDocument);

        //then
        assertThatThrownBy(() ->
                taskService.updateTask(taskId, managerTaskCreationRequest)
        ).isInstanceOf(RetroDueDateException.class);
    }
}
