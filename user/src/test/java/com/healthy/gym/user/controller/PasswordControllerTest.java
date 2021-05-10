package com.healthy.gym.user.controller;

import com.healthy.gym.user.configuration.tests.TestCountry;
import com.healthy.gym.user.exceptions.token.ExpiredTokenException;
import com.healthy.gym.user.exceptions.token.InvalidTokenException;
import com.healthy.gym.user.service.TokenService;
import com.healthy.gym.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.user.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.user.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordController.class)
class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @Nested
    class WhenResetPasswordIsCalled {

        private URI uri;

        @BeforeEach
        void setUp() throws URISyntaxException {
            uri = new URI("/resetPassword");
        }

        @Nested
        class WhenInvalidEmailProvided {

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowRestExceptionWhenNoEmailProvided(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                String requestBody = "{\"email\":\"\"}";

                RequestBuilder request = MockMvcRequestBuilders
                        .post(uri)
                        .header("Accept-Language", testedLocale.toString())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON);

                String expectedMessage = messages.get("field.email.failure");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(status().reason(is(expectedMessage)))
                        .andExpect(result ->
                                assertThat(result.getResolvedException().getCause())
                                        .isInstanceOf(BindException.class)
                        );
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowRestExceptionWhenBlankEmailProvided(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                String requestBody = "{\"email\":\"    \"}";

                RequestBuilder request = MockMvcRequestBuilders
                        .post(uri)
                        .header("Accept-Language", testedLocale.toString())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON);

                String expectedMessage = messages.get("field.email.failure");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(status().reason(is(expectedMessage)))
                        .andExpect(result ->
                                assertThat(result.getResolvedException().getCause())
                                        .isInstanceOf(BindException.class)
                        );
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowRestExceptionWhenInvalidEmailProvided(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                String requestBody = "{\"email\":\"xmr09697@zwohocom\"}";

                RequestBuilder request = MockMvcRequestBuilders
                        .post(uri)
                        .header("Accept-Language", testedLocale.toString())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON);

                String expectedMessage = messages.get("field.email.failure");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(status().reason(is(expectedMessage)))
                        .andExpect(result ->
                                assertThat(result.getResolvedException().getCause())
                                        .isInstanceOf(BindException.class)
                        );
            }
        }

        @Nested
        class WhenValidEmailProvided {

            private String requestBody;

            @BeforeEach
            void setUp() {
                requestBody = "{\"email\":\"xmr09697@zwoho.com\"}";
            }

            @Nested
            class ShouldThrowException {

                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void shouldThrowUsernameNotFoundExceptionWhenEmailIsNotFound(TestCountry country)
                        throws Exception {
                    Map<String, String> messages = getMessagesAccordingToLocale(country);
                    Locale testedLocale = convertEnumToLocale(country);

                    RequestBuilder request = MockMvcRequestBuilders
                            .post(uri)
                            .header("Accept-Language", testedLocale.toString())
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON);

                    doThrow(UsernameNotFoundException.class).when(userService).resetPassword(anyString());
                    String expectedMessage = messages.get("field.email.failure");

                    mockMvc.perform(request)
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(status().reason(is(expectedMessage)))
                            .andExpect(result ->
                                    assertThat(result.getResolvedException().getCause())
                                            .isInstanceOf(UsernameNotFoundException.class)
                            );
                }

                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void shouldThrowExceptionWhenInternalServerErrorHappened(TestCountry country)
                        throws Exception {
                    Map<String, String> messages = getMessagesAccordingToLocale(country);
                    Locale testedLocale = convertEnumToLocale(country);

                    RequestBuilder request = MockMvcRequestBuilders
                            .post(uri)
                            .header("Accept-Language", testedLocale.toString())
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON);

                    doThrow(IllegalStateException.class).when(userService).resetPassword(anyString());
                    String expectedMessage = messages.get("reset.password.error");

                    mockMvc.perform(request)
                            .andDo(print())
                            .andExpect(status().isInternalServerError())
                            .andExpect(status().reason(is(expectedMessage)))
                            .andExpect(result ->
                                    assertThat(result.getResolvedException().getCause())
                                            .isInstanceOf(Exception.class)
                            );
                }

                @Nested
                class InstanceOfAccountStatusException {

                    @ParameterizedTest
                    @EnumSource(TestCountry.class)
                    void accountExpiredExceptionWhenAccountIsExpired(TestCountry country)
                            throws Exception {
                        Map<String, String> messages = getMessagesAccordingToLocale(country);
                        Locale testedLocale = convertEnumToLocale(country);

                        RequestBuilder request = MockMvcRequestBuilders
                                .post(uri)
                                .header("Accept-Language", testedLocale.toString())
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON);

                        doThrow(AccountExpiredException.class).when(userService).resetPassword(anyString());
                        String expectedMessage = messages.get("reset.password.exception.account.expired");

                        mockMvc.perform(request)
                                .andDo(print())
                                .andExpect(status().isForbidden())
                                .andExpect(status().reason(is(expectedMessage)))
                                .andExpect(result ->
                                        assertThat(result.getResolvedException().getCause())
                                                .isInstanceOf(AccountExpiredException.class)
                                );
                    }

                    @ParameterizedTest
                    @EnumSource(TestCountry.class)
                    void credentialsExpiredExceptionWhenCredentialsExpired(TestCountry country)
                            throws Exception {
                        Map<String, String> messages = getMessagesAccordingToLocale(country);
                        Locale testedLocale = convertEnumToLocale(country);

                        RequestBuilder request = MockMvcRequestBuilders
                                .post(uri)
                                .header("Accept-Language", testedLocale.toString())
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON);

                        doThrow(CredentialsExpiredException.class).when(userService).resetPassword(anyString());
                        String expectedMessage = messages.get("reset.password.exception.credentials.expired");

                        mockMvc.perform(request)
                                .andDo(print())
                                .andExpect(status().isForbidden())
                                .andExpect(status().reason(is(expectedMessage)))
                                .andExpect(result ->
                                        assertThat(result.getResolvedException().getCause())
                                                .isInstanceOf(CredentialsExpiredException.class)
                                );
                    }

                    @ParameterizedTest
                    @EnumSource(TestCountry.class)
                    void disabledExceptionWhenAccountIsDisabled(TestCountry country)
                            throws Exception {
                        Map<String, String> messages = getMessagesAccordingToLocale(country);
                        Locale testedLocale = convertEnumToLocale(country);

                        RequestBuilder request = MockMvcRequestBuilders
                                .post(uri)
                                .header("Accept-Language", testedLocale.toString())
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON);

                        doThrow(DisabledException.class).when(userService).resetPassword(anyString());
                        String expectedMessage = messages.get("reset.password.exception.account.disabled");

                        mockMvc.perform(request)
                                .andDo(print())
                                .andExpect(status().isForbidden())
                                .andExpect(status().reason(is(expectedMessage)))
                                .andExpect(result ->
                                        assertThat(result.getResolvedException().getCause())
                                                .isInstanceOf(DisabledException.class)
                                );
                    }

                    @ParameterizedTest
                    @EnumSource(TestCountry.class)
                    void lockedExceptionWhenAccountIsLocked(TestCountry country)
                            throws Exception {
                        Map<String, String> messages = getMessagesAccordingToLocale(country);
                        Locale testedLocale = convertEnumToLocale(country);

                        RequestBuilder request = MockMvcRequestBuilders
                                .post(uri)
                                .header("Accept-Language", testedLocale.toString())
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON);

                        doThrow(LockedException.class).when(userService).resetPassword(anyString());
                        String expectedMessage = messages.get("reset.password.exception.account.locked");

                        mockMvc.perform(request)
                                .andDo(print())
                                .andExpect(status().isForbidden())
                                .andExpect(status().reason(is(expectedMessage)))
                                .andExpect(result ->
                                        assertThat(result.getResolvedException().getCause())
                                                .isInstanceOf(LockedException.class)
                                );
                    }

                    @ParameterizedTest
                    @EnumSource(TestCountry.class)
                    void anotherAccountStatusExceptionWhenSthUnexpectedHappened(TestCountry country)
                            throws Exception {
                        Map<String, String> messages = getMessagesAccordingToLocale(country);
                        Locale testedLocale = convertEnumToLocale(country);

                        RequestBuilder request = MockMvcRequestBuilders
                                .post(uri)
                                .header("Accept-Language", testedLocale.toString())
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON);

                        doThrow(UnexpectedAccountStatusException.class).when(userService).resetPassword(anyString());
                        String expectedMessage = messages.get("reset.password.error");

                        mockMvc.perform(request)
                                .andDo(print())
                                .andExpect(status().isForbidden())
                                .andExpect(status().reason(is(expectedMessage)))
                                .andExpect(result ->
                                        assertThat(result.getResolvedException().getCause())
                                                .isInstanceOf(UnexpectedAccountStatusException.class)
                                );
                    }

                    private class UnexpectedAccountStatusException extends AccountStatusException {
                        public UnexpectedAccountStatusException(String msg) {
                            super(msg);
                        }
                    }
                }
            }
        }
    }

    @Nested
    class WhileConfirmResettingPasswordIsCalled {
        private URI uri;
        private String token;

        @BeforeEach
        void setUp() throws URISyntaxException {
            uri = new URI("/confirmNewPassword");
            token = UUID.randomUUID().toString();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldAcceptResettingPasswordWhenProvidedTokenIsValid(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .param("token", token)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

            doNothing().when(tokenService).verifyRegistrationToken(anyString());
            String expectedMessage = messages.get("registration.confirmation.token.valid");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(is(true)))
                    .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                    .andExpect(jsonPath("$.errors", is(anEmptyMap())));
        }

        @Nested
        class ShouldThrowExceptionOfInstance {

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void expiredTokenExceptionWhenTokenIsExpired(TestCountry country)
                    throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri)
                        .param("token", token)
                        .header("Accept-Language", testedLocale.toString())
                        .contentType(MediaType.APPLICATION_JSON);

                doThrow(ExpiredTokenException.class).when(tokenService).verifyResetPasswordToken(anyString());
                String expectedMessage = messages.get("registration.confirmation.token.expired");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isUnauthorized())
                        .andExpect(status().reason(is(expectedMessage)))
                        .andExpect(result ->
                                assertThat(result.getResolvedException().getCause())
                                        .isInstanceOf(ExpiredTokenException.class)
                        );
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void invalidTokenExceptionWhenProvidedTokenIsInvalid(TestCountry country)
                    throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri)
                        .param("token", token)
                        .header("Accept-Language", testedLocale.toString())
                        .contentType(MediaType.APPLICATION_JSON);

                doThrow(InvalidTokenException.class).when(tokenService).verifyResetPasswordToken(anyString());
                String expectedMessage = messages.get("registration.confirmation.token.invalid");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(status().reason(is(expectedMessage)))
                        .andExpect(result ->
                                assertThat(result.getResolvedException().getCause())
                                        .isInstanceOf(InvalidTokenException.class)
                        );
            }


            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void genericExceptionWhenInternalServerErrorHappened(TestCountry country)
                    throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri)
                        .param("token", token)
                        .header("Accept-Language", testedLocale.toString())
                        .contentType(MediaType.APPLICATION_JSON);

                doThrow(IllegalStateException.class).when(tokenService).verifyResetPasswordToken(anyString());
                String expectedMessage = messages.get("registration.confirmation.token.error");

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
    }
}