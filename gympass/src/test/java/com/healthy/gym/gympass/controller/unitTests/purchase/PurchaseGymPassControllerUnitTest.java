package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.PurchaseController;
import com.healthy.gym.gympass.dto.BasicUserInfoDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;
import com.healthy.gym.gympass.service.PurchaseService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PurchaseController.class)
public class PurchaseGymPassControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private PurchaseService purchaseService;

    private String adminToken;
    private String managerToken;
    private String employeeToken;
    private String userToken;
    private String timeLimitedRequestContent;
    private String entriesLimitedRequestContent;
    private String validUserId;
    private String validGymPassOfferId;
    private URI uri;

    @BeforeEach
    void setUp() throws JsonProcessingException, URISyntaxException {
        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        String employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getEmployeeToken(employeeId);

        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        validGymPassOfferId = UUID.randomUUID().toString();
        validUserId = UUID.randomUUID().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        PurchasedGymPassRequest timeLimitedPurchasedGymPassRequest = new PurchasedGymPassRequest();
        timeLimitedPurchasedGymPassRequest.setGymPassOfferId(validGymPassOfferId);
        timeLimitedPurchasedGymPassRequest.setUserId(validUserId);
        timeLimitedPurchasedGymPassRequest.setStartDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
        timeLimitedPurchasedGymPassRequest.setEndDate(LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ISO_DATE));
        timeLimitedPurchasedGymPassRequest.setEntries(Integer.MAX_VALUE);

        timeLimitedRequestContent = objectMapper.writeValueAsString(timeLimitedPurchasedGymPassRequest);

        PurchasedGymPassRequest entriesLimitedPurchasedGymPassRequest = new PurchasedGymPassRequest();
        entriesLimitedPurchasedGymPassRequest.setGymPassOfferId(validGymPassOfferId);
        entriesLimitedPurchasedGymPassRequest.setUserId(validUserId);
        entriesLimitedPurchasedGymPassRequest.setStartDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
        entriesLimitedPurchasedGymPassRequest.setEndDate(LocalDateTime.MAX.format(DateTimeFormatter.ISO_DATE));
        entriesLimitedPurchasedGymPassRequest.setEntries(5);

        entriesLimitedRequestContent = objectMapper.writeValueAsString(entriesLimitedPurchasedGymPassRequest);

        uri = new URI("/purchase");

    }

    @Nested
    class ShouldPurchaseGymPass{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldPurchaseGymPassWhenValidRequest_timeLimitedGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .content(timeLimitedRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String title = "Karnet miesięczny";
            double amount = 139.99;
            String currency = "zł";
            String period = "miesiąc";
            Price price = new Price(amount, currency, period);
            boolean isPremium = false;
            SimpleGymPassDTO gymPassOffer = new SimpleGymPassDTO(validGymPassOfferId, title, price, isPremium);
            String name = "Jan";
            String surname = "Kowalski";
            BasicUserInfoDTO user = new BasicUserInfoDTO(validUserId, name, surname);
            String purchaseDateAndTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            String startDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
            String endDate = LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ISO_DATE);
            int entries = Integer.MAX_VALUE;

            when(purchaseService.purchaseGymPass(any()))
                    .thenReturn(
                            new PurchasedGymPassDTO(
                                gymPassOffer,
                                user,
                                purchaseDateAndTime,
                                startDate,
                                endDate,
                                entries
                            )
                    );

            String expectedMessage = messages.get("gympass.purchased");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.purchasedGymPass").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer.gymPassOfferId")
                                    .value(is(validGymPassOfferId)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.title").value(is(title)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.amount").value(is(amount)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.currency").value(is(currency)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.period").value(is(period)),
                            jsonPath("$.purchasedGymPass.user").exists(),
                            jsonPath("$.purchasedGymPass.user.userId").value(is(validUserId)),
                            jsonPath("$.purchasedGymPass.user.name").value(is(name)),
                            jsonPath("$.purchasedGymPass.user.surname").value(is(surname)),
                            jsonPath("$.purchasedGymPass.purchaseDateAndTime").value(is(purchaseDateAndTime)),
                            jsonPath("$.purchasedGymPass.startDate").value(is(startDate)),
                            jsonPath("$.purchasedGymPass.endDate").value(is(endDate)),
                            jsonPath("$.purchasedGymPass.entries").value(is(entries))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldPurchaseGymPassWhenValidRequest_entriesLimitedGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .post(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .content(timeLimitedRequestContent)
                    .contentType(MediaType.APPLICATION_JSON);

            String title = "Karnet miesięczny";
            double amount = 139.99;
            String currency = "zł";
            String period = "miesiąc";
            Price price = new Price(amount, currency, period);
            boolean isPremium = false;
            SimpleGymPassDTO gymPassOffer = new SimpleGymPassDTO(validGymPassOfferId, title, price, isPremium);
            String name = "Jan";
            String surname = "Kowalski";
            BasicUserInfoDTO user = new BasicUserInfoDTO(validUserId, name, surname);
            String purchaseDateAndTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            String startDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
            String endDate = LocalDateTime.MAX.format(DateTimeFormatter.ISO_DATE);
            int entries = 4;

            when(purchaseService.purchaseGymPass(any()))
                    .thenReturn(
                            new PurchasedGymPassDTO(
                                    gymPassOffer,
                                    user,
                                    purchaseDateAndTime,
                                    startDate,
                                    endDate,
                                    entries
                            )
                    );

            String expectedMessage = messages.get("gympass.purchased");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.purchasedGymPass").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer.gymPassOfferId")
                                    .value(is(validGymPassOfferId)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.title").value(is(title)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.amount").value(is(amount)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.currency").value(is(currency)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.period").value(is(period)),
                            jsonPath("$.purchasedGymPass.user").exists(),
                            jsonPath("$.purchasedGymPass.user.userId").value(is(validUserId)),
                            jsonPath("$.purchasedGymPass.user.name").value(is(name)),
                            jsonPath("$.purchasedGymPass.user.surname").value(is(surname)),
                            jsonPath("$.purchasedGymPass.purchaseDateAndTime").value(is(purchaseDateAndTime)),
                            jsonPath("$.purchasedGymPass.startDate").value(is(startDate)),
                            jsonPath("$.purchasedGymPass.endDate").value(is(endDate)),
                            jsonPath("$.purchasedGymPass.entries").value(is(entries))
                    ));
        }
    }

    @Nested
    class ShouldNotPurchaseGymPass{

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
                        .content(timeLimitedRequestContent)
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
        class ShouldThrowBindExceptionWhenInvalidRequest{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindException_whenInvalidRequest(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                ObjectMapper objectMapper = new ObjectMapper();
                PurchasedGymPassRequest purchasedGymPassRequest = new PurchasedGymPassRequest();
                purchasedGymPassRequest.setGymPassOfferId("A");
                purchasedGymPassRequest.setUserId("U");
                purchasedGymPassRequest.setStartDate("2021");
                purchasedGymPassRequest.setEndDate("X");
                String invalidRequestContent = objectMapper.writeValueAsString(purchasedGymPassRequest);

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
                                status().isBadRequest(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.error").value(is(HttpStatus.BAD_REQUEST.getReasonPhrase())),
                                jsonPath("$.message").value(is(expectedMessage)),
                                jsonPath("$.errors").value(is(notNullValue())),
                                jsonPath("$.errors.gymPassOfferId")
                                        .value(is(messages.get("exception.invalid.id.format"))),
                                jsonPath("$.errors.userId")
                                        .value(is(messages.get("exception.invalid.id.format"))),
                                jsonPath("$.errors.startDate")
                                        .value(is(messages.get("exception.invalid.date.format"))),
                                jsonPath("$.errors.endDate")
                                        .value(is(messages.get("exception.invalid.date.format")))
                        ));
            }
        }
    }
}
