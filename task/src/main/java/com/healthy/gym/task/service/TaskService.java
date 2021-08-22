package com.healthy.gym.task.service;

import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.exception.*;
import com.healthy.gym.task.pojo.request.EmployeeAcceptDeclineTaskRequest;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
import com.healthy.gym.task.pojo.request.ManagerReportVerificationRequest;
import com.healthy.gym.task.pojo.request.ManagerTaskCreationRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    TaskDTO createTask(ManagerTaskCreationRequest managerTaskCreationRequest)
            throws ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException, InvalidPriorityException;

    TaskDTO updateTask(String taskId, ManagerTaskCreationRequest managerTaskCreationRequest)
            throws TaskNotFoundException, ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException;

    TaskDTO deleteTask(String taskId) throws TaskNotFoundException;

    TaskDTO acceptDeclineTaskByEmployee(
            String taskId,
            String userId,
            EmployeeAcceptDeclineTaskRequest employeeAcceptDeclineTaskRequest
    ) throws TaskNotFoundException, EmployeeNotFoundException, InvalidStatusException;

    TaskDTO sendReport(String taskId, String userId, EmployeeReportRequest report)
            throws TaskNotFoundException, TaskDeclinedByEmployeeException, DueDateExceedException,
            ReportAlreadySentException;

    TaskDTO verifyReport(String taskId, ManagerReportVerificationRequest managerReportVerificationRequest)
            throws TaskNotFoundException, InvalidMarkException, InvalidStatusException, TaskDeclinedByEmployeeException,
            ReportNotSentException;

    List<TaskDTO> getTasks(String startDueDate, String endDueDate, String userId, String priority, Pageable pageable)
            throws StartDateAfterEndDateException, NoTasksException, EmployeeNotFoundException, InvalidPriorityException;
}
