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
import com.healthy.gym.gympass.exception.StartDateAfterEndDateException;
import com.healthy.gym.gympass.service.PurchaseService;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class GetGymPassesServiceUnitTest {

    @Autowired
    private PurchaseService purchaseService;

    @MockBean
    private PurchasedGymPassDAO purchasedGymPassDAO;

    @MockBean
    private GymPassOfferDAO gymPassOfferDAO;

    @MockBean
    private UserDAO userDAO;

    private int page;
    private int size;
    private Pageable paging;
    private List<PurchasedGymPassDocument> purchasedGymPassDocuments;
    private Page<PurchasedGymPassDocument> purchasedGymPassDocumentsPages;
    private List<PurchasedGymPassDTO> response;

    @BeforeEach
    void setUp() {

        page = 0;
        size = 10;
        paging = PageRequest.of(page, size);

        long pastDays1 = 5;
        long pastDays2 = 65;
        String userId = UUID.randomUUID().toString();
        String startDate1 = LocalDate.now().minusDays(pastDays1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate1 = LocalDate.now().minusDays(pastDays1).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String startDate2 = LocalDate.now().minusDays(pastDays2).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate2 = LocalDate.now().minusDays(pastDays2).plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        int entries = Integer.MAX_VALUE;


        String name = "Jan";
        String surname = "Kowalski";
        UserDocument userDocument = new UserDocument();
        userDocument.setName(name);
        userDocument.setSurname(surname);
        userDocument.setUserId(userId);
        userDocument.setGymRoles(List.of(GymRole.USER));
        userDocument.setId("507f1f77bcf86cd799435213");

        String gymPassOfferId1 = UUID.randomUUID().toString();
        String gymPassOfferId2 = UUID.randomUUID().toString();
        String title1 = "Karnet miesięczny";
        String title2 = "Karnet miesięczny PLUS";
        double amount1 = 139.99;
        double amount2 = 159.99;
        String currency = "zł";
        String period = "miesiąc";
        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        String synopsis = "Nielimitowana liczba wejść";
        List<String> features1 = List.of("siłownia", "fitness", "TRX", "rowery");
        List<String> features2 = List.of("siłownia", "fitness", "TRX", "rowery");

        GymPassDocument gymPassOfferDocument1 = new GymPassDocument(
                gymPassOfferId1,
                title1,
                subheader,
                new Price(amount1, currency, period),
                false,
                new Description(synopsis,features1)
        );
        gymPassOfferDocument1.setId("507f1f77bcf86cd799439011");

        GymPassDocument gymPassOfferDocument2 = new GymPassDocument(
                gymPassOfferId2,
                title2,
                subheader,
                new Price(amount2, currency, period),
                true,
                new Description(synopsis,features2)
        );
        gymPassOfferDocument1.setId("507f1f77bcf86cd7994129055");

        String purchasedGymPassDocumentId1 = UUID.randomUUID().toString();
        LocalDateTime purchaseDateTime1 = LocalDateTime.now().minusDays(pastDays1);
        PurchasedGymPassDocument purchasedGymPassDocument1 = new PurchasedGymPassDocument(
                purchasedGymPassDocumentId1,
                gymPassOfferDocument1,
                userDocument,
                purchaseDateTime1,
                LocalDate.parse(startDate1, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate1, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );
        purchasedGymPassDocument1.setId("507f1f77bcf86cd799439944");

        PurchasedGymPassDTO purchasedGymPassDTO1 = new PurchasedGymPassDTO(
                purchasedGymPassDocumentId1,
                new SimpleGymPassDTO(
                        gymPassOfferId1,
                        title1,
                        new Price(amount1, currency, period),
                        false
                ),
                new BasicUserInfoDTO(userId, name, surname),
                purchaseDateTime1,
                LocalDate.parse(startDate1, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate1, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );


        String purchasedGymPassDocumentId2 = UUID.randomUUID().toString();
        LocalDateTime purchaseDateTime2 = LocalDateTime.now().minusDays(pastDays2);
        PurchasedGymPassDocument purchasedGymPassDocument2 = new PurchasedGymPassDocument(
                purchasedGymPassDocumentId2,
                gymPassOfferDocument2,
                userDocument,
                purchaseDateTime2,
                LocalDate.parse(startDate2, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate2, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );
        purchasedGymPassDocument2.setId("507f1f77bcf86cd799422356");

        PurchasedGymPassDTO purchasedGymPassDTO2 = new PurchasedGymPassDTO(
                purchasedGymPassDocumentId2,
                new SimpleGymPassDTO(
                        gymPassOfferId2,
                        title2,
                        new Price(amount2, currency, period),
                        true
                ),
                new BasicUserInfoDTO(userId, name, surname),
                purchaseDateTime2,
                LocalDate.parse(startDate2, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.parse(endDate2, DateTimeFormatter.ISO_LOCAL_DATE),
                entries
        );

        purchasedGymPassDocuments = List.of(
                purchasedGymPassDocument1,
                purchasedGymPassDocument2
        );

        response = List.of(purchasedGymPassDTO1, purchasedGymPassDTO2);

        purchasedGymPassDocumentsPages = new PageImpl<>(
                purchasedGymPassDocuments, paging, size);
    }

    @Test
    void shouldGetPurchasedGymPasses_whenValidDates() throws StartDateAfterEndDateException {
        //before
        LocalDateTime purchaseStartDateTimeMinusOneDay
                = LocalDateTime.of(1999,12,31, 23,59,59);
        LocalDateTime purchaseEndDateTimePlusOneDay
                = LocalDateTime.of(2031,1,1, 0,0,0);

        //when
        when(purchasedGymPassDAO.findAllByPurchaseDateTimeBetween(
                purchaseStartDateTimeMinusOneDay,
                purchaseEndDateTimePlusOneDay,
                paging
        )).thenReturn(purchasedGymPassDocumentsPages);

        //then
        assertThat(purchaseService.getGymPasses("2000-01-01", "2030-12-31", paging))
                .isEqualTo(response);
    }


    @Test
    void shouldGetPurchasedGymPasses_whenDatesNotDeclared() throws StartDateAfterEndDateException {
        //when
        when(purchasedGymPassDAO.findAllByPurchaseDateTimeBetween(
                any(),
                any(),
                any()
        )).thenReturn(purchasedGymPassDocumentsPages);

        //then
        assertThat(purchaseService.getGymPasses(null, null, paging))
                .isEqualTo(response);
    }

    @Test
    void shouldNotGetGymPasses_whenStartDateAfterEndDate() {
        //before
        String startDate = "2030-12-31";
        String endDate = "2000-01-01";

        //then
        assertThatThrownBy(() ->
                purchaseService.getGymPasses(startDate, endDate, paging)
        ).isInstanceOf(StartDateAfterEndDateException.class);
    }
}
