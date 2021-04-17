package com.healthy.gym.trainings;

import com.healthy.gym.trainings.db.GroupTrainingReviewsDbRepository;
import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import com.healthy.gym.trainings.mock.TrainingsServiceGroupTrainingsImpl;
import com.healthy.gym.trainings.mock.TrainingsServiceGroupTrainingsReviewsImpl;
import com.healthy.gym.trainings.service.TrainingsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TrainingsServiceGroupTrainingsReviewsTest {

    private final String validReviewId = "111111111111111111111111";
    private final String invalidReviewId = "999999999999999999999999";
    private final String validClientId = "Client123";
    private final String sampleTrainingName = "Zumba";
    private final String sampleDate = "2020-01-01";
    private final int sampleStars = 5;
    private final String sampleText = "good";

    @TestConfiguration
    static class TrainingsReviewServiceImplTestContextConfiguration {

        @Bean
        public TrainingsService trainingsReviewsService() {
            return new TrainingsServiceGroupTrainingsReviewsImpl(null, null, null);
        }
    }

    @Autowired
    TrainingsService trainingsService;

    @MockBean
    private GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository;

    @Before
    public void setUp() {
        List<GroupTrainingsReviews> groupTrainingsReviewsList = new ArrayList<>();
        GroupTrainingsReviews groupTrainingsReviews = new GroupTrainingsReviews(sampleTrainingName,
                validClientId,
                sampleDate,
                sampleStars,
                sampleText);
        groupTrainingsReviews.setId(validReviewId);
        groupTrainingsReviewsList.add(groupTrainingsReviews);

        when(groupTrainingReviewsDbRepository.getGroupTrainingReviews())
                .thenReturn(groupTrainingsReviewsList);
    }

    @Test
    public void shouldReturnFirstReviewTrainingName_whenValidRequest() {
        assertThat(trainingsService.getGroupTrainingReviews().get(0).getTrainingName())
                .isEqualTo("Zumba");
    }
}
