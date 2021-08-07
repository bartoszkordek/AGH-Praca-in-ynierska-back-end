package com.healthy.gym.trainings.controller.individual.training.trainer.unit.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.individual.training.TrainerIndividualTrainerController;
import com.healthy.gym.trainings.model.request.IndividualTrainingAcceptanceRequest;
import com.healthy.gym.trainings.service.individual.training.TrainerIndividualTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerIndividualTrainerController.class)
class AcceptIndividualTrainingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private TrainerIndividualTrainingService trainerIndividualTrainingService;

    private String adminToken;
    private String employeeToken;
    private String managerToken;
    private String trainerToken;
    private String userToken;
    private String trainingId;
    private URI uri;
    private String content;

    @BeforeEach
    void setUp() throws URISyntaxException, JsonProcessingException {
        adminToken = tokenFactory.getAdminToken();
        employeeToken = tokenFactory.getEmployeeToken();
        managerToken = tokenFactory.getManagerToken();
        trainerToken = tokenFactory.getTrainerToken();
        userToken = tokenFactory.getUserToken();

        trainingId = UUID.randomUUID().toString();
        uri = getUri(trainingId);
        IndividualTrainingAcceptanceRequest request = new IndividualTrainingAcceptanceRequest(2);
        ObjectMapper objectMapper = new ObjectMapper();
        content = objectMapper.writeValueAsString(request);
    }

    private URI getUri(String trainingId) throws URISyntaxException {
        return new URI("/individual/trainer/" + trainingId + "/accept");
    }

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    @Nested
    class ShouldRejectRequest {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserTriesToGetData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = getValidRequest(userToken, testedLocale);
            String expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied(request, expectedMessage);
        }

        private void performAndTestAccessDenied(RequestBuilder request, String expectedMessage) throws Exception {
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
        void whenManagerTriesToGetData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = getValidRequest(managerToken, testedLocale);
            String expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied(request, expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenEmployeeTriesToGetData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = getValidRequest(employeeToken, testedLocale);
            String expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied(request, expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
