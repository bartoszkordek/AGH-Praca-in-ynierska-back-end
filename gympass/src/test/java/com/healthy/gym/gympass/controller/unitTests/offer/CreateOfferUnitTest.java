package com.healthy.gym.gympass.controller.unitTests.offer;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.offer.OfferController;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import com.healthy.gym.gympass.service.OfferService;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;

@WebMvcTest(OfferController.class)
@ActiveProfiles(value = "test")
class CreateOfferUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private OfferService offerService;

    private String managerToken;
    private String adminToken;
    private String userToken;
    private String requestContent;
    private String invalidTitleRequestContent;
    private String invalidSubheaderRequestContent;
    private String invalidPeriodRequestContent;
    private String invalidSynopsisRequestContent;
    private String invalidFeaturesRequestContent;
    private URI uri;

    @BeforeEach
    void setUp() throws JsonProcessingException, URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        ObjectMapper objectMapper = new ObjectMapper();
        GymPassOfferRequest gymPassOfferRequest = new GymPassOfferRequest();
        gymPassOfferRequest.setTitle("Karnet złoty");
        gymPassOfferRequest.setSubheader("Najlepszy wybór dla osób aktywnych");
        gymPassOfferRequest.setAmount(199.99);
        gymPassOfferRequest.setCurrency("zł");
        gymPassOfferRequest.setPeriod("miesiąc");
        gymPassOfferRequest.setPremium(true);
        gymPassOfferRequest.setSynopsis("Karnet uprawniający do korzystania w pełni z usług ośrodka");
        gymPassOfferRequest.setFeatures(List.of("Full pakiet", "sauna", "siłownia", "basen"));

        requestContent = objectMapper.writeValueAsString(gymPassOfferRequest);

        GymPassOfferRequest invalidTitleGymPassOfferRequest = new GymPassOfferRequest();
        invalidTitleGymPassOfferRequest.setTitle("A");
        invalidTitleRequestContent = objectMapper.writeValueAsString(invalidTitleGymPassOfferRequest);

        GymPassOfferRequest invalidSubheaderGymPassOfferRequest = new GymPassOfferRequest();
        invalidSubheaderGymPassOfferRequest.setSubheader("S");
        invalidSubheaderRequestContent = objectMapper.writeValueAsString(invalidSubheaderGymPassOfferRequest);

        GymPassOfferRequest invalidPeriodGymPassOfferRequest = new GymPassOfferRequest();
        invalidPeriodGymPassOfferRequest.setPeriod("S");
        invalidPeriodRequestContent = objectMapper.writeValueAsString(invalidPeriodGymPassOfferRequest);

        GymPassOfferRequest invalidSynopsisGymPassOfferRequest = new GymPassOfferRequest();
        invalidSynopsisGymPassOfferRequest.setSynopsis("S");
        invalidSynopsisRequestContent = objectMapper.writeValueAsString(invalidSynopsisGymPassOfferRequest);


        GymPassOfferRequest invalidFeaturesGymPassOfferRequest = new GymPassOfferRequest();

        List<String> features = new ArrayList<>();
        for (int i = 0; i<21; i++)
            features.add("element "+i+1);

        invalidFeaturesGymPassOfferRequest.setFeatures(features);
        invalidFeaturesRequestContent = objectMapper.writeValueAsString(invalidFeaturesGymPassOfferRequest);


        uri = new URI("/offer");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldCreateOffer(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .post(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .content(requestContent)
                .contentType(MediaType.APPLICATION_JSON);

        String gymPassId = UUID.randomUUID().toString();
        String title = "Karnet złoty";
        String subheader = "Najlepszy wybór dla osób aktywnych";
        Price price = new Price(199.99, "zł", "miesiąc");
        Description description = new Description(
                "Karnet uprawniający do korzystania w pełni z usług ośrodka",
                List.of("Full pakiet", "sauna", "siłownia", "basen")
        );

        when(offerService.createGymPassOffer(any()))
                .thenReturn(
                        new GymPassDTO(
                                gymPassId,
                                title,
                                subheader,
                                price,
                                true,
                                description
                        )
                );

        String expectedMessage = messages.get("offer.created");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(is(expectedMessage)),
                        jsonPath("$.gymPass").exists(),
                        jsonPath("$.gymPass.documentId").value(is(gymPassId)),
                        jsonPath("$.gymPass.title").value(is(title)),
                        jsonPath("$.gymPass.subheader").value(is(subheader)),
                        jsonPath("$.gymPass.price.amount").value(is(199.99)),
                        jsonPath("$.gymPass.price.currency").value(is("zł")),
                        jsonPath("$.gymPass.price.period").value(is("miesiąc")),
                        jsonPath("$.gymPass.isPremium").value(is(true)),
                        jsonPath("$.gymPass.description.synopsis")
                                .value(is("Karnet uprawniający do korzystania w pełni z usług ośrodka")),
                        jsonPath("$.gymPass.description.features").isArray(),
                        jsonPath("$.gymPass.description.features").value(hasItem("Full pakiet")),
                        jsonPath("$.gymPass.description.features").value(hasItem("sauna")),
                        jsonPath("$.gymPass.description.features").value(hasItem("siłownia")),
                        jsonPath("$.gymPass.description.features").value(hasItem("basen"))
                ));
    }

    @Nested
    class ShouldNotCreateOfferWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogInAsUsualUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .content(requestContent)
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

    @Nested
    class ShouldNotCreateOfferWhenInvalidRequest{

        @Nested
        class ShouldThrowBindExceptionWhenInvalidRequest{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindException_whenInvalidTitle(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .post(uri)
                        .header("Accept-Language", testedLocale.toString())
                        .header("Authorization", managerToken)
                        .content(invalidTitleRequestContent)
                        .contentType(MediaType.APPLICATION_JSON);

                String expectedMessage = messages.get("request.bind.exception");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(matchAll(
                                status().isBadRequest(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                jsonPath("$.message").value(is(expectedMessage)),
                                jsonPath("$.errors").value(is(notNullValue())),
                                jsonPath("$.errors.title")
                                        .value(is(messages.get("field.name.failure"))),
                                jsonPath("$.errors.period")
                                        .value(is(messages.get("field.required")))
                        ));
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindException_whenInvalidSubheader(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .post(uri)
                        .header("Accept-Language", testedLocale.toString())
                        .header("Authorization", managerToken)
                        .content(invalidSubheaderRequestContent)
                        .contentType(MediaType.APPLICATION_JSON);

                String expectedMessage = messages.get("request.bind.exception");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(matchAll(
                                status().isBadRequest(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                jsonPath("$.message").value(is(expectedMessage)),
                                jsonPath("$.errors").value(is(notNullValue())),
                                jsonPath("$.errors.title")
                                        .value(is(messages.get("field.required"))),
                                jsonPath("$.errors.subheader")
                                        .value(is(messages.get("field.subheader.failure"))),
                                jsonPath("$.errors.period")
                                        .value(is(messages.get("field.required")))
                        ));
            }


            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindException_whenInvalidPeriod(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .post(uri)
                        .header("Accept-Language", testedLocale.toString())
                        .header("Authorization", managerToken)
                        .content(invalidPeriodRequestContent)
                        .contentType(MediaType.APPLICATION_JSON);

                String expectedMessage = messages.get("request.bind.exception");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(matchAll(
                                status().isBadRequest(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                jsonPath("$.message").value(is(expectedMessage)),
                                jsonPath("$.errors").value(is(notNullValue())),
                                jsonPath("$.errors.title")
                                        .value(is(messages.get("field.required"))),
                                jsonPath("$.errors.period")
                                        .value(is(messages.get("field.period.failure")))
                        ));
            }


            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindException_whenInvalidSynopsis(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .post(uri)
                        .header("Accept-Language", testedLocale.toString())
                        .header("Authorization", managerToken)
                        .content(invalidSynopsisRequestContent)
                        .contentType(MediaType.APPLICATION_JSON);

                String expectedMessage = messages.get("request.bind.exception");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(matchAll(
                                status().isBadRequest(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                jsonPath("$.message").value(is(expectedMessage)),
                                jsonPath("$.errors").value(is(notNullValue())),
                                jsonPath("$.errors.title")
                                        .value(is(messages.get("field.required"))),
                                jsonPath("$.errors.period")
                                        .value(is(messages.get("field.required"))),
                                jsonPath("$.errors.synopsis")
                                        .value(is(messages.get("field.synopsis.failure")))
                        ));
            }


            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindException_whenInvalidFeatures(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .post(uri)
                        .header("Accept-Language", testedLocale.toString())
                        .header("Authorization", managerToken)
                        .content(invalidFeaturesRequestContent)
                        .contentType(MediaType.APPLICATION_JSON);

                String expectedMessage = messages.get("request.bind.exception");

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(matchAll(
                                status().isBadRequest(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                jsonPath("$.message").value(is(expectedMessage)),
                                jsonPath("$.errors").value(is(notNullValue())),
                                jsonPath("$.errors.title")
                                        .value(is(messages.get("field.required"))),
                                jsonPath("$.errors.period")
                                        .value(is(messages.get("field.required"))),
                                jsonPath("$.errors.features")
                                        .value(is(messages.get("field.features.failure")))
                        ));
            }

        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowDuplicatedOffersException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.duplicated.offers");

            doThrow(DuplicatedOffersException.class)
                    .when(offerService)
                    .createGymPassOffer(any());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(DuplicatedOffersException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(IllegalStateException.class)
                    .when(offerService)
                    .createGymPassOffer(any());

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

}
