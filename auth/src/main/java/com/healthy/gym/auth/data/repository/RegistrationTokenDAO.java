package com.healthy.gym.auth.data.repository;

import com.healthy.gym.auth.data.entity.RegistrationToken;
import com.healthy.gym.auth.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationTokenDAO extends JpaRepository<RegistrationToken, Long> {
    RegistrationToken findByToken(String token);

    RegistrationToken findByUserEntity(UserEntity userEntity);
}
