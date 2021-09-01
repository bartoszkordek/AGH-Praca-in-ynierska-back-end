package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.ImageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageDAO extends MongoRepository<ImageDocument, String> {
    ImageDocument findByImageId(String imageId);

    void deleteByImageId(String imageId);
}
