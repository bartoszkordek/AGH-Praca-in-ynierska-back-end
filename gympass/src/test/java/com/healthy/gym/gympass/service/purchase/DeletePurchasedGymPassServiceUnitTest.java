package com.healthy.gym.gympass.service.purchase;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.data.repository.PurchasedGymPassDAO;
import com.healthy.gym.gympass.data.repository.UserDAO;
import com.healthy.gym.gympass.dto.BasicUserInfoDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
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
class DeletePurchasedGymPassServiceUnitTest {

    @Autowired
    private PurchaseService purchaseService;

    @MockBean
    private PurchasedGymPassDAO purchasedGymPassDAO;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    @MockBean
    private UserDAO userDAO;

    private String purchasedGymPassDocumentIdToDelete;
    private PurchasedGymPassDTO purchasedGymPassDTO;
    private PurchasedGymPassDocument purchasedGymPassDocumentToDelete;


    @BeforeEach
    void setUp() {

        //assumptions
        long pastDays = 5;

        //request
        String gymPassOfferId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String startDate = LocalDate.now().minusDays(pastDays).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = LocalDate.now().minusDays(pastDays).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        int entries = Integer.MAX_VALUE;

        //response
        purchasedGymPassDocumentIdToDelete = UUID.randomUUID().toString();
        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String name = "Jan";
        String surname = "Kowalski";
        LocalDateTime purchaseDateAndTime = LocalDateTime.now();
        LocalDate responseStartDate = LocalDate.now().minusDays(pastDays);
        LocalDate responseEndDate = LocalDate.now().minusDays(pastDays).plusMonths(1);
        purchasedGymPassDTO = new PurchasedGymPassDTO(
                purchasedGymPassDocumentIdToDelete,
                new SimpleGymPassDTO(
                        gymPassOfferId,
                        title,
                        new Price(amount, currency, period),
                        isPremium
                ),
                new BasicUserInfoDTO(userId, name, surname),
                purchaseDateAndTime,
                responseStartDate,
                responseEndDate,
                entries
        );

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
        purchasedGymPassDocumentToDelete = new PurchasedGymPassDocument(
                purchasedGymPassDocumentIdToDelete,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );
    }

    @Test
    void shouldDeletePurchasedGymPass_whenValidId() throws GymPassNotFoundException {
        //when
        when(purchasedGymPassDAO.findByPurchasedGymPassDocumentId(purchasedGymPassDocumentIdToDelete))
                .thenReturn(purchasedGymPassDocumentToDelete);
        //then
        assertThat(purchaseService.deleteGymPass(purchasedGymPassDocumentIdToDelete))
                .isEqualTo(purchasedGymPassDTO);
    }
}
