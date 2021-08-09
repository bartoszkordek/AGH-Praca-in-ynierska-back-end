package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IndividualTrainingMapperTest {

    private String trainerId1;
    private String trainerId2;
    private String userId;
    private String individualTrainingId;
    private IndividualTrainingDocument individualTrainingDocument;

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

        LocalDateTime startDateTime = LocalDateTime
                .parse("2021-07-30T10:10", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endDateTime = LocalDateTime
                .parse("2021-07-30T11:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        LocationDocument location = new LocationDocument(UUID.randomUUID().toString(), "Room no 2");
        String remarks = "Test remarks";

        individualTrainingId = UUID.randomUUID().toString();
        individualTrainingDocument = new IndividualTrainingDocument(
                individualTrainingId,
                trainingType,
                basicList,
                trainers,
                startDateTime,
                endDateTime,
                location,
                remarks
        );
    }

    @Nested
    class WhenMapGroupTrainingsDocumentToDTO {

        private IndividualTrainingDTO individualTrainingDTO;

        @BeforeEach
        void setUp() {
            System.out.println(individualTrainingDocument);
            individualTrainingDTO = IndividualTrainingMapper
                    .mapIndividualTrainingDocumentToDTO(individualTrainingDocument);
            System.out.println(individualTrainingDTO);
        }

        @Test
        void shouldHaveProperGroupTrainingId() {
            assertThat(individualTrainingDTO.getIndividualTrainingId()).isEqualTo(individualTrainingId);
        }

        @Test
        void shouldHaveProperTrainers() {
            var trainerList = individualTrainingDTO.getTrainers();
            assertThat(trainerList.size()).isEqualTo(2);

            assertThat(individualTrainingDTO.getTrainers().get(0).getUserId()).isEqualTo(trainerId1);
            assertThat(individualTrainingDTO.getTrainers().get(1).getUserId()).isEqualTo(trainerId2);
        }

        @Test
        void shouldHaveProperTitle() {
            assertThat(individualTrainingDTO.getTitle()).isEqualTo("TestTrainingType");
        }

        @Test
        void shouldHaveProperLocation() {
            assertThat(individualTrainingDTO.getLocation()).isEqualTo("Room no 2");
        }

        @Test
        void shouldHaveProperStartAndEndDate() {
            assertThat(individualTrainingDTO.getStartDate()).isEqualTo("2021-07-30T10:10");
            assertThat(individualTrainingDTO.getEndDate()).isEqualTo("2021-07-30T11:00");
        }

        @Test
        void shouldHaveProperBasicList() {
            var basicList = individualTrainingDTO.getParticipants().getBasicList();
            assertThat(basicList.size()).isEqualTo(1);

            assertThat(basicList.get(0).getUserId()).isEqualTo(userId);
            assertThat(basicList.get(0).getName()).isEqualTo("UserName2");
            assertThat(basicList.get(0).getSurname()).isEqualTo("UserSUrname2");
            assertThat(basicList.get(0).getAvatarUrl()).isNull();
        }

        @Test
        void shouldHaveProperReserveList() {
            var reserveList = individualTrainingDTO.getParticipants().getReserveList();
            assertThat(reserveList.size()).isZero();
        }
    }

}