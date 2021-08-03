package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.*;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.shared.BasicUserInfoDTO;
import com.healthy.gym.trainings.shared.GroupTrainingEnrollmentDTO;
import com.healthy.gym.trainings.model.response.GroupTrainingsPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.shared.GetGroupTrainingPublicDTO;
import com.healthy.gym.trainings.shared.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.healthy.gym.trainings.utils.ParticipantsExtractor.isClientAlreadyEnrolledToGroupTraining;
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.isClientAlreadyExistInReserveList;

@Service
public class UserGroupTrainingServiceImpl implements UserGroupTrainingService {

    private final GroupTrainingsRepository groupTrainingsRepository;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final UserDAO userRepository;
    private final Pageable paging;
    private final ReviewDAO groupTrainingsReviewsRepository;
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timeFormatter;

    @Autowired
    public UserGroupTrainingServiceImpl(
            GroupTrainingsRepository groupTrainingsRepository,
            GroupTrainingsDAO groupTrainingsDAO,
            UserDAO userRepository,
            ReviewDAO groupTrainingsReviewsRepository
    ) {
        this.groupTrainingsRepository = groupTrainingsRepository;
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.userRepository = userRepository;
        this.groupTrainingsReviewsRepository = groupTrainingsReviewsRepository;
        this.paging = PageRequest.of(0, 1000000);

        dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    }

    @Override
    public GroupTrainingsPublicResponse getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException, UserNotFoundException {

        UserDocument userDocument = userRepository.findByUserId(clientId);
        if (userDocument == null) throw new UserNotFoundException();

        List<GetGroupTrainingPublicDTO> groupTrainingsDTO = new ArrayList<>();
        List<GroupTrainingDocument> groupTrainings = groupTrainingsDAO.findByBasicListContains(userDocument);

        for (GroupTrainingDocument groupTrainingDocument : groupTrainings) {

            LocalDateTime documentStartDate = groupTrainingDocument.getStartDate();
            LocalDateTime documentEndDate = groupTrainingDocument.getEndDate();

            double rating = getRatingForGroupTrainings(groupTrainingDocument);

            List<BasicUserInfoDTO> trainersDTO = mapUserDocumentsToBasicUserInfoDTO(groupTrainingDocument.getTrainers());


            GetGroupTrainingPublicDTO groupTrainingDTO = new GetGroupTrainingPublicDTO(
                    groupTrainingDocument.getGroupTrainingId(),
                    groupTrainingDocument.getTraining().getName(),
                    documentStartDate.format(dateFormatter).concat("T").concat(documentStartDate.format(timeFormatter)),
                    documentEndDate.format(dateFormatter).concat("T").concat(documentEndDate.format(timeFormatter)),
                    false,
                    groupTrainingDocument.getLocation().getLocationId(),
                    rating,
                    trainersDTO
            );

            groupTrainingsDTO.add(groupTrainingDTO);
        }

        return new GroupTrainingsPublicResponse(groupTrainingsDTO);
    }

