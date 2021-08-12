package com.healthy.gym.trainings.controller.group.training.manager.unit.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.group.training.ManagerGroupTrainingController;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerGroupTrainingController.class)
class CreateGroupTrainingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private ManagerGroupTrainingService managerGroupTrainingService;

    private String managerToken;
    private String adminToken;
    private String userToken;
    private String requestContent;
    private String invalidRequestContent;
    private URI uri;
    private GroupTrainingDTO validResponse;
    private String trainingID;

    @BeforeEach
    void setUp() throws JsonProcessingException, URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getManagerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        ManagerGroupTrainingRequest request = getTestRequest();
        ObjectMapper mapper = new ObjectMapper();

        requestContent = mapper.writeValueAsString(request);

        ManagerGroupTrainingRequest invalidRequest = getInvalidTestRequest();
        invalidRequestContent = mapper.writeValueAsString(invalidRequest);
        uri = new URI("/group");

        trainingID = UUID.randomUUID().toString();

        validResponse = new GroupTrainingDTO(
                trainingID,
                "Test training title",
                "2020-10-10T16:00",
                "2020-10-10T16:30",
                false,
                "Room no 2",
                List.of(
                        new BasicUserInfoDTO(
                                UUID.randomUUID().toString(),
                                "TestName",
                                "TestSurname",
                                "testAvatarUrl"
                        )
                )
        );
    }

    private ManagerGroupTrainingRequest getTestRequest() {
        ManagerGroupTrainingRequest request = new ManagerGroupTrainingRequest();
        request.setTrainingTypeId(UUID.randomUUID().toString());
        request.setTrainerIds(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        request.setStartDate("2020-10-10T16:00");
        request.setEndDate("2020-10-10T16:30");
        request.setLocationId(UUID.randomUUID().toString());
        request.setLimit(20);
        return request;
    }

    private ManagerGroupTrainingRequest getInvalidTestRequest() {
        ManagerGroupTrainingRequest request = new ManagerGroupTrainingRequest();
        request.setTrainingTypeId(UUID.randomUUID().toString());
        request.setTrainerIds(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        request.setStartDate("2020-10-10 16:00");
        request.setEndDate("2020-10-10 6:30");
        request.setLocationId("1");
        request.setLimit(0);
        return request;
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldCreateGroupTraining(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .content(requestContent)
                .contentType(MediaType.APPLICATION_JSON);

        when(managerGroupTrainingService.createGroupTraining(any()))
                .thenReturn(validResponse);

        String expectedMessage = messages.get("request.create.training.success");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(is(expectedMessage)),
                        jsonPath("$.training.id").value(is(trainingID)),
                        jsonPath("$.training.title").value(is("Test training title")),
                        jsonPath("$.training.startDate").value(is("2020-10-10T16:00")),
                        jsonPath("$.training.endDate").value(is("2020-10-10T16:30")),
                        jsonPath("$.training.allDay").value(is(false)),
                        jsonPath("$.training.location").value(is("Room no 2")),
                        jsonPath("$.training.trainers[0].name").value(is("TestName")),
                        jsonPath("$.training.trainers[0].surname").value(is("TestSurname")),
                        jsonPath("$.training.trainers[0].avatar").value(is("testAvatarUrl"))
                ));
    }

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .post(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .content(requestContent)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Nested
    class ShouldAcceptRequestWhenUserHasAdminOrManagerRoleAndShouldThrow {

        private RequestBuilder request;
        private String expectedMessage;

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(invalidRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("request.bind.exception");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.errors").value(is(notNullValue())),
                            jsonPath("$.errors.endDate")
                                    .value(is(messages.get("exception.invalid.date.time.format"))),
                            jsonPath("$.errors.locationId")
                                    .value(is(messages.get("exception.invalid.id.format"))),
                            jsonPath("$.errors.limit")
                                    .value(is(messages.get("field.training.limit.min.value"))),
                            jsonPath("$.errors.startDate")
                                    .value(is(messages.get("exception.invalid.date.time.format")))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowLocationNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(LocationNotFoundException.class).when(managerGroupTrainingService).createGroupTraining(any());
            request = getValidRequest(managerToken, testedLocale);
            expectedMessage = messages.get("exception.location.not.found");

            performRequestAndTestErrorResponse(status().isNotFound(), LocationNotFoundException.class);
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
        void shouldThrowLocationOccupiedExceptionWhenLocationIsOccupied(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(LocationOccupiedException.class).when(managerGroupTrainingService).createGroupTraining(any());
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.create.group.training.location.occupied");

            performRequestAndTestErrorResponse(status().isBadRequest(), LocationOccupiedException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowPastDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(PastDateException.class).when(managerGroupTrainingService).createGroupTraining(any());
            request = getValidRequest(managerToken, testedLocale);
            expectedMessage = messages.get("exception.past.date");

            performRequestAndTestErrorResponse(status().isBadRequest(), PastDateException.class);
        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowStartDateAfterEndDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(StartDateAfterEndDateException.class).when(managerGroupTrainingService).createGroupTraining(any());
            request = getValidRequest(managerToken, testedLocale);
            expectedMessage = messages.get("exception.start.date.after.end.date");

            performRequestAndTestErrorResponse(status().isBadRequest(), StartDateAfterEndDateException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainerOccupiedExceptionWhenTrainerIsOccupied(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainerOccupiedException.class).when(managerGroupTrainingService).createGroupTraining(any());
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.create.group.training.trainer.occupied");

            performRequestAndTestErrorResponse(status().isBadRequest(), TrainerOccupiedException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainerNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainerNotFoundException.class).when(managerGroupTrainingService).createGroupTraining(any());
            request = getValidRequest(managerToken, testedLocale);
            expectedMessage = messages.get("exception.create.group.training.trainer.not.found");

            performRequestAndTestErrorResponse(status().isNotFound(), TrainerNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainingTypeNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainingTypeNotFoundException.class).when(managerGroupTrainingService).createGroupTraining(any());
            request = getValidRequest(managerToken, testedLocale);
            expectedMessage = messages.get("exception.create.group.training.trainingType.not.found");

            performRequestAndTestErrorResponse(status().isNotFound(), TrainingTypeNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerErrorWhenIllegalStateExceptionOccurred(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class).when(managerGroupTrainingService).createGroupTraining(any());
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.internal.error");

            performRequestAndTestErrorResponse(status().isInternalServerError(), IllegalStateException.class);
        }
    }

    @Nested
    class ShouldRejectRequest {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserDoesNotHaveAdminOrManagerRole(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.access.denied");

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
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestContent);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
