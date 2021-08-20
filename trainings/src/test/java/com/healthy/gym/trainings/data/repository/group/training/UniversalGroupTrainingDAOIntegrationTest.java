package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.healthy.gym.trainings.utils.DateParser.parseDate;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@ActiveProfiles(value = "test")
@Tags({@Tag("repository"), @Tag("integration")})
class UniversalGroupTrainingDAOIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);

    @Autowired
    private UniversalGroupTrainingDAO universalGroupTrainingDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestDocumentUtilComponent testUtil;

    private TrainingTypeDocument trainingType;
    private String trainingTypeName;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        trainingType = testUtil.saveAndGetTestTrainingType();
        trainingTypeName = trainingType.getName();

        testUtil.createTestGroupTraining("2020-10-01T11:30", "2020-10-01T12:00");
        testUtil.createTestGroupTraining(trainingType, "2020-10-01T12:30", "2020-10-01T13:00");
        testUtil.createTestGroupTraining("2020-10-02T12:30", "2020-10-02T13:00");
        testUtil.createTestGroupTraining("2020-10-03T13:30", "2020-10-03T14:00");
        testUtil.createTestGroupTraining("2020-10-04T14:30", "2020-10-04T15:00");
        testUtil.createTestGroupTraining("2020-10-05T15:30", "2020-10-05T16:00");
        testUtil.createTestGroupTraining("2020-10-06T16:30", "2020-10-06T17:00");
        testUtil.createTestGroupTraining(trainingType, "2020-10-06T19:30", "2020-10-06T20:00");
        testUtil.createTestGroupTraining("2020-10-07T17:30", "2020-10-07T18:00");
        testUtil.createTestGroupTraining("2020-10-08T18:30", "2020-10-08T19:00");
        testUtil.createTestGroupTraining(trainingType, "2020-10-08T20:30", "2020-10-08T22:00");
        testUtil.createTestGroupTraining("2020-10-09T19:30", "2020-10-09T20:00");
        testUtil.createTestGroupTraining("2020-10-10T20:30", "2020-10-10T21:00");
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GroupTrainingDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @CsvSource({
            "2020-10-01,2020-11-03,3",
            "2020-10-01,2020-10-05,1",
            "2020-10-09,2020-10-10,0",
            "2020-10-04,2020-10-07,1",
            "2020-10-03,2020-10-06,1",
            "2020-10-04,2020-10-08,2",
            "2020-10-01,2020-10-05,1",
    })
    void shouldReturnGroupTrainingsWithSpecifiedTrainingTypeOnly(
            String startDateStr,
            String endDateStr,
            int numberOfTrainingsWithinDates
    ) {
        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTIme = LocalDateTime.of(endDate, LocalTime.MAX);

        List<GroupTrainingDocument> groupTrainingList = universalGroupTrainingDAO
                .getGroupTrainingDocumentsByTrainingType(startDateTime, endDateTIme, trainingType);

        assertThat(groupTrainingList.size()).isEqualTo(numberOfTrainingsWithinDates);

        long numberOfTrainings = groupTrainingList
                .stream()
                .map(GroupTrainingDocument::getTraining)
                .filter(training -> training.getName().equals(trainingTypeName)).count();

        assertThat(numberOfTrainings).isEqualTo(numberOfTrainingsWithinDates);
    }

    @ParameterizedTest
    @CsvSource({
            "2020-10-01,2020-11-03,13",
            "2020-10-01,2020-10-05,6",
            "2020-10-09,2020-10-10,2",
            "2020-10-04,2020-10-07,5",
            "2020-10-03,2020-10-06,5",
            "2020-10-04,2020-10-08,7",
            "2020-10-01,2020-10-05,6",
    })
    void shouldReturnGroupTrainingsWithinDates(
            String startDateStr,
            String endDateStr,
            int numberOfTrainingsWithinDates
    ) {
        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTIme = LocalDateTime.of(endDate, LocalTime.MAX);

        List<GroupTrainingDocument> groupTrainingList = universalGroupTrainingDAO
                .getGroupTrainingDocuments(startDateTime, endDateTIme);

        assertThat(groupTrainingList.size()).isEqualTo(numberOfTrainingsWithinDates);
    }
}