package com.healthy.gym.task.service;

import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.exception.*;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
import com.healthy.gym.task.pojo.request.ManagerOrderRequest;

public interface TaskService {

    TaskDTO createTask(ManagerOrderRequest managerOrderRequest)
            throws ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException;

    TaskDTO updateTask(String taskId, ManagerOrderRequest managerOrderRequest)
            throws TaskNotFoundException, ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException;

    TaskDTO deleteTask(String taskId) throws TaskNotFoundException;

    TaskDTO acceptDeclineTaskByEmployee(String taskId, String userId, String status)
            throws TaskNotFoundException, EmployeeNotFoundException, InvalidStatusException;

    TaskDTO sendReport(String taskId, String userId, EmployeeReportRequest report)
            throws EmployeeNotFoundException, TaskDeclinedByEmployeeException;
}
