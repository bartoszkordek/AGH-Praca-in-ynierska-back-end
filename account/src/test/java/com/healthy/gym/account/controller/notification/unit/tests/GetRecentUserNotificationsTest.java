package com.healthy.gym.account.controller.notification.unit.tests;

import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.controller.NotificationController;
import com.healthy.gym.account.dto.BasicUserInfoDTO;
import com.healthy.gym.account.dto.UserNotificationDTO;
import com.healthy.gym.account.exception.NoNotificationFoundException;
import com.healthy.gym.account.exception.UserNotFoundException;
import com.healthy.gym.account.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class GetRecentUserNotificationsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private NotificationService notificationService;

    private String adminToken;
    private String userToken;
    private String employeeToken;
    private String trainerToken;
    private String managerToken;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        adminToken = tokenFactory.getAdminToken();
        employeeToken = tokenFactory.getEmployeeToken();
        trainerToken = tokenFactory.getTrainerToken();
        managerToken = tokenFactory.getMangerToken();

        uri = getUri(userId, "0", "10");
    }

    private URI getUri(String userId, String pageNumber, String pageSize) throws URISyntaxException {
        return new URI("/notification/user/" + userId + "?pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    }

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void shouldReturnListOfNotificationsDTO() throws Exception {

        when(
                notificationService.getRecentUserNotifications(anyString(), anyInt(), anyInt())
        ).thenReturn(List.of(getUserNotificationDTO()));

        RequestBuilder request = getValidRequest(adminToken, Locale.ENGLISH);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON)
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.[0].notificationId").value(is(notNullValue())),
                                jsonPath("$.[0].from.userId").value(is(notNullValue())),
                                jsonPath("$.[0].from.name").value(is("TestName")),
                                jsonPath("$.[0].from.surname").value(is("TestSurname")),
                                jsonPath("$.[0].from.avatar").value(is("testUrlAvatar")),
                                jsonPath("$.[0].title").value(is("TestTitle")),
                                jsonPath("$.[0].content").value(is("TestContent")),
                                jsonPath("$.[0].created").value(is("2020-10-10T20:20:10")),
                                jsonPath("$.[0].markAsRead").value(is(false))
                        )
                );
    }

    private UserNotificationDTO getUserNotificationDTO() {
        return new UserNotificationDTO(
                UUID.randomUUID().toString(),
                new BasicUserInfoDTO(
                        UUID.randomUUID().toString(),
                        "TestName",
                        "TestSurname",
                        "testUrlAvatar"
                ),
                "TestTitle",
                "TestContent",
                "2020-10-10T20:20:10",
                false
        );
    }

    @Nested
    class ShouldAcceptRequestWhenUserHasAdminOrManagerRoleAndShouldThrow {

        private RequestBuilder request;
        private String expectedMessage;

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowConstraintViolationException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI invalidUri = getUri("rerfds", "-10", "60");

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
                                    jsonPath("$.errors.pageNumber")
                                            .value(is(messages.get("exception.invalid.page.number.format"))),
                                    jsonPath("$.errors.pageSize")
                                            .value(is(messages.get("exception.invalid.page.size.format")))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowNoNotificationFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(NoNotificationFoundException.class)
                    .when(notificationService)
                    .getRecentUserNotifications(anyString(), anyInt(), anyInt());
            request = getValidRequest(userToken, testedLocale);
            expectedMessage = messages.get("exception.no.notification.found");

            performRequestAndTestErrorResponse(status().isNotFound(), NoNotificationFoundException.class);
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
        void shouldThrowUserNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(UserNotFoundException.class)
                    .when(notificationService)
                    .getRecentUserNotifications(anyString(), anyInt(), anyInt());
            request = getValidRequest(userToken, testedLocale);
            expectedMessage = messages.get("exception.not.found.user.id");

            performRequestAndTestErrorResponse(status().isNotFound(), UserNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerError(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(notificationService)
                    .getRecentUserNotifications(anyString(), anyInt(), anyInt());
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
        void whenUserTriesTriesToPerformRequestOnBehalfOfAnotherUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            uri = getUri(UUID.randomUUID().toString(), "0", "10");
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
        void whenTrainerTriesTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(trainerToken, testedLocale);
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
