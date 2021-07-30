package com.healthy.gym.trainings.controller.group.training.manager.unitTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.group.training.GroupTrainingManagerController;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.CreateGroupTrainingRequest;
import com.healthy.gym.trainings.service.GroupTrainingService;
import com.healthy.gym.trainings.shared.BasicUserInfoDTO;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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

@WebMvcTest(GroupTrainingManagerController.class)
class WhenCreateGroupTrainingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private GroupTrainingService groupTrainingService;

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
        managerToken = tokenFactory.getMangerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        CreateGroupTrainingRequest request = getTestRequest();
        ObjectMapper mapper = new ObjectMapper();

        requestContent = mapper.writeValueAsString(request);

        CreateGroupTrainingRequest invalidRequest = getInvalidTestRequest();
        invalidRequestContent = mapper.writeValueAsString(invalidRequest);
        uri = new URI("/group");

        trainingID = UUID.randomUUID().toString();

        validResponse = new GroupTrainingDTO(
                trainingID,
                "Test training title",
                "2020-10-10T16:00",
                "2020-10-10T16:30",
                false,
                "Sala nr 2",
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

    private CreateGroupTrainingRequest getTestRequest() {
        CreateGroupTrainingRequest request = new CreateGroupTrainingRequest();
        request.setTrainingTypeId(UUID.randomUUID().toString());
        request.setTrainerIds(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        request.setStartDate("2020-10-10T16:00");
        request.setEndDate("2020-10-10T16:30");
        request.setLocationId(UUID.randomUUID().toString());
        request.setLimit(20);
        return request;
    }

    private CreateGroupTrainingRequest getInvalidTestRequest() {
        CreateGroupTrainingRequest request = new CreateGroupTrainingRequest();
        request.setTrainingTypeId(UUID.randomUUID().toString());
        request.setTrainerIds(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        request.setStartDate("2020-10-10 16:00");
        request.setEndDate("2020-10-10 6:30");
        request.setLocationId("1");
        request.setLimit(0);
        return request;
    }

    @Nested
    class ShouldAcceptRequestWhenUserHasAdminOrManagerRoleAnd {

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

            when(groupTrainingService.createGroupTraining((CreateGroupTrainingRequest) any()))
                    .thenReturn(validResponse);

            String expectedMessage = messages.get("request.create.training.success");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.training.id").value(is(trainingID)),
                            jsonPath("$.training.title").value(is("Test training title")),
                            jsonPath("$.training.startDate").value(is("2020-10-10T16:00")),
                            jsonPath("$.training.endDate").value(is("2020-10-10T16:30")),
                            jsonPath("$.training.allDay").value(is(false)),
                            jsonPath("$.training.location").value(is("Sala nr 2")),
                            jsonPath("$.training.trainers[0].name").value(is("TestName")),
                            jsonPath("$.training.trainers[0].surname").value(is("TestSurname")),
                            jsonPath("$.training.trainers[0].avatar").value(is("testAvatarUrl"))
                    ));
        }

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
                                    .value(is(messages.get("field.invalid.date.time.format"))),
                            jsonPath("$.errors.locationId")
                                    .value(is(messages.get("exception.invalid.id.format"))),
                            jsonPath("$.errors.limit")
                                    .value(is(messages.get("field.training.limit.min.value"))),
                            jsonPath("$.errors.startDate")
                                    .value(is(messages.get("field.invalid.date.time.format")))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowStartDateAfterEndDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(StartDateAfterEndDateException.class)
                    .when(groupTrainingService)
                    .createGroupTraining((CreateGroupTrainingRequest) any());

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.start.date.after.end.date");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(StartDateAfterEndDateException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainerNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainerNotFoundException.class)
                    .when(groupTrainingService)
                    .createGroupTraining((CreateGroupTrainingRequest) any());

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.create.group.training.trainer.not.found");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(TrainerNotFoundException.class)
                    );
        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowLocationNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(LocationNotFoundException.class)
                    .when(groupTrainingService)
                    .createGroupTraining((CreateGroupTrainingRequest) any());

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.location.not.found");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(LocationNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainingTypeNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainingTypeNotFoundException.class)
                    .when(groupTrainingService)
                    .createGroupTraining((CreateGroupTrainingRequest) any());

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.create.group.training.trainingType.not.found");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(TrainingTypeNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowLocationOccupiedExceptionWhenLocationIsOccupied(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(LocationOccupiedException.class)
                    .when(groupTrainingService)
                    .createGroupTraining((CreateGroupTrainingRequest) any());

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.create.group.training.location.occupied");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(LocationOccupiedException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainerOccupiedExceptionWhenTrainerIsOccupied(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(TrainerOccupiedException.class)
                    .when(groupTrainingService)
                    .createGroupTraining((CreateGroupTrainingRequest) any());

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.create.group.training.trainer.occupied");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(TrainerOccupiedException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerErrorWhenIllegalStateExceptionOccurred(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(groupTrainingService)
                    .createGroupTraining((CreateGroupTrainingRequest) any());


            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.internal.error");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
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
