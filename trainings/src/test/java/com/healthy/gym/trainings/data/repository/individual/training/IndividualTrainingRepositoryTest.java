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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.List;

import static com.healthy.gym.trainings.utils.DateParser.parseDate;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@Tags({@Tag("repository"), @Tag("integration")})
class IndividualTrainingRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @Autowired
    private IndividualTrainingRepository individualTrainingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestDocumentUtilComponent testUtil;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {

        List<String[]> data = List.of(
                new String[]{"2021-01-01T10:10", "2021-01-01T11:00", "false"},
                new String[]{"2021-01-02T10:10", "2021-01-02T11:00", "true"},
                new String[]{"2021-01-03T10:10", "2021-01-03T11:00", "false"},
                new String[]{"2021-01-04T10:10", "2021-01-04T11:00", "true"},
                new String[]{"2021-01-05T10:10", "2021-01-05T11:00", "true"},

                new String[]{"2021-01-06T10:10", "2021-01-06T11:00", "false"},
                new String[]{"2021-01-06T20:10", "2021-01-06T21:00", "true"},
                new String[]{"2021-01-07T10:10", "2021-01-07T11:00", "true"},
                new String[]{"2021-01-08T10:10", "2021-01-08T11:00", "false"},
                new String[]{"2021-01-09T10:10", "2021-01-09T11:00", "true"},

                new String[]{"2021-01-10T10:10", "2021-01-10T11:00", "true"},
                new String[]{"2021-01-10T12:10", "2021-01-10T12:00", "true"}
        );

        data.forEach(
                strings -> testUtil
                        .saveAndGetTestIndividualTraining(
                                strings[0],
                                strings[1],
                                Boolean.parseBoolean(strings[2])
                        )
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
            "2021-01-01,2021-01-09,0,5,5",
            "2021-01-01,2021-01-09,1,5,1",
            "2021-01-01,2021-01-09,2,5,0",
            "2021-01-05,2021-01-06,0,4,2",
            "2021-01-03,2021-01-05,1,4,0",
            "2021-01-05,2021-01-10,1,4,2",
            "2021-01-01,2021-01-05,0,10,3",
            "2021-01-09,2021-01-10,0,2,2",
            "2021-01-09,2021-01-10,1,2,1",
            "2021-01-04,2021-01-07,0,5,4",
            "2021-01-03,2021-01-06,1,3,0",
            "2021-01-03,2021-01-06,0,3,3",
            "2021-01-04,2021-01-08,1,2,2",
    })
    void shouldReturnExpectedNumberOfTrainingsWithinDatesWhenAcceptedIsTrueForProvidedPageAndItsSize(
            String startDateStr,
            String endDateStr,
            int page,
            int size,
            long expectedNumberOfTrainingsWithinDates
    ) {
        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTIme = LocalDateTime.of(endDate, LocalTime.MAX);

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());

        var groupTrainingList = individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBeforeAndAcceptedIsTrue(
                        startDateTime, endDateTIme, pageable);

        List<IndividualTrainingDocument> list = groupTrainingList.getContent();

        assertThat(list.size()).isEqualTo(expectedNumberOfTrainingsWithinDates);
    }

    @ParameterizedTest
    @CsvSource({
            "2021-01-01,2021-01-09,0,5,5,10",
            "2021-01-01,2021-01-09,1,5,5,10",
            "2021-01-01,2021-01-09,2,5,0,10",
            "2021-01-05,2021-01-06,0,4,3,3",
            "2021-01-03,2021-01-05,1,4,0,3",
            "2021-01-05,2021-01-10,0,4,4,8",
            "2021-01-05,2021-01-10,1,4,4,8",
            "2021-01-01,2021-01-05,0,10,5,5",
            "2021-01-09,2021-01-10,0,2,2,3",
            "2021-01-09,2021-01-10,1,2,1,3",
    })
    void shouldReturnExpectedNumberOfTrainingsWithinDatesForProvidedPageAndItsSize(
            String startDateStr,
            String endDateStr,
            int page,
            int size,
            long expectedNumberOfTrainingsWithinDates,
            long expectedTotalNumber
    ) {
        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTIme = LocalDateTime.of(endDate, LocalTime.MAX);

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());

        var groupTrainingList = individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(startDateTime, endDateTIme, pageable);

        List<IndividualTrainingDocument> list = groupTrainingList.getContent();

        assertThat(list.size()).isEqualTo(expectedNumberOfTrainingsWithinDates);
        assertThat(groupTrainingList.getTotalElements()).isEqualTo(expectedTotalNumber);
    }
}