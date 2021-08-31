package com.healthy.gym.account.service;

import com.healthy.gym.account.dto.TrainerDTO;
import com.healthy.gym.account.exception.NoUserFound;
import com.healthy.gym.account.pojo.request.TrainerRequest;
import org.springframework.web.multipart.MultipartFile;

public interface TrainerService {

    TrainerDTO createTrainer(String userId, TrainerRequest trainerRequest, MultipartFile multipartFile)
            throws NoUserFound;
}
