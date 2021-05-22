package com.healthy.gym.auth.security;

import com.healthy.gym.auth.configuration.tests.TestCountry;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.RegistrationTokenDAO;
import com.healthy.gym.auth.data.repository.mongo.ResetPasswordTokenDAO;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
import com.healthy.gym.auth.enums.GymRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

import static com.healthy.gym.auth.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.auth.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class AuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;

    @MockBean // DO NOT REMOVE
    private RegistrationTokenDAO registrationTokenDAO;

    @MockBean // DO NOT REMOVE
    private ResetPasswordTokenDAO resetPasswordTokenDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserDocument userDocument;

    @BeforeEach
    void setUp() {
        userDocument = new UserDocument(
                "Jan",
                "Kowalski",
                "jan.kowalski@wp.pl",
                "666 777 888",
                bCryptPasswordEncoder.encode("test12345"),
                UUID.randomUUID().toString(),
                true,
                true,
                true,
                true
        );
        Set<GymRole> userRoles = new HashSet<>();
        userRoles.add(GymRole.USER);
        userDocument.setGymRoles(userRoles);

        when(userDAO.findByEmail("jan.kowalski@wp.pl")).thenReturn(userDocument);
    }

    @Nested
    class WhileSuccessfulAuthenticationIsCalled {

        @Test
        void shouldAcceptUserLoginWhenProvidedValidCredentials() throws Exception {
            URI uri = new URI("/login");
            String requestBody = "{" +
                    "\"email\": \"jan.kowalski@wp.pl\",\n" +
                    "\"password\": \"test12345\"\n" +
                    "}";

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON);

            Pattern uuidPattern = Pattern
                    .compile("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(
                            matchAll(
                                    status().isOk(),
                                    header().exists("userId"),
                                    header().exists("token"),
                                    header().string("userId", matchesPattern(uuidPattern)),
                                    header().string("token", startsWith("Bearer ")),
                                    jsonPath("$").doesNotExist()
                            )
                    );
        }
    }

    @Nested
    class WhileUnsuccessfulAuthenticationIsCalled {
        private String requestBody;

        @BeforeEach
        void setUp() {
            requestBody = "{" +
                    "\"email\": \"jan.kowalski@wp.pl\",\n" +
                    "\"password\": \"test12345\"\n" +
                    "}";
        }

        private RequestBuilder buildRequest(Locale testedLocale, String requestBody) throws URISyntaxException {
            URI uri = new URI("/login");
            return MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON);
        }

        private void performPrintAndTestRequest(RequestBuilder request, String expectedMessage) throws Exception {
            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(
                            matchAll(
                                    status().isUnauthorized(),
                                    header().doesNotExist("userId"),
                                    header().doesNotExist("token"),
                                    jsonPath("$.path").exists(),
                                    jsonPath("$.error").exists(),
                                    jsonPath("$.timestamp").exists(),
                                    jsonPath("$.status").exists(),
                                    jsonPath("$.message")
                                            .value(is(expectedMessage))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectUserLoginWhenProvidedInvalidCredentials(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String requestBody = "{" +
                    "\"email\": \"jan.kowalski@wp.pl\",\n" +
                    "\"password\": \"test123451\"\n" +
                    "}";

            RequestBuilder request = buildRequest(testedLocale, requestBody);

            String expectedMessage = messages.get("user.log-in.fail");
            performPrintAndTestRequest(request, expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectUserLoginWhenUserAccountIsNotEnabled(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            userDocument.setEnabled(false);
            RequestBuilder request = buildRequest(testedLocale, requestBody);
            String expectedMessage = messages.get("mail.registration.confirmation.log-in.exception");

            performPrintAndTestRequest(request, expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectUserLoginWhenUserAccountIsExpired(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            userDocument.setAccountNonExpired(false);
            RequestBuilder request = buildRequest(testedLocale, requestBody);
            String expectedMessage = messages.get("user.log-in.fail.account.expired");

            performPrintAndTestRequest(request, expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectUserLoginWhenUserAccountIsLocked(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            userDocument.setAccountNonLocked(false);
            RequestBuilder request = buildRequest(testedLocale, requestBody);
            String expectedMessage = messages.get("user.log-in.fail.account.locked");

            performPrintAndTestRequest(request, expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectUserLoginWhenUserCredentialsAreExpired(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            userDocument.setCredentialsNonExpired(false);
            RequestBuilder request = buildRequest(testedLocale, requestBody);
            String expectedMessage = messages.get("user.log-in.fail.credentials.expired");

            performPrintAndTestRequest(request, expectedMessage);
        }
    }
}