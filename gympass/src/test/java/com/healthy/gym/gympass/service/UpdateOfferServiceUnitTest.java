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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UpdateOfferServiceUnitTest {

    @Autowired
    private OfferService offerService;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    private String documentId;
    private GymPassDocument existingGymPassDocument;
    private GymPassOfferRequest gymPassOfferRequest;
    private GymPassDTO gymPassDTO;
    private GymPassDocument gymPassDocumentSavedInDB;

    private String title;

    @BeforeEach
    void setUp() throws IOException {

        //document
        documentId = UUID.randomUUID().toString();
        title = "Karnet miesięczny";
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

        //request
        gymPassOfferRequest = new GymPassOfferRequest();
        gymPassOfferRequest.setTitle(title);
        gymPassOfferRequest.setSubheader(subheader);
        gymPassOfferRequest.setAmount(amount);
        gymPassOfferRequest.setCurrency(currency);
        gymPassOfferRequest.setPeriod(period);
        gymPassOfferRequest.setPremium(isPremium);
        gymPassOfferRequest.setSynopsis(synopsis);
        gymPassOfferRequest.setFeatures(features);

        //response
        gymPassDTO = new GymPassDTO(
                documentId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis,features)
        );


        //saved document
        gymPassDocumentSavedInDB = new GymPassDocument(
                documentId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis,features)
        );
        gymPassDocumentSavedInDB.setId("507f1f77bcf86cd799439011");
    }

    @Nested
    class ShouldUpdateOffer{
        @Test
        void shouldUpdateOffer_whenValidRequestAndDocumentId_updatedPrice() throws InvalidGymPassOfferId, DuplicatedOffersException {

            //request document
            double amount = 149.99;
            String currency = "zł";
            String period = "miesiąc";
            gymPassOfferRequest.setAmount(amount);

            //response
            gymPassDTO.setPrice(new Price(amount , currency, period));

            //document
            gymPassDocumentSavedInDB.setPrice(new Price(amount, currency, period));

            //when
            when(gymPassOfferDAO.findByDocumentId(documentId)).thenReturn(existingGymPassDocument);
            when(gymPassOfferDAO.findByTitle(title)).thenReturn(existingGymPassDocument);
            when(gymPassOfferDAO.save(gymPassDocumentSavedInDB)).thenReturn(gymPassDocumentSavedInDB);

            //then
            assertThat(offerService.updateGymPassOffer(documentId, gymPassOfferRequest)).isEqualTo(gymPassDTO);
        }

        @Test
        void shouldUpdateOffer_whenValidRequestAndDocumentId_updatedTitle() throws InvalidGymPassOfferId, DuplicatedOffersException {

            //request
            String title = "Karnet miesięczny plus";
            gymPassOfferRequest.setTitle(title);

            //response
            gymPassDTO.setTitle(title);

            //saved document
            gymPassDocumentSavedInDB.setTitle(title);

            //when
            when(gymPassOfferDAO.findByDocumentId(documentId)).thenReturn(existingGymPassDocument);
            when(gymPassOfferDAO.findByTitle(title)).thenReturn(existingGymPassDocument);
            when(gymPassOfferDAO.save(gymPassDocumentSavedInDB)).thenReturn(gymPassDocumentSavedInDB);

            //then
            assertThat(offerService.updateGymPassOffer(documentId, gymPassOfferRequest)).isEqualTo(gymPassDTO);
        }
    }

    @Nested
    class ShouldNotUpdateOffer{

        @Test
        void shouldNotUpdateOffer_whenInvalidId(){

            //when
            when(gymPassOfferDAO.findByDocumentId(documentId)).thenReturn(null);


            //then
            assertThatThrownBy(() ->
                    offerService.updateGymPassOffer(any(),gymPassOfferRequest)
            ).isInstanceOf(InvalidGymPassOfferId.class);
        }

    }

}
