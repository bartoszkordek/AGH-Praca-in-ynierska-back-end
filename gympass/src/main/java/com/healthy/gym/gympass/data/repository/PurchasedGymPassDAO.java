package com.healthy.gym.gympass.data.repository;

import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PurchasedGymPassDAO extends MongoRepository<PurchasedGymPassDocument, String> {

    PurchasedGymPassDocument findByPurchasedGymPassDocumentId(String purchasedGymPassDocumentId);

    List<PurchasedGymPassDocument> findAllByUser(UserDocument userDocument);
}
