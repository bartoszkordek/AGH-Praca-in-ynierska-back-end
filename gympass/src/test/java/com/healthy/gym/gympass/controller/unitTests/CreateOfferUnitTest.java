package com.healthy.gym.gympass.controller.unitTests;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.OfferController;
import com.healthy.gym.gympass.dto.GymPassDTO;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;

@WebMvcTest(OfferController.class)
public class CreateOfferUnitTest {

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

        GymPassOfferRequest invalidGymPassOfferRequest = new GymPassOfferRequest();
        invalidGymPassOfferRequest.setTitle("A");
        invalidRequestContent = objectMapper.writeValueAsString(invalidGymPassOfferRequest);

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
        boolean isPremium = true;
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
                                isPremium,
                                description
                        )
                );

        String expectedMessage = messages.get("offer.created");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(is(expectedMessage))
                ));
    }

    @Nested
    class shouldNotCreateOfferWhenNotAuthorized{

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

    }

    @Nested
    class shouldNotCreateOfferWhenInvalidRequest{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowBindException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
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
    }

}
