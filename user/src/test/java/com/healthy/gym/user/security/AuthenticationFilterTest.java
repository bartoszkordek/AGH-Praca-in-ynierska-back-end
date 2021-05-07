package com.healthy.gym.user.security;

import com.healthy.gym.user.configuration.tests.TestCountry;
import com.healthy.gym.user.data.entity.UserEntity;
import com.healthy.gym.user.data.repository.RegistrationTokenDAO;
import com.healthy.gym.user.data.repository.UserDAO;
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
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.healthy.gym.user.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.user.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
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

    @MockBean
    private RegistrationTokenDAO registrationTokenDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity(
                "Jan",
                "Kowalski",
                "jan.kowalski@wp.pl",
                "666 777 888",
                bCryptPasswordEncoder.encode("test12345"),
                UUID.randomUUID().toString(),
                true
        );

        when(userDAO.findByEmail(any())).thenReturn(userEntity);
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
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectUserLoginWhenProvidedInvalidCredentials(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/login");
            String requestBody = "{" +
                    "\"email\": \"jan.kowalski@wp.pl\",\n" +
                    "\"password\": \"test123451\"\n" +
                    "}";

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON);

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
                                    jsonPath("$.message").value(is(messages.get("user.log-in.fail")))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectUserLoginWhenUserAccountIsNotEnabled(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            userEntity.setEnabled(false);

            URI uri = new URI("/login");
            String requestBody = "{" +
                    "\"email\": \"jan.kowalski@wp.pl\",\n" +
                    "\"password\": \"test123451\"\n" +
                    "}";

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON);

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
                                            .value(is(messages.get("mail.registration.confirmation.log-in.exception")))
                            )
                    );
        }
    }
}