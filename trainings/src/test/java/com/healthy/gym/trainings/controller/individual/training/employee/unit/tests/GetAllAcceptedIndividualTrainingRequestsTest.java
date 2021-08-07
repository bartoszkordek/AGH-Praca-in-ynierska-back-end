package com.healthy.gym.trainings.controller.individual.training.employee.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.individual.training.EmployeeIndividualTrainingController;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
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

@WebMvcTest(EmployeeIndividualTrainingController.class)
class GetAllAcceptedIndividualTrainingRequestsTest {

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
    private URI uri;

    private String startDate;
    private String endDate;
    private String pageNumber;
    private String pageSize;

    @BeforeEach
    void setUp() throws URISyntaxException {
        adminToken = tokenFactory.getAdminToken();
        employeeToken = tokenFactory.getEmployeeToken();
        managerToken = tokenFactory.getManagerToken();
        trainerToken = tokenFactory.getTrainerToken();
        userToken = tokenFactory.getUserToken();

        startDate = "2020-10-10";
        endDate = "2020-10-17";
        pageNumber = "1";
        pageSize = "10";

        uri = getUri(startDate, endDate, pageNumber, pageSize);
    }

    private URI getUri(String startDate, String endDate, String pageNumber, String pageSize)
            throws URISyntaxException {
        return new URI("/individual/employee/accepted"
                + "?startDate=" + startDate
                + "&endDate=" + endDate
                + "&pageNumber=" + pageNumber
                + "&pageSize=" + pageSize
        );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetAllAcceptedIndividualTrainingRequestsList(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        int number = Integer.parseInt(pageNumber);
        int size = Integer.parseInt(pageSize);

        when(individualTrainingsService
                .getAllAcceptedIndividualTrainings(startDate, endDate, number, size))
                .thenReturn(List.of(getIndividualTrainingDTO()));

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
                                jsonPath("$[0].id")
                                        .value(is("74fe07a5-fb18-4006-a721-1a312dc2d398")),
                                jsonPath("$[0].title").value(is("Test training title")),
                                jsonPath("$[0].startDate").value(is("2020-10-10T16:00")),
                                jsonPath("$[0].endDate").value(is("2020-10-10T16:30")),
                                jsonPath("$[0].allDay").value(is(false)),
                                jsonPath("$[0].location").value(is("Room no 2"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$[0].trainers[0].name").value(is("TestName")),
                                jsonPath("$[0].trainers[0].surname").value(is("TestSurname")),
                                jsonPath("$[0].trainers[0].avatar").value(is("testAvatarUrl"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$[0].participants.basicList").isEmpty(),
                                jsonPath("$[0].participants.reserveList").isEmpty()
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

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .get(uri)
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

            uri = getUri("fdsg", "202df", "-1fds", "jiuoh");
            request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);
            expectedMessage = messages.get("exception.constraint.violation");

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
                                            .value(is(messages.get("exception.invalid.date.format"))),
                                    jsonPath("$.errors.pageNumber")
                                            .value(is(messages.get("exception.invalid.page.number.format"))),
                                    jsonPath("$.errors.pageSize")
                                            .value(is(messages.get("exception.invalid.page.size.format")))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowNoIndividualTrainingFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            int number = Integer.parseInt(pageNumber);
            int size = Integer.parseInt(pageSize);

            doThrow(NoIndividualTrainingFoundException.class)
                    .when(individualTrainingsService)
                    .getAllAcceptedIndividualTrainings(startDate, endDate, number, size);

            request = getValidRequest(employeeToken, testedLocale);
            expectedMessage = messages.get("exception.no.individual.training.found");

            performRequestAndTestErrorResponse(status().isNotFound(), NoIndividualTrainingFoundException.class);
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
        void shouldThrowInternalServerError(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            int number = Integer.parseInt(pageNumber);
            int size = Integer.parseInt(pageSize);

            doThrow(IllegalStateException.class)
                    .when(individualTrainingsService)
                    .getAllAcceptedIndividualTrainings(startDate, endDate, number, size);

            request = getValidRequest(managerToken, testedLocale);
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
        void whenUserTriesToGetData(TestCountry country) throws Exception {
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
        void whenTrainerTriesToGetData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(trainerToken, testedLocale);
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
