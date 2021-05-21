package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistrationTokenDAO extends MongoRepository<RegistrationTokenDocument, String> {
    RegistrationTokenDocument findByToken(String token);

    RegistrationTokenDocument findByUserDocument(UserDocument userDocument);
}
