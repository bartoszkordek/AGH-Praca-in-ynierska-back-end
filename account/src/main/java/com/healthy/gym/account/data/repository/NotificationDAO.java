package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.NotificationDocument;
import com.healthy.gym.account.data.document.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationDAO extends MongoRepository<NotificationDocument, String> {
    Page<NotificationDocument> findAllByToEquals(UserDocument userDocument, Pageable pageable);

    Optional<NotificationDocument> findByNotificationId(String notificationId);
}