    @Override
    public GroupTrainingEnrollmentDTO enrollToGroupTraining(String trainingId, String clientId)
            throws TrainingEnrollmentException, NotExistingGroupTrainingException, UserNotFoundException, InvalidHourException, InvalidDateException {

        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        UserDocument newParticipant = userRepository.findByUserId(clientId);
        if (newParticipant == null) throw new UserNotFoundException();

        if(groupTraining.getStartDate().isAfter(LocalDateTime.now())){
            throw new TrainingEnrollmentException("Retro event");
        }

        if(groupTraining.getLimit()<=groupTraining.getBasicList().size()){
            throw new TrainingEnrollmentException("Full participants list");
        }

        if (isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        List<UserDocument> trainersDocuments = groupTraining.getTrainers();
        List<BasicUserInfoDTO> trainersResponse = mapUserDocumentsToBasicUserInfoDTO(trainersDocuments);

        List<UserDocument> participants = groupTraining.getBasicList();
        participants.add(newParticipant);
        groupTraining.setBasicList(participants);
        groupTrainingsDAO.save(groupTraining);

        // TODO nie rozumiem tego kodu poniżej, czemu on tutaj jest?
        // to jest po to żeby usunął go z listy rezerwowej jak już się zapisze na listę postawową
        if (isClientAlreadyExistInReserveList(groupTraining, clientId))
            removeFromReserveList(trainingId, clientId);

        return new GroupTrainingEnrollmentDTO(
                groupTraining.getGroupTrainingId(),
                groupTraining.getTraining().getName(),
                trainersResponse,
                groupTraining.getStartDate(),
                groupTraining.getEndDate(),
                groupTraining.getLocation().getName()
        );
    }

    @Override
    public void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException {

        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        UserDocument newReserveListParticipant = userRepository.findByUserId(clientId);
        if (newReserveListParticipant == null) throw new UserNotFoundException();

        if (isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        if (isClientAlreadyExistInReserveList(groupTraining, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        if(groupTraining.getStartDate().isAfter(LocalDateTime.now())){
            throw new TrainingEnrollmentException("Retro event");
        }

        if(groupTraining.getLimit()<=groupTraining.getBasicList().size()){
            throw new TrainingEnrollmentException("Full participants list");
        }

        List<UserDocument> reserveList = groupTraining.getReserveList();
        reserveList.add(newReserveListParticipant);
        groupTraining.setReserveList(reserveList);
        groupTrainingsDAO.save(groupTraining);
    }

    @Override
    public void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException {

        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        UserDocument participantToRemove = userRepository.findByUserId(clientId);
        if (participantToRemove == null) throw new UserNotFoundException();

        if(groupTraining.getStartDate().isAfter(LocalDateTime.now())){
            throw new TrainingEnrollmentException("Retro event");
        }

        boolean clientIsEnrolled = isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId);
        boolean clientIsInReserveList = isClientAlreadyExistInReserveList(groupTraining, clientId);

        if (!clientIsEnrolled && !clientIsInReserveList)
            throw new TrainingEnrollmentException("Client is not enrolled to this training");

        if (clientIsEnrolled) {
            List<UserDocument> participants = groupTraining.getBasicList();
            participants.remove(participantToRemove);
            groupTraining.setBasicList(participants);
            groupTrainingsDAO.save(groupTraining);
        }

        if (clientIsInReserveList) removeFromReserveList(trainingId, clientId);
    }

    private double getRatingForGroupTrainings(GroupTrainingDocument groupTraining) {
        List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository
                .findByDateBetweenAndTrainingTypeId(
                        null,
                        null,
                        groupTraining.getTraining().getTrainingTypeId(),
                        paging
                ).getContent();

        double rating = 0.0;
        double sum = 0;
        int counter = 0;
        for (GroupTrainingReviewResponse review : groupTrainingsReviews) {
            sum += review.getStars();
            counter++;
        }
        if (counter != 0) rating = sum / counter;

        return rating;
    }

    private List<BasicUserInfoDTO> mapUserDocumentsToBasicUserInfoDTO(List<UserDocument> userDocuments){

        List<BasicUserInfoDTO> basicUserInfoDTOs = new ArrayList<>();
        for(UserDocument userDocument : userDocuments){
            BasicUserInfoDTO basicUserInfoDTO = new BasicUserInfoDTO(
                    userDocument.getUserId(),
                    userDocument.getName(),
                    userDocument.getSurname(),
                    null//TODO getAvatarUrl
            );
            basicUserInfoDTOs.add(basicUserInfoDTO);
        }
        return basicUserInfoDTOs;
    }

    private List<UserDTO> mapUserResponse(List<UserDocument> documents){
        List<UserDTO> userResponses = new ArrayList<>();
        for(UserDocument document : documents){
            UserDTO userResponse = new UserDTO(
                    document.getUserId(),
                    document.getName(),
                    document.getSurname()
                    );
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    private void removeFromReserveList(String trainingId, String clientId) {
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        UserDocument reserveListParticipantToRemove = userRepository.findByUserId(clientId);
        List<UserDocument> reserveList = groupTrainings.getReserveList();
        reserveList.remove(reserveListParticipantToRemove);
        groupTrainings.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTrainings);
    }
}
