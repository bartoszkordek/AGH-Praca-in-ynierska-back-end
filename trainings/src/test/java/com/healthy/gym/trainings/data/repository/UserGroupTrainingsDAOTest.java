package com.healthy.gym.trainings.data.repository;

import com.github.javafaker.Faker;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.enums.GymRole;
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

import static com.healthy.gym.trainings.utils.DateParser.parseDate;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class UserGroupTrainingsDAOTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @Autowired
    private UserGroupTrainingsDAO userGroupTrainingsDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    private UserDocument user;
    private String userId;
    private Faker faker;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private void setUpTestGroupTraining(String startDate,
                                        String endDate,
                                        boolean isInBasic,
                                        boolean isInReserve) {

        String groupTrainingId = UUID.randomUUID().toString();
        TrainingTypeDocument trainingType = getTestTrainingTypeSaved();
        UserDocument trainer = getTestTrainerSaved();
        LocationDocument location = getTestLocationSaved();
        UserDocument user1 = getTestUserSaved();
        UserDocument user2 = getTestUserSaved();

        var basicList = isInBasic ? List.of(user1, user2, user) : List.of(user2);
        var reserveList = isInReserve ? List.of(user1, user2, user) : List.of(user2);

        GroupTrainingDocument groupTrainingDocument = new GroupTrainingDocument(
                groupTrainingId,
                trainingType,
                List.of(trainer),
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate),
                location,
                20,
                basicList,
                reserveList
        );
        mongoTemplate.save(groupTrainingDocument);
    }

    private TrainingTypeDocument getTestTrainingType() {
        String trainingTypeId = UUID.randomUUID().toString();
        String name = faker.funnyName().name();
        return new TrainingTypeDocument(trainingTypeId, name);
    }

    private TrainingTypeDocument getTestTrainingTypeSaved() {
        return mongoTemplate.save(getTestTrainingType());
    }

    private UserDocument getTestUser(String userId) {
        String name = faker.name().firstName();
        String surname = faker.name().lastName();
        String email = faker.internet().emailAddress();
        var roles = List.of(GymRole.USER);
        return new UserDocument(name, surname, email, userId, roles);
    }

    private UserDocument getTestUser() {
        String userId = UUID.randomUUID().toString();
        return getTestUser(userId);
    }

    private UserDocument getTestUserSaved() {
        return mongoTemplate.save(getTestUser());
    }

    private UserDocument getTestTrainer() {
        var user = getTestUser();
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }

    private UserDocument getTestTrainerSaved() {
        return mongoTemplate.save(getTestTrainer());
    }

    private LocationDocument getTestLocation() {
        String locationId = UUID.randomUUID().toString();
        String name = faker.address().cityName();
        return new LocationDocument(locationId, name);
    }

    private LocationDocument getTestLocationSaved() {
        return mongoTemplate.save(getTestLocation());
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        faker = new Faker();
        user = mongoTemplate.save(getTestUser(userId));

        setUpTestGroupTraining("2020-10-01T11:30", "2020-10-01T12:00", false, true);
        setUpTestGroupTraining("2020-10-02T12:30", "2020-10-02T13:00", false, false);
        setUpTestGroupTraining("2020-10-03T13:30", "2020-10-03T14:00", false, false);
        setUpTestGroupTraining("2020-10-04T14:30", "2020-10-04T15:00", true, false);
        setUpTestGroupTraining("2020-10-05T15:30", "2020-10-05T16:00", false, true);
        setUpTestGroupTraining("2020-10-06T16:30", "2020-10-06T17:00", true, false);
        setUpTestGroupTraining("2020-10-07T17:30", "2020-10-07T18:00", false, false);
        setUpTestGroupTraining("2020-10-08T18:30", "2020-10-08T19:00", false, true);
        setUpTestGroupTraining("2020-10-09T19:30", "2020-10-09T20:00", false, false);
        setUpTestGroupTraining("2020-10-10T20:30", "2020-10-10T21:00", true, false);
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