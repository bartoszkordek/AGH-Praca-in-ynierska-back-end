package com.healthy.gym.trainings.service.group.training.manager;

import com.healthy.gym.trainings.component.CollisionValidatorComponent;
import com.healthy.gym.trainings.component.CollisionValidatorComponentImpl;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.group.training.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.service.group.training.GroupTrainingDocumentUpdateBuilder;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingService;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingServiceImpl;
import com.healthy.gym.trainings.test.utils.TestDocumentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UpdateGroupTrainingServiceTest {

    private IndividualTrainingRepository individualTrainingRepository;
    private GroupTrainingsDAO groupTrainingsDAO;
    private ManagerGroupTrainingRequest groupTrainingRequest;
    private ManagerGroupTrainingService managerGroupTrainingService;
    private GroupTrainingDocumentUpdateBuilder groupTrainingDocumentUpdateBuilder;
    private String groupTrainingId;

    @BeforeEach
    void setUp() {
        individualTrainingRepository = mock(IndividualTrainingRepository.class);
        groupTrainingId = UUID.randomUUID().toString();
        groupTrainingsDAO = mock(GroupTrainingsDAO.class);
        TrainingTypeDAO trainingTypeDAO = mock(TrainingTypeDAO.class);
        LocationDAO locationDAO = mock(LocationDAO.class);
        UserDAO userDAO = mock(UserDAO.class);
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T18:00:00.00Z"), ZoneId.of("Europe/Warsaw"));

        groupTrainingDocumentUpdateBuilder = mock(GroupTrainingDocumentUpdateBuilder.class, RETURNS_DEEP_STUBS);
        groupTrainingRequest = getGroupTrainingRequest();

        CollisionValidatorComponent collisionValidatorComponent =
                new CollisionValidatorComponentImpl(groupTrainingsDAO, individualTrainingRepository);

        managerGroupTrainingService = new ManagerGroupTrainingServiceImpl(
                collisionValidatorComponent,
                groupTrainingsDAO,
                trainingTypeDAO,
                locationDAO,
                userDAO,
                clock,
                groupTrainingDocumentUpdateBuilder
        );
    }

    private ManagerGroupTrainingRequest getGroupTrainingRequest() {
        ManagerGroupTrainingRequest createGroupTrainingRequest = new ManagerGroupTrainingRequest();
        createGroupTrainingRequest.setTrainingTypeId("122ed953-e37f-435a-bd1e-9fb2a327c4d3");
        createGroupTrainingRequest.setTrainerIds(
                List.of("100ed952-es7f-435a-bd1e-9fb2a327c4dk", "501692e9-2a79-46bb-ac62-55f980581bad")
        );
        createGroupTrainingRequest.setStartDate("2021-07-10T20:00");
        createGroupTrainingRequest.setEndDate("2021-07-10T21:00");
        createGroupTrainingRequest.setLocationId("05cbccea-6248-4e40-931b-a34031a8c678");
        createGroupTrainingRequest.setLimit(10);
        return createGroupTrainingRequest;
    }

    @Test
    void shouldThrowNotExistingGroupTrainingException() {
        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(null);

        assertThatThrownBy(
                () -> managerGroupTrainingService.updateGroupTraining(groupTrainingId, null)
        ).isInstanceOf(NotExistingGroupTrainingException.class);
    }

    @Test
    void shouldThrowStartDateAfterEndDateException() throws
            TrainingTypeNotFoundException,
            PastDateException,
            LocationNotFoundException,
            TrainerNotFoundException {

        GroupTrainingDocument groupTrainingUpdated = new GroupTrainingDocument(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                getTestTrainingTypeDocument(),
                List.of(getTestTrainer1(), getTestTrainer2()),
                LocalDateTime.parse("2021-07-10T22:00"),
                LocalDateTime.parse("2021-07-10T21:00"),
                getTestLocationDocument(),
                10,
                List.of(getTestUserDocument1()),
                List.of(getTestUserDocument2())
        );

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(any())
                .setGroupTrainingRequest(any())
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update()
        ).thenReturn(groupTrainingUpdated);

        assertThatThrownBy(
                () -> managerGroupTrainingService.updateGroupTraining(groupTrainingId, groupTrainingRequest)
        ).isInstanceOf(StartDateAfterEndDateException.class);
    }

    @Test
    void shouldThrowLocationOccupiedExceptionByGroupTrainings() throws
            TrainingTypeNotFoundException,
            PastDateException,
            LocationNotFoundException,
            TrainerNotFoundException {

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(any())
                .setGroupTrainingRequest(any())
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update()
        ).thenReturn(getUpdatedGroupTrainingDocument());

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MAX);
        when(groupTrainingsDAO.findAllByStartDateIsAfterAndEndDateIsBefore(startDateTime, endDateTime, Sort.by("startDate")))
                .thenReturn(List.of(
                        TestDocumentUtil.getTestGroupTraining(
                                "2021-07-10T19:00", "2021-07-10T20:30", getTestLocationDocument()
                        ),
                        TestDocumentUtil.getTestGroupTraining(
                                "2021-07-10T21:00", "2021-07-10T22:00", getTestLocationDocument()
                        )
                ));

        when(individualTrainingRepository.findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(
                startDateTime, endDateTime, Sort.by("startDateTime")
        )).thenReturn(List.of());

        assertThatThrownBy(
                () -> managerGroupTrainingService.updateGroupTraining(groupTrainingId, groupTrainingRequest)
        ).isInstanceOf(LocationOccupiedException.class);
    }

    private GroupTrainingDocument getUpdatedGroupTrainingDocument() {
        return new GroupTrainingDocument(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                getTestTrainingTypeDocument(),
                List.of(getTestTrainer1(), getTestTrainer2()),
                LocalDateTime.parse("2021-07-10T20:00"),
                LocalDateTime.parse("2021-07-10T21:00"),
                getTestLocationDocument(),
                10,
                List.of(getTestUserDocument1()),
                List.of(getTestUserDocument2())
        );
    }

    @Test
    void shouldThrowLocationOccupiedExceptionByIndividualTrainings() throws
            TrainingTypeNotFoundException,
            PastDateException,
            LocationNotFoundException,
            TrainerNotFoundException {

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(any())
                .setGroupTrainingRequest(any())
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update()
        ).thenReturn(getUpdatedGroupTrainingDocument());

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MAX);

        when(groupTrainingsDAO
                .findAllByStartDateIsAfterAndEndDateIsBefore(startDateTime, endDateTime, Sort.by("startDate"))
        ).thenReturn(List.of());

        when(individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(startDateTime, endDateTime, Sort.by("startDateTime"))
        ).thenReturn(List.of(
                TestDocumentUtil.getTestIndividualTraining(
                        "2021-07-10T19:00", "2021-07-10T20:30", getTestLocationDocument()
                ),
                TestDocumentUtil.getTestIndividualTraining(
                        "2021-07-10T21:00", "2021-07-10T22:00", getTestLocationDocument()
                )
        ));

        assertThatThrownBy(
                () -> managerGroupTrainingService.updateGroupTraining(groupTrainingId, groupTrainingRequest)
        ).isInstanceOf(LocationOccupiedException.class);
    }

    @Test
    void shouldThrowTrainerOccupiedExceptionByGroupTrainings() throws
            TrainingTypeNotFoundException,
            PastDateException,
            LocationNotFoundException,
            TrainerNotFoundException {

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(any())
                .setGroupTrainingRequest(any())
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update()
        ).thenReturn(getUpdatedGroupTrainingDocument());

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MAX);
        when(groupTrainingsDAO.findAllByStartDateIsAfterAndEndDateIsBefore(startDateTime, endDateTime, Sort.by("startDate")))
                .thenReturn(List.of(
                        TestDocumentUtil.getTestGroupTraining(
                                "2021-07-10T19:00", "2021-07-10T20:30", List.of(getTestTrainer2())
                        ),
                        TestDocumentUtil.getTestGroupTraining(
                                "2021-07-10T21:00", "2021-07-10T22:00", List.of(getTestTrainer1())
                        )
                ));

        when(individualTrainingRepository.findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(
                startDateTime, endDateTime, Sort.by("startDateTime")
        )).thenReturn(List.of());

        assertThatThrownBy(
                () -> managerGroupTrainingService.updateGroupTraining(groupTrainingId, groupTrainingRequest)
        ).isInstanceOf(TrainerOccupiedException.class);
    }

    @Test
    void shouldThrowTrainerOccupiedExceptionByIndividualTrainings() throws
            TrainingTypeNotFoundException,
            PastDateException,
            LocationNotFoundException,
            TrainerNotFoundException {

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(any())
                .setGroupTrainingRequest(any())
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update()
        ).thenReturn(getUpdatedGroupTrainingDocument());

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MAX);

        when(groupTrainingsDAO
                .findAllByStartDateIsAfterAndEndDateIsBefore(startDateTime, endDateTime, Sort.by("startDate"))
        ).thenReturn(List.of());

        when(individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(startDateTime, endDateTime, Sort.by("startDateTime"))
        ).thenReturn(List.of(
                TestDocumentUtil.getTestIndividualTraining(
                        "2021-07-10T19:00", "2021-07-10T20:30", List.of(getTestTrainer2())
                ),
                TestDocumentUtil.getTestIndividualTraining(
                        "2021-07-10T21:00", "2021-07-10T22:00", List.of(getTestTrainer1())
                )
        ));

        assertThatThrownBy(
                () -> managerGroupTrainingService.updateGroupTraining(groupTrainingId, groupTrainingRequest)
        ).isInstanceOf(TrainerOccupiedException.class);
    }

    @Test
    void shouldProperlySaveAndReturnGroupTrainingDTO() throws TrainingTypeNotFoundException,
            LocationOccupiedException,
            PastDateException,
            LocationNotFoundException,
            NotExistingGroupTrainingException,
            StartDateAfterEndDateException,
            TrainerOccupiedException,
            TrainerNotFoundException {

        GroupTrainingDocument groupTrainingUpdated = getUpdatedGroupTrainingDocument();

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(any())
                .setGroupTrainingRequest(any())
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update()
        ).thenReturn(groupTrainingUpdated);
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MAX);
        when(groupTrainingsDAO
                .findAllByStartDateIsAfterAndEndDateIsBefore(startDateTime, endDateTime, Sort.by("startDate"))
        ).thenReturn(List.of());

        when(individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(startDateTime, endDateTime, Sort.by("startDateTime"))
        ).thenReturn(List.of(
        ));
        when(groupTrainingsDAO.save(any())).thenReturn(groupTrainingUpdated);

        assertThat(managerGroupTrainingService.updateGroupTraining(groupTrainingId, groupTrainingRequest))
                .isEqualTo(getExpectedGroupTrainingDTO());
    }

    private TrainingTypeDocument getTestTrainingTypeDocument() {
        return new TrainingTypeDocument(
                "122ed953-e37f-435a-bd1e-9fb2a327c4d3",
                "TestTraining",
                "TestDescription",
                null,
                null
        );
    }

    private UserDocument getTestTrainer1() {
        var user = new UserDocument();
        user.setName("TestTrainerName1");
        user.setSurname("TestTrainerSurname1");
        user.setUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk");
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }

    private UserDocument getTestTrainer2() {
        var user = new UserDocument();
        user.setName("TestTrainerName2");
        user.setSurname("TestTrainerSurname2");
        user.setUserId("501692e9-2a79-46bb-ac62-55f980581bad");
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }

    private LocationDocument getTestLocationDocument() {
        return new LocationDocument("f0001d12-a79a-4b8d-a78d-f71c9726a6d9", "TestLocation");
    }

    private UserDocument getTestUserDocument1() {
        var user = new UserDocument();
        user.setUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk");
        user.setName("TestName1");
        user.setSurname("TestSurname1");
        user.setGymRoles(List.of(GymRole.USER));
        return user;
    }

    private UserDocument getTestUserDocument2() {
        var user = new UserDocument();
        user.setUserId("501692e9-2a79-46bb-ac62-55f980581bad");
        user.setName("TestName2");
        user.setSurname("TestSurname2");
        user.setGymRoles(List.of(GymRole.USER));
        return user;
    }

    private GroupTrainingDTO getExpectedGroupTrainingDTO() {
        var trainer1 = new BasicUserInfoDTO(
                "100ed952-es7f-435a-bd1e-9fb2a327c4dk",
                "TestTrainerName1",
                "TestTrainerSurname1",
                null
        );

        var trainer2 = new BasicUserInfoDTO(
                "501692e9-2a79-46bb-ac62-55f980581bad",
                "TestTrainerName2",
                "TestTrainerSurname2",
                null
        );

        var user1 = new BasicUserInfoDTO(
                "100ed952-es7f-435a-bd1e-9fb2a327c4dk",
                "TestName1",
                "TestSurname1",
                null
        );

        var user2 = new BasicUserInfoDTO(
                "501692e9-2a79-46bb-ac62-55f980581bad",
                "TestName2",
                "TestSurname2",
                null
        );

        var groupTrainingDTO = new GroupTrainingDTO(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                "TestTraining",
                "2021-07-10T20:00",
                "2021-07-10T21:00",
                false,
                "TestLocation",
                List.of(trainer1, trainer2)
        );
        groupTrainingDTO.setBasicList(List.of(user1));
        groupTrainingDTO.setReserveList(List.of(user2));

        return groupTrainingDTO;
    }
}
