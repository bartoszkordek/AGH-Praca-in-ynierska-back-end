package com.healthy.gym.user.data.entity;

import javax.persistence.Entity;

@Entity
public class ResetPasswordToken extends AbstractTokenEntity {
    private static final int EXPIRATION_IN_HOURS = 2;

    public ResetPasswordToken() {
    }

    public ResetPasswordToken(String token, UserEntity userEntity) {
        super(token, userEntity, EXPIRATION_IN_HOURS);
    }
}
