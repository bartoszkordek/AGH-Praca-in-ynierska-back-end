package com.healthy.gym.task.data.repository;

import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.enums.GymRole;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDAO  extends MongoRepository<UserDocument, String> {

    UserDocument findByUserId(String userId);

    UserDocument findByGymRolesExists(GymRole role);
}
