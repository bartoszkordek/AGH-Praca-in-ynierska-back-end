package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.group.training.UniversalGroupTrainingDAO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.GroupTrainingWithoutParticipantsDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.utils.GroupTrainingMapper;
import com.healthy.gym.trainings.utils.StartEndDateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UniversalGroupTrainingServiceImpl implements UniversalGroupTrainingService {

    private final UniversalGroupTrainingDAO universalGroupTrainingDAO;
    private final TrainingTypeDAO trainingTypeDAO;

    @Autowired
    public UniversalGroupTrainingServiceImpl(
            UniversalGroupTrainingDAO universalGroupTrainingDAO,
            TrainingTypeDAO trainingTypeDAO
    ) {
        this.universalGroupTrainingDAO = universalGroupTrainingDAO;
        this.trainingTypeDAO = trainingTypeDAO;
    }

    @Override
    public List<GroupTrainingDTO> getGroupTrainingsWithParticipants(String startDateStr, String endDateStr)
            throws StartDateAfterEndDateException {
        List<GroupTrainingDocument> groupTrainingDocumentList = getGroupTrainingDocumentList(startDateStr, endDateStr);
        return returnDTOsWithParticipants(groupTrainingDocumentList);
    }

    private List<GroupTrainingDocument> getGroupTrainingDocumentList(String startDate, String endDate)
            throws StartDateAfterEndDateException {
        StartEndDateValidator validator = new StartEndDateValidator(startDate, endDate);
        LocalDateTime startDateTime = validator.getBeginningOfStartDate();
        LocalDateTime endDateTime = validator.getEndOfEndDate();
        return universalGroupTrainingDAO.getGroupTrainingDocuments(startDateTime, endDateTime);
    }

    private List<GroupTrainingDTO> returnDTOsWithParticipants(List<GroupTrainingDocument> groupTrainingDocumentList) {
        if (groupTrainingDocumentList.isEmpty()) return List.of();
        return groupTrainingDocumentList
                .stream()
                .map(GroupTrainingMapper::mapGroupTrainingsDocumentToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupTrainingWithoutParticipantsDTO> getGroupTrainingsWithoutParticipants(
            String startDateStr,
            String endDateStr
    ) throws StartDateAfterEndDateException {
        List<GroupTrainingDocument> groupTrainingDocumentList = getGroupTrainingDocumentList(startDateStr, endDateStr);
        return returnDTOsWithoutParticipants(groupTrainingDocumentList);
    }

    private List<GroupTrainingWithoutParticipantsDTO> returnDTOsWithoutParticipants(
            List<GroupTrainingDocument> groupTrainingDocumentList
    ) {
        if (groupTrainingDocumentList.isEmpty()) return List.of();
        return groupTrainingDocumentList
                .stream()
                .map(GroupTrainingMapper::mapGroupTrainingsDocumentToDTOWithoutParticipants)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupTrainingDTO> getGroupTrainingsByTypeWithParticipants(
            String trainingTypeId,
            String startDateStr,
            String endDateStr
    ) throws StartDateAfterEndDateException, TrainingTypeNotFoundException {
        List<GroupTrainingDocument> groupTrainingDocumentList =
                getGroupTrainingDocumentList(startDateStr, endDateStr, trainingTypeId);
        return returnDTOsWithParticipants(groupTrainingDocumentList);
    }

    private List<GroupTrainingDocument> getGroupTrainingDocumentList(
            String startDate,
            String endDate,
            String trainingTypeId
    ) throws StartDateAfterEndDateException, TrainingTypeNotFoundException {
        TrainingTypeDocument trainingType = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);
        if (trainingType == null) throw new TrainingTypeNotFoundException();

        StartEndDateValidator validator = new StartEndDateValidator(startDate, endDate);
        LocalDateTime startDateTime = validator.getBeginningOfStartDate();
        LocalDateTime endDateTime = validator.getEndOfEndDate();

        return universalGroupTrainingDAO
                .getGroupTrainingDocumentsByTrainingType(startDateTime, endDateTime, trainingType);
    }

    @Override
    public List<GroupTrainingWithoutParticipantsDTO> getGroupTrainingsByTypeWithoutParticipants(
            String trainingTypeId,
            String startDateStr,
            String endDateStr
    ) throws StartDateAfterEndDateException, TrainingTypeNotFoundException {
        List<GroupTrainingDocument> groupTrainingDocumentList =
                getGroupTrainingDocumentList(startDateStr, endDateStr, trainingTypeId);
        return returnDTOsWithoutParticipants(groupTrainingDocumentList);
    }
}
