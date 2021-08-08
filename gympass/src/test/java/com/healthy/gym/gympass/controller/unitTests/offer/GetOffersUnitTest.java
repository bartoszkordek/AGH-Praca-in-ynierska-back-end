package com.healthy.gym.gympass.controller.unitTests.offer;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.OfferController;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.service.OfferService;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OfferController.class)
class GetOffersUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private OfferService offerService;

    private String managerToken;
    private String userToken;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        uri = new URI("/offer");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnAllOffers(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON);

        String gymPass1Id = UUID.randomUUID().toString();
        String gymPass2Id = UUID.randomUUID().toString();


        GymPassDTO gymPass1 = new GymPassDTO(
                gymPass1Id,
                "Standardowy",
                "Najpopularniejszy",
                new Price(139.99, "zł", "miesiąc"),
                true,
                new Description(
                        "Najlepszy wybór dla osób regularnie ćwiczących",
                        List.of(
                                "dostęp do każdego sprzętu fitness",
                                "grupowe i indywidualne zajęcia fitness",
                                "dowolne godziny wejścia",
                                "nieograniczony czas wejścia",
                                "nieograniczona liczba wejść",
                                "ważność 30 dni",
                                "dostęp do sauny"
                        )
                )
        );

        GymPassDTO gymPass2 = new GymPassDTO(
                gymPass2Id,
                "Wejście jednorazowe",
                null,
                new Price(19.99, "zł", "wejście"),
                false,
                new Description(
                        "Gdy potrzebujesz skorzystać jednorazowo z naszej siłowni",
                        List.of(
                                "dostęp do każdego sprzętu fitness",
                                "dowolne godziny wejścia",
                                "nieograniczony czas wejścia",
                                "dostęp do sauny"
                        )
                )
        );

        List<GymPassDTO> returnedList = List.of(gymPass1, gymPass2);

        when(offerService.getGymPassOffers()).thenReturn(returnedList);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").doesNotHaveJsonPath(),
                        jsonPath("$.[0].documentId").value(is(gymPass1Id)),
                        jsonPath("$.[0].title").value(is("Standardowy")),
                        jsonPath("$.[0].subheader").value(is("Najpopularniejszy")),
                        jsonPath("$.[0].price.amount").value(is(139.99)),
                        jsonPath("$.[0].price.currency").value(is("zł")),
                        jsonPath("$.[0].price.period").value(is("miesiąc")),
                        jsonPath("$.[0].description.synopsis")
                                .value(is("Najlepszy wybór dla osób regularnie ćwiczących")),

                        jsonPath("$.[0].description.features").isArray(),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("dostęp do każdego sprzętu fitness")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("grupowe i indywidualne zajęcia fitness")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("dowolne godziny wejścia")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("nieograniczony czas wejścia")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("nieograniczona liczba wejść")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("ważność 30 dni")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("dostęp do sauny")),

                        jsonPath("$.[1].documentId").value(is(gymPass2Id)),
                        jsonPath("$.[1].title").value(is("Wejście jednorazowe")),
                        jsonPath("$.[1].subheader").doesNotExist(),
                        jsonPath("$.[1].price.amount").value(is(19.99)),
                        jsonPath("$.[1].price.currency").value(is("zł")),
                        jsonPath("$.[1].price.period").value(is("wejście")),
                        jsonPath("$.[1].description.synopsis")
                                .value(is("Gdy potrzebujesz skorzystać jednorazowo z naszej siłowni")),

                        jsonPath("$.[1].description.features").isArray(),

                        jsonPath("$.[1].description.features")
                                .value(hasItem("dostęp do każdego sprzętu fitness")),

                        jsonPath("$.[1].description.features")
                                .value(hasItem("dowolne godziny wejścia")),

                        jsonPath("$.[1].description.features")
                                .value(hasItem("nieograniczony czas wejścia")),

                        jsonPath("$.[1].description.features")
                                .value(hasItem("dostęp do sauny"))
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotReturnAnyOfferWhenEmptyOffersList(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.no.offers");

        doThrow(NoOffersException.class)
                .when(offerService)
                .getGymPassOffers();

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(NoOffersException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void whenUserIsNotLogIn(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString());

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
            throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        doThrow(IllegalStateException.class)
                .when(offerService)
                .getGymPassOffers();

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
