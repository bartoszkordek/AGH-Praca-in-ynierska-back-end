package com.healthy.gym.trainings.controller.group.training.manager.unit.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.group.training.ManagerGroupTrainingController;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingService;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
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

@WebMvcTest(ManagerGroupTrainingController.class)
@ActiveProfiles(value = "test")
class UpdateGroupTrainingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private ManagerGroupTrainingService managerGroupTrainingService;

    private String managerToken;
    private String adminToken;
    private String userToken;
    private URI uri;
    private GroupTrainingDTO validResponse;
    private String invalidRequestContent;
    private String trainingID;
    private String requestContent;
    private ManagerGroupTrainingRequest groupTrainingRequest;

    @BeforeEach
    void setUp() throws URISyntaxException, JsonProcessingException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getManagerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        trainingID = UUID.randomUUID().toString();
        uri = new URI("/group/" + trainingID);

        groupTrainingRequest = getTestRequest();
        ObjectMapper mapper = new ObjectMapper();

        requestContent = mapper.writeValueAsString(groupTrainingRequest);

        ManagerGroupTrainingRequest invalidRequest = getInvalidTestRequest();
        invalidRequestContent = mapper.writeValueAsString(invalidRequest);

        validResponse = getValidResponse();
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

    private GroupTrainingDTO getValidResponse() {
        var groupTrainingDTO = new GroupTrainingDTO(
                trainingID,
                "Test training title",
                "2020-10-10T16:00",
                "2020-10-10T16:30",
                false,
                "Room no 2",
                List.of(
                        new BasicUserInfoDTO(
                                "138f6113-8acc-41f7-9427-7c5f9c63cbae",
                                "TestName",
                                "TestSurname",
                                "testAvatarUrl"
                        )
                )
        );
        groupTrainingDTO.setBasicList(
                List.of(
                        new BasicUserInfoDTO(
                                "561840b4-b2ad-4249-b344-e82240b88110",
                                "TestName1",
                                "TestSurname1",
                                "testAvatarUrl1"
                        )
                )
        );
        groupTrainingDTO.setReserveList(
                List.of(
                        new BasicUserInfoDTO(
                                "b98e281f-1a25-478f-9451-2e1f54931330",
                                "TestName2",
                                "TestSurname2",
                                "testAvatarUrl2"
                        )
                )
        );
        return groupTrainingDTO;
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

        RequestBuilder request = getValidRequest(managerToken, testedLocale);
        when(managerGroupTrainingService.updateGroupTraining(trainingID, groupTrainingRequest))
                .thenReturn(validResponse);
        String expectedMessage = messages.get("request.update.training.success");

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
                                jsonPath("$.training.id").value(is(trainingID)),
                                jsonPath("$.training.title").value(is("Test training title")),
                                jsonPath("$.training.startDate").value(is("2020-10-10T16:00")),
                                jsonPath("$.training.endDate").value(is("2020-10-10T16:30")),
                                jsonPath("$.training.allDay").value(is(false)),
                                jsonPath("$.training.location").value(is("Room no 2"))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.training.trainers[0].userId")
                                        .value(is("138f6113-8acc-41f7-9427-7c5f9c63cbae")),
                                jsonPath("$.training.trainers[0].name").value(is("TestName")),
                                jsonPath("$.training.trainers[0].surname").value(is("TestSurname")),
                                jsonPath("$.training.trainers[0].avatar").value(is("testAvatarUrl"))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.training.participants.basicList[0].userId")
                                        .value(is("561840b4-b2ad-4249-b344-e82240b88110")),
                                jsonPath("$.training.participants.basicList[0].name")
                                        .value(is("TestName1")),
                                jsonPath("$.training.participants.basicList[0].surname")
                                        .value(is("TestSurname1")),
                                jsonPath("$.training.participants.basicList[0].avatar")
                                        .value(is("testAvatarUrl1"))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.training.participants.reserveList[0].userId")
                                        .value(is("b98e281f-1a25-478f-9451-2e1f54931330")),
                                jsonPath("$.training.participants.reserveList[0].name")
                                        .value(is("TestName2")),
                                jsonPath("$.training.participants.reserveList[0].surname")
                                        .value(is("TestSurname2")),
                                jsonPath("$.training.participants.reserveList[0].avatar")
                                        .value(is("testAvatarUrl2"))
                        )
                );
    }

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .content(requestContent)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Nested
    class ShouldAcceptRequestWhenUserHasAdminOrManagerRoleAndShouldThrow {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(invalidRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("request.bind.exception");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(
                            matchAll(
                                    status().isBadRequest(),
                                    content().contentType(MediaType.APPLICATION_JSON),
                                    jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                    jsonPath("$.message").value(is(expectedMessage))
                            )
                    )
                    .andExpect(
                            matchAll(
                                    jsonPath("$.errors").value(is(notNullValue())),
                                    jsonPath("$.errors.endDate")
                                            .value(is(messages.get("exception.invalid.date.time.format"))),
                                    jsonPath("$.errors.locationId")
                                            .value(is(messages.get("exception.invalid.id.format"))),
                                    jsonPath("$.errors.limit")
                                            .value(is(messages.get("field.training.limit.min.value"))),
                                    jsonPath("$.errors.startDate")
                                            .value(is(messages.get("exception.invalid.date.time.format")))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowLocationNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(LocationNotFoundException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);
            RequestBuilder request = getValidRequest(managerToken, testedLocale);
            String expectedMessage = messages.get("exception.location.not.found");

            performRequestAndTestErrorResponse(request, expectedMessage, LocationNotFoundException.class);
        }

        private void performRequestAndTestErrorResponse(
                RequestBuilder request,
                String expectedMessage,
                Class<? extends Exception> expectedException
        ) throws Exception {
            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(expectedException)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowLocationOccupiedException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(LocationOccupiedException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);
            RequestBuilder request = getValidRequest(managerToken, testedLocale);
            String expectedMessage = messages.get("exception.create.group.training.location.occupied");

            performRequestAndTestErrorResponse(request, expectedMessage, LocationOccupiedException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowNotExistingGroupTrainingException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(NotExistingGroupTrainingException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);
            RequestBuilder request = getValidRequest(managerToken, testedLocale);
            String expectedMessage = messages.get("exception.group.training.not.found");

            performRequestAndTestErrorResponse(request, expectedMessage, NotExistingGroupTrainingException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowPastDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(PastDateException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);
            RequestBuilder request = getValidRequest(managerToken, testedLocale);
            String expectedMessage = messages.get("exception.past.date");

            performRequestAndTestErrorResponse(request, expectedMessage, PastDateException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowStartDateAfterEndDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(StartDateAfterEndDateException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);
            RequestBuilder request = getValidRequest(adminToken, testedLocale);
            String expectedMessage = messages.get("exception.start.date.after.end.date");

            performRequestAndTestErrorResponse(request, expectedMessage, StartDateAfterEndDateException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainerNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainerNotFoundException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);
            RequestBuilder request = getValidRequest(adminToken, testedLocale);
            String expectedMessage = messages.get("exception.create.group.training.trainer.not.found");

            performRequestAndTestErrorResponse(request, expectedMessage, TrainerNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainingTypeNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainingTypeNotFoundException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);
            RequestBuilder request = getValidRequest(managerToken, testedLocale);
            String expectedMessage = messages.get("exception.create.group.training.trainingType.not.found");

            performRequestAndTestErrorResponse(request, expectedMessage, TrainingTypeNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainerOccupiedException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainerOccupiedException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);
            RequestBuilder request = getValidRequest(managerToken, testedLocale);
            String expectedMessage = messages.get("exception.create.group.training.trainer.occupied");

            performRequestAndTestErrorResponse(request, expectedMessage, TrainerOccupiedException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerErrorWhenIllegalStateExceptionOccurred(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(managerGroupTrainingService)
                    .updateGroupTraining(trainingID, groupTrainingRequest);

            RequestBuilder request = getValidRequest(adminToken, testedLocale);
            String expectedMessage = messages.get("exception.internal.error");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(IllegalStateException.class)
                    );
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
                    .put(uri)
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
                    .put(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestContent);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
