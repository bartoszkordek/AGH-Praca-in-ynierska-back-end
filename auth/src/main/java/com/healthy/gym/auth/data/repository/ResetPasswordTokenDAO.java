package com.healthy.gym.auth.data.repository;

import com.healthy.gym.auth.data.entity.ResetPasswordToken;
import com.healthy.gym.auth.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordTokenDAO extends JpaRepository<ResetPasswordToken, Long> {
    ResetPasswordToken findByToken(String token);

    ResetPasswordToken findByUserEntity(UserEntity userEntity);
}
