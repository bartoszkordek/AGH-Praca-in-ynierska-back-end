package com.healthy.gym.task.service;

import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.pojo.request.ManagerOrderRequest;

public interface TaskService {

    TaskDTO createTask(ManagerOrderRequest managerOrderRequest);
}
