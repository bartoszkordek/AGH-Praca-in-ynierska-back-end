package com.healthy.gym.trainings;

import com.healthy.gym.trainings.config.EmailConfig;
import com.healthy.gym.trainings.db.GroupTrainingReviewsDbRepository;
import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.mock.TrainingsServiceGroupGroupTrainingsReviewsImpl;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsModel;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsUpdateModel;
import com.healthy.gym.trainings.service.GroupTrainingsService;
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
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class TrainingsServiceGroupGroupTrainingsReviewsTest {

    private final String validReviewId = "111111111111111111111111";
    private final String invalidReviewId = "999999999999999999999999";
    private final String validClientId = "Client123";
    private final String invalidClientId = "InvalidClient";
    private final String sampleTrainingName = "Zumba";
    private final String sampleDate = "2020-01-01";
    private final int sampleStars = 5;
    private final String sampleText = "good";

    private final String validReviewIdToUpdate = "222222222222222222222222";
    private String sampleTrainingNameToUpdate = "Spinning";
    private int sampleStarsToUpdate = 4;
    private String sampleTextToUpdate = "OK";
    private String sampleDateToUpdate = "2021-11-03";

    private GroupTrainingsReviewsUpdateModel groupTrainingsReviewsUpdateModel;

    @TestConfiguration
    static class TrainingsReviewServiceImplTestContextConfiguration {

        @Bean
        public GroupTrainingsService trainingsReviewsService() {
            return new TrainingsServiceGroupGroupTrainingsReviewsImpl(null, null, null);
        }
        @Bean
        EmailConfig emailConfig(){
            return new EmailConfig();
        }
    }

    @Autowired
    GroupTrainingsService groupTrainingsService;

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

        GroupTrainingsReviews groupTrainingsReviewsToUpdate = new GroupTrainingsReviews(sampleTrainingNameToUpdate,
                validClientId,
                sampleDateToUpdate,
                sampleStarsToUpdate,
                sampleTextToUpdate);
        groupTrainingsReviewsToUpdate.setId(validReviewIdToUpdate);
        groupTrainingsReviewsList.add(groupTrainingsReviewsToUpdate);

        when(groupTrainingReviewsDbRepository.getGroupTrainingReviews())
                .thenReturn(groupTrainingsReviewsList);
        when(groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(validReviewId))
                .thenReturn(true);
        when(groupTrainingReviewsDbRepository.getGroupTrainingsReviewById(validReviewId))
                .thenReturn(groupTrainingsReviews);

        GroupTrainingsReviewsModel groupTrainingsReviewsModel = new GroupTrainingsReviewsModel(sampleTrainingNameToUpdate,
                sampleStarsToUpdate, sampleTextToUpdate);


        int starsToUpdate = 5;
        String textToUpdate = "Good";

        GroupTrainingsReviews groupTrainingsReviewsSampleAfterUpdate = new GroupTrainingsReviews(sampleTrainingNameToUpdate,
                validClientId,
                sampleDateToUpdate,
                starsToUpdate,
                textToUpdate);
        groupTrainingsReviewsSampleAfterUpdate.setId(validReviewIdToUpdate);

        groupTrainingsReviewsUpdateModel = new GroupTrainingsReviewsUpdateModel(starsToUpdate, textToUpdate);
        when(groupTrainingReviewsDbRepository.updateGroupTrainingsReview(groupTrainingsReviewsUpdateModel, validReviewIdToUpdate))
                .thenReturn(groupTrainingsReviewsSampleAfterUpdate);

        when(groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(validReviewIdToUpdate))
                .thenReturn(true);
        when(groupTrainingReviewsDbRepository.isClientReviewOwner(validReviewIdToUpdate, validClientId))
                .thenReturn(true);
    }

    @Test
    public void shouldReturnFirstReviewTrainingName_whenValidRequest() {
        assertThat(groupTrainingsService.getGroupTrainingReviews().get(0).getTrainingName())
                .isEqualTo("Zumba");
    }

    @Test
    public void shouldReturnValidReviewTrainingName_whenValidReviewId() throws NotExistingGroupTrainingReviewException {
        assertThat(groupTrainingsService.getGroupTrainingReviewById(validReviewId).getTrainingName())
                .isEqualTo("Zumba");
    }

    @Test(expected = NotExistingGroupTrainingReviewException.class)
    public void shouldReturnNotExistingGroupTrainingException_whenInvalidReviewId() throws NotExistingGroupTrainingReviewException {
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(NotExistingGroupTrainingReviewException.class)
                .when(groupTrainingsService)
                .getGroupTrainingReviewById(invalidReviewId);
        groupTrainingsService.getGroupTrainingReviewById(invalidReviewId);
    }

    @Test
    public void shouldUpdateReview_whenValidRequest() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //when
        int starsToUpdate = 5;
        String textToUpdate = "Good";
        GroupTrainingsReviews groupTrainingsReviewsSampleAfterUpdate = new GroupTrainingsReviews(sampleTrainingNameToUpdate,
                validClientId,
                sampleDateToUpdate,
                starsToUpdate,
                textToUpdate);
        groupTrainingsReviewsSampleAfterUpdate.setId(validReviewIdToUpdate);

        //then
        assertThat(groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId).getId())
                .isEqualTo(groupTrainingsReviewsSampleAfterUpdate.getId());
        assertThat(groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId).getTrainingName())
                .isEqualTo(groupTrainingsReviewsSampleAfterUpdate.getTrainingName());
        assertThat(groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId).getClientId())
                .isEqualTo(groupTrainingsReviewsSampleAfterUpdate.getClientId());
        assertThat(groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId).getDate())
                .isEqualTo(groupTrainingsReviewsSampleAfterUpdate.getDate());
        assertThat(groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId).getStars())
                .isEqualTo(groupTrainingsReviewsSampleAfterUpdate.getStars());
        assertThat(groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId).getText())
                .isEqualTo(groupTrainingsReviewsSampleAfterUpdate.getText());
    }

    @Test(expected = NotAuthorizedClientException.class)
    public void shouldNotUpdateReview_whenInvalidClientId() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //when
        int starsToUpdate = 5;
        String textToUpdate = "Good";
        GroupTrainingsReviews groupTrainingsReviewsSampleAfterUpdate = new GroupTrainingsReviews(sampleTrainingNameToUpdate,
                invalidClientId,
                sampleDateToUpdate,
                starsToUpdate,
                textToUpdate);
        groupTrainingsReviewsSampleAfterUpdate.setId(validReviewIdToUpdate);

        //then
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(NotAuthorizedClientException.class)
                .when(groupTrainingsService)
                .updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId);
        groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId);
    }

    @Test(expected = NotExistingGroupTrainingReviewException.class)
    public void shouldNotUpdateReview_whenInvalidReviewId() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //when
        int starsToUpdate = 5;
        String textToUpdate = "Good";
        GroupTrainingsReviews groupTrainingsReviewsSampleAfterUpdate = new GroupTrainingsReviews(invalidReviewId,
                validClientId,
                sampleDateToUpdate,
                starsToUpdate,
                textToUpdate);
        groupTrainingsReviewsSampleAfterUpdate.setId(validReviewIdToUpdate);

        //then
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(NotExistingGroupTrainingReviewException.class)
                .when(groupTrainingsService)
                .updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,invalidReviewId,validClientId);
        groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,invalidReviewId,validClientId);
    }

    @Test(expected = StarsOutOfRangeException.class)
    public void shouldNotUpdateReview_whenInvalidStars() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //when
        int starsToUpdate = 50;
        String textToUpdate = "Good";
        GroupTrainingsReviews groupTrainingsReviewsSampleAfterUpdate = new GroupTrainingsReviews(sampleTrainingNameToUpdate,
                validClientId,
                sampleDateToUpdate,
                starsToUpdate,
                textToUpdate);
        groupTrainingsReviewsSampleAfterUpdate.setId(validReviewIdToUpdate);

        //then
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(StarsOutOfRangeException.class)
                .when(groupTrainingsService)
                .updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId);
        groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel,validReviewIdToUpdate,validClientId);
    }
}
