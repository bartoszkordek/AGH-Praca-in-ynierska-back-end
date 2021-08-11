package com.healthy.gym.gympass.service.purchase;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.data.repository.PurchasedGymPassDAO;
import com.healthy.gym.gympass.data.repository.UserDAO;
import com.healthy.gym.gympass.dto.PurchasedUserGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
import com.healthy.gym.gympass.enums.GymRole;
import com.healthy.gym.gympass.exception.NoGymPassesException;
import com.healthy.gym.gympass.exception.StartDateAfterEndDateException;
import com.healthy.gym.gympass.exception.UserNotFoundException;
import com.healthy.gym.gympass.service.PurchaseService;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class GetAllUserGymPassesServiceUnitTest {

    @Autowired
    private PurchaseService purchaseService;

    @MockBean
    private PurchasedGymPassDAO purchasedGymPassDAO;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    @MockBean
    private UserDAO userDAO;

    private String notSuspendedTimeTypeGymPassDocumentId;
    private String suspendedTimeTypeGymPassDocumentId;
    private String notSuspendedEntriesTypeGymPassDocumentId;
    private String userId;
    private UserDocument userDocument;
    private List<PurchasedGymPassDocument> purchasedGymPassDocuments;
    private List<PurchasedUserGymPassDTO> purchasedGymPassResponse;


    @BeforeEach
    void setUp() {

        //assumptions
        long suspendedDays = 2;
        long pastDays = 5;
        String suspensionDate = LocalDate.now().plusDays(suspendedDays).format(DateTimeFormatter.ISO_DATE);
        String startDate = LocalDate.now().minusDays(pastDays).format(DateTimeFormatter.ISO_DATE);
        String endDate = LocalDate.now().minusDays(pastDays).plusMonths(1).format(DateTimeFormatter.ISO_DATE);

        //DB documents and response
        String gymPassOfferId = UUID.randomUUID().toString();
        userId = UUID.randomUUID().toString();
        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String name = "Jan";
        String surname = "Kowalski";
        LocalDateTime purchaseDateAndTime = LocalDateTime.now();

        userDocument = new UserDocument();
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
        PurchasedGymPassDocument notSuspendedTimeTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                notSuspendedTimeTypeGymPassDocumentId ,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE),
                timeTypeEntries
        );

        PurchasedUserGymPassDTO notSuspendedTimeTypeUserGymPass = new PurchasedUserGymPassDTO(
                notSuspendedTimeTypeGymPassDocumentId,
                new SimpleGymPassDTO(
                        gymPassOfferId,
                        title,
                        new Price(amount, currency, period),
                        isPremium
                ),
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE),
                timeTypeEntries,
                null
        );


        suspendedTimeTypeGymPassDocumentId = UUID.randomUUID().toString();
        PurchasedGymPassDocument suspendedTimeTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                suspendedTimeTypeGymPassDocumentId ,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).plusDays(suspendedDays),
                timeTypeEntries,
                LocalDate.parse(suspensionDate, DateTimeFormatter.ISO_DATE)
        );

        PurchasedUserGymPassDTO suspendedTimeTypeUserGymPass = new PurchasedUserGymPassDTO(
                suspendedTimeTypeGymPassDocumentId,
                new SimpleGymPassDTO(
                        gymPassOfferId,
                        title,
                        new Price(amount, currency, period),
                        isPremium
                ),
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).plusDays(suspendedDays),
                timeTypeEntries,
                LocalDate.parse(suspensionDate, DateTimeFormatter.ISO_DATE)
        );


        notSuspendedEntriesTypeGymPassDocumentId = UUID.randomUUID().toString();
        String endDateForEntriesTypeDocuments = "9999-12-31";
        int entriesTypeEntries = 10;
        PurchasedGymPassDocument notSuspendedEntriesTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                notSuspendedEntriesTypeGymPassDocumentId ,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDateForEntriesTypeDocuments, DateTimeFormatter.ISO_DATE),
                entriesTypeEntries
        );

        PurchasedUserGymPassDTO notSuspendedEntriesTypeUserGymPass = new PurchasedUserGymPassDTO(
                notSuspendedEntriesTypeGymPassDocumentId,
                new SimpleGymPassDTO(
                        gymPassOfferId,
                        title,
                        new Price(amount, currency, period),
                        isPremium
                ),
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
                LocalDate.parse(endDateForEntriesTypeDocuments, DateTimeFormatter.ISO_DATE),
                entriesTypeEntries,
                null
        );

        purchasedGymPassDocuments = List.of(
                notSuspendedTimeTypePurchasedGymPassDocument,
                suspendedTimeTypePurchasedGymPassDocument,
                notSuspendedEntriesTypePurchasedGymPassDocument
        );

        purchasedGymPassResponse = List.of(
                notSuspendedTimeTypeUserGymPass,
                suspendedTimeTypeUserGymPass,
                notSuspendedEntriesTypeUserGymPass
        );
    }

    @Test
    void shouldGetUserGymPasses_whenValidUserId()
            throws UserNotFoundException, NoGymPassesException, StartDateAfterEndDateException {
        //when
        when(userDAO.findByUserId(userId))
                .thenReturn(userDocument);
        when(purchasedGymPassDAO.findAllByUserAndStartDateAfterAndEndDateBefore(
                userDocument,
                LocalDate.parse("1000-01-01", DateTimeFormatter.ISO_LOCAL_DATE).minusDays(1),
                LocalDate.parse("9999-12-31", DateTimeFormatter.ISO_LOCAL_DATE).plusDays(1)))
                .thenReturn(purchasedGymPassDocuments);
        //then
        assertThat(purchaseService.getAllUserGymPasses(userId, null, null).get(0))
                .isEqualTo(purchasedGymPassResponse.get(0));
        assertThat(purchaseService.getAllUserGymPasses(userId, null, null).get(1))
                .isEqualTo(purchasedGymPassResponse.get(1));
        assertThat(purchaseService.getAllUserGymPasses(userId, null, null).get(2))
                .isEqualTo(purchasedGymPassResponse.get(2));
    }

    @Test
    void shouldNotGetUserGymPasses_whenInvalidId() {
        //before
        String invalidUserId = UUID.randomUUID().toString();

        //when
        when(userDAO.findByUserId(invalidUserId))
                .thenReturn(null);

        //then
        assertThatThrownBy(() ->
                purchaseService.getAllUserGymPasses(invalidUserId, null, null)
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldNotGetUserGymPasses_whenStartDateAfterEndDate() {
        //before
        String startDate = "2030-12-31";
        String endDate = "2000-01-01";

        //then
        assertThatThrownBy(() ->
                purchaseService.getAllUserGymPasses(userId, startDate, endDate)
        ).isInstanceOf(StartDateAfterEndDateException.class);
    }

    @Test
    void shouldNotGetUserGymPasses_whenEmptyList() {
        //before
        String userIdWithNoGymPasses = UUID.randomUUID().toString();
        UserDocument userWithNoGymPassesDocument = new UserDocument();

        //when
        when(userDAO.findByUserId(userIdWithNoGymPasses))
                .thenReturn(userWithNoGymPassesDocument);
        when(purchasedGymPassDAO.findAllByUserAndStartDateAfterAndEndDateBefore(
                userWithNoGymPassesDocument, null, null))
                .thenReturn(null);

        //then
        assertThatThrownBy(() ->
                purchaseService.getAllUserGymPasses(userIdWithNoGymPasses, null, null)
        ).isInstanceOf(NoGymPassesException.class);
    }
}
