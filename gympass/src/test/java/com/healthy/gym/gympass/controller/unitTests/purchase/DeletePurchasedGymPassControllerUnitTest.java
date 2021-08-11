package com.healthy.gym.gympass.controller.unitTests.purchase;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.PurchaseController;
import com.healthy.gym.gympass.dto.BasicUserInfoDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.exception.RetroPurchasedException;
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
public class DeletePurchasedGymPassControllerUnitTest {

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

    private String purchasedGymPassDocumentIdToRemove;
    private PurchasedGymPassDTO existingPurchasedGymPassToRemove;
    private PurchasedGymPassDTO removedPurchasedGymPass;

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

        purchasedGymPassDocumentIdToRemove = UUID.randomUUID().toString();

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
        String startDate = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        int entries = Integer.MAX_VALUE;

        existingPurchasedGymPassToRemove = new PurchasedGymPassDTO(
                purchasedGymPassDocumentIdToRemove,
                gymPassOffer,
                user,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );

        removedPurchasedGymPass = new PurchasedGymPassDTO(
                purchasedGymPassDocumentIdToRemove,
                gymPassOffer,
                user,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );

        uri = new URI("/purchase");
    }


    @Nested
    class ShouldRemoveGymPass{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRemovePurchasedGymPassWhenValidId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String startDate = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String endDate = LocalDateTime.now().minusDays(5).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri+"/"+purchasedGymPassDocumentIdToRemove)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", employeeToken)
                    .contentType(MediaType.APPLICATION_JSON);

            when(purchaseService.deleteGymPass(purchasedGymPassDocumentIdToRemove))
                    .thenReturn(removedPurchasedGymPass);

            String expectedMessage = messages.get("gympass.removed");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(matchAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.message").value(is(expectedMessage)),
                            jsonPath("$.purchasedGymPass").exists(),
                            jsonPath("$.purchasedGymPass.purchasedGymPassDocumentId")
                                    .value(is(purchasedGymPassDocumentIdToRemove)),
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
                                    .value(is(LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).toString())),
                            jsonPath("$.purchasedGymPass.endDate")
                                    .value(is(LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).toString()))
                    ));
        }
    }

    @Nested
    class ShouldNotRemoveGymPass{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotRemovePurchasedGymPassWhenInvalidId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidGymPassId = UUID.randomUUID().toString();

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri+"/"+invalidGymPassId)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", managerToken)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(GymPassNotFoundException.class)
                    .when(purchaseService)
                    .deleteGymPass(invalidGymPassId);

            String expectedMessage = messages.get("exception.gympass.not.found");

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
                    .delete(uri+"/"+id)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON);

            doThrow(IllegalStateException.class)
                    .when(purchaseService)
                    .deleteGymPass(id);

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
        class ShouldNotDeletePurchaseGymPassWhenNotAuthorized{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenUserIsNotLogIn(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                RequestBuilder request = MockMvcRequestBuilders
                        .delete(uri+"/"+purchasedGymPassDocumentIdToRemove)
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
                        .delete(uri+"/"+purchasedGymPassDocumentIdToRemove)
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
