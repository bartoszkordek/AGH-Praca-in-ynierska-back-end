package com.healthy.gym.task.service;

import com.healthy.gym.task.data.repository.TaskDAO;
import com.healthy.gym.task.data.repository.UserDAO;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.pojo.request.ManagerOrderRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public TaskDTO createTask(ManagerOrderRequest managerOrderRequest) {
        return null;
    }
}
