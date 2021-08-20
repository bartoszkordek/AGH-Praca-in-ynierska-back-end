package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.PurchaseController;
import com.healthy.gym.gympass.dto.PurchasedUserGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
import com.healthy.gym.gympass.exception.NoGymPassesException;
import com.healthy.gym.gympass.exception.StartDateAfterEndDateException;
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

@ActiveProfiles(value = "test")
@WebMvcTest(PurchaseController.class)
public class GetUserGymPassesControllerUnitTest {

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

        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        validUserId = UUID.randomUUID().toString();
        invalidUserId = UUID.randomUUID().toString();

        uri = new URI("/purchase/user/");
    }

    @Nested
    class ShouldGetGymPasses{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldGetGymPassesWhenValidUserIdAndGymPassesExist(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+validUserId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String purchasedGymPassDocumentId1 = UUID.randomUUID().toString();
            String purchasedGymPassDocumentId2 = UUID.randomUUID().toString();
            String gymPassOfferId1 = UUID.randomUUID().toString();
            String gymPassOfferId2 = UUID.randomUUID().toString();
            String title1 = "Karnet miesięczny";
            String title2 = "Karnet kwartalny";
            double amount1 = 139.99;
            double amount2 = 399.99;
            String currency = "zł";
            String period1 = "miesiąc";
            String period2 = "kwartał";
            Price price1 = new Price(amount1, currency, period1);
            Price price2 = new Price(amount2, currency, period2);
            boolean isPremium = false;
            SimpleGymPassDTO gymPassOffer1 = new SimpleGymPassDTO(gymPassOfferId1, title1, price1, isPremium);
            SimpleGymPassDTO gymPassOffer2 = new SimpleGymPassDTO(gymPassOfferId2, title2, price2, isPremium);
            LocalDateTime purchaseDateTime = LocalDateTime.now();
            LocalDate startDate = LocalDate.now();
            LocalDate endDate1 = LocalDate.now().plusMonths(1);
            LocalDate endDate2 = LocalDate.now().plusMonths(3);
            int entries = Integer.MAX_VALUE;
            LocalDate suspensionDate2 = LocalDate.now().plusDays(5);

            PurchasedUserGymPassDTO purchasedUserGymPassDTO1 = new PurchasedUserGymPassDTO(
                    purchasedGymPassDocumentId1,
                    gymPassOffer1,
                    purchaseDateTime,
                    startDate,
                    endDate1,
                    entries,
                    null
            );

            PurchasedUserGymPassDTO purchasedUserGymPassDTO2 = new PurchasedUserGymPassDTO(
                    purchasedGymPassDocumentId2,
                    gymPassOffer2,
                    purchaseDateTime,
                    startDate,
                    endDate2,
                    entries,
                    suspensionDate2
            );

            List<PurchasedUserGymPassDTO> purchasedUserGymPassDTOs = List.of(
                    purchasedUserGymPassDTO1, purchasedUserGymPassDTO2
            );

            when(purchaseService.getUserGymPasses(validUserId, null, null))
                    .thenReturn(purchasedUserGymPassDTOs);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.[0].purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId1)),
                            jsonPath("$.[0].gymPassOffer.title").value(is(title1)),
                            jsonPath("$.[0].gymPassOffer.price.amount").value(is(amount1)),
                            jsonPath("$.[0].gymPassOffer.price.currency").value(is(currency)),
                            jsonPath("$.[0].gymPassOffer.premium").value(is(false)),
                            jsonPath("$.[0].purchaseDateTime").isNotEmpty(),
                            jsonPath("$.[0].startDate").value(is(startDate.toString())),
                            jsonPath("$.[0].endDate").value(is(endDate1.toString())),
                            jsonPath("$.[0].entries").value(is(entries)),
                            jsonPath("$.[0].suspensionDate").doesNotExist(),
                            jsonPath("$.[1].purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId2)),
                            jsonPath("$.[1].gymPassOffer.title").value(is(title2)),
                            jsonPath("$.[1].gymPassOffer.price.amount").value(is(amount2)),
                            jsonPath("$.[1].gymPassOffer.price.currency").value(is(currency)),
                            jsonPath("$.[1].gymPassOffer.premium").value(is(false)),
                            jsonPath("$.[1].purchaseDateTime").isNotEmpty(),
                            jsonPath("$.[1].startDate").value(is(startDate.toString())),
                            jsonPath("$.[1].endDate").value(is(endDate2.toString())),
                            jsonPath("$.[1].entries").value(is(entries)),
                            jsonPath("$.[1].suspensionDate").value(is(suspensionDate2.toString()))

                    ));
        }
    }

    @Nested
    class ShouldNotGetGymPasses{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetGymPassesWhenInvalidUserId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+invalidUserId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.user.not.found");

            doThrow(UserNotFoundException.class)
                    .when(purchaseService)
                    .getUserGymPasses(invalidUserId, null, null);

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
        void shouldNotGetGymPassesWhenNoneExists(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String userIdWithNoGymPass = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+userIdWithNoGymPass)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.no.gympasses");

            doThrow(NoGymPassesException.class)
                    .when(purchaseService)
                    .getUserGymPasses(userIdWithNoGymPass, null, null);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isNoContent())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(NoGymPassesException.class)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetGymPassesWhenStartDateAfterEndDate(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+validUserId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .param("startDate", "2030-12-31")
                    .param("endDate", "2000-01-01")
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.start.after.end");

            doThrow(StartDateAfterEndDateException.class)
                    .when(purchaseService)
                    .getUserGymPasses(validUserId, "2030-12-31", "2000-01-01");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(StartDateAfterEndDateException.class)
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
                    .get(uri+userId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(IllegalStateException.class)
                    .when(purchaseService)
                    .getUserGymPasses(userId, null, null);

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
        class ShouldNotGetUserGymPassesWhenNotAuthorized{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenUserIsNotLogIn(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri+validUserId)
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
                        .get(uri+validUserId)
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

    }

}
