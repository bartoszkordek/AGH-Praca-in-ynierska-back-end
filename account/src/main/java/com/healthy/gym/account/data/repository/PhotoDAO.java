package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.PhotoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoDAO extends MongoRepository<PhotoDocument, String> {
    PhotoDocument findByUserId(String userId);

    PhotoDocument findPhotoDocumentById(String id);
}
