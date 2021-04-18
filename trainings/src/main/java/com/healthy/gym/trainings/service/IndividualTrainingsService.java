package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.db.IndividualTrainingsDbRepository;
import com.healthy.gym.trainings.db.IndividualTrainingsRepository;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndividualTrainingsService {

    IndividualTrainingsDbRepository individualTrainingsDbRepository;

    public IndividualTrainingsService(IndividualTrainingsDbRepository individualTrainingsDbRepository){
        this.individualTrainingsDbRepository = individualTrainingsDbRepository;
    }

    public List<IndividualTrainings> getIndividualTrainings(){
        return individualTrainingsDbRepository.getIndividualTrainings();
    }
}
