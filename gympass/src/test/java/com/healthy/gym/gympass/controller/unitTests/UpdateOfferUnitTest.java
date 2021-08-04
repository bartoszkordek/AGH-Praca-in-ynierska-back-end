package com.healthy.gym.gympass.controller.unitTests;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.OfferController;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.exception.InvalidGymPassOfferId;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
public class UpdateOfferUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private OfferService offerService;

    private String managerToken;
    private String adminToken;
    private String userToken;
    private String validId;
    private String invalidId;
    private GymPassOfferRequest gymPassOfferRequest;
    private GymPassOfferRequest invalidGymPassOfferRequest;
    private String requestContent;
    private String invalidRequestContent;
    private URI uri;

    @BeforeEach
    void setUp() throws JsonProcessingException, URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        validId = UUID.randomUUID().toString();
        invalidId = UUID.randomUUID().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        gymPassOfferRequest = new GymPassOfferRequest();
        gymPassOfferRequest.setTitle("Karnet platynowy");
        gymPassOfferRequest.setSubheader("Najlepszy wybór dla osób aktywnych");
        gymPassOfferRequest.setAmount(199.99);
        gymPassOfferRequest.setCurrency("zł");
        gymPassOfferRequest.setPeriod("miesiąc");
        gymPassOfferRequest.setPremium(true);
        gymPassOfferRequest.setSynopsis("Karnet uprawniający do korzystania w pełni z usług ośrodka");
        gymPassOfferRequest.setFeatures(List.of("Full pakiet", "sauna", "siłownia", "basen"));

        requestContent = objectMapper.writeValueAsString(gymPassOfferRequest);

        invalidGymPassOfferRequest = new GymPassOfferRequest();
        invalidGymPassOfferRequest.setTitle("A");
        invalidRequestContent = objectMapper.writeValueAsString(invalidGymPassOfferRequest);

        uri = new URI("/offer");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldUpdateOffer(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri+"/"+validId)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .content(requestContent)
                .contentType(MediaType.APPLICATION_JSON);

        String gymPassId = UUID.randomUUID().toString();
        String title = "Karnet platynowy";
        String subheader = "Najlepszy wybór dla osób aktywnych";
        Price price = new Price(199.99, "zł", "miesiąc");
        boolean isPremium = true;
        Description description = new Description(
                "Karnet uprawniający do korzystania w pełni z usług ośrodka",
                List.of("Full pakiet", "sauna", "siłownia", "basen")
        );

        when(offerService.updateGymPassOffer(validId, gymPassOfferRequest))
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

        String expectedMessage = messages.get("offer.updated");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(is(expectedMessage))
                ));
    }

    @Nested
    class shouldNotUpdateOfferWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+"/"+validId)
                    .header("Accept-Language", testedLocale.toString());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogInAsUsualUser(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+"/"+validId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    class shouldNotUpdateOfferWhenInvalidRequest{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+"/"+validId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(invalidRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("request.bind.exception");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isBadRequest()
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInvalidGymPassOfferId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+"/"+invalidId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.invalid.offer.id");

            doThrow(InvalidGymPassOfferId.class)
                    .when(offerService)
                    .updateGymPassOffer(invalidId, gymPassOfferRequest);

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
        void shouldThrowDuplicatedOffersException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+"/"+validId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(requestContent)
                    .contentType(MediaType.APPLICATION_JSON);


            String expectedMessage = messages.get("exception.duplicated.offers");

            doThrow(DuplicatedOffersException.class)
                    .when(offerService)
                    .updateGymPassOffer(any(),any());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException().getCause())
                                    .isInstanceOf(DuplicatedOffersException.class)
                    );
        }
    }
}
