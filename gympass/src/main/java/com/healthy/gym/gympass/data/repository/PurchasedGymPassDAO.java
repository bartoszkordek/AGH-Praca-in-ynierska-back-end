package com.healthy.gym.gympass.data.repository;

import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PurchasedGymPassDAO extends MongoRepository<PurchasedGymPassDocument, String> {

    PurchasedGymPassDocument findByPurchasedGymPassDocumentId(String purchasedGymPassDocumentId);
}
