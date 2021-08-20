package com.healthy.gym.trainings.controller.training.type.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.TrainingTypeController;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.service.TrainingTypeService;
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
@ActiveProfiles(value = "test")
class WhenRemoveTrainingTypeByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private TrainingTypeService trainingTypeService;

    private String managerToken;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getManagerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRemoveTrainingType(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String trainingTypeId = UUID.randomUUID().toString();

        TrainingTypeDocument trainingTypeDocument = new TrainingTypeDocument(
                trainingTypeId,
                "Test name",
                "Test description",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                null
        );

        URI uri = new URI("/trainingType/" + trainingTypeId);

        RequestBuilder request = MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        String expectedMessage = messages.get("training.type.removed");

        when(trainingTypeService.removeTrainingTypeById(anyString()))
                .thenReturn(trainingTypeDocument);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ))
                .andExpect(matchAll(
                        jsonPath("$.message").value(is(expectedMessage)),
                        jsonPath("$.errors").doesNotHaveJsonPath(),
                        jsonPath("$.image").doesNotHaveJsonPath(),
                        jsonPath("$.trainingTypeId").value(is(trainingTypeId)),
                        jsonPath("$.name").value(is("Test name")),
                        jsonPath("$.description").value(is("Test description")),
                        jsonPath("$.duration").value(is("00:30:00.000"))
                ));
    }

    @Nested
    class ShouldAcceptRequestAndThrowException {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowTrainingTypeNotFoundExceptionWhenTrainingTypeDoesNotExist(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType/" + UUID.randomUUID());

            doThrow(TrainingTypeNotFoundException.class)
                    .when(trainingTypeService)
                    .removeTrainingTypeById(anyString());

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
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
                    .removeTrainingTypeById(anyString());

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
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

    @Nested
    class ShouldRejectRequest {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserDoesNotHaveAdminOrManagerRole(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("/trainingType/" + UUID.randomUUID());

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri)
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

            URI uri = new URI("/trainingType/" + UUID.randomUUID());

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
