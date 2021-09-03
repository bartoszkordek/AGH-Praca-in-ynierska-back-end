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
import com.healthy.gym.gympass.exception.*;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;
import com.healthy.gym.gympass.service.PurchaseService;
import com.healthy.gym.gympass.service.PurchaseServiceImpl;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchaseGymPassServiceUnitTest {

    private PurchaseService purchaseService;
    private PurchasedGymPassDAO purchasedGymPassDAO;
    private GymPassOfferDAO gymPassOfferDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        purchasedGymPassDAO = mock(PurchasedGymPassDAO.class);
        gymPassOfferDAO = mock(GymPassOfferDAO.class);
        userDAO = mock(UserDAO.class);

        purchaseService = new PurchaseServiceImpl(purchasedGymPassDAO, gymPassOfferDAO, userDAO);
    }

    @Test
    void shouldPurchaseGymPass_whenValidRequest() throws UserNotFoundException, StartDateAfterEndDateException, NotSpecifiedGymPassTypeException, RetroPurchasedException, OfferNotFoundException, PastDateException {

        //before
        //request
        String gymPassOfferId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String requestStartDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String requestEndDate = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        int entries = Integer.MAX_VALUE;
        PurchasedGymPassRequest purchasedGymPassRequest = new PurchasedGymPassRequest();
        purchasedGymPassRequest.setGymPassOfferId(gymPassOfferId);
        purchasedGymPassRequest.setUserId(userId);
        purchasedGymPassRequest.setStartDate(requestStartDate);

        //response
        String purchasedGymPassDocumentId = UUID.randomUUID().toString();
        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String name = "Jan";
        String surname = "kowalski";
        LocalDateTime purchaseDateAndTime = LocalDateTime.now();
        LocalDate responseStartDate = LocalDate.now();
        LocalDate responseEndDate = LocalDate.now().plusMonths(1);
        PurchasedGymPassDTO purchasedGymPassDTO = new PurchasedGymPassDTO(
                purchasedGymPassDocumentId,
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

        //DB documents
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
                new Description(synopsis, features)
        );
        gymPassOfferDocument.setId("507f1f77bcf86cd799439011");


        PurchasedGymPassDocument purchasedGymPassDocumentSaved = new PurchasedGymPassDocument(
                purchasedGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                LocalDate.parse(requestStartDate, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(requestEndDate, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );

        //when
        when(gymPassOfferDAO.findByDocumentId(gymPassOfferId)).thenReturn(gymPassOfferDocument);
        when(userDAO.findByUserId(userId)).thenReturn(userDocument);
        when(purchasedGymPassDAO.save(any())).thenReturn(purchasedGymPassDocumentSaved);

        //then
        assertThat(purchaseService.purchaseGymPass(purchasedGymPassRequest)).isEqualTo(purchasedGymPassDTO);
    }

    @Test
    void shouldNotPurchaseGymPass_whenInvalidOfferId() {

        //before
        //request
        String gymPassOfferId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String requestStartDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String requestEndDate = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        int entries = Integer.MAX_VALUE;
        PurchasedGymPassRequest purchasedGymPassRequest = new PurchasedGymPassRequest();
        purchasedGymPassRequest.setGymPassOfferId(gymPassOfferId);
        purchasedGymPassRequest.setUserId(userId);
        purchasedGymPassRequest.setStartDate(requestStartDate);

        //when
        when(gymPassOfferDAO.findByDocumentId(gymPassOfferId)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                purchaseService.purchaseGymPass(purchasedGymPassRequest)
        ).isInstanceOf(OfferNotFoundException.class);
    }

    @Test
    void shouldNotPurchaseGymPass_whenInvalidUserId() {

        //before
        //request
        String gymPassOfferId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String requestStartDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String requestEndDate = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        int entries = Integer.MAX_VALUE;
        PurchasedGymPassRequest purchasedGymPassRequest = new PurchasedGymPassRequest();
        purchasedGymPassRequest.setGymPassOfferId(gymPassOfferId);
        purchasedGymPassRequest.setUserId(userId);
        purchasedGymPassRequest.setStartDate(requestStartDate);

        //response
        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String name = "Jan";
        String surname = "kowalski";

        //DB documents
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
                new Description(synopsis, features)
        );
        gymPassOfferDocument.setId("507f1f77bcf86cd799439011");


        //when
        when(gymPassOfferDAO.findByDocumentId(gymPassOfferId)).thenReturn(gymPassOfferDocument);
        when(userDAO.findByUserId(userId)).thenReturn(null);

        //then
        assertThatThrownBy(() ->
                purchaseService.purchaseGymPass(purchasedGymPassRequest)
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldNotPurchaseGymPass_whenPastDate() {

        //before
        //request
        String gymPassOfferId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String requestStartDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String requestEndDate = LocalDate.now().minusDays(1).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        int entries = Integer.MAX_VALUE;
        PurchasedGymPassRequest purchasedGymPassRequest = new PurchasedGymPassRequest();
        purchasedGymPassRequest.setGymPassOfferId(gymPassOfferId);
        purchasedGymPassRequest.setUserId(userId);
        purchasedGymPassRequest.setStartDate(requestStartDate);

        //response
        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String name = "Jan";
        String surname = "kowalski";

        //DB documents
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
                new Description(synopsis, features)
        );
        gymPassOfferDocument.setId("507f1f77bcf86cd799439011");


        //when
        when(gymPassOfferDAO.findByDocumentId(gymPassOfferId)).thenReturn(gymPassOfferDocument);
        when(userDAO.findByUserId(userId)).thenReturn(userDocument);

        //then
        assertThatThrownBy(() ->
                purchaseService.purchaseGymPass(purchasedGymPassRequest)
        ).isInstanceOf(PastDateException.class);
    }

}
