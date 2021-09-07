package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.enums.GymRole;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDAO extends MongoRepository<UserDocument, String> {
    UserDocument findByEmail(String email);

    UserDocument findByUserId(String userId);

    List<UserDocument> findAllByGymRolesContains(GymRole gymRole, Sort sort);

    List<UserDocument> findAllByCreatedAtBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Sort sort
    );
}
