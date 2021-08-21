package com.healthy.gym.account.controller.photoController.unitTest;

import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.controller.PhotoController;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.activation.UnsupportedDataTypeException;
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
class WhenSetAvatarTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private PhotoService photoService;

    private String userToken;
    private String userId;
    private MockMultipartFile invalidFile;
    private MockMultipartFile validFile;
    private RequestBuilder request;
    private URI uri;


    @BeforeEach
    void setUp() throws URISyntaxException {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        invalidFile = new MockMultipartFile(
                "avatar",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes(StandardCharsets.UTF_8)
        );

        validFile = new MockMultipartFile(
                "avatar",
                "hello.png",
                MediaType.IMAGE_PNG_VALUE,
                "data".getBytes(StandardCharsets.UTF_8)
        );
        uri = getUri(userId);
    }

    private URI getUri(String userId) throws URISyntaxException {
        return new URI("/photos/" + userId + "/avatar");
    }

    private RequestBuilder getRequest(MockMultipartFile file, String token, Locale locale) {
        return MockMvcRequestBuilders
                .multipart(uri)
                .file(file)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldSetUserAvatar(TestCountry country) throws Exception {
        when(photoService.setAvatar(userId, validFile))
                .thenReturn("http://localhost:8020/account/photos/" + userId + "/avatar");

        Locale testedLocale = convertEnumToLocale(country);
        request = getRequest(validFile, userToken, testedLocale);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("avatar.update.success");
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                .andExpect(jsonPath("$.avatar")
                        .value(is("http://localhost:8020/account/photos/" + userId + "/avatar")));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowUsernameNotFoundExceptionWhenUserNotFound(TestCountry country)
            throws Exception {
        doThrow(UsernameNotFoundException.class).when(photoService).setAvatar(userId, validFile);

        Locale testedLocale = convertEnumToLocale(country);
        request = getRequest(validFile, userToken, testedLocale);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("exception.account.not.found");
        performAndTestException(status().isNotFound(), expectedMessage, UsernameNotFoundException.class);
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
    void shouldAcceptRequestAndShouldThrowPhotoSavingExceptionWhenErrorHappens(TestCountry country)
            throws Exception {
        doThrow(PhotoSavingException.class).when(photoService).setAvatar(userId, validFile);

        Locale testedLocale = convertEnumToLocale(country);
        request = getRequest(validFile, userToken, testedLocale);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("avatar.update.failure");
        performAndTestException(status().isBadRequest(), expectedMessage, PhotoSavingException.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowUnsupportedDataTypeExceptionWhenInvalidFileProvided(TestCountry country)
            throws Exception {
        Locale testedLocale = convertEnumToLocale(country);
        request = getRequest(invalidFile, userToken, testedLocale);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("avatar.update.data.exception");
        performAndTestException(status().isBadRequest(), expectedMessage, UnsupportedDataTypeException.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenInternalErrorHappens(TestCountry country) throws Exception {
        doThrow(IllegalStateException.class).when(photoService).setAvatar(userId, validFile);

        Locale testedLocale = convertEnumToLocale(country);
        request = getRequest(validFile, userToken, testedLocale);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("request.failure");
        performAndTestException(status().isInternalServerError(), expectedMessage, IllegalStateException.class);
    }

    @Nested
    class ShouldRejectRequest {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenIdDoesNotMatchUserIdInToken(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();
            uri = getUri(invalidId);

            request = getRequest(invalidFile, userToken, testedLocale);

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
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();
            uri = getUri(invalidId);

            request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(invalidFile)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            String expectedMessage = messages.get("exception.access.denied");
            performAndTestAccessDenied(expectedMessage);
        }
    }


}
