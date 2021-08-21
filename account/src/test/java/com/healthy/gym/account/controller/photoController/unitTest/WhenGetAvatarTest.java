package com.healthy.gym.account.controller.photoController.unitTest;

import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.controller.PhotoController;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhotoController.class)
@ActiveProfiles(value = "test")
class WhenGetAvatarTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private PhotoService photoService;

    private String userToken;
    private String userId;
    private URI uri;
    private RequestBuilder request;

    @BeforeEach
    void setUp() throws URISyntaxException {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        uri = getUri(userId);
    }

    private URI getUri(String userId) throws URISyntaxException {
        return new URI("/photos/" + userId + "/avatar/" + UUID.randomUUID());
    }

    private RequestBuilder performRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldReturnAvatar(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        when(photoService.getAvatar(userId)).thenReturn("data".getBytes(StandardCharsets.UTF_8));

        request = performRequest(userToken, testedLocale);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes("data".getBytes(StandardCharsets.UTF_8)))
                .andExpect(header().exists("Content-Length"))
                .andExpect(header().exists("Cache-Control"))
                .andExpect(header().exists("ETag"));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenAvatarNotFound(TestCountry country) throws Exception {
        doThrow(UserAvatarNotFoundException.class).when(photoService).getAvatar(userId);

        Locale testedLocale = convertEnumToLocale(country);
        request = performRequest(userToken, testedLocale);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("avatar.not.found.exception");

        performAndTestException(status().isNotFound(), expectedMessage, UserAvatarNotFoundException.class);
    }

    private void performAndTestException(
            ResultMatcher matcher,
            String expectedMessage,
            Class<? extends Exception> exception
    ) throws Exception {

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matcher)
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(exception)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenUsernameNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String expectedMessage = messages.get("exception.account.not.found");
        doThrow(UsernameNotFoundException.class).when(photoService).getAvatar(userId);

        request = performRequest(userToken, testedLocale);
        performAndTestException(status().isNotFound(), expectedMessage, UsernameNotFoundException.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenInternalErrorHappens(TestCountry country) throws Exception {
        doThrow(IllegalStateException.class).when(photoService).getAvatar(userId);

        Locale testedLocale = convertEnumToLocale(country);
        request = performRequest(userToken, testedLocale);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("request.failure");
        performAndTestException(status().isInternalServerError(), expectedMessage, IllegalStateException.class);
    }
}
