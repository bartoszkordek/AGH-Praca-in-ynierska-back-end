package com.healthy.gym.account.service;

import com.healthy.gym.account.dto.TrainerDTO;
import com.healthy.gym.account.exception.NoUserFound;
import com.healthy.gym.account.pojo.request.TrainerRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrainerService {

    TrainerDTO createTrainer(String userId, TrainerRequest trainerRequest, MultipartFile multipartFile)
            throws NoUserFound;

    TrainerDTO updateTrainer(String userId, TrainerRequest trainerRequest, MultipartFile multipartFile)
            throws NoUserFound;

    TrainerDTO deleteByUserId(String userId) throws NoUserFound;

    List<TrainerDTO> getTrainers() throws NoUserFound;

    TrainerDTO getTrainerByUserId(String userId) throws NoUserFound;
}
