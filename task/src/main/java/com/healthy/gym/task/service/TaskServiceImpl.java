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
import com.healthy.gym.task.pojo.request.EmployeeAcceptDeclineTaskRequest;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
import com.healthy.gym.task.pojo.request.ManagerReportVerificationRequest;
import com.healthy.gym.task.pojo.request.ManagerTaskCreationRequest;
import com.healthy.gym.task.util.RequestDateFormatter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String ACCEPT_STATUS = "APPROVE";
    private static final String DECLINE_STATUS = "DECLINE";
    private static final LocalDate DEFAULT_START_DATE = LocalDate.now().minusMonths(1).minusDays(1);
    private static final LocalDate DEFAULT_END_DATE = LocalDate.now().plusMonths(2).plusDays(1);
    private final TaskDAO taskDAO;
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;
    private final RequestDateFormatter requestDateFormatter;
    private final GymRole managerRole;
    private final GymRole employeeRole;
    private final GymRole trainerRole;

    @Autowired
    public TaskServiceImpl(
            TaskDAO taskDAO,
            UserDAO userDAO
    ) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        requestDateFormatter = new RequestDateFormatter();
        managerRole = GymRole.MANAGER;
        employeeRole = GymRole.EMPLOYEE;
        trainerRole = GymRole.TRAINER;
    }


    @Override
    public TaskDTO createTask(String managerId, ManagerTaskCreationRequest managerTaskCreationRequest) throws ManagerNotFoundException,
            EmployeeNotFoundException, RetroDueDateException, InvalidPriorityException {

        UserDocument managerDocument = getManagerDocument(managerId);

        String employeeId = managerTaskCreationRequest.getEmployeeId();
        UserDocument employeeDocument = getEmployeeOrTrainerDocument(employeeId);

        String dueDate = managerTaskCreationRequest.getDueDate();
        var now = LocalDateTime.now();
        LocalDateTime parsedDueDate = LocalDateTime.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (parsedDueDate.isBefore(now)) throw new RetroDueDateException();

        String title = managerTaskCreationRequest.getTitle();
        String description = managerTaskCreationRequest.getDescription();
        String requestReminderDate = managerTaskCreationRequest.getReminderDate();
        String requestPriority = managerTaskCreationRequest.getPriority();
        TaskDocument taskDocumentToBeSaved = new TaskDocument();
        taskDocumentToBeSaved.setTaskId(UUID.randomUUID().toString());
        taskDocumentToBeSaved.setManager(managerDocument);
        taskDocumentToBeSaved.setEmployee(employeeDocument);
        taskDocumentToBeSaved.setTitle(title);
        taskDocumentToBeSaved.setDescription(description);
        taskDocumentToBeSaved.setTaskCreationDate(now);
        taskDocumentToBeSaved.setLastTaskUpdateDate(now);
        taskDocumentToBeSaved.setDueDate(parsedDueDate);
        taskDocumentToBeSaved.setEmployeeAccept(AcceptanceStatus.NO_ACTION);
        taskDocumentToBeSaved.setManagerAccept(AcceptanceStatus.NO_ACTION);
        if (requestReminderDate != null)
            taskDocumentToBeSaved.setReminderDate(LocalDateTime.parse(requestReminderDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (requestPriority != null) checkPriority(requestPriority);
        setPriority(taskDocumentToBeSaved, requestPriority);

        TaskDocument taskDocumentSaved = taskDAO.save(taskDocumentToBeSaved);
        return modelMapper.map(taskDocumentSaved, TaskDTO.class);
    }

    @Override
    public TaskDTO updateTask(String taskId, String managerId, ManagerTaskCreationRequest managerTaskCreationRequest)
            throws TaskNotFoundException, ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException, InvalidPriorityException {

        TaskDocument taskDocumentToBeUpdated = getTaskDocument(taskId);

        UserDocument managerDocument = getManagerDocument(managerId);
        taskDocumentToBeUpdated.setManager(managerDocument);

        String requestEmployeeId = managerTaskCreationRequest.getEmployeeId();
        if (requestEmployeeId != null) {
            UserDocument employeeDocument = userDAO.findByUserId(requestEmployeeId);
            if (employeeDocument == null) throw new EmployeeNotFoundException();
            if (!employeeDocument.getGymRoles().contains(employeeRole) && !employeeDocument.getGymRoles().contains(trainerRole))
                throw new EmployeeNotFoundException();

            taskDocumentToBeUpdated.setEmployee(employeeDocument);
        }

        var now = LocalDateTime.now();
        String requestDueDate = managerTaskCreationRequest.getDueDate();
        if (requestDueDate != null) {
            LocalDateTime parsedDueDate = LocalDateTime.parse(requestDueDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            if (parsedDueDate.isBefore(now)) throw new RetroDueDateException();
            taskDocumentToBeUpdated.setDueDate(parsedDueDate);
        }

        taskDocumentToBeUpdated.setLastTaskUpdateDate(now);

        String title = managerTaskCreationRequest.getTitle();
        if (title != null) taskDocumentToBeUpdated.setTitle(title);

        String description = managerTaskCreationRequest.getDescription();
        if (description != null) taskDocumentToBeUpdated.setDescription(description);

        String requestReminderDate = managerTaskCreationRequest.getReminderDate();
        if (requestReminderDate != null)
            taskDocumentToBeUpdated.setReminderDate(LocalDateTime.parse(requestReminderDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        String requestPriority = managerTaskCreationRequest.getPriority();
        if (requestPriority != null) checkPriority(requestPriority);
        setPriority(taskDocumentToBeUpdated, requestPriority);

        TaskDocument updatedTaskDocument = taskDAO.save(taskDocumentToBeUpdated);
        return modelMapper.map(updatedTaskDocument, TaskDTO.class);
    }

    @Override
    public TaskDTO deleteTask(String taskId) throws TaskNotFoundException {

        TaskDocument taskDocumentToBeRemoved = taskDAO.findByTaskId(taskId);
        if (taskDocumentToBeRemoved == null) throw new TaskNotFoundException();

        taskDAO.delete(taskDocumentToBeRemoved);
        return modelMapper.map(taskDocumentToBeRemoved, TaskDTO.class);
    }

    @Override
    public TaskDTO acceptDeclineTaskByEmployee(
            String taskId,
            String userId,
            EmployeeAcceptDeclineTaskRequest employeeAcceptDeclineTaskRequest
    ) throws TaskNotFoundException, EmployeeNotFoundException, InvalidStatusException {

        TaskDocument taskDocumentToBeUpdated = getTaskDocument(taskId);

        checkEmployeeOrTrainerDocument(userId);

        String status = employeeAcceptDeclineTaskRequest.getAcceptanceStatus();

        if (!status.equalsIgnoreCase(ACCEPT_STATUS) && !status.equalsIgnoreCase(DECLINE_STATUS))
            throw new InvalidStatusException();

        if (status.equalsIgnoreCase(ACCEPT_STATUS))
            taskDocumentToBeUpdated.setEmployeeAccept(AcceptanceStatus.ACCEPTED);

        if (status.equalsIgnoreCase(DECLINE_STATUS))
            taskDocumentToBeUpdated.setEmployeeAccept(AcceptanceStatus.NOT_ACCEPTED);

        String employeeComment = employeeAcceptDeclineTaskRequest.getEmployeeComment();
        taskDocumentToBeUpdated.setEmployeeComment(employeeComment);

        TaskDocument updatedTaskDocument = taskDAO.save(taskDocumentToBeUpdated);
        return modelMapper.map(updatedTaskDocument, TaskDTO.class);
    }

    @Override
    public TaskDTO sendReport(String taskId, String userId, EmployeeReportRequest reportRequest)
            throws TaskNotFoundException, TaskDeclinedByEmployeeException, DueDateExceedException,
            ReportAlreadySentException {

        var now = LocalDateTime.now();
        TaskDocument taskDocumentReportToBeAdded = getTaskDocument(taskId);

        AcceptanceStatus status = taskDocumentReportToBeAdded.getEmployeeAccept();
        if (status.equals(AcceptanceStatus.NOT_ACCEPTED)) throw new TaskDeclinedByEmployeeException();
        taskDocumentReportToBeAdded.setEmployeeAccept(AcceptanceStatus.ACCEPTED);

        LocalDateTime dueDate = taskDocumentReportToBeAdded.getDueDate();
        if (dueDate.isBefore(now)) throw new DueDateExceedException();

        if (taskDocumentReportToBeAdded.getReport() != null || taskDocumentReportToBeAdded.getReportDate() != null)
            throw new ReportAlreadySentException();

        String report = reportRequest.getResult();
        taskDocumentReportToBeAdded.setReport(report);

        taskDocumentReportToBeAdded.setLastTaskUpdateDate(now);
        taskDocumentReportToBeAdded.setReportDate(now);

        TaskDocument updatedTaskDocument = taskDAO.save(taskDocumentReportToBeAdded);
        return modelMapper.map(updatedTaskDocument, TaskDTO.class);
    }

    @Override
    public TaskDTO verifyReport(String taskId, ManagerReportVerificationRequest managerReportVerificationRequest)
            throws TaskNotFoundException, InvalidMarkException, InvalidStatusException, TaskDeclinedByEmployeeException,
            ReportNotSentException {

        TaskDocument taskDocument = getTaskDocument(taskId);

        AcceptanceStatus employeeAcceptanceStatus = taskDocument.getEmployeeAccept();
        if (employeeAcceptanceStatus.equals(AcceptanceStatus.NOT_ACCEPTED)) throw new TaskDeclinedByEmployeeException();

        int mark = managerReportVerificationRequest.getMark();
        if (mark < 1 || mark > 5) throw new InvalidMarkException();
        taskDocument.setMark(mark);

        String report = taskDocument.getReport();
        if (report == null) throw new ReportNotSentException();

        taskDocument.setLastTaskUpdateDate(LocalDateTime.now());

        String managerAcceptanceStatus = managerReportVerificationRequest.getApprovalStatus();
        if (!managerAcceptanceStatus.equalsIgnoreCase(ACCEPT_STATUS) && !managerAcceptanceStatus.equalsIgnoreCase(DECLINE_STATUS))
            throw new InvalidStatusException();

        if (managerAcceptanceStatus.equalsIgnoreCase(ACCEPT_STATUS))
            taskDocument.setManagerAccept(AcceptanceStatus.ACCEPTED);

        if (managerAcceptanceStatus.equalsIgnoreCase(DECLINE_STATUS))
            taskDocument.setManagerAccept(AcceptanceStatus.NOT_ACCEPTED);

        TaskDocument updatedTaskDocument = taskDAO.save(taskDocument);
        return modelMapper.map(updatedTaskDocument, TaskDTO.class);
    }

    @Override
    public List<TaskDTO> getTasks(
            String startDueDate,
            String endDueDate,
            String userId,
            String priority,
            Pageable pageable
    ) throws StartDateAfterEndDateException, NoTasksException, EmployeeNotFoundException, InvalidPriorityException {
        LocalDate startDate = DEFAULT_START_DATE;
        LocalDate endDate = DEFAULT_END_DATE;
        if (startDueDate != null) startDate = requestDateFormatter.formatStartDate(startDueDate);
        if (endDueDate != null) endDate = requestDateFormatter.formatEndDate(endDueDate);

        if (startDate.isAfter(endDate))
            throw new StartDateAfterEndDateException();

        UserDocument employeeOrTrainerDocument = null;
        if (userId != null) {
            employeeOrTrainerDocument = getEmployeeOrTrainerDocument(userId);
        }

        Priority transformedPriority = null;
        if (priority != null) {
            checkPriority(priority);
            transformedPriority = transformPriority(priority);
        }

        List<TaskDocument> taskDocuments = null;
        if (userId == null && priority == null) {
            taskDocuments = taskDAO.findAllByDueDateBetween(
                    startDate,
                    endDate,
                    pageable
            ).getContent();
        }

        if (userId != null && priority == null) {
            taskDocuments = taskDAO.findAllByDueDateBetweenAndEmployee(
                    startDate,
                    endDate,
                    employeeOrTrainerDocument,
                    pageable
            ).getContent();
        }

        if (userId == null && priority != null) {
            taskDocuments = taskDAO.findAllByDueDateBetweenAndPriorityEquals(
                    startDate,
                    endDate,
                    transformedPriority,
                    pageable
            ).getContent();
        }

        if (userId != null && priority != null) {
            taskDocuments = taskDAO.findAllByDueDateBetweenAndEmployeeAndPriorityEquals(
                    startDate,
                    endDate,
                    employeeOrTrainerDocument,
                    transformedPriority,
                    pageable
            ).getContent();
        }

        if (taskDocuments.isEmpty()) throw new NoTasksException();

        return taskDocuments
                .stream()
                .map(taskDocument -> modelMapper.map(taskDocument, TaskDTO.class))
                .collect(Collectors.toList());
    }


    private TaskDocument getTaskDocument(String taskId) throws TaskNotFoundException {
        TaskDocument taskDocument = taskDAO.findByTaskId(taskId);
        if (taskDocument == null) throw new TaskNotFoundException();
        return taskDocument;
    }

    private UserDocument getManagerDocument(String userId) throws ManagerNotFoundException {
        UserDocument managerDocument = userDAO.findByUserId(userId);
        if (managerDocument == null) throw new ManagerNotFoundException();
        if (!managerDocument.getGymRoles().contains(managerRole))
            throw new ManagerNotFoundException();
        return managerDocument;
    }

    private UserDocument getEmployeeOrTrainerDocument(String userId) throws EmployeeNotFoundException {
        UserDocument employeeDocument = userDAO.findByUserId(userId);
        if (employeeDocument == null) throw new EmployeeNotFoundException();
        if (!employeeDocument.getGymRoles().contains(employeeRole) && !employeeDocument.getGymRoles().contains(trainerRole))
            throw new EmployeeNotFoundException();
        return employeeDocument;
    }

    private TaskDocument setPriority(TaskDocument taskDocument, String priority) {
        if (priority != null) {
            if (priority.equals(Priority.CRITICAL.toString()))
                taskDocument.setPriority(Priority.CRITICAL);
            if (priority.equals(Priority.HIGH.toString()))
                taskDocument.setPriority(Priority.HIGH);
            if (priority.equals(Priority.MEDIUM.toString()))
                taskDocument.setPriority(Priority.MEDIUM);
            if (priority.equals(Priority.LOW.toString()))
                taskDocument.setPriority(Priority.LOW);
        }
        return taskDocument;
    }

    private void checkEmployeeOrTrainerDocument(String userId) throws EmployeeNotFoundException {
        UserDocument employeeDocument = userDAO.findByUserId(userId);
        if (employeeDocument == null) throw new EmployeeNotFoundException();
        if (!employeeDocument.getGymRoles().contains(employeeRole) && !employeeDocument.getGymRoles().contains(trainerRole))
            throw new EmployeeNotFoundException();
    }

    private void checkPriority(String priority) throws InvalidPriorityException {
        if (!priority.equals(Priority.CRITICAL.toString()) && !priority.equals(Priority.HIGH.toString())
                && !priority.equals(Priority.MEDIUM.toString()) && !priority.equals(Priority.LOW.toString())) {
            throw new InvalidPriorityException();
        }
    }

    private Priority transformPriority(String priority) {
        if (priority.equals(Priority.CRITICAL.toString()))
            return Priority.CRITICAL;
        if (priority.equals(Priority.HIGH.toString()))
            return Priority.HIGH;
        if (priority.equals(Priority.MEDIUM.toString()))
            return Priority.MEDIUM;
        if (priority.equals(Priority.LOW.toString()))
            return Priority.LOW;
        return null;
    }
}
