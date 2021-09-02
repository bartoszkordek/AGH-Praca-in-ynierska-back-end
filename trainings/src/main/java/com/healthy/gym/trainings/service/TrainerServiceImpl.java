package com.healthy.gym.trainings.service;

import brave.internal.collect.Lists;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.TrainerDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.dto.GenericTrainingDTO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.NoTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.utils.StartEndDateValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDAO trainerDAO;
    private final UserDAO userDAO;

    @Autowired
    public TrainerServiceImpl(TrainerDAO trainerDAO, UserDAO userDAO) {
        this.trainerDAO = trainerDAO;
        this.userDAO = userDAO;
    }

    @Override
    public List<GenericTrainingDTO> getAllTrainerTrainings(String userId, String startDate, String endDate)
            throws UserNotFoundException, StartDateAfterEndDateException, NoTrainingFoundException {

        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UserNotFoundException();

        if (userDocument.getGymRoles() == null || !userDocument.getGymRoles().contains(GymRole.TRAINER)) {
            throw new AccessDeniedException("Indicated user does not have trainer role");
        }

        StartEndDateValidator validator = new StartEndDateValidator(startDate, endDate);

        LocalDateTime startDateTime = validator.getBeginningOfStartDate();
        LocalDateTime endDateTime = validator.getEndOfEndDate();

        List<GroupTrainingDocument> groupList = trainerDAO
                .getTrainerGroupTrainings(userDocument, startDateTime, endDateTime);

        List<IndividualTrainingDocument> individualTrainings = trainerDAO
                .getTrainerIndividualTrainings(userDocument, startDateTime, endDateTime);

        if (groupList.isEmpty() && individualTrainings.isEmpty()) throw new NoTrainingFoundException();

        Function<IndividualTrainingDocument, GenericTrainingDTO> mapIndividualToGenericDTO =
                individualTrainingDocument -> new ModelMapper()
                        .typeMap(IndividualTrainingDocument.class, GenericTrainingDTO.class)
                        .addMapping(source -> source.getLocation().getName(), GenericTrainingDTO::setLocation)
                        .addMapping(source -> source.getTraining().getName(), GenericTrainingDTO::setTitle)
                        .addMapping(source -> false, GenericTrainingDTO::setIsGroupTraining)
                        .map(individualTrainingDocument);

        Function<GroupTrainingDocument, GenericTrainingDTO> mapGroupToGenericDTO =
                groupTrainingDocument -> new ModelMapper()
                        .typeMap(GroupTrainingDocument.class, GenericTrainingDTO.class)
                        .addMapping(source -> source.getLocation().getName(), GenericTrainingDTO::setLocation)
                        .addMapping(source -> source.getTraining().getName(), GenericTrainingDTO::setTitle)
                        .addMapping(source -> true, GenericTrainingDTO::setIsGroupTraining)
                        .map(groupTrainingDocument);

        var mappedIndividualList = individualTrainings
                .stream()
                .map(mapIndividualToGenericDTO)
                .collect(Collectors.toList());

        var mappedGroupList = groupList
                .stream()
                .map(mapGroupToGenericDTO)
                .collect(Collectors.toList());

        return Lists.concat(mappedGroupList, mappedIndividualList);
    }


}
