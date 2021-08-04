package com.healthy.gym.trainings.controller.training.type.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.controller.TrainingTypeController;
import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.bson.types.Binary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingTypeController.class)
class WhenGetAllTrainingTypesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypeService trainingTypeService;

    private ImageDocument imageDocument1;
    private ImageDocument imageDocument2;

    @BeforeEach
    void setUp() throws IOException {
        Resource imageResource1 = new ClassPathResource("testImages/shiba_inu_smile_1.jpg");

        imageDocument1 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource1)),
                MediaType.IMAGE_JPEG_VALUE
        );

        Resource imageResource2 = new ClassPathResource("testImages/shiba_inu_smile_2.jpg");

        imageDocument2 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource2)),
                MediaType.IMAGE_JPEG_VALUE
        );
    }

    private byte[] getImageBytes(Resource imageResource) throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        return inputStream.readAllBytes();
    }

    private String getExpectedImageBase64(ImageDocument imageDocument) {
        byte[] imageData = imageDocument.getImageData().getData();
        return Base64.getEncoder()
                .encodeToString(imageData);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnAllTrainingTypes(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name1",
                "Test description1",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                imageDocument1
        );
        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name2",
                "Test description2",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                imageDocument2
        );

        URI uri = new URI("/trainingType");
        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(trainingTypeService.getAllTrainingTypes())
                .thenReturn(List.of(trainingTypeDocument1, trainingTypeDocument2));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").isArray()
                ))
                .andExpect(matchAll(
                        jsonPath("$.[0].trainingTypeId").value(is(trainingTypeDocument1.getTrainingTypeId())),
                        jsonPath("$.[0].name").value(is(trainingTypeDocument1.getName())),
                        jsonPath("$.[0].description").value(is(trainingTypeDocument1.getDescription())),
                        jsonPath("$.[0].duration").value(is("00:30:00.000")),
                        jsonPath("$.[0].image.data").value(is(getExpectedImageBase64(imageDocument1))),
                        jsonPath("$.[0].image.format").value(is(MediaType.IMAGE_JPEG_VALUE)),
                        jsonPath("$.[0].message").doesNotHaveJsonPath(),
                        jsonPath("$.[0].errors").doesNotHaveJsonPath()
                ))
                .andExpect(matchAll(
                        jsonPath("$.[1].trainingTypeId").value(is(trainingTypeDocument2.getTrainingTypeId())),
                        jsonPath("$.[1].name").value(is(trainingTypeDocument2.getName())),
                        jsonPath("$.[1].description").value(is(trainingTypeDocument2.getDescription())),
                        jsonPath("$.[1].duration").value(is("00:30:00.000")),
                        jsonPath("$.[1].image.data").value(is(getExpectedImageBase64(imageDocument2))),
                        jsonPath("$.[1].image.format").value(is(MediaType.IMAGE_JPEG_VALUE)),
                        jsonPath("$.[1].message").doesNotHaveJsonPath(),
                        jsonPath("$.[1].errors").doesNotHaveJsonPath()
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnAllTrainingTypesWithoutImages(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name1",
                "Test description1",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                null
        );
        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name2",
                "Test description2",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                null
        );

        URI uri = new URI("/trainingType");
        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(trainingTypeService.getAllTrainingTypes())
                .thenReturn(List.of(trainingTypeDocument1, trainingTypeDocument2));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").isArray()
                ))
                .andExpect(matchAll(
                        jsonPath("$.[0].trainingTypeId").value(is(trainingTypeDocument1.getTrainingTypeId())),
                        jsonPath("$.[0].name").value(is(trainingTypeDocument1.getName())),
                        jsonPath("$.[0].description").value(is(trainingTypeDocument1.getDescription())),
                        jsonPath("$.[0].duration").value(is("00:30:00.000")),
                        jsonPath("$.[0].image").doesNotHaveJsonPath(),
                        jsonPath("$.[0].message").doesNotHaveJsonPath(),
                        jsonPath("$.[0].errors").doesNotHaveJsonPath()
                ))
                .andExpect(matchAll(
                        jsonPath("$.[1].trainingTypeId").value(is(trainingTypeDocument2.getTrainingTypeId())),
                        jsonPath("$.[1].name").value(is(trainingTypeDocument2.getName())),
                        jsonPath("$.[1].description").value(is(trainingTypeDocument2.getDescription())),
                        jsonPath("$.[1].duration").value(is("00:30:00.000")),
                        jsonPath("$.[1].image").doesNotHaveJsonPath(),
                        jsonPath("$.[1].message").doesNotHaveJsonPath(),
                        jsonPath("$.[1].errors").doesNotHaveJsonPath()
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowTrainingTypeNotFoundExceptionWhenNoTrainingTypeFound(TestCountry country)
            throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/trainingType");
        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(TrainingTypeNotFoundException.class).when(trainingTypeService).getAllTrainingTypes();
        String expectedMessage = messages.get("exception.not.found.training.type.all");

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

        URI uri = new URI("/trainingType");
        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(IllegalStateException.class).when(trainingTypeService).getAllTrainingTypes();
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
