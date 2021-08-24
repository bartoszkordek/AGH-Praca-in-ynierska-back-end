package com.healthy.gym.trainings.controller.training.type.unit.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.TrainingTypeController;
import com.healthy.gym.trainings.dto.TrainingTypeDTO;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.activation.UnsupportedDataTypeException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingTypeController.class)
@ActiveProfiles(value = "test")
class WhenCreateTrainingTypeTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private TrainingTypeService trainingTypeService;

    private String managerToken;
    private String adminToken;
    private String userToken;
    private MockMultipartFile invalidFile;
    private MockMultipartFile validFile;
    private MockMultipartFile validBody;
    private MockMultipartFile invalidBody;
    private RequestBuilder request;
    private String expectedMessage;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getManagerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        invalidFile = new MockMultipartFile(
                "image",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes(StandardCharsets.UTF_8)
        );

        validFile = new MockMultipartFile(
                "image",
                "hello.png",
                MediaType.IMAGE_PNG_VALUE,
                "data".getBytes(StandardCharsets.UTF_8)
        );

        TrainingTypeRequest trainingTypeRequestValid = new TrainingTypeRequest();
        trainingTypeRequestValid.setName("Test name");
        trainingTypeRequestValid.setDescription("Test description");
        trainingTypeRequestValid.setDuration("02:30:00.000");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(trainingTypeRequestValid);

        validBody = new MockMultipartFile(
                "body",
                "body.json",
                MediaType.APPLICATION_JSON_VALUE,
                jsonBody.getBytes(StandardCharsets.UTF_8)
        );

        TrainingTypeRequest trainingTypeRequestInvalid = new TrainingTypeRequest();
        trainingTypeRequestInvalid.setName("a");
        trainingTypeRequestInvalid.setDescription("T");
        trainingTypeRequestInvalid.setDuration("02:30");
        String invalidJsonBody = objectMapper.writeValueAsString(trainingTypeRequestInvalid);

        invalidBody = new MockMultipartFile(
                "body",
                "body.json",
                MediaType.APPLICATION_JSON_VALUE,
                invalidJsonBody.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Nested
    class ShouldAcceptRequestWhenUserHasAdminOrManagerRoleAnd {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldCreateNewTrainingType(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType");

            request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(validFile)
                    .file(validBody)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            TrainingTypeDTO trainingTypeDTO = new TrainingTypeDTO(
                    UUID.randomUUID().toString(),
                    "Test name",
                    "Test description",
                    LocalTime.parse("02:30:00.000"),
                    "http://localhost:8020/trainings/trainingType/image/imageurl"
            );

            when(trainingTypeService.createTrainingType(
                    ArgumentMatchers.any(TrainingTypeRequest.class),
                    ArgumentMatchers.any(MockMultipartFile.class)
            )).thenReturn(trainingTypeDTO);

            expectedMessage = messages.get("training.type.created");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.errors").doesNotHaveJsonPath(),
                            jsonPath("$.trainingType.trainingTypeId")
                                    .value(is(trainingTypeDTO.getTrainingTypeId())),
                            jsonPath("$.trainingType.name").value(is("Test name")),
                            jsonPath("$.trainingType.description").value(is("Test description")),
                            jsonPath("$.trainingType.duration").value(is("02:30:00")),
                            jsonPath("$.trainingType.image")
                                    .value(is("http://localhost:8020/trainings/trainingType/image/imageurl"))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldCreateNewTrainingTypeWithoutImage(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType");

            request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(validBody)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            TrainingTypeDTO trainingTypeDTO = new TrainingTypeDTO(
                    UUID.randomUUID().toString(),
                    "Test name",
                    "Test description",
                    LocalTime.parse("02:30:00.000"),
                    "http://localhost:8020/trainings/trainingType/image/imageurl"
            );

            when(trainingTypeService.createTrainingType(
                    ArgumentMatchers.any(TrainingTypeRequest.class),
                    ArgumentMatchers.isNull()
            )).thenReturn(trainingTypeDTO);

            expectedMessage = messages.get("training.type.created");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.errors").doesNotHaveJsonPath(),
                            jsonPath("$.trainingType.trainingTypeId")
                                    .value(is(trainingTypeDTO.getTrainingTypeId())),
                            jsonPath("$.trainingType.name").value(is("Test name")),
                            jsonPath("$.trainingType.description").value(is("Test description")),
                            jsonPath("$.trainingType.duration").value(is("02:30:00")),
                            jsonPath("$.trainingType.image")
                                    .value(is("http://localhost:8020/trainings/trainingType/image/imageurl"))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowUnsupportedDataTypeExceptionWhenInvalidFileProvided(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType");

            expectedMessage = messages.get("exception.unsupported.data.type");

            request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(invalidFile)
                    .file(validBody)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            performRequestAndTestErrorResponse(status().isBadRequest(), UnsupportedDataTypeException.class);
        }

        private void performRequestAndTestErrorResponse(
                ResultMatcher matcher,
                Class<? extends Exception> expectedException
        ) throws Exception {
            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matcher)
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(expectedException)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowMultipartBodyExceptionWhenInvalidBody(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType");

            RequestBuilder request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(validFile)
                    .file(invalidBody)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            expectedMessage = messages.get("exception.multipart.body");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.errors").value(is(notNullValue())),
                            jsonPath("$.errors.name")
                                    .value(is(messages.get("field.required"))),
                            jsonPath("$.errors.description")
                                    .value(is(messages.get("field.required"))),
                            jsonPath("$.errors.duration")
                                    .value(is(messages.get("exception.duration.format"))),
                            jsonPath("$.image").doesNotHaveJsonPath(),
                            jsonPath("$.name").doesNotHaveJsonPath(),
                            jsonPath("$.description").doesNotHaveJsonPath()
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowDuplicatedTrainingTypeExceptionWhenTrainingTypeNameAlreadyExists(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType");

            request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(validFile)
                    .file(validBody)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            doThrow(DuplicatedTrainingTypeException.class)
                    .when(trainingTypeService)
                    .createTrainingType(
                            ArgumentMatchers.any(TrainingTypeRequest.class),
                            ArgumentMatchers.any(MockMultipartFile.class)
                    );

            expectedMessage = messages.get("exception.duplicated.training.type");

            performRequestAndTestErrorResponse(status().isBadRequest(), DuplicatedTrainingTypeException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType");

            request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(validFile)
                    .file(validBody)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            doThrow(IllegalStateException.class)
                    .when(trainingTypeService)
                    .createTrainingType(
                            ArgumentMatchers.any(TrainingTypeRequest.class),
                            ArgumentMatchers.any(MockMultipartFile.class)
                    );

            expectedMessage = messages.get("exception.internal.error");
            performRequestAndTestErrorResponse(status().isInternalServerError(), IllegalStateException.class);
        }
    }

    @Nested
    class ShouldRejectRequest {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserDoesNotHaveAdminOrManagerRole(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType");

            RequestBuilder request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(validFile)
                    .file(validBody)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

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

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType");

            RequestBuilder request = MockMvcRequestBuilders
                    .multipart(uri)
                    .file(validFile)
                    .file(validBody)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
