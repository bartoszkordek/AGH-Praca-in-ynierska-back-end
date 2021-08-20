package com.healthy.gym.trainings.controller.group.training.user.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.group.training.UserGroupTrainingController;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.UserAlreadyEnrolledToTrainingException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingService;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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

@WebMvcTest(UserGroupTrainingController.class)
@ActiveProfiles(value = "test")
class EnrollToGroupTrainingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private UserGroupTrainingService userGroupTrainingService;

    private String employeeToken;
    private String adminToken;
    private String userToken;
    private String userId;
    private String groupTrainingId;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getEmployeeToken(employeeId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        groupTrainingId = UUID.randomUUID().toString();
        uri = new URI("/group/" + groupTrainingId + "/enroll?clientId=" + userId);
    }

    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .post(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldEnrollToGroupTrainingOnBasicList(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        GroupTrainingDTO training = getGroupTrainingDTO();
        training.setBasicList(List.of(getTestUser()));

        when(userGroupTrainingService.enrollToGroupTraining(groupTrainingId, userId)).thenReturn(training);

        RequestBuilder request = getValidRequest(userToken, testedLocale);
        String expectedMessage = messages.get("enrollment.success.basic.list");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.message").value(is(expectedMessage))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.training.id").value(is(groupTrainingId)),
                                jsonPath("$.training.title").value(is("Test training title")),
                                jsonPath("$.training.startDate").value(is("2020-10-10T16:00")),
                                jsonPath("$.training.endDate").value(is("2020-10-10T16:30")),
                                jsonPath("$.training.allDay").value(is(false)),
                                jsonPath("$.training.location").value(is("Room no 2"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.training.trainers[0].name").value(is("TestName")),
                                jsonPath("$.training.trainers[0].surname").value(is("TestSurname")),
                                jsonPath("$.training.trainers[0].avatar").value(is("testAvatarUrl"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.training.participants.basicList[0].userId")
                                        .value(is(userId)),
                                jsonPath("$.training.participants.basicList[0].name")
                                        .value(is("TestUserName")),
                                jsonPath("$.training.participants.basicList[0].surname")
                                        .value(is("TestUserSurname")),
                                jsonPath("$.training.participants.basicList[0].avatar")
                                        .value(is("testUserAvatarUrl"))
                        )
                )
                .andExpect(jsonPath("$.training.participants.reserveList").isEmpty());
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldEnrollToGroupTrainingOnReserveList(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        GroupTrainingDTO training = getGroupTrainingDTO();
        training.setReserveList(List.of(getTestUser()));

        when(userGroupTrainingService.enrollToGroupTraining(groupTrainingId, userId)).thenReturn(training);

        RequestBuilder request = getValidRequest(userToken, testedLocale);
        String expectedMessage = messages.get("enrollment.success.reserve.list");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.message").value(is(expectedMessage))
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$.training.id").value(is(groupTrainingId)),
                                jsonPath("$.training.title").value(is("Test training title")),
                                jsonPath("$.training.startDate").value(is("2020-10-10T16:00")),
                                jsonPath("$.training.endDate").value(is("2020-10-10T16:30")),
                                jsonPath("$.training.allDay").value(is(false)),
                                jsonPath("$.training.location").value(is("Room no 2"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.training.trainers[0].name").value(is("TestName")),
                                jsonPath("$.training.trainers[0].surname").value(is("TestSurname")),
                                jsonPath("$.training.trainers[0].avatar").value(is("testAvatarUrl"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$.training.participants.reserveList[0].userId")
                                        .value(is(userId)),
                                jsonPath("$.training.participants.reserveList[0].name")
                                        .value(is("TestUserName")),
                                jsonPath("$.training.participants.reserveList[0].surname")
                                        .value(is("TestUserSurname")),
                                jsonPath("$.training.participants.reserveList[0].avatar")
                                        .value(is("testUserAvatarUrl"))
                        )
                )
                .andExpect(jsonPath("$.training.participants.basicList").isEmpty());
    }

    private GroupTrainingDTO getGroupTrainingDTO() {
        return new GroupTrainingDTO(
                groupTrainingId,
                "Test training title",
                "2020-10-10T16:00",
                "2020-10-10T16:30",
                false,
                "Room no 2",
                List.of(
                        new BasicUserInfoDTO(
                                UUID.randomUUID().toString(),
                                "TestName",
                                "TestSurname",
                                "testAvatarUrl"
                        )
                )
        );
    }

    private BasicUserInfoDTO getTestUser() {
        return new BasicUserInfoDTO(
                userId,
                "TestUserName",
                "TestUserSurname",
                "testUserAvatarUrl"
        );
    }

    @Nested
    class ShouldAcceptRequestWhenUserHasAdminOrManagerRoleAndShouldThrow {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowConstraintViolationException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI invalidUri  = new URI("/group/" + 123123 + "/enroll?clientId=" + 1231);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(invalidUri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
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
                                    jsonPath("$.errors").value(is(notNullValue()))
                            )
                    ).andExpect(
                            matchAll(
                                    jsonPath("$.errors.userId")
                                            .value(is(messages.get("exception.invalid.id.format"))),
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
                    .when(userGroupTrainingService).enrollToGroupTraining(groupTrainingId, userId);
            RequestBuilder request = getValidRequest(employeeToken, testedLocale);
            String expectedMessage = messages.get("exception.group.training.not.found");

            performRequestAndTestErrorResponse(request, expectedMessage, NotExistingGroupTrainingException.class);
        }

        private void performRequestAndTestErrorResponse(
                RequestBuilder request,
                String expectedMessage,
                Class<? extends Exception> expectedException
        ) throws Exception {
            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(expectedException)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowPastDateException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(PastDateException.class)
                    .when(userGroupTrainingService).enrollToGroupTraining(groupTrainingId, userId);
            RequestBuilder request = getValidRequest(userToken, testedLocale);
            String expectedMessage = messages.get("exception.past.date.enrollment");

            performRequestAndTestErrorResponse(request, expectedMessage, PastDateException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowUserNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(UserNotFoundException.class)
                    .when(userGroupTrainingService).enrollToGroupTraining(groupTrainingId, userId);
            RequestBuilder request = getValidRequest(adminToken, testedLocale);
            String expectedMessage = messages.get("exception.not.found.user.id");

            performRequestAndTestErrorResponse(request, expectedMessage, UserNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowUserAlreadyEnrolledToTrainingException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(UserAlreadyEnrolledToTrainingException.class)
                    .when(userGroupTrainingService).enrollToGroupTraining(groupTrainingId, userId);
            RequestBuilder request = getValidRequest(userToken, testedLocale);
            String expectedMessage = messages.get("exception.user.already.enrolled.to.training");

            performRequestAndTestErrorResponse(request, expectedMessage, UserAlreadyEnrolledToTrainingException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerErrorWhenIllegalStateExceptionOccurred(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(userGroupTrainingService).enrollToGroupTraining(groupTrainingId, userId);
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
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserTriesToEnrollOtherUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            uri = new URI("/group/" + UUID.randomUUID() + "/enroll?clientId=" + UUID.randomUUID());

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
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

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

}
