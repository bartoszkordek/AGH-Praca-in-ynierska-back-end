package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.db.TestRepository;
import org.springframework.stereotype.Service;

@Service
public class TrainingsService {
    TestRepository testRepository;

    public TrainingsService(TestRepository testRepository){
        this.testRepository = testRepository;
    }

    public String getFirstTestDocument(){
        return testRepository.getFirstTestDocument();
    }
}
