package com.healthy.gym.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.auth.configuration.tests.TestCountry;
import com.healthy.gym.auth.exceptions.token.ExpiredTokenException;
import com.healthy.gym.auth.exceptions.token.InvalidTokenException;
import com.healthy.gym.auth.service.TokenService;
import com.healthy.gym.auth.service.UserService;
import com.healthy.gym.auth.shared.UserDTO;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.healthy.gym.auth.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.auth.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordController.class)
@ActiveProfiles(value = "test")
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
                                assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                    assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                    assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
        private String requestBody;

        @BeforeEach
        void setUp() throws URISyntaxException {
            uri = new URI("/confirmNewPassword");
            token = UUID.randomUUID().toString();
            requestBody = "{" +
                    "\"password\": \"test1234\",\n" +
                    "\"matchingPassword\": \"test1234\"" +
                    "}";
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldAcceptResettingPasswordWhenProvidedTokenIsValid(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .param("token", token)
                    .content(requestBody)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

            doReturn(new UserDTO()).when(tokenService).verifyTokenAndResetPassword(anyString(), anyString());
            String expectedMessage = messages.get("reset.password.confirmation.token.valid");

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
                        .post(uri)
                        .param("token", token)
                        .content(requestBody)
                        .param("token", token)
                        .header("Accept-Language", testedLocale.toString())
                        .contentType(MediaType.APPLICATION_JSON);

                doThrow(ExpiredTokenException.class).when(tokenService)
                        .verifyTokenAndResetPassword(anyString(), anyString());
                String expectedMessage = messages.get("reset.password.confirmation.token.expired");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isUnauthorized())
                        .andExpect(status().reason(is(expectedMessage)))
                        .andExpect(result ->
                                assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                        .post(uri)
                        .param("token", token)
                        .content(requestBody)
                        .header("Accept-Language", testedLocale.toString())
                        .contentType(MediaType.APPLICATION_JSON);

                doThrow(InvalidTokenException.class).when(tokenService)
                        .verifyTokenAndResetPassword(anyString(), anyString());
                String expectedMessage = messages.get("reset.password.confirmation.token.invalid");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(status().reason(is(expectedMessage)))
                        .andExpect(result ->
                                assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
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
                        .post(uri)
                        .param("token", token)
                        .content(requestBody)
                        .header("Accept-Language", testedLocale.toString())
                        .contentType(MediaType.APPLICATION_JSON);

                doThrow(IllegalStateException.class).when(tokenService)
                        .verifyTokenAndResetPassword(anyString(), anyString());
                String expectedMessage = messages.get("reset.password.error");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isInternalServerError())
                        .andExpect(status().reason(is(expectedMessage)))
                        .andExpect(result ->
                                assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                        .isInstanceOf(IllegalStateException.class)
                        );
            }

            @Nested
            class BindExceptionWhenInvalidPasswordProvided {

                private Map<String, String> fieldErrors;
                private ObjectMapper objectMapper;

                @BeforeEach
                void setUp() {
                    fieldErrors = new HashMap<>();
                    objectMapper = new ObjectMapper();
                }

                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void WhenPasswordsLengthIsTooSmall(TestCountry country)
                        throws Exception {
                    Map<String, String> messages = getMessagesAccordingToLocale(country);
                    Locale testedLocale = convertEnumToLocale(country);

                    String requestBody = "{" +
                            "\"password\": \"test123\",\n" +
                            "\"matchingPassword\": \"test123\"" +
                            "}";

                    RequestBuilder request = MockMvcRequestBuilders
                            .post(uri)
                            .content(requestBody)
                            .param("token", token)
                            .header("Accept-Language", testedLocale.toString())
                            .contentType(MediaType.APPLICATION_JSON);

                    fieldErrors.put("password", messages.get("field.password.failure"));
                    fieldErrors.put("matchingPassword", messages.get("field.password.failure"));

                    String expectedMessage = objectMapper.writeValueAsString(fieldErrors);

                    mockMvc.perform(request)
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(status().reason(is(expectedMessage)));
                }

                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void WhenPasswordsLengthIsTooBig(TestCountry country)
                        throws Exception {
                    Map<String, String> messages = getMessagesAccordingToLocale(country);
                    Locale testedLocale = convertEnumToLocale(country);

                    String requestBody = "{" +
                            "\"password\": \"test1234test1234test1234test12345\",\n" +
                            "\"matchingPassword\": \"test1234test1234test1234test12345\"" +
                            "}";

                    RequestBuilder request = MockMvcRequestBuilders
                            .post(uri)
                            .content(requestBody)
                            .param("token", token)
                            .header("Accept-Language", testedLocale.toString())
                            .contentType(MediaType.APPLICATION_JSON);

                    fieldErrors.put("password", messages.get("field.password.failure"));
                    fieldErrors.put("matchingPassword", messages.get("field.password.failure"));

                    String expectedMessage = objectMapper.writeValueAsString(fieldErrors);

                    mockMvc.perform(request)
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(status().reason(is(expectedMessage)));
                }


                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void WhenPasswordsDoNotMatch(TestCountry country)
                        throws Exception {
                    Map<String, String> messages = getMessagesAccordingToLocale(country);
                    Locale testedLocale = convertEnumToLocale(country);

                    String requestBody = "{" +
                            "\"password\": \"test1234\",\n" +
                            "\"matchingPassword\": \"test12345\"" +
                            "}";

                    RequestBuilder request = MockMvcRequestBuilders
                            .post(uri)
                            .content(requestBody)
                            .param("token", token)
                            .header("Accept-Language", testedLocale.toString())
                            .contentType(MediaType.APPLICATION_JSON);

                    fieldErrors.put("password", messages.get("field.password.match.failure"));

                    String expectedMessage = objectMapper.writeValueAsString(fieldErrors);

                    mockMvc.perform(request)
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(status().reason(is(expectedMessage)));

                }
            }
        }
    }
}