package com.healthy.gym.trainings.controller.group.training.universal.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.group.training.UniversalGroupTrainingController;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.service.group.training.UniversalGroupTrainingService;
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
import java.util.Objects;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestGroupTraining;
import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapGroupTrainingsDocumentToDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UniversalGroupTrainingController.class)
class GetGroupTrainingsWithParticipantsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private UniversalGroupTrainingService universalGroupTrainingService;

    private URI uri;
    private String startDate;
    private String endDate;
    private String userToken;

    @BeforeEach
    void setUp() throws URISyntaxException {
        userToken = tokenFactory.getUserToken();
        startDate = "2020-08-02";
        endDate = "2020-08-08";
        uri = new URI("/group?startDate=" + startDate + "&endDate=" + endDate);
    }

    private RequestBuilder getValidRequest(Locale locale) {
        return MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetGroupTrainingsWithParticipants(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        var training1 = getTestGroupTraining("2020-08-03T18:00", "2020-08-03T19:00");
        var training2 = getTestGroupTraining("2020-08-04T18:00", "2020-08-04T19:00");
        GroupTrainingDTO trainingDTO1 = mapGroupTrainingsDocumentToDTO(training1);
        GroupTrainingDTO trainingDTO2 = mapGroupTrainingsDocumentToDTO(training2);
        when(universalGroupTrainingService.getGroupTrainingsWithParticipants(startDate, endDate))
                .thenReturn(List.of(trainingDTO1, trainingDTO2));
        RequestBuilder request = getValidRequest(testedLocale);

        BasicUserInfoDTO trainer1 = trainingDTO1.getTrainers().get(0);
        BasicUserInfoDTO trainer2 = trainingDTO2.getTrainers().get(0);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.message").doesNotExist(),
                                jsonPath("$").isArray()
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$[0].id").value(is(trainingDTO1.getGroupTrainingId())),
                                jsonPath("$[0].title").value(is(trainingDTO1.getTitle())),
                                jsonPath("$[0].startDate").value(is(trainingDTO1.getStartDate())),
                                jsonPath("$[0].endDate").value(is(trainingDTO1.getEndDate())),
                                jsonPath("$[0].allDay").value(is(false)),
                                jsonPath("$[0].location").value(is(trainingDTO1.getLocation()))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$[0].trainers[0].name").value(is(trainer1.getName())),
                                jsonPath("$[0].trainers[0].surname").value(is(trainer1.getSurname())),
                                jsonPath("$[0].trainers[0].userId").value(is(trainer1.getUserId()))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$[0].participants.basicList").hasJsonPath(),
                                jsonPath("$[0].participants.basicList").isArray(),
                                jsonPath("$[0].participants.reserveList").hasJsonPath(),
                                jsonPath("$[0].participants.reserveList").isArray()
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$[1].id").value(is(trainingDTO2.getGroupTrainingId())),
                                jsonPath("$[1].title").value(is(trainingDTO2.getTitle())),
                                jsonPath("$[1].startDate").value(is(trainingDTO2.getStartDate())),
                                jsonPath("$[1].endDate").value(is(trainingDTO2.getEndDate())),
                                jsonPath("$[1].allDay").value(is(false)),
                                jsonPath("$[1].location").value(is(trainingDTO2.getLocation()))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$[1].trainers[0].name").value(is(trainer2.getName())),
                                jsonPath("$[1].trainers[0].surname").value(is(trainer2.getSurname())),
                                jsonPath("$[1].trainers[0].userId").value(is(trainer2.getUserId()))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$[1].participants.basicList").hasJsonPath(),
                                jsonPath("$[1].participants.basicList").isArray(),
                                jsonPath("$[1].participants.reserveList").hasJsonPath(),
                                jsonPath("$[1].participants.reserveList").isArray()
                        )
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void ShouldRejectRequestWhenUserIsNotLogIn(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Nested
    class ShouldAcceptRequestAndShouldThrow {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowConstraintViolationException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            startDate = "20200802";
            endDate = "20200808";

            uri = new URI("/group?startDate=" + startDate + "&endDate=" + endDate);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
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
                                    jsonPath("$.errors.startDate")
                                            .value(is(messages.get("exception.invalid.date.format"))),
                                    jsonPath("$.errors.endDate")
                                            .value(is(messages.get("exception.invalid.date.format")))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowStartDateAfterEndDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(StartDateAfterEndDateException.class)
                    .when(universalGroupTrainingService).getGroupTrainingsWithParticipants(startDate, endDate);
            RequestBuilder request = getValidRequest(testedLocale);
            String expectedMessage = messages.get("exception.start.date.after.end.date");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(StartDateAfterEndDateException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerError(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(universalGroupTrainingService).getGroupTrainingsWithParticipants(startDate, endDate);
            RequestBuilder request = getValidRequest(testedLocale);
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
}
