package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.IndividualTrainings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IndividualTrainingsDbRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private IndividualTrainingsRepository individualTrainingsRepository;

    public List<IndividualTrainings> getIndividualTrainings(){
        return individualTrainingsRepository.findAll();
    }
}
