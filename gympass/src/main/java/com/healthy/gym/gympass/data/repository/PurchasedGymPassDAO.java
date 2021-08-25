package com.healthy.gym.gympass.data.repository;

import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchasedGymPassDAO extends MongoRepository<PurchasedGymPassDocument, String> {

    PurchasedGymPassDocument findByPurchasedGymPassDocumentId(String purchasedGymPassDocumentId);

    List<PurchasedGymPassDocument> findAllByUserAndStartDateAfterAndEndDateBefore(
            UserDocument userDocument,
            LocalDate startDate,
            LocalDate endDate
    );

    Page<PurchasedGymPassDocument> findAllByPurchaseDateTimeBetween(
            LocalDateTime purchasedStartDateTime,
            LocalDateTime purchasedEndDateTime,
            Pageable pageable
    );

    PurchasedGymPassDocument findFirstByUserAndEndDateAfter(
            UserDocument userDocument,
            LocalDate endDate
    );

    List<PurchasedGymPassDocument> findAllByUserAndEndDateAfter(
            UserDocument userDocument,
            LocalDate endDate
    );
}
