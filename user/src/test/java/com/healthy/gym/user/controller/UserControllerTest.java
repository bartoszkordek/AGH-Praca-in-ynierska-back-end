package com.healthy.gym.user.controller;

import com.healthy.gym.user.service.UserService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnDefaultStatusMessage() throws Exception {
        URI uri = new URI("/users/status");

        when(userService.status()).thenReturn("OK");

        mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK")));
    }

    @Test
    void shouldRejectUserRegistrationWhenInvalidRequestWasSend() throws Exception {
        URI uri = new URI("/users/");
        String newUser = "{" +
                "\"name\": \"G\",\n" +
                "\"surname\": \"Z\",\n" +
                "\"email\": \"g.zacharskiwp.pl\",\n" +
                "\"phone\": \"665 763 6831\",\n" +
                "\"password\": \"testtest123\",\n" +
                "\"matchingPassword\": \"testtest1234\"" +
                "}";

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .content(newUser)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value("false"),
                        jsonPath("$.message").value("Rejestracja zakończona niepowodzeniem."),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.matchingPassword").value("Podane hasła się nie zgadzają"),
                        jsonPath("$.errors.surname", Is.is("Pole nazwisko powinno mieć od 2 do 60 znaków."))
                )
        );
    }
}