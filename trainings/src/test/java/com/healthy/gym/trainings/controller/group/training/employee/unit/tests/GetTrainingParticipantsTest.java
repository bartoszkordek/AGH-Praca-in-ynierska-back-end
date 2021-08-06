package com.healthy.gym.trainings.controller.group.training.employee.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.group.training.EmployeeGroupTrainingController;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.service.group.training.EmployeeGroupTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestUserDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeGroupTrainingController.class)
class GetTrainingParticipantsTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private EmployeeGroupTrainingService employeeGroupTrainingService;

    private String employeeToken;
    private String adminToken;
    private String trainerToken;
    private String managerToken;
    private String userToken;
    private URI uri;
    private String groupTrainingId;
    private List<BasicUserInfoDTO> basicList;
    private List<BasicUserInfoDTO> reserveList;

    @BeforeEach
    void setUp() throws URISyntaxException {
        groupTrainingId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken();
        employeeToken = tokenFactory.getEmployeeToken();
        adminToken = tokenFactory.getAdminToken();
        trainerToken = tokenFactory.getTrainerToken();
        managerToken = tokenFactory.getManagerToken();
        uri = new URI("/group/" + groupTrainingId + "/participants");
        basicList = List.of(getTestUserDTO(), getTestUserDTO());
        reserveList = List.of(getTestUserDTO());
    }

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnGroupTraining(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        when(employeeGroupTrainingService.getTrainingParticipants(groupTrainingId))
                .thenReturn(new ParticipantsDTO(basicList, reserveList));

        RequestBuilder request = getValidRequest(employeeToken, testedLocale);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.message").doesNotExist()
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.basicList[0].userId").value(is(basicList.get(0).getUserId())),
                                jsonPath("$.basicList[0].name").value(is(basicList.get(0).getName())),
                                jsonPath("$.basicList[0].surname").value(is(basicList.get(0).getSurname())),
                                jsonPath("$.basicList[0].avatar").doesNotExist()
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.basicList[1].userId").value(is(basicList.get(1).getUserId())),
                                jsonPath("$.basicList[1].name").value(is(basicList.get(1).getName())),
                                jsonPath("$.basicList[1].surname").value(is(basicList.get(1).getSurname())),
                                jsonPath("$.basicList[1].avatar").doesNotExist()
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.reserveList[0].userId").value(is(reserveList.get(0).getUserId())),
                                jsonPath("$.reserveList[0].name").value(is(reserveList.get(0).getName())),
                                jsonPath("$.reserveList[0].surname").value(is(reserveList.get(0).getSurname())),
                                jsonPath("$.reserveList[0].avatar").doesNotExist()
                        )
                );
    }

    @Nested
    class ShouldAcceptRequestWhenUserHasAdminOrManagerRoleAndShouldThrow {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowConstraintViolationException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI invalidUri = new URI("/group/testInvalidId/participants");

            RequestBuilder request = MockMvcRequestBuilders
                    .get(invalidUri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.constraint.violation");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(
                            matchAll(
                                    status().isBadRequest(),
                                    content().contentType(MediaType.APPLICATION_JSON),
                                    jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                    jsonPath("$.message").value(is(expectedMessage)),
                                    jsonPath("$.errors").value(is(notNullValue())),
                                    jsonPath("$.errors.trainingId")
                                            .value(is(messages.get("exception.invalid.id.format")))
                            )
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowNotExistingGroupTrainingException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(NotExistingGroupTrainingException.class)
                    .when(employeeGroupTrainingService).getTrainingParticipants(groupTrainingId);
            RequestBuilder request = getValidRequest(managerToken, testedLocale);
            String expectedMessage = messages.get("exception.group.training.not.found");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(NotExistingGroupTrainingException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerError(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(employeeGroupTrainingService).getTrainingParticipants(groupTrainingId);
            RequestBuilder request = getValidRequest(adminToken, testedLocale);
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

    @Nested
    class ShouldRejectRequest {

        private void performAndTestAccessDenied(RequestBuilder request, TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
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
        void whenUserTriesToGetData(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);
            RequestBuilder request = getValidRequest(userToken, testedLocale);

            performAndTestAccessDenied(request, country);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenTrainerTriesToGetData(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);
            RequestBuilder request = getValidRequest(trainerToken, testedLocale);

            performAndTestAccessDenied(request, country);
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
