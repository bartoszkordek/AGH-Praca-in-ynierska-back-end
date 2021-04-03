package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.db.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.db.TestRepository;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.TrainingEnrollmentException;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingsService {
    TestRepository testRepository;
    GroupTrainingsDbRepository groupTrainingsDbRepository;

    public TrainingsService(TestRepository testRepository,
                            GroupTrainingsDbRepository groupTrainingsDbRepository){
        this.testRepository = testRepository;
        this.groupTrainingsDbRepository = groupTrainingsDbRepository;
    }

    public String getFirstTestDocument(){
        return testRepository.getFirstTestDocument();
    }

    public List<GroupTrainings> getGroupTrainings() {
        return groupTrainingsDbRepository.getGroupTrainings();
    }

    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if(trainingId.length() != 24 || !groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(trainingId)) throw new TrainingEnrollmentException("Cannot enroll to this training");
    }

    public GroupTrainings createGroupTraining(GroupTrainingModel groupTrainingModel){
        return groupTrainingsDbRepository.createTraining(groupTrainingModel);
    }
}
