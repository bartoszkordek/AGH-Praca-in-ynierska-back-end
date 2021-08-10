package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.PurchaseController;
import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.dto.BasicUserInfoDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.PurchasedUserGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PurchaseController.class)
public class GetAllUserGymPassesUnitTest {

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
            LocalDateTime purchaseDateAndTime = LocalDateTime.now();
            LocalDate startDate = LocalDate.now();
            LocalDate endDate1 = LocalDate.now().plusMonths(1);
            LocalDate endDate2 = LocalDate.now().plusMonths(3);
            int entries = Integer.MAX_VALUE;

            PurchasedUserGymPassDTO purchasedUserGymPassDTO1 = new PurchasedUserGymPassDTO(
                    purchasedGymPassDocumentId1,
                    gymPassOffer1,
                    purchaseDateAndTime,
                    startDate,
                    endDate1,
                    entries,
                    null
            );

            PurchasedUserGymPassDTO purchasedUserGymPassDTO2 = new PurchasedUserGymPassDTO(
                    purchasedGymPassDocumentId2,
                    gymPassOffer2,
                    purchaseDateAndTime,
                    startDate,
                    endDate2,
                    entries,
                    null
            );

            List<PurchasedUserGymPassDTO> purchasedUserGymPassDTOs = List.of(
                    purchasedUserGymPassDTO1, purchasedUserGymPassDTO2
            );

            when(purchaseService.getAllUserGymPasses(validUserId, null, null))
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
                            jsonPath("$.[0].purchaseDateAndTime").isNotEmpty(),
                            jsonPath("$.[0].startDate").value(is(startDate.toString())),
                            jsonPath("$.[0].endDate").value(is(endDate1.toString())),
                            jsonPath("$.[0].entries").value(is(entries)),
                            jsonPath("$.[1].suspensionDate").doesNotExist(),
                            jsonPath("$.[1].purchasedGymPassDocumentId").value(is(purchasedGymPassDocumentId2)),
                            jsonPath("$.[1].gymPassOffer.title").value(is(title2)),
                            jsonPath("$.[1].gymPassOffer.price.amount").value(is(amount2)),
                            jsonPath("$.[1].gymPassOffer.price.currency").value(is(currency)),
                            jsonPath("$.[1].gymPassOffer.premium").value(is(false)),
                            jsonPath("$.[1].purchaseDateAndTime").isNotEmpty(),
                            jsonPath("$.[1].startDate").value(is(startDate.toString())),
                            jsonPath("$.[1].endDate").value(is(endDate2.toString())),
                            jsonPath("$.[1].entries").value(is(entries)),
                            jsonPath("$.[1].suspensionDate").doesNotExist()

                    ));
        }
    }


}
