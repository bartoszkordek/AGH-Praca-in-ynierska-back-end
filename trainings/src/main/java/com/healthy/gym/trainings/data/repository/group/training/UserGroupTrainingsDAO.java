package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;

import java.time.LocalDateTime;
import java.util.List;

public interface UserGroupTrainingsDAO {

    List<GroupTrainingDocument> findAllGroupTrainings(UserDocument userDocument,
                                                      LocalDateTime startDateTime,
                                                      LocalDateTime endDateTime);

    List<GroupTrainingDocument> findAllGroupTrainingsByUserAndStartDateAfterNow(UserDocument userDocument);
}
