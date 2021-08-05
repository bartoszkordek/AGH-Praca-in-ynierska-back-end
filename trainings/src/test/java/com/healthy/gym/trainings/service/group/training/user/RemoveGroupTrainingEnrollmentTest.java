package com.healthy.gym.trainings.service.group.training.user;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingService;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingServiceImpl;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RemoveGroupTrainingEnrollmentTest {

    private GroupTrainingsDAO groupTrainingsDAO;
    private UserDAO userDAO;
    private UserGroupTrainingService userGroupTrainingService;
    private String groupTrainingId;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        groupTrainingId = UUID.randomUUID().toString();
        groupTrainingsDAO = mock(GroupTrainingsDAO.class);
        userDAO = mock(UserDAO.class);
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T18:00:00.00Z"), ZoneId.of("Europe/Warsaw"));

        userGroupTrainingService = new UserGroupTrainingServiceImpl(
                null,
                groupTrainingsDAO,
                userDAO,
                clock
        );
    }

    @Test
    void shouldThrowNotExistingGroupTrainingException() {
        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(null);

        assertThatThrownBy(
                () -> userGroupTrainingService.removeGroupTrainingEnrollment(groupTrainingId, userId)
        ).isInstanceOf(NotExistingGroupTrainingException.class);
    }

    @Test
    void shouldThrowUserNotFoundException() {
        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(userDAO.findByUserId(anyString())).thenReturn(null);

        assertThatThrownBy(
                () -> userGroupTrainingService.removeGroupTrainingEnrollment(groupTrainingId, userId)
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowPastDateException() {
        var groupTraining = new GroupTrainingDocument();
        groupTraining.setStartDate(LocalDateTime.parse("2021-07-10T17:00"));
        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(groupTraining);
        when(userDAO.findByUserId(anyString())).thenReturn(new UserDocument());

        assertThatThrownBy(
                () -> userGroupTrainingService.removeGroupTrainingEnrollment(groupTrainingId, userId)
        ).isInstanceOf(PastDateException.class);
    }

    @Test
    void shouldThrowTrainingEnrollmentException() {
        var groupTraining = new GroupTrainingDocument();
        groupTraining.setStartDate(LocalDateTime.parse("2021-07-10T20:00"));
        groupTraining.setBasicList(List.of());
        groupTraining.setReserveList(List.of());

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(groupTraining);
        when(userDAO.findByUserId(anyString())).thenReturn(new UserDocument());

        assertThatThrownBy(
                () -> userGroupTrainingService.removeGroupTrainingEnrollment(groupTrainingId, userId)
        ).isInstanceOf(TrainingEnrollmentException.class);
    }

    @Test
    void shouldRemoveFromBasicList() throws UserNotFoundException, TrainingEnrollmentException,
            PastDateException, NotExistingGroupTrainingException {

        var groupTraining = new GroupTrainingDocument();
        groupTraining.setStartDate(LocalDateTime.parse("2021-07-11T20:00"));
        groupTraining.setBasicList(List.of(getTestUser(), getTestUser(), getTestUser(userId)));
        groupTraining.setReserveList(List.of());

        var groupTrainingWithoutUser = new GroupTrainingDocument(
                UUID.randomUUID().toString(),
                new TrainingTypeDocument(
                        UUID.randomUUID().toString(),
                        "TestTraining",
                        "TestDescription",
                        null,
                        null
                ),
                List.of(getTestUser()),
                LocalDateTime.parse("2021-07-11T20:00"),
                LocalDateTime.parse("2021-07-11T22:00"),
                new LocationDocument(UUID.randomUUID().toString(), "TestLocation"),
                10,
                List.of(getTestUser(), getTestUser()),
                List.of()
        );

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(groupTraining);
        when(userDAO.findByUserId(anyString())).thenReturn(new UserDocument());
        when(groupTrainingsDAO.save(any())).thenReturn(groupTrainingWithoutUser);

        GroupTrainingDTO groupTrainingDTO = userGroupTrainingService
                .removeGroupTrainingEnrollment(groupTrainingId, userId);
        List<String> userIds = groupTrainingDTO.getParticipants()
                .getBasicList()
                .stream()
                .map(BasicUserInfoDTO::getUserId)
                .collect(Collectors.toList());

        assertThat(groupTrainingDTO.getStartDate()).isEqualTo("2021-07-11T20:00");
        assertThat(groupTrainingDTO.getParticipants().getBasicList().size()).isEqualTo(2);
        assertThat(userIds.contains(userId)).isFalse();
        assertThat(groupTrainingDTO.getParticipants().getReserveList().size()).isZero();
    }

    private UserDocument getTestUser(String userId) {
        var user = new UserDocument();
        user.setUserId(userId);
        user.setName("TestName");
        user.setSurname("TestSurname");
        return user;
    }

    private UserDocument getTestUser() {
        return getTestUser(UUID.randomUUID().toString());
    }

    @Test
    void shouldRemoveFromReserveList() throws UserNotFoundException, TrainingEnrollmentException,
            PastDateException, NotExistingGroupTrainingException {

        var groupTraining = new GroupTrainingDocument();
        groupTraining.setStartDate(LocalDateTime.parse("2021-07-11T20:00"));
        groupTraining.setBasicList(List.of());
        groupTraining.setReserveList(List.of(getTestUser(), getTestUser(), getTestUser(userId)));

        var groupTrainingWithoutUser = new GroupTrainingDocument(
                UUID.randomUUID().toString(),
                new TrainingTypeDocument(
                        UUID.randomUUID().toString(),
                        "TestTraining",
                        "TestDescription",
                        null,
                        null
                ),
                List.of(getTestUser()),
                LocalDateTime.parse("2021-07-11T20:00"),
                LocalDateTime.parse("2021-07-11T22:00"),
                new LocationDocument(UUID.randomUUID().toString(), "TestLocation"),
                10,
                List.of(getTestUser(), getTestUser()),
                List.of()
        );

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(groupTraining);
        when(userDAO.findByUserId(anyString())).thenReturn(new UserDocument());
        when(groupTrainingsDAO.save(any())).thenReturn(groupTrainingWithoutUser);

        GroupTrainingDTO groupTrainingDTO = userGroupTrainingService
                .removeGroupTrainingEnrollment(groupTrainingId, userId);
        List<String> userIds = groupTrainingDTO.getParticipants()
                .getBasicList()
                .stream()
                .map(BasicUserInfoDTO::getUserId)
                .collect(Collectors.toList());

        assertThat(groupTrainingDTO.getStartDate()).isEqualTo("2021-07-11T20:00");
        assertThat(groupTrainingDTO.getParticipants().getBasicList().size()).isEqualTo(2);
        assertThat(userIds.contains(userId)).isFalse();
        assertThat(groupTrainingDTO.getParticipants().getReserveList().size()).isZero();
    }
}
