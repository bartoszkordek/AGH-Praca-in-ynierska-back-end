package com.healthy.gym.user.controller;

import com.healthy.gym.user.service.UserService;
import com.healthy.gym.user.shared.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @MockBean
    private UserService userService;

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

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptUserRegistrationWhenRequestHasEveryFieldValid(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

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
                .header("Accept-Language", testedLocale.toString())
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(true),
                        jsonPath("$.message").value(messages.get("user.sing-up.success")),
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

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptUserRegistrationWhenRequestHasEveryFieldValidWithoutPhoneNumber(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

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
                .header("Accept-Language", testedLocale.toString())
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(true),
                        jsonPath("$.message").value(messages.get("user.sing-up.success")),
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

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRejectUserRegistrationWhenRequestHasEveryFieldInvalid(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

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
                .header("Accept-Language", testedLocale.toString())
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(false),
                        jsonPath("$.message").value(messages.get("user.sing-up.failure")),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.name").value(messages.get("field.name.failure")),
                        jsonPath("$.errors.surname").value(messages.get("field.surname.failure")),
                        jsonPath("$.errors.email").value(messages.get("field.email.failure")),
                        jsonPath("$.errors.phoneNumber").value(messages.get("field.phone.number.failure")),
                        jsonPath("$.errors.password").value(messages.get("field.password.failure")),
                        jsonPath("$.errors.matchingPassword").value(messages.get("field.password.match.failure"))
                )
        );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRejectUserRegistrationWhenRequestHasSomeFieldInvalidEmpty(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/users");
        String requestBody = "{" +
                "\"email\": \"jan.kowalski@wp.pl\",\n" +
                "\"password\": \"test12345\",\n" +
                "\"matchingPassword\": \"test12345\"" +
                "}";

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .header("Accept-Language", testedLocale.toString())
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(false),
                        jsonPath("$.message").value(messages.get("user.sing-up.failure")),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.name").value(messages.get("field.required")),
                        jsonPath("$.errors.surname").value(messages.get("field.required")),
                        jsonPath("$.errors.email").doesNotHaveJsonPath(),
                        jsonPath("$.errors.phoneNumber").doesNotHaveJsonPath(),
                        jsonPath("$.errors.password").doesNotHaveJsonPath(),
                        jsonPath("$.errors.matchingPassword").doesNotHaveJsonPath()
                )
        );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRejectUserRegistrationWhenRequestHasEveryFieldInvalidEmpty(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/users");
        String requestBody = "{}";

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .header("Accept-Language", testedLocale.toString())
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(false),
                        jsonPath("$.message").value(messages.get("user.sing-up.failure")),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.errors.name").value(messages.get("field.required")),
                        jsonPath("$.errors.surname").value(messages.get("field.required")),
                        jsonPath("$.errors.email").value(messages.get("field.required")),
                        jsonPath("$.errors.phoneNumber").doesNotHaveJsonPath(),
                        jsonPath("$.errors.password").value(messages.get("field.required")),
                        jsonPath("$.errors.matchingPassword").value(messages.get("field.required"))
                )
        );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRejectUserRegistrationWhenProvidedUserAlreadyExists(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

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
        when(userService.loadUserByUsername(any())).thenReturn(any());

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .header("Accept-Language", testedLocale.toString())
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(
                matchAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.success").value(false),
                        jsonPath("$.message").value(messages.get("user.sing-up.email.exists")),
                        jsonPath("$.errors").isMap(),
                        jsonPath("$.id").isEmpty()
                )
        );

    }

    private Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }

    private Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"user.sing-up.failure", "Rejestracja zakończona niepowodzeniem."},
                {"user.sing-up.success", "Użytkownik został zarejestrowany."},
                {"field.required", "Pole jest wymagane."},
                {"field.name.failure", "Imię powinno mieć od 2 do 60 znaków."},
                {"field.surname.failure", "Nazwisko powinno mieć od 2 do 60 znaków."},
                {"field.email.failure", "Proszę podać poprawny adres email."},
                {"field.phone.number.failure", "Niepoprawny format numeru telefonu."},
                {"field.password.failure", "Hasło powinno mieć od 8 do 24 znaków."},
                {"field.password.match.failure", "Podane hasła powinny być identyczne."},
                {"user.sing-up.email.exists", "Podany adres email jest już zajęty."},
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"user.sing-up.failure", "Registration failed."},
                {"user.sing-up.success", "Registration successful."},
                {"field.required", "Field is required."},
                {"field.name.failure", "Name should have from 2 to 60 characters."},
                {"field.surname.failure", "Surname should have from 2 to 60 characters."},
                {"field.email.failure", "Provide valid email address."},
                {"field.phone.number.failure", "Invalid phone number format."},
                {"field.password.failure", "Password should have from 8 to 24 characters."},
                {"field.password.match.failure", "Provided passwords should match."},
                {"user.sing-up.email.exists", "Provided email already exists."},
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private Locale convertEnumToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return new Locale("pl");
        return Locale.ENGLISH;
    }

    private enum TestCountry {
        POLAND, ENGLAND
    }
}