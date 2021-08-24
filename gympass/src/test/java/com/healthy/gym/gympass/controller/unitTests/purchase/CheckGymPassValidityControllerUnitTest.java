package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.purchase.GeneralPurchaseController;
import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.service.PurchaseService;
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

@WebMvcTest(GeneralPurchaseController.class)
@ActiveProfiles(value = "test")
public class CheckGymPassValidityControllerUnitTest {

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

        uri = new URI("/purchase/");
    }

    @Nested
    class ShouldReturnValidationStatus{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnValid_timeValidTypeGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String endDate = LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+validTimeValidPurchasedGymPassDocumentId+"/status")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.checkGymPassValidityStatus(validTimeValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            true,
                            endDate,
                            Integer.MAX_VALUE,
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
                            jsonPath("$.result.entries").value(is(Integer.MAX_VALUE)),
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
                    .get(uri+"/"+validEntriesValidPurchasedGymPassDocumentId+"/status")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.checkGymPassValidityStatus(validEntriesValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            true,
                            "9999-12-31",
                            entries,
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
                            jsonPath("$.result.endDate").value(is("9999-12-31")),
                            jsonPath("$.result.suspensionDate").doesNotExist(),
                            jsonPath("$.result.entries").value(is(entries))
                    ));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotValidWhenRetroEndDate_timeValidTypeGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String endDate = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+notValidTimeValidPurchasedGymPassDocumentId+"/status")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.checkGymPassValidityStatus(notValidTimeValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            false,
                            endDate,
                            Integer.MAX_VALUE,
                            null
                    ));

            String expectedMessage = messages.get("gympass.not.valid.retro.end.date");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.result.valid").value(is(false)),
                            jsonPath("$.result.endDate").value(is(endDate)),
                            jsonPath("$.result.entries").value(is(Integer.MAX_VALUE)),
                            jsonPath("$.result.suspensionDate").doesNotExist()
                    ));

        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotValidWhenSuspended_entriesValidTypeGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String suspensionDate = LocalDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
            int entries = 5;

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+notValidEntriesValidPurchasedGymPassDocumentId+"/status")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.checkGymPassValidityStatus(notValidEntriesValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            false,
                            "9999-12-31",
                            entries,
                            suspensionDate
                    ));

            String expectedMessage = messages.get("gympass.not.valid.suspended");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.result.valid").value(is(false)),
                            jsonPath("$.result.endDate").value(is("9999-12-31")),
                            jsonPath("$.result.suspensionDate").value(is(suspensionDate)),
                            jsonPath("$.result.entries").value(is(entries))
                    ));

        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotValidWhenNotSuspendedNoEntries_entriesValidTypeGymPass(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String suspensionDate = LocalDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
            int entries = 0;

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+notValidEntriesValidPurchasedGymPassDocumentId+"/status")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.checkGymPassValidityStatus(notValidEntriesValidPurchasedGymPassDocumentId))
                    .thenReturn(new PurchasedGymPassStatusValidationResultDTO(
                            false,
                            "9999-12-31",
                            entries,
                            suspensionDate
                    ));

            String expectedMessage = messages.get("gympass.not.valid.no.entries");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.result.valid").value(is(false)),
                            jsonPath("$.result.endDate").value(is("9999-12-31")),
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
                    .get(uri+"/"+invalidGymPassDocumentId+"/status")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.gympass.not.found");

            doThrow(GymPassNotFoundException.class)
                    .when(purchaseService)
                    .checkGymPassValidityStatus(invalidGymPassDocumentId);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(GymPassNotFoundException.class)
                    );

        }

        @Nested
        class ShouldNotCheckGymPassValidationWhenNotAuthorized{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenUserIsNotLogIn(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .get(uri+"/"+validEntriesValidPurchasedGymPassDocumentId+"/status")
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
                        .get(uri+"/"+validEntriesValidPurchasedGymPassDocumentId+"/status")
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

            String id = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri+"/"+id+"/status")
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(IllegalStateException.class)
                    .when(purchaseService)
                    .checkGymPassValidityStatus(id);

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
