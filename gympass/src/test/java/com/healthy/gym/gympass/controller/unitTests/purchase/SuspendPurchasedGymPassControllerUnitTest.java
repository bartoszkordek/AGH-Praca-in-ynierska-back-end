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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

@WebMvcTest(PurchaseController.class)
class SuspendPurchasedGymPassControllerUnitTest {

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
    private String purchasedGymPassDocumentId;
    private PurchasedGymPassDTO existingPurchasedGymPass;
    private PurchasedGymPassDTO suspendedPurchasedGymPass;
    private String suspensionDate;
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

        String gymPassOfferId = UUID.randomUUID().toString();

        purchasedGymPassDocumentId = UUID.randomUUID().toString();

        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        Price price = new Price(amount, currency, period);
        boolean isPremium = false;
        SimpleGymPassDTO gymPassOffer = new SimpleGymPassDTO(gymPassOfferId, title, price, isPremium);
        String name = "Jan";
        String surname = "Kowalski";
        BasicUserInfoDTO user = new BasicUserInfoDTO(userId, name, surname);
        LocalDateTime purchaseDateAndTime = LocalDateTime.now().minusDays(5);
        String startDate = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_DATE);
        String endDate = LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_DATE);
        int entries = Integer.MAX_VALUE;

        existingPurchasedGymPass = new PurchasedGymPassDTO(
                purchasedGymPassDocumentId,
                gymPassOffer,
                user,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE),
                entries
        );

        suspensionDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);

        suspendedPurchasedGymPass = new PurchasedGymPassDTO(
                purchasedGymPassDocumentId,
                gymPassOffer,
                user,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE),
                entries,
                LocalDate.parse(suspensionDate, DateTimeFormatter.ISO_DATE)
        );

        uri = new URI("/purchase");
    }

    @Nested
    class ShouldSuspendGymPass{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldSuspendPurchasedGymPassWhenValidIdAndDate(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String startDate = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_DATE);
            String endDate = LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_DATE);

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri+"/"+purchasedGymPassDocumentId+"/suspend/"+suspensionDate)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.suspendGymPass(purchasedGymPassDocumentId, suspensionDate))
                    .thenReturn(suspendedPurchasedGymPass);

            String expectedMessage = messages.get("gympass.suspended");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.purchasedGymPass").exists(),
                            jsonPath("$.purchasedGymPass.purchasedGymPassDocumentId")
                                    .value(is(purchasedGymPassDocumentId)),
                            jsonPath("$.purchasedGymPass.gymPassOffer").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer.gymPassOfferId").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer.title").value(is("Karnet miesięczny")),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price").exists(),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.amount").value(is(139.99)),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.currency").value(is("zł")),
                            jsonPath("$.purchasedGymPass.gymPassOffer.price.period").value(is("miesiąc")),
                            jsonPath("$.purchasedGymPass.user").exists(),
                            jsonPath("$.purchasedGymPass.user.userId").exists(),
                            jsonPath("$.purchasedGymPass.user.name").value(is("Jan")),
                            jsonPath("$.purchasedGymPass.user.surname").value(is("Kowalski")),
                            jsonPath("$.purchasedGymPass.purchaseDateAndTime").exists(),
                            jsonPath("$.purchasedGymPass.startDate")
                                    .value(is(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE).toString())),
                            jsonPath("$.purchasedGymPass.endDate")
                                    .value(is(LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).toString())),
                            jsonPath("$.purchasedGymPass.suspensionDate").value(suspensionDate)

            ));

        }
    }
}
