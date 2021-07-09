package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.UserPrivacyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPrivacyDAO extends MongoRepository<UserPrivacyDocument, String> {
}
