package com.healthy.gym.user.data.repository;

import com.healthy.gym.user.data.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}
