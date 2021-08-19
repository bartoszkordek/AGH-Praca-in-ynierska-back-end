package com.healthy.gym.account.controller.privacyController.unitTest;

import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.controller.PrivacyController;
import com.healthy.gym.account.exception.UserPrivacyNotFoundException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.PhotoService;
import com.healthy.gym.account.dto.UserPrivacyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrivacyController.class)
class WhenGetUserPrivacyTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @MockBean
    private AccountService accountService;
    @MockBean
    private PhotoService photoService;

    private String userToken;
    private String managerToken;
    private String trainerToken;
    private String employeeToken;
    private String adminToken;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        managerToken = tokenFactory.getMangerToken();
        trainerToken = tokenFactory.getTrainerToken();
        employeeToken = tokenFactory.getEmployeeToken();
        adminToken = tokenFactory.getAdminToken();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnUserPrivacy(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/" + userId + "/privacy");

        when(accountService.getUserPrivacy(userId)).thenReturn(
                new UserPrivacyDTO(
                        true,
                        false,
                        true,
                        false
                )
        );

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.regulationsAccepted")
                        .value(is(true)))
                .andExpect(jsonPath("$.allowShowingTrainingsParticipation")
                        .value(is(false)))
                .andExpect(jsonPath("$.allowShowingUserStatistics")
                        .value(is(true)))
                .andExpect(jsonPath("$.allowShowingAvatar")
                        .value(is(false)))
        ;
    }

    @Nested
    class ShouldAcceptRequestAndShouldThrowError {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUsernameNotFound(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/" + userId + "/privacy");

            String expectedMessage = messages.get("exception.account.not.found");
            doThrow(UsernameNotFoundException.class).when(accountService).getUserPrivacy(userId);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(UsernameNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserPrivacyNotFound(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/" + userId + "/privacy");

            String expectedMessage = messages.get("exception.account.privacy.not.found");
            doThrow(UserPrivacyNotFoundException.class).when(accountService).getUserPrivacy(userId);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(UserPrivacyNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenInternalErrorHappens(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/" + userId + "/privacy");

            String expectedMessage = messages.get("request.failure");
            doThrow(IllegalStateException.class).when(accountService).getUserPrivacy(userId);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

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

        private void mockPerformAndVerify(RequestBuilder request, String expectedMessage) throws Exception {
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
        void whenUserDoesNotOwnAccount(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();
            URI uri = new URI("/" + invalidId + "/privacy");

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken);

            String expectedMessage = messages.get("exception.access.denied");
            mockPerformAndVerify(request, expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenHasTrainerRole(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();
            URI uri = new URI("/" + invalidId + "/privacy");

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", trainerToken);

            String expectedMessage = messages.get("exception.access.denied");
            mockPerformAndVerify(request, expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenHasEmployeeRole(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();
            URI uri = new URI("/" + invalidId + "/privacy");

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken);

            String expectedMessage = messages.get("exception.access.denied");
            mockPerformAndVerify(request, expectedMessage);
        }
    }
}
