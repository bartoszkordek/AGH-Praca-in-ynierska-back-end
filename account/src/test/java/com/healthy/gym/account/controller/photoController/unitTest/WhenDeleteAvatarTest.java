package com.healthy.gym.account.controller.photoController.unitTest;

import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.controller.PhotoController;
import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
class WhenDeleteAvatarTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private PhotoService photoService;

    private String userToken;
    private String userId;
    private RequestBuilder request;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        uri = getUri(userId);
    }

    private URI getUri(String userId) throws URISyntaxException {
        return new URI("/photos/" + userId + "/avatar");
    }

    private RequestBuilder getRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldReturnAvatar(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String expectedMessage = messages.get("avatar.removed");
        when(photoService.removeAvatar(userId)).thenReturn(new PhotoDocument());

        request = getRequest(userToken, testedLocale);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                .andExpect(jsonPath("$.avatar").doesNotExist());
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenAvatarNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String expectedMessage = messages.get("avatar.not.found.exception");
        doThrow(UserAvatarNotFoundException.class).when(photoService).removeAvatar(userId);

        request = getRequest(userToken, testedLocale);

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
        doThrow(UsernameNotFoundException.class).when(photoService).removeAvatar(userId);

        request = getRequest(userToken, testedLocale);

        performAndTestException(status().isNotFound(), expectedMessage, UsernameNotFoundException.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenInternalErrorHappens(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String expectedMessage = messages.get("request.failure");
        doThrow(IllegalStateException.class).when(photoService).removeAvatar(userId);

        request = getRequest(userToken, testedLocale);

        performAndTestException(status().isInternalServerError(), expectedMessage, IllegalStateException.class);
    }

    @Nested
    class ShouldRejectRequest {

        private RequestBuilder getUnauthenticatedRequest(Locale locale) {
            return MockMvcRequestBuilders
                    .delete(uri)
                    .header("Accept-Language", locale.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();
            uri = getUri(invalidId);

            request = getUnauthenticatedRequest(testedLocale);

            String expectedMessage = messages.get("exception.access.denied");
            performAndTestAccessDenied(expectedMessage);
        }

        private void performAndTestAccessDenied(String expectedMessage) throws Exception {
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
        void whenIdDoesNotMatchUserIdInToken(TestCountry country) throws Exception {
            String invalidId = UUID.randomUUID().toString();
            uri = getUri(invalidId);

            Locale testedLocale = convertEnumToLocale(country);
            request = getRequest(userToken, testedLocale);

            Map<String, String> messages = getMessagesAccordingToLocale(country);
            String expectedMessage = messages.get("exception.access.denied");
            performAndTestAccessDenied(expectedMessage);
        }
    }
}