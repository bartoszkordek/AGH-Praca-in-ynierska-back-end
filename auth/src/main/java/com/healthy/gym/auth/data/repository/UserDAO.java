package com.healthy.gym.auth.data.repository;

import com.healthy.gym.auth.data.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}
