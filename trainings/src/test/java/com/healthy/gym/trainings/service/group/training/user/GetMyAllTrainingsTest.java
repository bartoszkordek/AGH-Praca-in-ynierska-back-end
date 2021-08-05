package com.healthy.gym.trainings.service.group.training.user;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.UserGroupTrainingsDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingService;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingServiceImpl;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestGroupTrainingDocument;
import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetMyAllTrainingsTest {

    private UserGroupTrainingsDAO userGroupTrainingsDAO;
    private UserDAO userDAO;
    private UserGroupTrainingService userGroupTrainingService;
    private String userId;
    private UserDocument user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userGroupTrainingsDAO = mock(UserGroupTrainingsDAO.class);
        userDAO = mock(UserDAO.class);

        userGroupTrainingService = new UserGroupTrainingServiceImpl(
                userGroupTrainingsDAO,
                null,
                userDAO,
                null
        );

        user = getTestUser(userId);
    }

    @Test
    void shouldThrowNotExistingGroupTrainingException() {
        when(userDAO.findByUserId(userId)).thenReturn(null);

        assertThatThrownBy(
                () -> userGroupTrainingService.getMyAllTrainings(userId, "2020-10-01", "2020-10-02")
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowStartDateAfterEndDateException() {
        when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());

        assertThatThrownBy(
                () -> userGroupTrainingService.getMyAllTrainings(userId, "2020-10-10", "2020-10-02")
        ).isInstanceOf(StartDateAfterEndDateException.class);
    }

    @Test
    void shouldReturnEmptyList() throws UserNotFoundException, StartDateAfterEndDateException {
        when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
        when(userGroupTrainingsDAO.findAllGroupTrainings(any(), any(), any())).thenReturn(List.of());

        assertThat(
                userGroupTrainingService.getMyAllTrainings(userId, "2020-10-01", "2020-10-02")
        ).isEmpty();
    }

    @Test
    void shouldReturnNonEmptyList() throws UserNotFoundException, StartDateAfterEndDateException {
        when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
        when(userGroupTrainingsDAO.findAllGroupTrainings(any(), any(), any()))
                .thenReturn(List.of(getTestTraining1(), getTestTraining2()));

        List<GroupTrainingDTO> groupTrainingDTOS = userGroupTrainingService
                .getMyAllTrainings(userId, "2020-10-01", "2020-10-02");

        assertThat(groupTrainingDTOS.size()).isEqualTo(2);

        List<List<BasicUserInfoDTO>> listOfAllParticipantsList = groupTrainingDTOS
                .stream()
                .map(GroupTrainingDTO::getParticipants)
                .map(
                        participantsDTO -> {
                            var basicList = participantsDTO.getBasicList();
                            var reserveList = participantsDTO.getReserveList();
                            List<BasicUserInfoDTO> users = new ArrayList<>();
                            users.addAll(basicList);
                            users.addAll(reserveList);
                            return users;
                        }
                ).collect(Collectors.toList());

        for (List<BasicUserInfoDTO> participants : listOfAllParticipantsList) {
            long isUserPresent = participants.stream().filter(user -> user.getUserId().equals(userId)).count();
            assertThat(isUserPresent).isEqualTo(1);
        }
    }

    private GroupTrainingDocument getTestTraining1() {
        return getTestGroupTrainingDocument(
                "2020-10-01T10:00",
                "2020-10-01T12:00",
                user,
                true,
                false
        );
    }

    private GroupTrainingDocument getTestTraining2() {
        return getTestGroupTrainingDocument(
                "2020-10-02T10:00",
                "2020-10-02T12:00",
                user,
                false,
                true
        );
    }
}
