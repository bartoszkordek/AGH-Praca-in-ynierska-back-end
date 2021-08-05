package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTrainingMapperTest {

    private GroupTrainingDTO groupTrainingDTO;
    private String trainerId1;
    private String trainerId2;
    private String userId;
    private String reserveUserId1;
    private String reserveUserId2;
    private String groupTrainingId;

    @BeforeEach
    void setUp() {
        TrainingTypeDocument trainingType = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "TestTrainingType",
                "Test description",
                null,
                null
        );


        List<UserDocument> trainers = new ArrayList<>();
        trainerId1 = UUID.randomUUID().toString();
        trainers.add(
                new UserDocument(
                        "TrainerName1",
                        "TrainerSUrname1",
                        "testemail1",
                        "testPhoneNumber1",
                        null,
                        trainerId1
                )
        );
        trainerId2 = UUID.randomUUID().toString();
        trainers.add(
                new UserDocument(
                        "TrainerName2",
                        "TrainerSUrname2",
                        "testemail2",
                        "testPhoneNumber2",
                        null,
                        trainerId2
                )
        );

        List<UserDocument> basicList = new ArrayList<>();
        userId = UUID.randomUUID().toString();
        basicList.add(
                new UserDocument(
                        "UserName2",
                        "UserSUrname2",
                        "testemail2",
                        "testPhoneNumber2",
                        null,
                        userId
                )
        );

        List<UserDocument> reserveList = new ArrayList<>();
        reserveUserId1 = UUID.randomUUID().toString();
        reserveList.add(
                new UserDocument(
                        "UserName3",
                        "UserSUrname3",
                        "testemail3",
                        "testPhoneNumber3",
                        null,
                        reserveUserId1
                )
        );
        reserveUserId2 = UUID.randomUUID().toString();
        reserveList.add(
                new UserDocument(
                        "UserName4",
                        "UserSUrname4",
                        "testemail4",
                        "testPhoneNumber4",
                        null,
                        reserveUserId2
                )
        );

        LocalDateTime startDate = LocalDateTime.parse("2021-07-30T10:10", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endDate = LocalDateTime.parse("2021-07-30T11:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        LocationDocument location = new LocationDocument(UUID.randomUUID().toString(), "Sala nr 2");

        groupTrainingId = UUID.randomUUID().toString();
        GroupTrainingDocument groupTrainingToCreate = new GroupTrainingDocument(
                groupTrainingId,
                trainingType,
                trainers,
                startDate,
                endDate,
                location,
                10,
                basicList,
                reserveList
        );

        groupTrainingDTO = GroupTrainingMapper.mapToGroupTrainingsDocumentsToDTOs(groupTrainingToCreate);
    }

    @Test
    void shouldHaveProperGroupTrainingId() {
        assertThat(groupTrainingDTO.getGroupTrainingId()).isEqualTo(groupTrainingId);
    }

    @Test
    void shouldHaveProperTrainers() {
        var trainerList = groupTrainingDTO.getTrainers();
        assertThat(trainerList.size()).isEqualTo(2);

        var firstTrainer = trainerList.get(0);
        assertThat(groupTrainingDTO.getTrainers().get(0).getUserId()).isEqualTo(trainerId1);
        assertThat(groupTrainingDTO.getTrainers().get(1).getUserId()).isEqualTo(trainerId2);
    }

    @Test
    void shouldHaveProperTitle() {
        assertThat(groupTrainingDTO.getTitle()).isEqualTo("TestTrainingType");
    }

    @Test
    void shouldHaveProperLocation() {
        assertThat(groupTrainingDTO.getLocation()).isEqualTo("Sala nr 2");
    }

    @Test
    void shouldHaveProperStartAndEndDate() {
        assertThat(groupTrainingDTO.getStartDate()).isEqualTo("2021-07-30T10:10");
        assertThat(groupTrainingDTO.getEndDate()).isEqualTo("2021-07-30T11:00");
    }

    @Test
    void shouldHaveProperBasicList() {
        var basicList = groupTrainingDTO.getParticipants().getBasicList();
        assertThat(basicList.size()).isEqualTo(1);

        assertThat(basicList.get(0).getUserId()).isEqualTo(userId);
        assertThat(basicList.get(0).getName()).isEqualTo("UserName2");
        assertThat(basicList.get(0).getSurname()).isEqualTo("UserSUrname2");
        assertThat(basicList.get(0).getAvatarUrl()).isNull();
    }

    @Test
    void shouldHaveProperReserveList() {
        var reserveList = groupTrainingDTO.getParticipants().getReserveList();
        assertThat(reserveList.size()).isEqualTo(2);

        var personDetails1 = reserveList.get(0);
        assertThat(personDetails1.getUserId()).isEqualTo(reserveUserId1);
        assertThat(personDetails1.getName()).isEqualTo("UserName3");
        assertThat(personDetails1.getSurname()).isEqualTo("UserSUrname3");
        assertThat(personDetails1.getAvatarUrl()).isNull();

        var personDetails2 = reserveList.get(1);
        assertThat(personDetails2.getUserId()).isEqualTo(reserveUserId2);
        assertThat(personDetails2.getName()).isEqualTo("UserName4");
        assertThat(personDetails2.getSurname()).isEqualTo("UserSUrname4");
        assertThat(personDetails2.getAvatarUrl()).isNull();
    }


}