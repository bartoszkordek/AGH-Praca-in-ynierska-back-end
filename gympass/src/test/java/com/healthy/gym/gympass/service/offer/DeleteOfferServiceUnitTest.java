package com.healthy.gym.gympass.service.offer;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.service.OfferService;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DeleteOfferServiceUnitTest {

    @Autowired
    private OfferService offerService;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    private String documentId;
    private GymPassDocument existingGymPassDocument;
    private GymPassDTO gymPassDTO;

    @BeforeEach
    void setUp() {

        //existing document
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


        //response
        gymPassDTO = new GymPassDTO(
                documentId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis,features)
        );
    }

    @Nested
    class ShouldDeleteOffer{

        @Test
        void shouldDeleteOffer_whenValidDocumentId() throws GymPassNotFoundException {

            //when
            when(gymPassOfferDAO.findByDocumentId(documentId)).thenReturn(existingGymPassDocument);

            //then
            assertThat(offerService.deleteGymPassOffer(documentId)).isEqualTo(gymPassDTO);
        }
    }


    @Nested
    class ShouldNotDeleteOffer{

        @Test
        void shouldDeleteOffer_whenInvalidDocumentId() throws GymPassNotFoundException {

            //when
            when(gymPassOfferDAO.findByDocumentId(documentId)).thenReturn(null);

            //then
            //then
            assertThatThrownBy(() ->
                    offerService.deleteGymPassOffer(documentId)
            ).isInstanceOf(GymPassNotFoundException.class);
        }
    }

}
