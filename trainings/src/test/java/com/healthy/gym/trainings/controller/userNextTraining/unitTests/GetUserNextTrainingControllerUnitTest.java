package com.healthy.gym.trainings.controller.userNextTraining.unitTests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.UserNextTrainingController;
import com.healthy.gym.trainings.dto.BasicTrainingDTO;
import com.healthy.gym.trainings.exception.notfound.UserNextTrainingNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingService;
import com.healthy.gym.trainings.service.individual.training.UserIndividualTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;

@WebMvcTest(UserNextTrainingController.class)
@ActiveProfiles(value = "test")
public class GetUserNextTrainingControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private UserGroupTrainingService userGroupTrainingService;

    @MockBean
    private UserIndividualTrainingService userIndividualTrainingService;

    private String employeeToken;
    private String otherUserToken;
    private String adminToken;
    private String managerToken;
    private String userToken;
    private String userId;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getManagerToken(managerId);

        String employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getEmployeeToken(employeeId);

        String otherUserId = UUID.randomUUID().toString();
        otherUserToken = tokenFactory.getUserToken(otherUserId);

        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        uri = new URI("/user/");
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetUserNextTrainingWhenValidUserIdAndTrainingExist(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+userId+"/next")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON);

        var now = LocalDateTime.now();

        String groupTrainingId = UUID.randomUUID().toString();
        LocalDateTime groupTrainingStartDate = now.plusHours(10);
        BasicTrainingDTO group = new BasicTrainingDTO();
        group.setTrainingId(groupTrainingId);
        group.setTitle("TRX");
        group.setStartDate(groupTrainingStartDate.toString());
        group.setLocation("Sala TRX");

        String individualTrainingId = UUID.randomUUID().toString();
        LocalDateTime individualTrainingStartDate = now.plusHours(11);
        BasicTrainingDTO individual = new BasicTrainingDTO();
        individual.setTrainingId(individualTrainingId);
        individual.setTitle("Trening indywidualny");
        individual.setStartDate(individualTrainingStartDate.toString());
        individual.setLocation("Sala nr 10");


        when(userGroupTrainingService.getMyNextTraining(userId))
                .thenReturn(group);
        when(userIndividualTrainingService.getMyNextTraining(userId))
                .thenReturn(individual);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(is(groupTrainingId)),
                        jsonPath("$.title").value(is("TRX")),
                        jsonPath("$.startDate").value(is(groupTrainingStartDate.toString())),
                        jsonPath("$.location").value(is("Sala TRX"))

                ));
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetUserNextGroupTrainingWhenIndividualNotExist(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+userId+"/next")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON);

        var now = LocalDateTime.now();

        String groupTrainingId = UUID.randomUUID().toString();
        LocalDateTime groupTrainingStartDate = now.plusHours(10);
        BasicTrainingDTO group = new BasicTrainingDTO();
        group.setTrainingId(groupTrainingId);
        group.setTitle("TRX");
        group.setStartDate(groupTrainingStartDate.toString());
        group.setLocation("Sala TRX");

        when(userGroupTrainingService.getMyNextTraining(userId))
                .thenReturn(group);
        when(userIndividualTrainingService.getMyNextTraining(userId))
                .thenReturn(null);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(is(groupTrainingId)),
                        jsonPath("$.title").value(is("TRX")),
                        jsonPath("$.startDate").value(is(groupTrainingStartDate.toString())),
                        jsonPath("$.location").value(is("Sala TRX"))

                ));
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetUserNextIndividualTrainingWhenGroupNotExist(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+userId+"/next")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON);

        var now = LocalDateTime.now();

        String individualTrainingId = UUID.randomUUID().toString();
        LocalDateTime individualTrainingStartDate = now.plusHours(11);
        BasicTrainingDTO individual = new BasicTrainingDTO();
        individual.setTrainingId(individualTrainingId);
        individual.setTitle("Trening indywidualny");
        individual.setStartDate(individualTrainingStartDate.toString());
        individual.setLocation("Sala nr 10");


        when(userGroupTrainingService.getMyNextTraining(userId))
                .thenReturn(null);
        when(userIndividualTrainingService.getMyNextTraining(userId))
                .thenReturn(individual);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(is(individualTrainingId)),
                        jsonPath("$.title").value(is("Trening indywidualny")),
                        jsonPath("$.startDate").value(is(individualTrainingStartDate.toString())),
                        jsonPath("$.location").value(is("Sala nr 10"))

                ));
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetNexTrainingWhenInvalidUserId(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String invalidUserId = UUID.randomUUID().toString();

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+invalidUserId+"/next")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.not.found.user.id");

        doThrow(UserNotFoundException.class)
                .when(userGroupTrainingService)
                .getMyNextTraining(invalidUserId);

        doThrow(UserNotFoundException.class)
                .when(userIndividualTrainingService)
                .getMyNextTraining(invalidUserId);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(UserNotFoundException.class)
                );
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetNexTrainingWhenNoTrainings(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+userId+"/next")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.user.next.training.not.found");

        when(userGroupTrainingService.getMyNextTraining(userId))
                .thenReturn(null);
        when(userIndividualTrainingService.getMyNextTraining(userId))
                .thenReturn(null);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(UserNextTrainingNotFoundException.class)
                );
    }

    @Nested
    class ShouldNotGetUserNextTrainingWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+userId+"/next")
                    .header("Accept-Language", testedLocale.toString());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogInAsOtherUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+userId+"/next")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", otherUserToken)
                    .contentType(MediaType.APPLICATION_JSON);

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
    }


}
