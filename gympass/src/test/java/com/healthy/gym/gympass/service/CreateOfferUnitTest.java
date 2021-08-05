package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CreateOfferUnitTest {

    @Autowired
    private OfferService offerService;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    @Test
    void shouldCreateOffer_whenValidRequest() throws DuplicatedOffersException {

        //request document
        String title = "Karnet miesięczny";
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
        String documentId = UUID.randomUUID().toString();
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
        when(gymPassOfferDAO.findFirstByTitle(title)).thenReturn(null);
        when(gymPassOfferDAO.save(any())).thenReturn(gymPassDocumentSavedInDB);

        //then
        assertThat(offerService.createGymPassOffer(gymPassOfferRequest)).isEqualTo(gymPassDTO);
    }

    @Test
    void shouldNotCreateOffer_whenDuplicatedTitles() throws DuplicatedOffersException {

        //request document
        String title = "Karnet miesięczny";
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
        String documentId = UUID.randomUUID().toString();
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

        GymPassDocument gymPassDocumentExisting = new GymPassDocument();

        //when
        when(gymPassOfferDAO.findFirstByTitle(title)).thenReturn(gymPassDocumentExisting);
        when(gymPassOfferDAO.save(any())).thenReturn(gymPassDocumentSavedInDB);

        //then
        assertThatThrownBy(() ->
                offerService.createGymPassOffer(gymPassOfferRequest)
        ).isInstanceOf(DuplicatedOffersException.class);
    }
}