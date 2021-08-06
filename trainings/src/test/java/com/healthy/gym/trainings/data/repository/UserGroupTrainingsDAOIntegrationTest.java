package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.group.training.UserGroupTrainingsDAO;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestUser;
import static com.healthy.gym.trainings.utils.DateParser.parseDate;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class UserGroupTrainingsDAOIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @Autowired
    private UserGroupTrainingsDAO userGroupTrainingsDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestDocumentUtilComponent testUtil;

    private UserDocument user;
    private String userId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        user = mongoTemplate.save(getTestUser(userId));

        testUtil.createTestGroupTraining(
                user, "2020-10-01T11:30", "2020-10-01T12:00", false, true);
        testUtil.createTestGroupTraining(
                user, "2020-10-02T12:30", "2020-10-02T13:00", false, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-03T13:30", "2020-10-03T14:00", false, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-04T14:30", "2020-10-04T15:00", true, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-05T15:30", "2020-10-05T16:00", false, true);
        testUtil.createTestGroupTraining(
                user, "2020-10-06T16:30", "2020-10-06T17:00", true, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-07T17:30", "2020-10-07T18:00", false, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-08T18:30", "2020-10-08T19:00", false, true);
        testUtil.createTestGroupTraining(
                user, "2020-10-09T19:30", "2020-10-09T20:00", false, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-10T20:30", "2020-10-10T21:00", true, false);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @CsvSource({
            "2020-10-01,2020-11-03,6",
            "2020-10-01,2020-10-05,3",
            "2020-10-09,2020-10-10,1",
            "2020-10-04,2020-10-05,2",
            "2020-10-03,2020-10-06,3",
            "2020-10-04,2020-10-08,4",
            "2020-10-01,2020-10-05,3",
    })
    void shouldReturnGroupTrainingsWithUserAsParticipantWithinIndicatedDates(
            String startDateStr,
            String endDateStr,
            int numberOfTrainingsWithinDates
    ) {
        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTIme = LocalDateTime.of(endDate, LocalTime.MAX);

        List<GroupTrainingDocument> userGroupTrainingList = userGroupTrainingsDAO
                .findAllGroupTrainings(user, startDateTime, endDateTIme);

        assertThat(userGroupTrainingList.size()).isEqualTo(numberOfTrainingsWithinDates);

        List<List<UserDocument>> listOfParticipantsList = userGroupTrainingList
                .stream()
                .map(groupTrainingDocument -> {
                            var basicList = groupTrainingDocument.getBasicList();
                            var reserveList = groupTrainingDocument.getReserveList();

                            List<UserDocument> allParticipants = new ArrayList<>();
                            allParticipants.addAll(basicList);
                            allParticipants.addAll(reserveList);
                            return allParticipants;
                        }
                )
                .collect(Collectors.toList());

        for (List<UserDocument> participants : listOfParticipantsList) {
            long isUserPresent = participants
                    .stream()
                    .filter(userDocument -> userDocument.getUserId().equals(userId))
                    .count();
            assertThat(isUserPresent).isEqualTo(1);
        }
    }
}