package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.db.GroupTrainingsRepository;
import com.healthy.gym.trainings.db.TestRepository;
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

    public List<String> getGroupTrainings(){
        return groupTrainingsRepository.getGroupTrainings();
    }
}
