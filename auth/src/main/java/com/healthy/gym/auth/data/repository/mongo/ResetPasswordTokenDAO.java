package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResetPasswordTokenDAO extends MongoRepository<ResetPasswordTokenDocument, String> {
    ResetPasswordTokenDocument findByToken(String token);

    ResetPasswordTokenDocument findByUserDocument(UserDocument userDocument);
}
