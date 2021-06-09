package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDAO extends MongoRepository<UserDocument, String> {
    UserDocument findByEmail(String email);

    UserDocument findByUserId(String userId);
}
