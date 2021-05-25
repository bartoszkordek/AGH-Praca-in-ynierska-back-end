package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.document.UserPrivacyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPrivacyDAO extends MongoRepository<UserPrivacyDocument, String> {
    UserPrivacyDocument findByUserDocument(UserDocument userDocument);
}
