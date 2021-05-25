package com.healthy.gym.account.controller.accountController.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.account.component.token.TokenManager;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.controller.AccountController;
import com.healthy.gym.account.exception.UserPrivacyNotUpdatedException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.PhotoService;
import com.healthy.gym.account.shared.UserDTO;
import com.healthy.gym.account.shared.UserPrivacyDTO;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.*;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class WhenChangeUserPrivacyTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenManager tokenManager;

    @MockBean
    private AccountService accountService;

    @MockBean
    private PhotoService photoService;

    private String userToken;
    private String adminToken;
    private String userId;
    private ObjectMapper objectMapper;
    private Map<String, String> requestMap;
    private UserDTO updatedUser;
    private UserPrivacyDTO userPrivacyDTOToUpdate;

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        userId = UUID.randomUUID().toString();
        userToken = tokenManager.getTokenPrefix() + " " + Jwts.builder()
                .setSubject(userId)
                .claim("roles", List.of("ROLE_USER"))
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();

        String adminId = UUID.randomUUID().toString();

        adminToken = tokenManager.getTokenPrefix() + " " + Jwts.builder()
                .setSubject(adminId)
                .claim("roles", List.of("ROLE_USER", "ROLE_ADMIN"))
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();

        requestMap = new HashMap<>();
        requestMap.put("regulationsAccepted", String.valueOf(true));
        requestMap.put("allowShowingTrainingsParticipation", String.valueOf(true));
        requestMap.put("allowShowingUserStatistics", String.valueOf(true));
        requestMap.put("allowShowingAvatar", String.valueOf(true));

        updatedUser = new UserDTO(
                userId,
                "Jan",
                "Kowalski",
                "xmr09697@zwoho.com",
                "+48 685 263 683",
                "testtest1234",
                "encryptedtesttest1234"
        );

        userPrivacyDTOToUpdate = new UserPrivacyDTO(
                true,
                true,
                true,
                true
        );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestWhenUserChangesItsOwnData(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePrivacy/" + userId);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("account.change.user.data.success");

        UserPrivacyDTO userPrivacyDTOUpdated = new UserPrivacyDTO(
                true,
                true,
                true,
                true
        );
        when(accountService.changeUserPrivacy(userPrivacyDTOToUpdate, userId)).thenReturn(userPrivacyDTOUpdated);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                .andExpect(jsonPath("$.regulationsAccepted")
                        .value(is(true)))
                .andExpect(jsonPath("$.allowShowingTrainingsParticipation")
                        .value(is(true)))
                .andExpect(jsonPath("$.allowShowingUserStatistics")
                        .value(is(true)))
                .andExpect(jsonPath("$.allowShowingAvatar")
                        .value(is(true)))
        ;
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRejectRequestWhenUserDoesNotChangeOwnData(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePrivacy/" + UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.access.denied");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(is(expectedMessage)));
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRejectRequestWhenAdminChangesUserData(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePrivacy/" + userId);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.access.denied");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(is(expectedMessage)));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhenUserNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePrivacy/" + userId);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.account.not.found");
        doThrow(UsernameNotFoundException.class).when(accountService)
                .changeUserPrivacy(userPrivacyDTOToUpdate, userId);

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
    void shouldThrowExceptionWhenUserPrivacyNotUpdated(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePrivacy/" + userId);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("account.change.user.data.failure");

        doThrow(UserPrivacyNotUpdatedException.class).when(accountService)
                .changeUserPrivacy(userPrivacyDTOToUpdate, userId);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(UserPrivacyNotUpdatedException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhenOtherErrorOccurs(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePrivacy/" + userId);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("request.failure");

        doThrow(IllegalStateException.class).when(accountService)
                .changeUserPrivacy(userPrivacyDTOToUpdate, userId);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(IllegalStateException.class)
                );
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowBindExceptionWhenInvalidDataProvided(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePrivacy/" + userId);
        String requestBody = objectMapper.writeValueAsString(new HashMap<>());

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value(is(messages.get("request.bind.exception"))))
                .andExpect(jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.regulationsAccepted")
                        .value(is(messages.get("field.required"))))
                .andExpect(jsonPath("$.errors.allowShowingTrainingsParticipation")
                        .value(is(messages.get("field.required"))))
                .andExpect(jsonPath("$.errors.allowShowingUserStatistics")
                        .value(is(messages.get("field.required"))))
                .andExpect(jsonPath("$.errors.allowShowingAvatar")
                        .value(is(messages.get("field.required"))));
    }

}
