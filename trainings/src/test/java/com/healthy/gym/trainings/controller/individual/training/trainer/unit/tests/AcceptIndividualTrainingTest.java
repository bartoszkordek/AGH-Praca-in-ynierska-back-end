package com.healthy.gym.trainings.controller.individual.training.trainer.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.individual.training.TrainerIndividualTrainerController;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.AlreadyAcceptedIndividualTrainingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.service.individual.training.TrainerIndividualTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
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
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerIndividualTrainerController.class)
class AcceptIndividualTrainingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private TrainerIndividualTrainingService trainerIndividualTrainingService;

    private String adminToken;
    private String employeeToken;
    private String managerToken;
    private String trainerId;
    private String trainerToken;
    private String userToken;
    private String trainingId;
    private String locationId;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        adminToken = tokenFactory.getAdminToken();
        employeeToken = tokenFactory.getEmployeeToken();
        managerToken = tokenFactory.getManagerToken();
        trainerId = UUID.randomUUID().toString();
        trainerToken = tokenFactory.getTrainerToken(trainerId);
        userToken = tokenFactory.getUserToken();

        trainingId = UUID.randomUUID().toString();
        locationId = UUID.randomUUID().toString();

        uri = getUri(trainerId, trainingId, locationId);
    }

    private URI getUri(String trainerId, String trainingId, String locationId) throws URISyntaxException {
        return new URI("/individual/trainer/" + trainerId + "/training/"
                + trainingId + "?locationId=" + locationId);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptIndividualTraining(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        when(trainerIndividualTrainingService.acceptIndividualTraining(trainerId, trainingId, locationId))
                .thenReturn(getIndividualTrainingDTO());
        RequestBuilder request = getValidRequest(adminToken, testedLocale);
        String expectedMessage = messages.get("enrollment.individual.accepted");

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
                                jsonPath("$.training.trainers[0].userId")
                                        .value(is("4c9aa156-a3dd-4b25-8004-60831f82d8ae")),
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

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Nested
    class ShouldAcceptRequestAndShouldThrow {

        private RequestBuilder request;
        private String expectedMessage;

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowConstraintViolationException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI invalidUri = getUri("dasda", "3123", "2131das");

            RequestBuilder request = MockMvcRequestBuilders
                    .put(invalidUri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.constraint.violation");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(
                            matchAll(
                                    status().isBadRequest(),
                                    content().contentType(MediaType.APPLICATION_JSON),
                                    jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                    jsonPath("$.message").value(is(expectedMessage)),
                                    jsonPath("$.errors").value(is(notNullValue()))
                            )
                    ).andExpect(
                            matchAll(
                                    jsonPath("$.errors.userId")
                                            .value(is(messages.get("exception.invalid.id.format"))),
                                    jsonPath("$.errors.trainingId")
                                            .value(is(messages.get("exception.invalid.id.format"))),
                                    jsonPath("$.errors.locationId")
                                            .value(is(messages.get("exception.invalid.id.format")))
                            )
                    );
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
        void shouldThrowAccessDeniedException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(AccessDeniedException.class)
                    .when(trainerIndividualTrainingService)
                    .acceptIndividualTraining(trainerId, trainingId, locationId);
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

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
        void shouldThrowAlreadyAcceptedIndividualTrainingException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(AlreadyAcceptedIndividualTrainingException.class)
                    .when(trainerIndividualTrainingService)
                    .acceptIndividualTraining(trainerId, trainingId, locationId);
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.already.accepted.individual.training");

            performRequestAndTestErrorResponse(status().isBadRequest(), AlreadyAcceptedIndividualTrainingException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowLocationNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(LocationNotFoundException.class)
                    .when(trainerIndividualTrainingService)
                    .acceptIndividualTraining(trainerId, trainingId, locationId);
            request = getValidRequest(trainerToken, testedLocale);
            expectedMessage = messages.get("exception.location.not.found");

            performRequestAndTestErrorResponse(status().isNotFound(), LocationNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowLocationOccupiedException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(LocationOccupiedException.class)
                    .when(trainerIndividualTrainingService)
                    .acceptIndividualTraining(trainerId, trainingId, locationId);
            request = getValidRequest(trainerToken, testedLocale);
            expectedMessage = messages.get("exception.create.group.training.location.occupied");

            performRequestAndTestErrorResponse(status().isBadRequest(), LocationOccupiedException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowNotExistingIndividualTrainingException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(NotExistingIndividualTrainingException.class)
                    .when(trainerIndividualTrainingService)
                    .acceptIndividualTraining(trainerId, trainingId, locationId);
            request = getValidRequest(trainerToken, testedLocale);
            expectedMessage = messages.get("exception.not.existing.individual.training");

            performRequestAndTestErrorResponse(status().isNotFound(), NotExistingIndividualTrainingException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowPastDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(PastDateException.class)
                    .when(trainerIndividualTrainingService)
                    .acceptIndividualTraining(trainerId, trainingId, locationId);
            request = getValidRequest(trainerToken, testedLocale);
            expectedMessage = messages.get("exception.past.date");

            performRequestAndTestErrorResponse(status().isBadRequest(), PastDateException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowUserNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(UserNotFoundException.class)
                    .when(trainerIndividualTrainingService)
                    .acceptIndividualTraining(trainerId, trainingId, locationId);
            request = getValidRequest(trainerToken, testedLocale);
            expectedMessage = messages.get("exception.not.found.user.id");

            performRequestAndTestErrorResponse(status().isNotFound(), UserNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerError(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(trainerIndividualTrainingService)
                    .acceptIndividualTraining(trainerId, trainingId, locationId);
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
        void whenUserTriesTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(userToken, testedLocale);
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
        void whenManagerTriesTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(managerToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenEmployeeTriesTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(employeeToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenAnotherTrainerTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            uri = getUri(UUID.randomUUID().toString(), trainingId, locationId);
            request = getValidRequest(trainerToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenAnotherUserWithoutTrainerRoleTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String userId = UUID.randomUUID().toString();
            userToken = tokenFactory.getUserToken(userId);

            uri = getUri(userId, trainingId, locationId);
            request = getValidRequest(userToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
