package com.healthy.gym.trainings.data.repository.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tags({@Tag("repository"), @Tag("integration")})
class UserIndividualTrainingDAOTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);

    @Autowired
    private UserIndividualTrainingDAO userIndividualTrainingDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestDocumentUtilComponent testUtil;

    private UserDocument userDocument;
    private String trainingTypeName;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {

        List<String[]> data = List.of(
                new String[]{"2021-01-01T10:10", "2021-01-01T11:00"},
                new String[]{"2021-01-02T10:10", "2021-01-02T11:00"},
                new String[]{"2021-01-03T10:10", "2021-01-03T11:00"},
                new String[]{"2021-01-04T10:10", "2021-01-04T11:00"},
                new String[]{"2021-01-05T10:10", "2021-01-05T11:00"},

                new String[]{"2021-01-06T10:10", "2021-01-06T11:00"},
                new String[]{"2021-01-06T20:10", "2021-01-06T21:00"},
                new String[]{"2021-01-07T10:10", "2021-01-07T11:00"},
                new String[]{"2021-01-08T10:10", "2021-01-08T11:00"},
                new String[]{"2021-01-09T10:10", "2021-01-09T11:00"},

                new String[]{"2021-01-10T10:10", "2021-01-10T11:00"},
                new String[]{"2021-01-10T12:10", "2021-01-10T12:00"}
        );
        data.forEach(strings -> testUtil.saveAndGetTestIndividualTraining(strings[0], strings[1]));

        userDocument = testUtil.saveAndGetTestUser();
        testUtil.saveAndGetTestIndividualTraining(
                "2021-01-05T10:00",
                "2021-01-05T12:00",
                userDocument
        );
        testUtil.saveAndGetTestIndividualTraining(
                "2021-01-09T10:00",
                "2021-01-09T12:00",
                userDocument
        );
        testUtil.saveAndGetTestIndividualTraining(
                "2021-01-09T13:00",
                "2021-01-09T14:00",
                userDocument
        );
        testUtil.saveAndGetTestIndividualTraining(
                "2021-01-10T10:00",
                "2021-01-10T12:00",
                userDocument
        );
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @CsvSource({
            "2021-01-01,2021-01-02,0",
            "2021-01-05,2021-01-06,1",
            "2021-01-01,2021-01-10,4",
            "2021-01-10,2021-01-10,1",
            "2021-01-06,2021-01-10,3",
            "2021-01-02,2021-01-06,1",
    })
    void shouldReturnExpectedNumberOfTrainingsWithinDatesWhenAcceptedIsTrueForProvidedPageAndItsSize(
            String startDateStr,
            String endDateStr,
            long expectedNumberOfTrainingsWithinDates
    ) {
        var list = mongoTemplate.findAll(IndividualTrainingDocument.class);
        System.out.println(list.size());

        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTIme = LocalDateTime.of(endDate, LocalTime.MAX);

        var groupTrainingList = userIndividualTrainingDAO
                .findAllIndividualTrainingsWithDatesByUserDocument(userDocument, startDateTime, endDateTIme);

        assertThat(groupTrainingList.size()).isEqualTo(expectedNumberOfTrainingsWithinDates);
    }

}