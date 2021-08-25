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
import com.healthy.gym.gympass.exception.UserNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GetUserLatestGymPassServiceUnitTest {

    @Autowired
    private PurchaseService purchaseService;

    @MockBean
    private PurchasedGymPassDAO purchasedGymPassDAO;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    @MockBean
    private UserDAO userDAO;

    private String gymPassDocumentId;
    private String userId;
    private UserDocument userDocument;
    private PurchasedGymPassDocument purchasedGymPassDocument;
    private PurchasedUserGymPassDTO timeTypeUserGymPass;

    @BeforeEach
    void setUp() {

        //assumptions
        long suspendedDays = 2;
        long pastDays = 5;
        String suspensionDate = LocalDate.now().plusDays(suspendedDays).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String startDate = LocalDate.now().minusDays(pastDays).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = LocalDate.now().minusDays(pastDays).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

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

        gymPassDocumentId = UUID.randomUUID().toString();
        int timeTypeEntries = Integer.MAX_VALUE;
        purchasedGymPassDocument = new PurchasedGymPassDocument(
                gymPassDocumentId ,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE),
                timeTypeEntries
        );

        timeTypeUserGymPass = new PurchasedUserGymPassDTO(
                gymPassDocumentId,
                new SimpleGymPassDTO(
                        gymPassOfferId,
                        title,
                        new Price(amount, currency, period),
                        isPremium
                ),
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE),
                timeTypeEntries,
                null
        );
    }

    @Test
    void shouldGetUserLatestGymPass_whenValidUserId()
            throws UserNotFoundException, NoGymPassesException {
        //when
        when(userDAO.findByUserId(userId))
                .thenReturn(userDocument);
        when(purchasedGymPassDAO.findFirstByUserAndEndDateAfter(
                userDocument,
                LocalDate.now()))
                .thenReturn(purchasedGymPassDocument);
        //then
        assertThat(purchaseService.getUserLatestGympass(userId))
                .isEqualTo(timeTypeUserGymPass);
    }

    @Test
    void shouldNotGetUserLatestGymPass_whenInvalidId() {
        //before
        String invalidUserId = UUID.randomUUID().toString();

        //when
        when(userDAO.findByUserId(invalidUserId))
                .thenReturn(null);

        //then
        assertThatThrownBy(() ->
                purchaseService.getUserLatestGympass(invalidUserId)
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldNotGetUserLatestGymPass_whenEmptyList() {
        //before
        String userIdWithNoGymPasses = UUID.randomUUID().toString();
        UserDocument userWithNoGymPassesDocument = new UserDocument();

        //when
        when(userDAO.findByUserId(userIdWithNoGymPasses))
                .thenReturn(userWithNoGymPassesDocument);
        when(purchasedGymPassDAO.findFirstByUserAndEndDateAfter(userDocument, LocalDate.now()))
                .thenReturn(null);

        //then
        assertThatThrownBy(() ->
                purchaseService.getUserLatestGympass(userIdWithNoGymPasses)
        ).isInstanceOf(NoGymPassesException.class);
    }
}
