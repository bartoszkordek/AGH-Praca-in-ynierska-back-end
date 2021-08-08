package com.healthy.gym.gympass.data.repository;

import com.healthy.gym.gympass.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDAO extends MongoRepository<UserDocument, String> {

    UserDocument findByUserId(String userId);
}
