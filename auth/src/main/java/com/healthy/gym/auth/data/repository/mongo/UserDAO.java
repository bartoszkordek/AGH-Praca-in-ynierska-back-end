package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDAO extends MongoRepository<UserDocument, String> {
    UserDocument findByEmail(String email);
}
