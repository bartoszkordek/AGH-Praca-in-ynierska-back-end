package com.healthy.gym.user.data.repository;

import com.healthy.gym.user.data.entity.ResetPasswordToken;
import com.healthy.gym.user.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordTokenDAO extends JpaRepository<ResetPasswordToken, Long> {
    ResetPasswordToken findByToken(String token);

    ResetPasswordToken findByUserEntity(UserEntity userEntity);
}
