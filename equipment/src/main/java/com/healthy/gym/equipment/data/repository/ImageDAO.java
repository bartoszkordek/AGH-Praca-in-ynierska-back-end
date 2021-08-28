package com.healthy.gym.equipment.data.repository;

import com.healthy.gym.equipment.data.document.ImageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageDAO extends MongoRepository<ImageDocument, String> {
    ImageDocument findByImageId(String imageId);

    void deleteByImageId(String imageId);
}
