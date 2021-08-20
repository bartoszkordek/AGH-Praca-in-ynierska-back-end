package com.healthy.gym.trainings.controller.individual.training.employee.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.individual.training.EmployeeIndividualTrainingController;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.service.individual.training.EmployeeIndividualTrainingService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeIndividualTrainingController.class)
@ActiveProfiles(value = "test")
class GetIndividualTrainingByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private EmployeeIndividualTrainingService individualTrainingsService;

    private String adminToken;
    private String employeeToken;
    private String managerToken;
    private String trainerToken;
    private String userToken;
    private String trainingId;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        adminToken = tokenFactory.getAdminToken();
        employeeToken = tokenFactory.getEmployeeToken();
        managerToken = tokenFactory.getManagerToken();
        trainerToken = tokenFactory.getTrainerToken();
        userToken = tokenFactory.getUserToken();

        trainingId = UUID.randomUUID().toString();
        uri = getUri(trainingId);
    }

    private URI getUri(String trainingId) throws URISyntaxException {
        return new URI("/individual/employee/" + trainingId);
    }

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetIndividualTrainingById(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        when(individualTrainingsService.getIndividualTrainingById(anyString()))
                .thenReturn(getIndividualTrainingDTO());

        RequestBuilder request = getValidRequest(managerToken, testedLocale);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.message").doesNotExist()
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.id")
                                        .value(is("74fe07a5-fb18-4006-a721-1a312dc2d398")),
                                jsonPath("$.title").value(is("Test training title")),
                                jsonPath("$.startDate").value(is("2020-10-10T16:00")),
                                jsonPath("$.endDate").value(is("2020-10-10T16:30")),
                                jsonPath("$.allDay").value(is(false)),
                                jsonPath("$.location").value(is("Room no 2"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.trainers[0].name").value(is("TestName")),
                                jsonPath("$.trainers[0].surname").value(is("TestSurname")),
                                jsonPath("$.trainers[0].avatar").value(is("testAvatarUrl"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.participants.basicList").isEmpty(),
                                jsonPath("$.participants.reserveList").isEmpty()
                        )
                );
    }

    private IndividualTrainingDTO getIndividualTrainingDTO() {
        return new IndividualTrainingDTO(
                "74fe07a5-fb18-4006-a721-1a312dc2d398",
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

    @Nested
    class ShouldAcceptRequestAndShouldThrow {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowConstraintViolationException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI invalidUri = getUri("3123");

            RequestBuilder request = MockMvcRequestBuilders
                    .get(invalidUri)
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
                                    jsonPath("$.errors.trainingId")
                                            .value(is(messages.get("exception.invalid.id.format")))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowNotExistingIndividualTrainingException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(NotExistingIndividualTrainingException.class)
                    .when(individualTrainingsService).getIndividualTrainingById(trainingId);
            RequestBuilder request = getValidRequest(employeeToken, testedLocale);
            String expectedMessage = messages.get("exception.not.existing.individual.training");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(NotExistingIndividualTrainingException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerError(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(individualTrainingsService).getIndividualTrainingById(anyString());
            RequestBuilder request = getValidRequest(managerToken, testedLocale);
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
        void whenUserTriesToGetData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = getValidRequest(userToken, testedLocale);
            String expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied(request, expectedMessage);
        }

        private void performAndTestAccessDenied(RequestBuilder request, String expectedMessage) throws Exception {
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
        void whenTrainerTriesToGetData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = getValidRequest(trainerToken, testedLocale);
            String expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied(request, expectedMessage);
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