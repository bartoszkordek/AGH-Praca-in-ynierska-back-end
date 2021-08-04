package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.shared.BasicUserInfoDTO;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
import com.healthy.gym.trainings.shared.ParticipantsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParticipantsExtractor {

    private ParticipantsExtractor() {
        throw new IllegalStateException("Utility class");
    }

    public static List<UserResponse> getBasicList(GroupTrainingDocument training) {
        List<UserDocument> participants = training.getBasicList();
        return getParticipants(participants);
    }

    public static List<UserResponse> getReserveList(GroupTrainingDocument training) {
        List<UserDocument> reserveList = training.getReserveList();
        return getParticipants(reserveList);
    }

    private static List<UserResponse> getParticipants(List<UserDocument> participants) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return participants
                .stream()
                .map(userDocument -> modelMapper.map(userDocument, UserResponse.class))
                .collect(Collectors.toList());
    }

    public static boolean isClientAlreadyExistInReserveList(
            @NotNull GroupTrainingDocument groupTrainings,
            String clientId
    ) {
        List<UserDocument> reserveListUsers = groupTrainings.getReserveList();
        return checkByIdIfUserExistsInList(reserveListUsers, clientId);
    }

    public static boolean isClientAlreadyEnrolledToGroupTraining(
            @NotNull GroupTrainingDocument groupTrainings,
            String clientId
    ) {
        List<UserDocument> participantsUsers = groupTrainings.getBasicList();
        return checkByIdIfUserExistsInList(participantsUsers, clientId);
    }

    private static boolean checkByIdIfUserExistsInList(
            List<UserDocument> participants,
            String clientId
    ) {
        Optional<UserDocument> foundUser = participants
                .stream()
                .filter(userDocument -> userDocument.getUserId().equals(clientId))
                .findFirst();

        return foundUser.isPresent();
    }

    public static boolean userIsInBasicList(GroupTrainingDTO enrolledTraining, String userId) {
        ParticipantsDTO participants = enrolledTraining.getParticipants();
        List<BasicUserInfoDTO> basicList = participants.getBasicList();
        List<BasicUserInfoDTO> filteredList = basicList
                .stream()
                .filter(user -> user.getUserId().equals(userId))
                .collect(Collectors.toList());
        return !filteredList.isEmpty();
    }
}
