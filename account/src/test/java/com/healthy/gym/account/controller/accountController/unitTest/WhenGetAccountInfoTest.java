package com.healthy.gym.account.controller.accountController.unitTest;

import com.healthy.gym.account.component.TokenManager;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.controller.AccountController;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.PhotoService;
import com.healthy.gym.account.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class WhenGetAccountInfoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private AccountService accountService;

    @MockBean
    private PhotoService photoService;

    private String userToken;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestWhenUserIsLoggedIn(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/" + userId);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken);

        when(accountService.getAccountInfo(userId))
                .thenReturn(
                        new UserDTO(
                                userId,
                                "testName",
                                "testSurname",
                                "testEmail",
                                "testPhoneNumber",
                                null,
                                null
                        )
                );

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        matchAll(
                                jsonPath("$.message").doesNotHaveJsonPath(),
                                jsonPath("$.errors").doesNotHaveJsonPath(),
                                jsonPath("$.name").value(is("testName")),
                                jsonPath("$.surname").value(is("testSurname")),
                                jsonPath("$.email").value(is("testEmail")),
                                jsonPath("$.phone").value(is("testPhoneNumber"))
                        )
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRejectRequestWhenUserIsNotLoggedIn(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/" + UUID.randomUUID());

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString());

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
    void shouldRejectRequestWhenUserNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/" + userId);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken);

        String expectedMessage = messages.get("exception.account.not.found");

        doThrow(UsernameNotFoundException.class).when(accountService).getAccountInfo(anyString());

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
    void shouldRejectRequestWhenInternalErrorHappens(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/" + userId);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken);

        String expectedMessage = messages.get("request.failure");

        doThrow(IllegalStateException.class).when(accountService).getAccountInfo(anyString());

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
