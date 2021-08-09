package com.healthy.gym.gympass.service.purchase;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.data.repository.PurchasedGymPassDAO;
import com.healthy.gym.gympass.data.repository.UserDAO;
import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;
import com.healthy.gym.gympass.enums.GymRole;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.service.PurchaseService;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CheckGymPassValidationServiceUnitTest {

    @Autowired
    private PurchaseService purchaseService;

    @MockBean
    private PurchasedGymPassDAO purchasedGymPassDAO;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    @MockBean
    private UserDAO userDAO;

    private PurchasedGymPassStatusValidationResultDTO validGymPassResponse;
    private String notSuspendedTimeTypeGymPassDocumentId;
    private String suspendedTimeTypeGymPassDocumentId;
    private String notSuspendedNotValidTimeTypeGymPassDocumentId;
    private String notSuspendedEntriesTypeGymPassDocumentId;
    private String suspendedEntriesTypeGymPassDocumentId;
    private String purchasedEntriesTypeGymPassDocumentId;
    private PurchasedGymPassDocument notSuspendedTimeTypePurchasedGymPassDocument;
    private PurchasedGymPassDocument suspendedTimeTypePurchasedGymPassDocument;
    private PurchasedGymPassDocument notSuspendedNotValidTimeTypePurchasedGymPassDocument;
    private PurchasedGymPassDocument notSuspendedEntriesTypePurchasedGymPassDocument;
    private PurchasedGymPassDocument suspendedEntriesTypePurchasedGymPassDocument;
    private PurchasedGymPassStatusValidationResultDTO notSuspendedTimeTypePurchasedGymPassDTO;
    private PurchasedGymPassStatusValidationResultDTO suspendedTimeTypePurchasedGymPassDTO;
    private PurchasedGymPassStatusValidationResultDTO notSuspendedNotValidTimeTypePurchasedGymPassDTO;
    private PurchasedGymPassStatusValidationResultDTO notSuspendedEntriesTypePurchasedGymPassDTO;
    private PurchasedGymPassStatusValidationResultDTO suspendedEntriesTypePurchasedGymPassDTO;

    @BeforeEach
    void setUp() {

        //assumptions
        long suspendedDays = 2;
        long pastDays = 5;
        String suspensionDate = LocalDate.now().plusDays(suspendedDays).format(DateTimeFormatter.ISO_DATE);
        String startDate = LocalDate.now().minusDays(pastDays).format(DateTimeFormatter.ISO_DATE);
        String endDate = LocalDate.now().minusDays(pastDays).plusMonths(1).format(DateTimeFormatter.ISO_DATE);

        //DB documents
        String gymPassOfferId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String name = "Jan";
        String surname = "Kowalski";
        LocalDateTime purchaseDateAndTime = LocalDateTime.now();

        UserDocument userDocument = new UserDocument();
        userDocument.setName(name);
        userDocument.setSurname(surname);
        userDocument.setUserId(userId);
        userDocument.setGymRoles(List.of(GymRole.USER));
        userDocument.setId("507f1f77bcf86cd799435213");

        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        String synopsis = "Nielimitowana liczba wejść";
        List<String> features = List.of("siłownia", "fitness", "TRX", "rowery");

        GymPassDocument gymPassOfferDocument = new GymPassDocument(
                gymPassOfferId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis,features)
        );
        gymPassOfferDocument.setId("507f1f77bcf86cd799439011");

        notSuspendedTimeTypeGymPassDocumentId = UUID.randomUUID().toString();
        int timeTypeEntries = Integer.MAX_VALUE;
        notSuspendedTimeTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                notSuspendedTimeTypeGymPassDocumentId ,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE),
                timeTypeEntries
        );

        notSuspendedTimeTypePurchasedGymPassDTO
                = new PurchasedGymPassStatusValidationResultDTO(
                   true,
                endDate,
                null
        );

        suspendedTimeTypeGymPassDocumentId = UUID.randomUUID().toString();
        suspendedTimeTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                suspendedTimeTypeGymPassDocumentId ,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).plusDays(suspendedDays),
                timeTypeEntries,
                LocalDate.parse(suspensionDate, DateTimeFormatter.ISO_DATE)
        );

        suspendedTimeTypePurchasedGymPassDTO
                = new PurchasedGymPassStatusValidationResultDTO(
                false,
                endDate,
                suspensionDate
        );

        notSuspendedNotValidTimeTypeGymPassDocumentId = UUID.randomUUID().toString();
        notSuspendedNotValidTimeTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                notSuspendedNotValidTimeTypeGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.now().minusDays(1),
                timeTypeEntries,
                LocalDate.parse(suspensionDate, DateTimeFormatter.ISO_DATE)
        );

        notSuspendedNotValidTimeTypePurchasedGymPassDTO
                = new PurchasedGymPassStatusValidationResultDTO(
                false,
                LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                suspensionDate
        );

        notSuspendedEntriesTypeGymPassDocumentId = UUID.randomUUID().toString();
        int entriesTypeEntries = 10;
        notSuspendedEntriesTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                notSuspendedEntriesTypeGymPassDocumentId ,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE),
                entriesTypeEntries
        );

        notSuspendedEntriesTypePurchasedGymPassDTO
                = new PurchasedGymPassStatusValidationResultDTO(
                true,
                null,
                entriesTypeEntries
        );

        suspendedEntriesTypeGymPassDocumentId = UUID.randomUUID().toString();
        suspendedEntriesTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                suspendedEntriesTypeGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).plusDays(suspendedDays),
                entriesTypeEntries,
                LocalDate.parse(suspensionDate, DateTimeFormatter.ISO_DATE)
        );

        suspendedEntriesTypePurchasedGymPassDTO
                = new PurchasedGymPassStatusValidationResultDTO(
                false,
                suspensionDate,
                entriesTypeEntries
        );
    }

    @Test
    void shouldReturnValidStatus_whenNotSuspendedTimeTypeDocument() throws GymPassNotFoundException {
        //when
        when(purchasedGymPassDAO.findByPurchasedGymPassDocumentId(notSuspendedTimeTypeGymPassDocumentId))
                .thenReturn(notSuspendedTimeTypePurchasedGymPassDocument);
        //then
        assertThat(purchaseService.isGymPassValid(notSuspendedTimeTypeGymPassDocumentId))
                .isEqualTo(notSuspendedTimeTypePurchasedGymPassDTO);
    }


}
