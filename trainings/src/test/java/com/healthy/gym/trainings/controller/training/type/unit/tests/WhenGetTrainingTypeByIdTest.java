package com.healthy.gym.trainings.controller.training.type.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.controller.TrainingTypeController;
import com.healthy.gym.trainings.dto.TrainingTypeDTO;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
@ActiveProfiles(value = "test")
class WhenGetTrainingTypeByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypeService trainingTypeService;
    private TrainingTypeDTO trainingTypeDTO;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        trainingTypeDTO = new TrainingTypeDTO(
                UUID.randomUUID().toString(),
                "Test name",
                "Test description",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                "testUrl"
        );

        uri = new URI("/trainingType/" + UUID.randomUUID());
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnTrainingTypeById(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        uri = new URI("/trainingType/" + UUID.randomUUID());
        when(trainingTypeService.getTrainingTypeById(anyString())).thenReturn(trainingTypeDTO);

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
                        jsonPath("$.name").value(is(trainingTypeDTO.getName())),
                        jsonPath("$.description").value(is(trainingTypeDTO.getDescription())),
                        jsonPath("$.trainingTypeId").value(is(trainingTypeDTO.getTrainingTypeId())),
                        jsonPath("$.duration").value(is("00:30:00")),
                        jsonPath("$.image").value(is("testUrl"))
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnTrainingTypeByIdWithoutImage(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        uri = new URI("/trainingType/" + UUID.randomUUID());
        trainingTypeDTO.setImageUrl(null);
        when(trainingTypeService.getTrainingTypeById(anyString())).thenReturn(trainingTypeDTO);

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
                        jsonPath("$.name").value(is(trainingTypeDTO.getName())),
                        jsonPath("$.description").value(is(trainingTypeDTO.getDescription())),
                        jsonPath("$.trainingTypeId").value(is(trainingTypeDTO.getTrainingTypeId())),
                        jsonPath("$.duration").value(is("00:30:00"))
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowTrainingTypeNotFoundExceptionWhenTrainingTypeDoesNotExist(TestCountry country)
            throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        uri = new URI("/trainingType/" + UUID.randomUUID());
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
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(TrainingTypeNotFoundException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhenInternalErrorOccurs(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        uri = new URI("/trainingType/" + UUID.randomUUID());
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
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(IllegalStateException.class)
                );
    }
}
