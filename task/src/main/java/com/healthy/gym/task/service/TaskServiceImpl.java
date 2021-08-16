package com.healthy.gym.task.service;

import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.data.repository.TaskDAO;
import com.healthy.gym.task.data.repository.UserDAO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.exception.EmployeeNotFoundException;
import com.healthy.gym.task.exception.ManagerNotFoundException;
import com.healthy.gym.task.exception.RetroDueDateException;
import com.healthy.gym.task.pojo.request.ManagerOrderRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskDAO taskDAO;
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskServiceImpl(
            TaskDAO taskDAO,
            UserDAO userDAO
    ){
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }


    @Override
    public TaskDTO createTask(ManagerOrderRequest managerOrderRequest) throws ManagerNotFoundException,
            EmployeeNotFoundException, RetroDueDateException {

        GymRole managerRole = GymRole.MANAGER;
        UserDocument managerDocument = userDAO.findByGymRolesContaining(managerRole);
        if(managerDocument == null) throw new ManagerNotFoundException();

        GymRole employeeRole = GymRole.EMPLOYEE;
        String employeeId = managerDocument.getUserId();
        UserDocument employeeDocument = userDAO.findByUserId(employeeId);
        if(employeeDocument == null) throw new EmployeeNotFoundException();
        if(employeeDocument.getGymRoles().contains(employeeRole)) throw new EmployeeNotFoundException();

        String dueDate = managerOrderRequest.getDueDate();
        var now = LocalDate.now();
        LocalDate parsedDueDate = LocalDate.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE);
        if(parsedDueDate.isBefore(now)) throw new RetroDueDateException();

        String title = managerOrderRequest.getTitle();
        String description = managerOrderRequest.getDescription();
        TaskDocument taskDocumentToBeSaved = new TaskDocument();
        taskDocumentToBeSaved.setTaskId(UUID.randomUUID().toString());
        taskDocumentToBeSaved.setManager(managerDocument);
        taskDocumentToBeSaved.setEmployee(employeeDocument);
        taskDocumentToBeSaved.setTitle(title);
        taskDocumentToBeSaved.setDescription(description);
        taskDocumentToBeSaved.setLastOrderUpdateDate(now);
        taskDocumentToBeSaved.setDueDate(parsedDueDate);
        taskDocumentToBeSaved.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentToBeSaved.setManagerAccept(AcceptanceStatus.NO_ACTION);

        TaskDocument taskDocumentSaved = taskDAO.save(taskDocumentToBeSaved);
        return modelMapper.map(taskDocumentSaved, TaskDTO.class);
    }
}
