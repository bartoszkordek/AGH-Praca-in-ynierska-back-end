package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GetOffersServiceTest {

    @Autowired
    private OfferService offerService;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    @Test
    void shouldGetOffers_whenExist() throws NoOffersException {

        //before
        //documents
        String documentId1 = UUID.randomUUID().toString();

        String title1 = "Karnet miesięczny";
        String subheader1 = "Najepszy wybór dla regularnie uprawiających sport";

        double amount1 = 139.99;
        String currency1 = "zł";
        String period1 = "miesiąc";
        Price price1 = new Price(amount1, currency1, period1);

        boolean isPremium1 = false;

        String synopsis1 = "Nielimitowana liczba wejść";
        List<String> features1 = List.of("siłownia", "fitness", "TRX", "rowery");
        Description description1 = new Description(synopsis1, features1);

        GymPassDocument gymPassDocument1 = new GymPassDocument(
                documentId1,
                title1,
                subheader1,
                price1,
                isPremium1,
                description1
        );

        String documentId2 = UUID.randomUUID().toString();

        String title2 = "Karnet kwartalny";
        String subheader2 = "Najepszy wybór dla oszczędnych i regularnie uprawiających sport";

        double amount2 = 399.99;
        String currency2 = "zł";
        String period2 = "kwartał";
        Price price2 = new Price(amount2, currency2, period2);

        boolean isPremium2 = false;

        String synopsis2 = "Nielimitowana liczba wejść";
        List<String> features2 = List.of("siłownia", "fitness", "TRX", "rowery");
        Description description2 = new Description(synopsis1, features1);

        GymPassDocument gymPassDocument2 = new GymPassDocument(
                documentId2,
                title2,
                subheader2,
                price2,
                isPremium2,
                description2
        );

        List<GymPassDocument> gymPassDocuments = List.of(gymPassDocument1, gymPassDocument2);

        //DTOs
        GymPassDTO gymPassDTO1 = new GymPassDTO(
                documentId1,
                title1,
                subheader1,
                price1,
                isPremium1,
                description1
        );

        GymPassDTO gymPassDTO2 = new GymPassDTO(
                documentId2,
                title2,
                subheader2,
                price2,
                isPremium2,
                description2
        );

        List<GymPassDTO> gymPassOfferDTOs = List.of(gymPassDTO1, gymPassDTO2);

        //when
        when(gymPassOfferDAO.findAll()).thenReturn(gymPassDocuments);

        //then
        assertThat(offerService.getGymPassOffers().get(0)).isEqualTo(gymPassOfferDTOs.get(0));
        assertThat(offerService.getGymPassOffers().get(1)).isEqualTo(gymPassOfferDTOs.get(1));
    }

    @Test
    void shouldThrowNoOffersException_whenEmptyOffersList(){

        //before
        List<GymPassDocument> gymPassDocuments = new ArrayList<>();

        //when
        when(gymPassOfferDAO.findAll()).thenReturn(gymPassDocuments);

        assertThatThrownBy(() ->
                offerService.getGymPassOffers()
        ).isInstanceOf(NoOffersException.class);
    }

}
