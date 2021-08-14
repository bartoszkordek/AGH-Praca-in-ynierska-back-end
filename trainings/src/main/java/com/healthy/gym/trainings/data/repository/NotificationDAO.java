package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationDAO extends MongoRepository<NotificationDocument, String> {
}
