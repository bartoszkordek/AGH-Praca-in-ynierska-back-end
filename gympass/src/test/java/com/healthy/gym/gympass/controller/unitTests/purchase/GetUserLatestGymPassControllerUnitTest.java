package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.purchase.EmployeePurchaseController;
import com.healthy.gym.gympass.dto.PurchasedUserGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
import com.healthy.gym.gympass.exception.NoGymPassesException;
import com.healthy.gym.gympass.exception.UserNotFoundException;
import com.healthy.gym.gympass.service.PurchaseService;
import com.healthy.gym.gympass.shared.Price;
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
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(EmployeePurchaseController.class)
@ActiveProfiles(value = "test")
public class GetUserLatestGymPassControllerUnitTest {

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
    private String otherUserToken;
    private String validUserId;
    private String invalidUserId;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        String employeeId = UUID.randomUUID().toString();
        employeeToken = tokenFactory.getEmployeeToken(employeeId);

        String otherUserId = UUID.randomUUID().toString();
        otherUserToken = tokenFactory.getUserToken(otherUserId);

        validUserId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(validUserId);

        invalidUserId = UUID.randomUUID().toString();

        uri = new URI("/purchase/user/");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetGymPassWhenValidUserIdAndGymPassExist(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+validUserId+"/latest")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        String purchasedGymPassDocumentId = UUID.randomUUID().toString();
        String gymPassOfferId = UUID.randomUUID().toString();
        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        Price price = new Price(amount, currency, period);
        boolean isPremium = false;
        SimpleGymPassDTO gymPassOffer = new SimpleGymPassDTO(gymPassOfferId, title, price, isPremium);
        LocalDateTime purchaseDateTime = LocalDateTime.now();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(1);
        int entries = Integer.MAX_VALUE;

        PurchasedUserGymPassDTO purchasedUserGymPassDTO = new PurchasedUserGymPassDTO(
                purchasedGymPassDocumentId,
                gymPassOffer,
                purchaseDateTime,
                startDate,
                endDate,
                entries,
                null
        );


        when(purchaseService.getUserLatestGympass(validUserId))
                .thenReturn(purchasedUserGymPassDTO);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId)),
                        jsonPath("$.gymPassOffer.title").value(is(title)),
                        jsonPath("$.gymPassOffer.price.amount").value(is(amount)),
                        jsonPath("$.gymPassOffer.price.currency").value(is(currency)),
                        jsonPath("$.gymPassOffer.premium").value(is(false)),
                        jsonPath("$.purchaseDateTime").isNotEmpty(),
                        jsonPath("$.startDate").value(is(startDate.toString())),
                        jsonPath("$.endDate").value(is(endDate.toString())),
                        jsonPath("$.entries").value(is(entries)),
                        jsonPath("$.suspensionDate").doesNotExist()

                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetGymPassWhenValidUserIdAndGymPassExistAndLoggedAsSpecificUser(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri+validUserId+"/latest")
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON);

        String purchasedGymPassDocumentId = UUID.randomUUID().toString();
        String gymPassOfferId = UUID.randomUUID().toString();
        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        Price price = new Price(amount, currency, period);
        boolean isPremium = false;
        SimpleGymPassDTO gymPassOffer = new SimpleGymPassDTO(gymPassOfferId, title, price, isPremium);
        LocalDateTime purchaseDateTime = LocalDateTime.now();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(1);
        int entries = Integer.MAX_VALUE;

        PurchasedUserGymPassDTO purchasedUserGymPassDTO = new PurchasedUserGymPassDTO(
                purchasedGymPassDocumentId,
                gymPassOffer,
                purchaseDateTime,
                startDate,
                endDate,
                entries,
                null
        );


        when(purchaseService.getUserLatestGympass(validUserId))
                .thenReturn(purchasedUserGymPassDTO);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId)),
                        jsonPath("$.gymPassOffer.title").value(is(title)),
                        jsonPath("$.gymPassOffer.price.amount").value(is(amount)),
                        jsonPath("$.gymPassOffer.price.currency").value(is(currency)),
                        jsonPath("$.gymPassOffer.premium").value(is(false)),
                        jsonPath("$.purchaseDateTime").isNotEmpty(),
                        jsonPath("$.startDate").value(is(startDate.toString())),
                        jsonPath("$.endDate").value(is(endDate.toString())),
                        jsonPath("$.entries").value(is(entries)),
                        jsonPath("$.suspensionDate").doesNotExist()

                ));
    }

    @Nested
    class ShouldNotGetLatestGymPass {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetLatestGymPasseWhenInvalidUserId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri + invalidUserId+"/latest")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.user.not.found");

            doThrow(UserNotFoundException.class)
                    .when(purchaseService)
                    .getUserLatestGympass(invalidUserId);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(UserNotFoundException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetLatestGymPassWhenNoneExists(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String userIdWithNoGymPass = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+userIdWithNoGymPass+"/latest")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.no.gympasses");

            doThrow(NoGymPassesException.class)
                    .when(purchaseService)
                    .getUserLatestGympass(userIdWithNoGymPass);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(NoGymPassesException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String userId = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+userId+"/latest")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(IllegalStateException.class)
                    .when(purchaseService)
                    .getUserLatestGympass(userId);

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

        @Nested
        class ShouldNotGetUserLatestGymPassWhenNotAuthorized{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenUserIsNotLogIn(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri+validUserId+"/latest")
                        .header("Accept-Language", testedLocale.toString());

                mockMvc.perform(request)
                        .andDo(print())
                        .andExpect(status().isForbidden());
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenUserIsNotLogInAsOtherUser(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri+validUserId+"/latest")
                        .header("Accept-Language", testedLocale.toString())
                        .header("Authorization", otherUserToken)
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

    }
}
