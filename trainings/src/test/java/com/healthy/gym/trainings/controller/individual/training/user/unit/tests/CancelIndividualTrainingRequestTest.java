package com.healthy.gym.trainings.controller.individual.training.user.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.individual.training.UserIndividualTrainingController;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.AlreadyCancelledIndividualTrainingException;
import com.healthy.gym.trainings.exception.IndividualTrainingHasBeenRejectedException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.UserIsNotParticipantException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.service.individual.training.UserIndividualTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserIndividualTrainingController.class)
@ActiveProfiles(value = "test")
class CancelIndividualTrainingRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private UserIndividualTrainingService userIndividualTrainingService;

    private String adminToken;
    private String employeeToken;
    private String managerToken;
    private String userId;
    private String trainingId;
    private String trainerToken;
    private String userToken;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        adminToken = tokenFactory.getAdminToken();
        employeeToken = tokenFactory.getEmployeeToken();
        managerToken = tokenFactory.getManagerToken();
        trainerToken = tokenFactory.getTrainerToken();
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        trainingId = UUID.randomUUID().toString();

        uri = getUri(userId);
    }

    private URI getUri(String userId) throws URISyntaxException {
        return new URI("/individual/user/" + userId + "/training/" + trainingId);
    }

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldCancelIndividualTrainingRequest(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        when(userIndividualTrainingService.cancelIndividualTrainingRequest(trainingId, userId))
                .thenReturn(getIndividualTrainingDTO());
        RequestBuilder request = getValidRequest(userToken, testedLocale);
        String expectedMessage = messages.get("enrollment.remove");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.message").value(is(expectedMessage))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.training.id")
                                        .value(is("74fe07a5-fb18-4006-a721-1a312dc2d398")),
                                jsonPath("$.training.title").value(is("Test training title")),
                                jsonPath("$.training.startDate").value(is("2020-10-10T16:00")),
                                jsonPath("$.training.endDate").value(is("2020-10-10T16:30")),
                                jsonPath("$.training.allDay").value(is(false)),
                                jsonPath("$.training.location").value(is("Room no 2"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.training.trainers[0].name").value(is("TestName")),
                                jsonPath("$.training.trainers[0].surname").value(is("TestSurname")),
                                jsonPath("$.training.trainers[0].avatar").value(is("testAvatarUrl"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.training.participants.basicList").isArray(),
                                jsonPath("$.training.participants.basicList[0].userId")
                                        .value(is("20fe07a5-fb18-4006-a721-1a312dc2d370")),
                                jsonPath("$.training.participants.basicList[0].name")
                                        .value(is("TestUserName")),
                                jsonPath("$.training.participants.basicList[0].surname")
                                        .value(is("TestUserSurname")),
                                jsonPath("$.training.participants.basicList[0].avatar")
                                        .value(is("TestAvatarUserUrl")),
                                jsonPath("$.training.participants.reserveList").isEmpty()
                        )
                );
    }

    private IndividualTrainingDTO getIndividualTrainingDTO() {
        var training = new IndividualTrainingDTO(
                "74fe07a5-fb18-4006-a721-1a312dc2d398",
                "Test training title",
                "2020-10-10T16:00",
                "2020-10-10T16:30",
                false,
                "Room no 2",
                List.of(
                        new BasicUserInfoDTO(
                                "4c9aa156-a3dd-4b25-8004-60831f82d8ae",
                                "TestName",
                                "TestSurname",
                                "testAvatarUrl"
                        )
                )
        );
        var participants = new ParticipantsDTO();
        participants.setBasicList(List.of(getTestUser()));
        training.setParticipants(participants);
        return training;
    }

    private BasicUserInfoDTO getTestUser() {
        return new BasicUserInfoDTO(
                "20fe07a5-fb18-4006-a721-1a312dc2d370",
                "TestUserName",
                "TestUserSurname",
                "TestAvatarUserUrl"
        );
    }

    @Nested
    class ShouldAcceptRequestAndShouldThrow {

        private RequestBuilder request;
        private String expectedMessage;

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowPastDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(PastDateException.class)
                    .when(userIndividualTrainingService)
                    .cancelIndividualTrainingRequest(trainingId, userId);
            request = getValidRequest(userToken, testedLocale);
            expectedMessage = messages.get("exception.past.date.enrollment.remove");

            performRequestAndTestErrorResponse(status().isBadRequest(), PastDateException.class);
        }

        private void performRequestAndTestErrorResponse(
                ResultMatcher resultMatcher,
                Class<? extends Exception> expectedException
        ) throws Exception {
            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(resultMatcher)
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(expectedException)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowAlreadyCancelledIndividualTrainingException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(AlreadyCancelledIndividualTrainingException.class)
                    .when(userIndividualTrainingService)
                    .cancelIndividualTrainingRequest(trainingId, userId);
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.already.cancelled.individual.training");

            performRequestAndTestErrorResponse(status().isBadRequest(),
                    AlreadyCancelledIndividualTrainingException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowIndividualTrainingHasBeenRejectedException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IndividualTrainingHasBeenRejectedException.class)
                    .when(userIndividualTrainingService)
                    .cancelIndividualTrainingRequest(trainingId, userId);
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.already.rejected.individual.training");

            performRequestAndTestErrorResponse(
                    status().isBadRequest(),
                    IndividualTrainingHasBeenRejectedException.class
            );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowNotExistingIndividualTrainingException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(NotExistingIndividualTrainingException.class)
                    .when(userIndividualTrainingService)
                    .cancelIndividualTrainingRequest(trainingId, userId);
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.not.existing.individual.training");

            performRequestAndTestErrorResponse(status().isNotFound(), NotExistingIndividualTrainingException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowUserNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(UserNotFoundException.class)
                    .when(userIndividualTrainingService)
                    .cancelIndividualTrainingRequest(trainingId, userId);
            request = getValidRequest(employeeToken, testedLocale);
            expectedMessage = messages.get("exception.not.found.user.id");

            performRequestAndTestErrorResponse(status().isNotFound(), UserNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowUserIsNotParticipantException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(UserIsNotParticipantException.class)
                    .when(userIndividualTrainingService)
                    .cancelIndividualTrainingRequest(trainingId, userId);
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.user.is.not.participant");

            performRequestAndTestErrorResponse(
                    status().isBadRequest(),
                    UserIsNotParticipantException.class
            );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerError(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(userIndividualTrainingService)
                    .cancelIndividualTrainingRequest(trainingId, userId);
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.internal.error");

            performRequestAndTestErrorResponse(status().isInternalServerError(), IllegalStateException.class);
        }
    }

    @Nested
    class ShouldRejectRequest {

        private RequestBuilder request;
        private String expectedMessage;

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenManagerTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(managerToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        private void performAndTestAccessDenied() throws Exception {
            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                    .andExpect(jsonPath("$.error").value(is("Forbidden")))
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenTrainerTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(trainerToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserTriesToPerformRequestInBehalfOfAnotherUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String userId = UUID.randomUUID().toString();
            uri = getUri(userId);
            request = getValidRequest(userToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
