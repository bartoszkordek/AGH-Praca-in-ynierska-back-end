package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.TrainerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerDAO extends MongoRepository<TrainerDocument, String> {


}
