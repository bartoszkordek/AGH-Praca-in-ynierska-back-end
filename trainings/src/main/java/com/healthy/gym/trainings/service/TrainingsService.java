package com.healthy.gym.trainings.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.healthy.gym.trainings.db.GroupTrainingsRepository;
import com.healthy.gym.trainings.db.TestRepository;
import com.healthy.gym.trainings.exception.TrainingEnrollmentException;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingsService {
    TestRepository testRepository;
    GroupTrainingsRepository groupTrainingsRepository;

    public TrainingsService(TestRepository testRepository,
                            GroupTrainingsRepository groupTrainingsRepository){
        this.testRepository = testRepository;
        this.groupTrainingsRepository = groupTrainingsRepository;
    }

    public String getFirstTestDocument(){
        return testRepository.getFirstTestDocument();
    }

    public List<GroupTrainingModel> getGroupTrainings() throws JsonProcessingException {
        return groupTrainingsRepository.getGroupTrainings();
    }

    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if(trainingId.length() != 24 || !groupTrainingsRepository.isAbilityToGroupTrainingEnrollment(trainingId)) throw new TrainingEnrollmentException("Cannot enroll to this training");
    }
}
