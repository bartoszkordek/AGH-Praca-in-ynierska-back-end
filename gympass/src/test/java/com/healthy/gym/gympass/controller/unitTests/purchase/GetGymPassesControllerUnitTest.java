package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.PurchaseController;
import com.healthy.gym.gympass.dto.BasicUserInfoDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.exception.StartDateAfterEndDateException;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PurchaseController.class)
public class GetGymPassesControllerUnitTest {

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

    private String purchasedGymPassDocumentId1;
    private String purchasedGymPassDocumentId2;
    private PurchasedGymPassDTO existingPurchasedGymPass1;
    private PurchasedGymPassDTO existingPurchasedGymPass2;
    private List<PurchasedGymPassDTO> purchasedGymPassesResponseList;

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

        String gymPassOfferId = UUID.randomUUID().toString();

        purchasedGymPassDocumentId1 = UUID.randomUUID().toString();
        purchasedGymPassDocumentId2 = UUID.randomUUID().toString();

        String title1 = "Karnet miesięczny";
        String title2 = "Karnet miesięczny PREMIUM";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        Price price = new Price(amount, currency, period);
        SimpleGymPassDTO gymPassOffer1 = new SimpleGymPassDTO(gymPassOfferId, title1, price, false);
        SimpleGymPassDTO gymPassOffer2 = new SimpleGymPassDTO(gymPassOfferId, title2, price, true);
        String name = "Jan";
        String surname = "Kowalski";
        BasicUserInfoDTO user = new BasicUserInfoDTO(userId, name, surname);
        LocalDateTime purchaseDateTime = LocalDateTime.now().minusDays(5);
        String startDate = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        int entries = Integer.MAX_VALUE;

        existingPurchasedGymPass1 = new PurchasedGymPassDTO(
                purchasedGymPassDocumentId1,
                gymPassOffer1,
                user,
                purchaseDateTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );

