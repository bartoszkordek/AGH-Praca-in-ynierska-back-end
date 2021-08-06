package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapGroupTrainingsDocumentToDTO;

@Service
public class EmployeeGroupTrainingServiceImpl implements EmployeeGroupTrainingService {

    private final GroupTrainingsDAO groupTrainingsDAO;

    @Autowired
    public EmployeeGroupTrainingServiceImpl(GroupTrainingsDAO groupTrainingsDAO) {
        this.groupTrainingsDAO = groupTrainingsDAO;
    }

    @Override
    public ParticipantsDTO getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException {
        GroupTrainingDTO groupTrainingDTO = getGroupTrainingDTO(trainingId);
        return groupTrainingDTO.getParticipants();
    }

    private GroupTrainingDTO getGroupTrainingDTO(String trainingId) throws NotExistingGroupTrainingException {
        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();
        return mapGroupTrainingsDocumentToDTO(groupTraining);
    }

    @Override
    public GroupTrainingDTO getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException {
        return getGroupTrainingDTO(trainingId);
    }
}
