package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.PurchaseController;
import com.healthy.gym.gympass.dto.BasicUserInfoDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.exception.OfferNotFoundException;
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
import java.util.Objects;
import java.util.UUID;

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
    private String notValidTimeValidPurchasedGymPassDocumentId;
    private String validEntriesValidPurchasedGymPassDocumentId;
    private String notValidEntriesValidPurchasedGymPassDocumentId;
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
        notValidTimeValidPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        notValidEntriesValidPurchasedGymPassDocumentId = UUID.randomUUID().toString();

        uri = new URI("/purchase/valid");
    }

    @Nested
    class ShouldReturnValidationStatus{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnValid_timeValidTypeGymPass(TestCountry country) throws Exception {
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
        void shouldReturnValid_entriesValidTypeGymPass(TestCountry country) throws Exception {
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

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotValidWhenRetroEndDate_timeValidTypeGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String endDate = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_DATE);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+notValidTimeValidPurchasedGymPassDocumentId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.isGymPassValid(notValidTimeValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            false,
                            endDate,
                            null
                    ));

            String expectedMessage = messages.get("gympass.not.valid");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.result.valid").value(is(false)),
                            jsonPath("$.result.endDate").value(is(endDate)),
                            jsonPath("$.result.suspensionDate").doesNotExist()
                    ));

        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotValidWhenSuspended_entriesValidTypeGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String suspensionDate = LocalDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_DATE);
            int entries = 5;

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+notValidEntriesValidPurchasedGymPassDocumentId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.isGymPassValid(notValidEntriesValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            false,
                            suspensionDate,
                            entries
                    ));

            String expectedMessage = messages.get("gympass.not.valid");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.result.valid").value(is(false)),
                            jsonPath("$.result.suspensionDate").value(is(suspensionDate)),
                            jsonPath("$.result.entries").value(is(entries))
                    ));

        }
    }

    @Nested
    class ShouldNotReturnValidationStatus{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotReturnValidStatus_whenInvalidGymPassDocumentId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidGymPassDocumentId = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+invalidGymPassDocumentId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.gympass.not.found");

            doThrow(GymPassNotFoundException.class)
                    .when(purchaseService)
                    .isGymPassValid(invalidGymPassDocumentId);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(GymPassNotFoundException.class)
                    );

        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String id = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+id)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(IllegalStateException.class)
                    .when(purchaseService)
                    .isGymPassValid(id);

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
