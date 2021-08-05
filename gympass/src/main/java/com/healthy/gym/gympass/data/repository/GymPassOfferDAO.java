package com.healthy.gym.gympass.data.repository;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GymPassOfferDAO extends MongoRepository<GymPassDocument, String> {

    GymPassDocument findFirstByTitle(String title);

    GymPassDocument findByDocumentId(String documentId);

    GymPassDocument findByTitle(String title);

}
