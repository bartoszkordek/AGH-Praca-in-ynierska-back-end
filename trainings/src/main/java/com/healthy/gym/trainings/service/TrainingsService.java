package com.healthy.gym.trainings.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.healthy.gym.trainings.db.GroupTrainingsRepository;
import com.healthy.gym.trainings.db.TestRepository;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    public void enrollToGroupTraining(Object trainingId, String clientId){

    }
}
