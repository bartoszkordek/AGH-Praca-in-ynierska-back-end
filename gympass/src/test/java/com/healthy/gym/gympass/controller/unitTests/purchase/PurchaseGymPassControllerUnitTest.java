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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
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
        timeLimitedPurchasedGymPassRequest.setStartDate("9999-01-01");
        timeLimitedPurchasedGymPassRequest.setEndDate("9999-02-01");
        timeLimitedPurchasedGymPassRequest.setEntries(Integer.MAX_VALUE);

        timeLimitedRequestContent = objectMapper.writeValueAsString(timeLimitedPurchasedGymPassRequest);

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
            String startDate = "9999-01-01";
            String endDate = "9999-02-01";
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
                            jsonPath("$.purchasedGymPass").exists()
                    ));
        }
    }


}
