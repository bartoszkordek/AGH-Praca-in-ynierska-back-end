package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDAO extends MongoRepository<UserDocument, String> {
    UserDocument findByEmail(String email);
}
