package com.healthy.gym.trainings.controller.trainingTypeController.unitTest;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.controller.TrainingTypeController;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.service.TrainingTypeService;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingTypeController.class)
class WhenGetTrainingTypeByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypeService trainingTypeService;

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnTrainingTypeById(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/trainingType/" + UUID.randomUUID());

        TrainingTypeDocument trainingTypeDocument = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name",
                "Test description",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                null
        );

        when(trainingTypeService.getTrainingTypeById(anyString())).thenReturn(trainingTypeDocument);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").doesNotHaveJsonPath(),
                        jsonPath("$.errors").doesNotHaveJsonPath(),
                        jsonPath("$.image").doesNotHaveJsonPath(),
                        jsonPath("$.name").value(is(trainingTypeDocument.getName())),
                        jsonPath("$.description").value(is(trainingTypeDocument.getDescription())),
                        jsonPath("$.trainingTypeId").value(is(trainingTypeDocument.getTrainingTypeId())),
                        jsonPath("$.duration").value(is("00:30:00.000"))
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowTrainingTypeNotFoundExceptionWhenTrainingTypeDoesNotExist(TestCountry country)
            throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/trainingType/" + UUID.randomUUID());

        doThrow(TrainingTypeNotFoundException.class)
                .when(trainingTypeService)
                .getTrainingTypeById(anyString());

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        String expectedMessage = messages.get("exception.not.found.training.type");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(TrainingTypeNotFoundException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhenInternalErrorOccurs(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/trainingType/" + UUID.randomUUID());

        doThrow(IllegalStateException.class)
                .when(trainingTypeService)
                .getTrainingTypeById(anyString());

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        String expectedMessage = messages.get("exception.internal.error");

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
