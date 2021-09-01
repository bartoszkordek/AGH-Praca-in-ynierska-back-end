package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.TrainerDocument;
import com.healthy.gym.account.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerDAO extends MongoRepository<TrainerDocument, String> {

    TrainerDocument findByUserDocument(UserDocument userDocument);

    void deleteByUserDocument(UserDocument userDocument);
}
