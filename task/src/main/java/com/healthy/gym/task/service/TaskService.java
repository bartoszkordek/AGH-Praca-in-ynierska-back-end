package com.healthy.gym.task.service;

import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.exception.*;
import com.healthy.gym.task.pojo.request.EmployeeAcceptDeclineTaskRequest;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
import com.healthy.gym.task.pojo.request.ManagerReportVerificationRequest;
import com.healthy.gym.task.pojo.request.ManagerTaskCreationRequest;

public interface TaskService {

    TaskDTO createTask(ManagerTaskCreationRequest managerTaskCreationRequest)
            throws ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException;

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
        throws TaskNotFoundException, InvalidMarkException, TaskDeclinedByEmployeeException, ReportNotSentException;
}
