package com.healthy.gym.task.service;

import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.data.repository.TaskDAO;
import com.healthy.gym.task.data.repository.UserDAO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.GymRole;
import com.healthy.gym.task.enums.Priority;
import com.healthy.gym.task.exception.*;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
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
    private final GymRole managerRole;
    private final GymRole employeeRole;
    private final GymRole trainerRole;
    private static final String ACCEPT_STATUS = "APPROVE";
    private static final String DECLINE_STATUS = "DECLINE";

    @Autowired
    public TaskServiceImpl(
            TaskDAO taskDAO,
            UserDAO userDAO
    ){
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        managerRole = GymRole.MANAGER;
        employeeRole = GymRole.EMPLOYEE;
        trainerRole = GymRole.TRAINER;
    }


    @Override
    public TaskDTO createTask(ManagerOrderRequest managerOrderRequest) throws ManagerNotFoundException,
            EmployeeNotFoundException, RetroDueDateException {

        UserDocument managerDocument = getManagerDocument();

        String employeeId = managerOrderRequest.getEmployeeId();
        UserDocument employeeDocument = getEmployeeOrTrainerDocument(employeeId);

        String dueDate = managerOrderRequest.getDueDate();
        var now = LocalDate.now();
        LocalDate parsedDueDate = LocalDate.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE);
        if(parsedDueDate.isBefore(now)) throw new RetroDueDateException();

        String title = managerOrderRequest.getTitle();
        String description = managerOrderRequest.getDescription();
        String requestReminderDate = managerOrderRequest.getReminderDate();
        String requestPriority = managerOrderRequest.getPriority();
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
        if(requestReminderDate != null)
            taskDocumentToBeSaved.setReminderDate(LocalDate.parse(requestReminderDate, DateTimeFormatter.ISO_LOCAL_DATE));
        setPriority(taskDocumentToBeSaved, requestPriority);

        TaskDocument taskDocumentSaved = taskDAO.save(taskDocumentToBeSaved);
        return modelMapper.map(taskDocumentSaved, TaskDTO.class);
    }

    @Override
    public TaskDTO updateTask(String taskId, ManagerOrderRequest managerOrderRequest)
            throws TaskNotFoundException, ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException {

        TaskDocument taskDocumentToBeUpdated = getTaskDocument(taskId);

        UserDocument managerDocument = getManagerDocument();

        String requestEmployeeId = managerOrderRequest.getEmployeeId();
        if(requestEmployeeId != null){
            UserDocument employeeDocument = userDAO.findByUserId(requestEmployeeId);
            if(employeeDocument == null) throw new EmployeeNotFoundException();
            if(!employeeDocument.getGymRoles().contains(employeeRole) && !employeeDocument.getGymRoles().contains(trainerRole))
                throw new EmployeeNotFoundException();

            taskDocumentToBeUpdated.setEmployee(employeeDocument);
        }

        var now = LocalDate.now();
        String requestDueDate = managerOrderRequest.getDueDate();
        if(requestDueDate != null){
            LocalDate parsedDueDate = LocalDate.parse(requestDueDate, DateTimeFormatter.ISO_LOCAL_DATE);
            if(parsedDueDate.isBefore(now)) throw new RetroDueDateException();
            taskDocumentToBeUpdated.setDueDate(parsedDueDate);
        }

        taskDocumentToBeUpdated.setLastOrderUpdateDate(now);

        String title = managerOrderRequest.getTitle();
        if(title != null) taskDocumentToBeUpdated.setTitle(title);

        String description = managerOrderRequest.getDescription();
        if(description != null) taskDocumentToBeUpdated.setDescription(description);

        String requestReminderDate = managerOrderRequest.getReminderDate();
        if(requestReminderDate != null)
            taskDocumentToBeUpdated.setReminderDate(LocalDate.parse(requestReminderDate, DateTimeFormatter.ISO_LOCAL_DATE));
        String requestPriority = managerOrderRequest.getPriority();
        setPriority(taskDocumentToBeUpdated, requestPriority);

        TaskDocument updatedTaskDocument = taskDAO.save(taskDocumentToBeUpdated);
        return modelMapper.map(updatedTaskDocument, TaskDTO.class);
    }

    @Override
    public TaskDTO deleteTask(String taskId) throws TaskNotFoundException {

        TaskDocument taskDocumentToBeRemoved = taskDAO.findByTaskId(taskId);
        if(taskDocumentToBeRemoved == null) throw new TaskNotFoundException();

        taskDAO.delete(taskDocumentToBeRemoved);
        return modelMapper.map(taskDocumentToBeRemoved, TaskDTO.class);
    }

    @Override
    public TaskDTO acceptDeclineTaskByEmployee(String taskId, String userId, String status)
            throws TaskNotFoundException, EmployeeNotFoundException, InvalidStatusException {

        TaskDocument taskDocumentToBeUpdated = getTaskDocument(taskId);

        UserDocument employeeDocument = getEmployeeOrTrainerDocument(userId);

        if(!status.equalsIgnoreCase(ACCEPT_STATUS) && !status.equalsIgnoreCase(DECLINE_STATUS))
            throw new InvalidStatusException();

        if(status.equalsIgnoreCase(ACCEPT_STATUS))
            taskDocumentToBeUpdated.setEmployeeAccept(AcceptanceStatus.ACCEPTED);

        if(status.equalsIgnoreCase(DECLINE_STATUS))
            taskDocumentToBeUpdated.setEmployeeAccept(AcceptanceStatus.NOT_ACCEPTED);

        TaskDocument updatedTaskDocument = taskDAO.save(taskDocumentToBeUpdated);
        return modelMapper.map(updatedTaskDocument, TaskDTO.class);
    }

    @Override
    public TaskDTO sendReport(String taskId, String userId, EmployeeReportRequest reportRequest)
            throws TaskNotFoundException, TaskDeclinedByEmployeeException {

        TaskDocument taskDocumentReportToBeAdded = getTaskDocument(taskId);

        AcceptanceStatus status = taskDocumentReportToBeAdded.getManagerAccept();
        if(status.equals(AcceptanceStatus.NOT_ACCEPTED)) throw new TaskDeclinedByEmployeeException();
        taskDocumentReportToBeAdded.setEmployeeAccept(AcceptanceStatus.ACCEPTED);

        String report = reportRequest.getResult();
        taskDocumentReportToBeAdded.setReport(report);

        taskDocumentReportToBeAdded.setReportDate(LocalDate.now());

        TaskDocument updatedTaskDocument = taskDAO.save(taskDocumentReportToBeAdded);
        return modelMapper.map(updatedTaskDocument, TaskDTO.class);
    }


    private TaskDocument getTaskDocument(String taskId) throws TaskNotFoundException {
        TaskDocument taskDocument = taskDAO.findByTaskId(taskId);
        if(taskDocument == null) throw new TaskNotFoundException();
        return taskDocument;
    }

    private UserDocument getManagerDocument() throws ManagerNotFoundException {
        UserDocument managerDocument = userDAO.findByGymRolesContaining(managerRole);
        if(managerDocument == null) throw new ManagerNotFoundException();
        return managerDocument;
    }

    private UserDocument getEmployeeOrTrainerDocument(String userId) throws EmployeeNotFoundException {
        UserDocument employeeDocument = userDAO.findByUserId(userId);
        if(employeeDocument == null) throw new EmployeeNotFoundException();
        if(!employeeDocument.getGymRoles().contains(employeeRole) && !employeeDocument.getGymRoles().contains(trainerRole))
            throw new EmployeeNotFoundException();
        return employeeDocument;
    }

    private TaskDocument setPriority(TaskDocument taskDocument, String priority){
        if(priority != null){
            if(priority.equals(Priority.CRITICAL.toString()))
                taskDocument.setPriority(Priority.CRITICAL);
            if(priority.equals(Priority.HIGH.toString()))
                taskDocument.setPriority(Priority.HIGH);
            if(priority.equals(Priority.MEDIUM.toString()))
                taskDocument.setPriority(Priority.MEDIUM);
            if(priority.equals(Priority.LOW.toString()))
                taskDocument.setPriority(Priority.LOW);
        }
        return taskDocument;
    }
}
