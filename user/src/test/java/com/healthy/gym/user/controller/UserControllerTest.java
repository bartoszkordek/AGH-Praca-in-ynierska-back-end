package com.healthy.gym.user.controller;

import com.healthy.gym.user.service.UserService;
import com.healthy.gym.user.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private UserService userService;

    private String userSingUpFailurePL,
            userSingUpSuccessPL,
            fieldRequiredPL,
            fieldNameFailurePL,
            fieldSurnameRequiredPL,
            fieldEmailFailurePL,
            fieldPhoneNumberFailurePL,
            fieldPasswordFailurePL,
            fieldPasswordMatchFailurePL;

    @BeforeEach
    void setUp() {
        Locale polandLocale = new Locale("pl", "PL");
        userSingUpFailurePL = messageSource.getMessage("user.sing-up.failure", null, polandLocale);
        userSingUpSuccessPL = messageSource.getMessage("user.sing-up.success", null, polandLocale);
        fieldRequiredPL = messageSource.getMessage("field.required", null, polandLocale);
        fieldNameFailurePL = messageSource.getMessage("field.name.failure", null, polandLocale);
        fieldSurnameRequiredPL = messageSource.getMessage("field.surname.failure", null, polandLocale);
        fieldEmailFailurePL = messageSource.getMessage("field.email.failure", null, polandLocale);
        fieldPhoneNumberFailurePL =
                messageSource.getMessage("field.phone.number.failure", null, polandLocale);
        fieldPasswordFailurePL = messageSource.getMessage("field.password.failure", null, polandLocale);
        fieldPasswordMatchFailurePL =
                messageSource.getMessage("field.password.match.failure", null, polandLocale);
    }

    @Test
    @WithMockUser
    void shouldReturnDefaultStatusMessage() throws Exception {
        URI uri = new URI("/users/status");

        when(userService.status()).thenReturn("OK");

        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK")));
    }

    @Test
    void shouldAcceptUserRegistrationWhenRequestHasEveryFieldValid() throws Exception {
        URI uri = new URI("/users");
        String requestBody = "{" +
                "\"name\": \"Jan\",\n" +
                "\"surname\": \"Kowalski\",\n" +
                "\"email\": \"jan.kowalski@wp.pl\",\n" +
                "\"phone\": \"+48 685 263 683\",\n" +
                "\"password\": \"test12345\",\n" +
                "\"matchingPassword\": \"test12345\"" +
                "}";

        UserDTO responseUserDTO = new UserDTO();
        responseUserDTO.setUserId("test");
        when(userService.loadUserByUsername(any())).thenThrow(UsernameNotFoundException.class);
        when(userService.createUser(any())).thenReturn(responseUserDTO);

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(true),
                        jsonPath("$.message").value(userSingUpSuccessPL),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.name").doesNotHaveJsonPath(),
                        jsonPath("$.errors.surname").doesNotHaveJsonPath(),
                        jsonPath("$.errors.email").doesNotHaveJsonPath(),
                        jsonPath("$.errors.phoneNumber").doesNotHaveJsonPath(),
                        jsonPath("$.errors.password").doesNotHaveJsonPath(),
                        jsonPath("$.errors.matchingPassword").doesNotHaveJsonPath()
                )
        );
    }


    @Test
    void shouldAcceptUserRegistrationWhenRequestHasEveryFieldValidWithoutPhoneNumber() throws Exception {
        URI uri = new URI("/users");
        String requestBody = "{" +
                "\"name\": \"Jan\",\n" +
                "\"surname\": \"Kowalski\",\n" +
                "\"email\": \"jan.kowalski3@wp.pl\",\n" +
                "\"password\": \"test12345\",\n" +
                "\"matchingPassword\": \"test12345\"" +
                "}";

        UserDTO responseUserDTO = new UserDTO();
        responseUserDTO.setUserId("test");
        when(userService.loadUserByUsername(any())).thenThrow(UsernameNotFoundException.class);
        when(userService.createUser(any())).thenReturn(responseUserDTO);

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(true),
                        jsonPath("$.message").value(userSingUpSuccessPL),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.name").doesNotHaveJsonPath(),
                        jsonPath("$.errors.surname").doesNotHaveJsonPath(),
                        jsonPath("$.errors.email").doesNotHaveJsonPath(),
                        jsonPath("$.errors.phoneNumber").doesNotHaveJsonPath(),
                        jsonPath("$.errors.password").doesNotHaveJsonPath(),
                        jsonPath("$.errors.matchingPassword").doesNotHaveJsonPath()
                )
        );
    }

    @Test
    void shouldRejectUserRegistrationWhenRequestHasEveryFieldInvalid() throws Exception {
        URI uri = new URI("/users");
        String requestBody = "{" +
                "\"name\": \"G\",\n" +
                "\"surname\": \"Z\",\n" +
                "\"email\": \"g.kowalskiwp.pl\",\n" +
                "\"phone\": \"685 263 6831\",\n" +
                "\"password\": \"test123\",\n" +
                "\"matchingPassword\": \"testtest1234\"" +
                "}";

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(false),
                        jsonPath("$.message").value(userSingUpFailurePL),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.name").value(fieldNameFailurePL),
                        jsonPath("$.errors.surname").value(fieldSurnameRequiredPL),
                        jsonPath("$.errors.email").value(fieldEmailFailurePL),
                        jsonPath("$.errors.phoneNumber").value(fieldPhoneNumberFailurePL),
                        jsonPath("$.errors.password").value(fieldPasswordFailurePL),
                        jsonPath("$.errors.matchingPassword").value(fieldPasswordMatchFailurePL)
                )
        );
    }

    @Test
    void shouldRejectUserRegistrationWhenRequestHasSomeFieldInvalidEmpty() throws Exception {
        URI uri = new URI("/users");
        String requestBody = "{" +
                "\"email\": \"jan.kowalski@wp.pl\",\n" +
                "\"password\": \"test12345\",\n" +
                "\"matchingPassword\": \"test12345\"" +
                "}";

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(false),
                        jsonPath("$.message").value(userSingUpFailurePL),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.name").value(fieldRequiredPL),
                        jsonPath("$.errors.surname").value(fieldRequiredPL),
                        jsonPath("$.errors.email").doesNotHaveJsonPath(),
                        jsonPath("$.errors.phoneNumber").doesNotHaveJsonPath(),
                        jsonPath("$.errors.password").doesNotHaveJsonPath(),
                        jsonPath("$.errors.matchingPassword").doesNotHaveJsonPath()
                )
        );
    }

    @Test
    void shouldRejectUserRegistrationWhenRequestHasEveryFieldInvalidEmpty() throws Exception {
        URI uri = new URI("/users");
        String requestBody = "{}";

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(false),
                        jsonPath("$.message").value(userSingUpFailurePL),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.name").value(fieldRequiredPL),
                        jsonPath("$.errors.surname").value(fieldRequiredPL),
                        jsonPath("$.errors.email").value(fieldRequiredPL),
                        jsonPath("$.errors.phoneNumber").doesNotHaveJsonPath(),
                        jsonPath("$.errors.password").value(fieldRequiredPL),
                        jsonPath("$.errors.matchingPassword").value(fieldRequiredPL)
                )
        );
    }
}