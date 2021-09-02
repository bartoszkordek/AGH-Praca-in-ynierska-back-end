package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.dto.GenericTrainingDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {
    @Override
    public List<GenericTrainingDTO> getAllTrainerTrainings(String userId, String startDate, String endDate) {
        return null;
    }
}
