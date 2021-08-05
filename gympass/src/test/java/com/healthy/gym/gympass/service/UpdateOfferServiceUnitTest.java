package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.exception.InvalidGymPassOfferId;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class UpdateOfferServiceUnitTest {

    @Autowired
    private OfferService offerService;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    private String documentId;
    private GymPassDocument existingGymPassDocument;

    @BeforeEach
    void setUp() throws IOException {
        documentId = UUID.randomUUID().toString();
        String title = "Karnet miesięczny";
        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String synopsis = "Nielimitowana liczba wejść";
        List<String> features = List.of("siłownia", "fitness", "TRX", "rowery");

        existingGymPassDocument = new GymPassDocument(
                documentId,
                title,
                subheader,
                new Price(
                        amount,
                        currency,
                        period
                ),
                isPremium,
                new Description(
                        synopsis,
                        features
                )
        );
        existingGymPassDocument.setId("507f1f77bcf86cd799439011");
    }

    @Test
    void shouldUpdateOffer_whenValidRequestAndDocumentId_updatedAmount() throws InvalidGymPassOfferId, DuplicatedOffersException {

        //request document
        String title = "Karnet miesięczny";
        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        double amount = 149.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String synopsis = "Nielimitowana liczba wejść";
        List<String> features = List.of("siłownia", "fitness", "TRX", "rowery");
        GymPassOfferRequest gymPassOfferRequest = new GymPassOfferRequest();
        gymPassOfferRequest.setTitle(title);
        gymPassOfferRequest.setSubheader(subheader);
        gymPassOfferRequest.setAmount(amount);
        gymPassOfferRequest.setCurrency(currency);
        gymPassOfferRequest.setPeriod(period);
        gymPassOfferRequest.setPremium(isPremium);
        gymPassOfferRequest.setSynopsis(synopsis);
        gymPassOfferRequest.setFeatures(features);

        //response
        GymPassDTO gymPassDTO = new GymPassDTO(
                documentId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis,features)
        );

        //document
        GymPassDocument gymPassDocumentSavedInDB = new GymPassDocument(
                documentId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis,features)
        );
        gymPassDocumentSavedInDB.setId("507f1f77bcf86cd799439011");

        //when
        when(gymPassOfferDAO.findByDocumentId(documentId)).thenReturn(existingGymPassDocument);
        when(gymPassOfferDAO.findByTitle(title)).thenReturn(existingGymPassDocument);
        when(gymPassOfferDAO.save(gymPassDocumentSavedInDB)).thenReturn(gymPassDocumentSavedInDB);

        //then
        assertThat(offerService.updateGymPassOffer(documentId, gymPassOfferRequest)).isEqualTo(gymPassDTO);
    }

    @Test
    void shouldUpdateOffer_whenValidRequestAndDocumentId_updatedTitle() throws InvalidGymPassOfferId, DuplicatedOffersException {

        //request document
        String title = "Karnet miesięczny plus";
        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
        String synopsis = "Nielimitowana liczba wejść";
        List<String> features = List.of("siłownia", "fitness", "TRX", "rowery");
        GymPassOfferRequest gymPassOfferRequest = new GymPassOfferRequest();
        gymPassOfferRequest.setTitle(title);
        gymPassOfferRequest.setSubheader(subheader);
        gymPassOfferRequest.setAmount(amount);
        gymPassOfferRequest.setCurrency(currency);
        gymPassOfferRequest.setPeriod(period);
        gymPassOfferRequest.setPremium(isPremium);
        gymPassOfferRequest.setSynopsis(synopsis);
        gymPassOfferRequest.setFeatures(features);

        //response
        GymPassDTO gymPassDTO = new GymPassDTO(
                documentId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis,features)
        );

        //document
        GymPassDocument gymPassDocumentSavedInDB = new GymPassDocument(
                documentId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis,features)
        );
        gymPassDocumentSavedInDB.setId("507f1f77bcf86cd799439011");

        //when
        when(gymPassOfferDAO.findByDocumentId(documentId)).thenReturn(existingGymPassDocument);
        when(gymPassOfferDAO.findByTitle(title)).thenReturn(existingGymPassDocument);
        when(gymPassOfferDAO.save(gymPassDocumentSavedInDB)).thenReturn(gymPassDocumentSavedInDB);

        //then
        assertThat(offerService.updateGymPassOffer(documentId, gymPassOfferRequest)).isEqualTo(gymPassDTO);
    }
}
