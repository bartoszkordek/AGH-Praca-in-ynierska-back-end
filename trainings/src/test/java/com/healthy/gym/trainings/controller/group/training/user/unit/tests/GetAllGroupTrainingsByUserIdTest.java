package com.healthy.gym.trainings.controller.group.training.user.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.group.training.UserGroupTrainingController;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingService;
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

@WebMvcTest(UserGroupTrainingController.class)
class GetAllGroupTrainingsByUserIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private UserGroupTrainingService userGroupTrainingService;

    private String employeeToken;
    private String adminToken;
    private String userToken;
    private String userId;
    private URI uri;

    private String startDate;
    private String endDate;

    @BeforeEach
    void setUp() throws URISyntaxException {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getEmployeeToken(employeeId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        startDate = "2020-08-02";
        endDate = "2020-08-08";

        uri = new URI("/group/trainings/" + userId + "?startDate=" + startDate + "&endDate=" + endDate);
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
    void shouldEnrollToGroupTrainingOnReserveList(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        when(userGroupTrainingService.getMyAllTrainings(userId, startDate, endDate))
                .thenReturn(List.of(getGroupTrainingDTO()));

        RequestBuilder request = getValidRequest(userToken, testedLocale);

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

    private GroupTrainingDTO getGroupTrainingDTO() {
        return new GroupTrainingDTO(
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
    class ShouldAcceptRequestWhenUserHasAdminOrManagerRoleAndShouldThrow {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowConstraintViolationException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            startDate = "20200802";
            endDate = "20200808";

            URI invalidUri = new URI("/group/trainings/" + 3423 + "?startDate=" + startDate
                    + "&endDate=" + endDate);

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
                                    jsonPath("$.errors.userId")
                                            .value(is(messages.get("exception.invalid.id.format"))),
                                    jsonPath("$.errors.startDate")
                                            .value(is(messages.get("exception.invalid.date.format"))),
                                    jsonPath("$.errors.endDate")
                                            .value(is(messages.get("exception.invalid.date.format")))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowUserNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(UserNotFoundException.class)
                    .when(userGroupTrainingService).getMyAllTrainings(userId, startDate, endDate);
            RequestBuilder request = getValidRequest(employeeToken, testedLocale);
            String expectedMessage = messages.get("exception.not.found.user.id");

            performRequestAndTestErrorResponse(request, expectedMessage, UserNotFoundException.class);
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
        void shouldThrowStartDateAfterEndDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(StartDateAfterEndDateException.class)
                    .when(userGroupTrainingService).getMyAllTrainings(userId, startDate, endDate);
            RequestBuilder request = getValidRequest(userToken, testedLocale);
            String expectedMessage = messages.get("exception.start.date.after.end.date");

            performRequestAndTestErrorResponse(request, expectedMessage, StartDateAfterEndDateException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerErrorWhenIllegalStateExceptionOccurred(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(userGroupTrainingService).getMyAllTrainings(userId, startDate, endDate);
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
        void whenUserTriesToGetInfoAboutOtherUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            uri = new URI("/group/trainings/" + UUID.randomUUID() + "?startDate=" + startDate
                    + "&endDate=" + endDate);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
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
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
