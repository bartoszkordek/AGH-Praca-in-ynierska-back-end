package com.healthy.gym.gympass.controller.unitTests;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.OfferController;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.InvalidGymPassOfferId;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OfferController.class)
public class DeleteOfferUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private OfferService offerService;

    private String managerToken;
    private String adminToken;
    private String userToken;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        uri = new URI("/offer/" + UUID.randomUUID());
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldDeleteOffer(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        String gymPassId = UUID.randomUUID().toString();
        String title = "Karnet złoty";
        String subheader = "Najlepszy wybór dla osób aktywnych";
        Price price = new Price(199.99, "zł", "miesiąc");
        boolean isPremium = true;
        Description description = new Description(
                "Karnet uprawniający do korzystania w pełni z usług ośrodka",
                List.of("Full pakiet", "sauna", "siłownia", "basen")
        );

        when(offerService.deleteGymPassOffer(any()))
                .thenReturn(
                        new GymPassDTO(
                                gymPassId,
                                title,
                                subheader,
                                price,
                                isPremium,
                                description
                        )
                );

        String expectedMessage = messages.get("offer.removed");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(is(expectedMessage)),
                        jsonPath("$.gymPass").exists(),
                        jsonPath("$.gymPass.documentId").value(is(gymPassId)),
                        jsonPath("$.gymPass.title").value(is(title)),
                        jsonPath("$.gymPass.subheader").value(is(subheader)),
                        jsonPath("$.gymPass.price.amount").value(is(199.99)),
                        jsonPath("$.gymPass.price.currency").value(is("zł")),
                        jsonPath("$.gymPass.price.period").value(is("miesiąc")),
                        jsonPath("$.gymPass.isPremium").value(is(isPremium)),
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
    class shouldNotDeleteOfferWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri)
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
                    .delete(uri)
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
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowInvalidGymPassOfferId(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON);


        String expectedMessage = messages.get("exception.invalid.offer.id");

        doThrow(InvalidGymPassOfferId.class)
                .when(offerService)
                .deleteGymPassOffer(any());

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(InvalidGymPassOfferId.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
            throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        doThrow(IllegalStateException.class)
                .when(offerService)
                .deleteGymPassOffer(any());

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
