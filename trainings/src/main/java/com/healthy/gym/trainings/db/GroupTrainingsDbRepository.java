package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.config.MongoConfig;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class GroupTrainingsDbRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private GroupTrainingsRepository groupTrainingsRepository;

    @Autowired
    private MongoConfig mongoConfig;

    public List<GroupTrainings> getGroupTrainings(){
        return groupTrainingsRepository.findAll();
    }

    public boolean isAbilityToGroupTrainingEnrollment(String trainingId){

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);

        if(!groupTrainingsRepository.existsById(trainingId)) return false;

        int participantsCount = groupTrainingsRepository.getFirstById(trainingId).getParticipants().size();

        return groupTrainingsRepository.existsByIdAndDateAfterAndLimitGreaterThan(trainingId, todayDateFormatted,
                participantsCount);

    }

    public GroupTrainings createTraining(GroupTrainingModel groupTrainingModel){
        GroupTrainings response = groupTrainingsRepository.insert(new GroupTrainings(
                groupTrainingModel.getTrainingName(),
                groupTrainingModel.getTrainerId(),
                groupTrainingModel.getDate(),
                groupTrainingModel.getStartTime(),
                groupTrainingModel.getEndTime(),
                groupTrainingModel.getHallNo(),
                groupTrainingModel.getLimit(),
                groupTrainingModel.getParticipants()
        ));
        return response;
    }
}
