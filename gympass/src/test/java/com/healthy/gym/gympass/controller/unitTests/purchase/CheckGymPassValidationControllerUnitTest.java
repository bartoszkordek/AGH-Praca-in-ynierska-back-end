package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.PurchaseController;
import com.healthy.gym.gympass.dto.BasicUserInfoDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PurchaseController.class)
public class CheckGymPassValidationControllerUnitTest {

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
    private String validTimeValidPurchasedGymPassDocumentId;
    private String validEntriesValidPurchasedGymPassDocumentId;
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

        validTimeValidPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        validEntriesValidPurchasedGymPassDocumentId = UUID.randomUUID().toString();

        uri = new URI("/purchase");
    }

    @Nested
    class ShouldReturnValidationStatus{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnValid_timeValidGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String endDate = LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_DATE);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+validTimeValidPurchasedGymPassDocumentId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.isGymPassValid(validTimeValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            true,
                            endDate,
                            null
                    ));

            String expectedMessage = messages.get("gympass.valid");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.result.valid").value(is(true)),
                            jsonPath("$.result.endDate").value(is(endDate)),
                            jsonPath("$.result.suspensionDate").doesNotExist()
                    ));

        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnValid_entriesValidGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            int entries = 5;

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+validEntriesValidPurchasedGymPassDocumentId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.isGymPassValid(validEntriesValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            true,
                            null,
                            entries
                    ));

            String expectedMessage = messages.get("gympass.valid");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.result.valid").value(is(true)),
                            jsonPath("$.result.suspensionDate").doesNotExist(),
                            jsonPath("$.result.entries").value(is(entries))
                    ));

        }
    }

}
