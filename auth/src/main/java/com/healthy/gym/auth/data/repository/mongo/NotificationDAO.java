package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationDAO extends MongoRepository<NotificationDocument, String> {
}