        existingPurchasedGymPass2 = new PurchasedGymPassDTO(
                purchasedGymPassDocumentId2,
                gymPassOffer2,
                user,
                purchaseDateTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).minusMonths(1),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).minusMonths(1),
                entries
        );

        purchasedGymPassesResponseList = List.of(
                existingPurchasedGymPass1,
                existingPurchasedGymPass2
        );

        uri = new URI("/purchase");
    }

    @Nested
    class ShouldGetGymPasses{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldGetPurchasedGymPassesWhenValidDates(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            String purchasedStartDate= "2000-01-01";
            String purchasedEndDate= "2030-12-31";
            int page = 0;

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/page/"+page
                            +"?purchasedStartDate="+purchasedStartDate+"&purchasedEndDate="+purchasedEndDate)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.getGymPasses(any(), any(), any()))
                    .thenReturn(purchasedGymPassesResponseList);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.[0].purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId1)),
                            jsonPath("$.[0].gymPassOffer").exists(),
                            jsonPath("$.[0].gymPassOffer.title").value(is("Karnet miesięczny")),
                            jsonPath("$.[0].gymPassOffer.price").exists(),
                            jsonPath("$.[0].gymPassOffer.price.amount").value(is(139.99)),
                            jsonPath("$.[0].gymPassOffer.price.currency").value(is("zł")),
                            jsonPath("$.[0].gymPassOffer.price.period").value(is("miesiąc")),
                            jsonPath("$.[0].gymPassOffer.premium").value(is(false)),
                            jsonPath("$.[0].user").exists(),
                            jsonPath("$.[0].user.userId").exists(),
                            jsonPath("$.[0].user.name").value(is("Jan")),
                            jsonPath("$.[0].user.surname").value(is("Kowalski")),
                            jsonPath("$.[0].purchaseDateTime").exists(),
                            jsonPath("$.[0].startDate")
                                    .value(is(LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE))),
                            jsonPath("$.[0].endDate")
                                    .value(is(LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE))),
                            jsonPath("$.[0].entries")
                                    .value(is(Integer.MAX_VALUE)),

                            jsonPath("$.[1].purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId2)),
                            jsonPath("$.[1].gymPassOffer").exists(),
                            jsonPath("$.[1].gymPassOffer.title").value(is("Karnet miesięczny PREMIUM")),
                            jsonPath("$.[1].gymPassOffer.price").exists(),
                            jsonPath("$.[1].gymPassOffer.price.amount").value(is(139.99)),
                            jsonPath("$.[1].gymPassOffer.price.currency").value(is("zł")),
                            jsonPath("$.[1].gymPassOffer.price.period").value(is("miesiąc")),
                            jsonPath("$.[1].gymPassOffer.premium").value(is(true)),
                            jsonPath("$.[1].user").exists(),
                            jsonPath("$.[1].user.userId").exists(),
                            jsonPath("$.[1].user.name").value(is("Jan")),
                            jsonPath("$.[1].user.surname").value(is("Kowalski")),
                            jsonPath("$.[1].purchaseDateTime").exists(),
                            jsonPath("$.[1].startDate")
                                    .value(is(LocalDateTime.now().minusDays(5).minusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE))),
                            jsonPath("$.[1].endDate")
                                    .value(is(LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE))),
                            jsonPath("$.[1].entries")
                                    .value(is(Integer.MAX_VALUE))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldGetPurchasedGymPassesWhenNotDeclaredDates(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            int page = 0;

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/page/"+page)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.getGymPasses(any(), any(), any()))
                    .thenReturn(purchasedGymPassesResponseList);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.[0].purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId1)),
                            jsonPath("$.[0].gymPassOffer").exists(),
                            jsonPath("$.[0].gymPassOffer.title").value(is("Karnet miesięczny")),
                            jsonPath("$.[0].gymPassOffer.price").exists(),
                            jsonPath("$.[0].gymPassOffer.price.amount").value(is(139.99)),
                            jsonPath("$.[0].gymPassOffer.price.currency").value(is("zł")),
                            jsonPath("$.[0].gymPassOffer.price.period").value(is("miesiąc")),
                            jsonPath("$.[0].gymPassOffer.premium").value(is(false)),
                            jsonPath("$.[0].user").exists(),
                            jsonPath("$.[0].user.userId").exists(),
                            jsonPath("$.[0].user.name").value(is("Jan")),
                            jsonPath("$.[0].user.surname").value(is("Kowalski")),
                            jsonPath("$.[0].purchaseDateTime").exists(),
                            jsonPath("$.[0].startDate")
                                    .value(is(LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE))),
                            jsonPath("$.[0].endDate")
                                    .value(is(LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE))),
                            jsonPath("$.[0].entries")
                                    .value(is(Integer.MAX_VALUE)),

                            jsonPath("$.[1].purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId2)),
                            jsonPath("$.[1].gymPassOffer").exists(),
                            jsonPath("$.[1].gymPassOffer.title").value(is("Karnet miesięczny PREMIUM")),
                            jsonPath("$.[1].gymPassOffer.price").exists(),
                            jsonPath("$.[1].gymPassOffer.price.amount").value(is(139.99)),
                            jsonPath("$.[1].gymPassOffer.price.currency").value(is("zł")),
                            jsonPath("$.[1].gymPassOffer.price.period").value(is("miesiąc")),
                            jsonPath("$.[1].gymPassOffer.premium").value(is(true)),
                            jsonPath("$.[1].user").exists(),
                            jsonPath("$.[1].user.userId").exists(),
                            jsonPath("$.[1].user.name").value(is("Jan")),
                            jsonPath("$.[1].user.surname").value(is("Kowalski")),
                            jsonPath("$.[1].purchaseDateTime").exists(),
                            jsonPath("$.[1].startDate")
                                    .value(is(LocalDateTime.now().minusDays(5).minusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE))),
                            jsonPath("$.[1].endDate")
                                    .value(is(LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE))),
                            jsonPath("$.[1].entries")
                                    .value(is(Integer.MAX_VALUE))
                    ));
        }
    }

    @Nested
    class ShouldNotGetGymPasses{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetGymPassesWhenStartDateAfterEndDate(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String purchasedStartDate= "2030-12-31";
            String purchasedEndDate= "2000-01-01";
            int page = 0;

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/page/"+page
                            +"?purchasedStartDate="+purchasedStartDate+"&purchasedEndDate="+purchasedEndDate)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(StartDateAfterEndDateException.class)
                    .when(purchaseService)
                    .getGymPasses(any(), any(), any());

            String expectedMessage = messages.get("exception.start.after.end");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(StartDateAfterEndDateException.class)
                    );
        }

        @Nested
        class ShouldNotGetPurchasedGymPassWhenNotAuthorized{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenUserIsNotLogIn(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                int page = 0;

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri+"/page/"+page)
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

                int page = 0;

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri+"/page/"+page)
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
        void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            int page = 0;

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/page/"+page)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(IllegalStateException.class)
                    .when(purchaseService)
                    .getGymPasses(any(), any(), any());

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
