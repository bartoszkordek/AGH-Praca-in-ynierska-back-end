package com.healthy.gym.task.service;

import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.exception.EmployeeNotFoundException;
import com.healthy.gym.task.exception.ManagerNotFoundException;
import com.healthy.gym.task.exception.RetroDueDateException;
import com.healthy.gym.task.exception.TaskNotFoundException;
import com.healthy.gym.task.pojo.request.ManagerOrderRequest;

public interface TaskService {

    TaskDTO createTask(ManagerOrderRequest managerOrderRequest)
            throws ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException;

    TaskDTO updateTask(String taskId, ManagerOrderRequest managerOrderRequest)
            throws TaskNotFoundException, ManagerNotFoundException, EmployeeNotFoundException, RetroDueDateException;
}
