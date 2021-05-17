package com.healthy.gym.auth.data.entity;

import javax.persistence.Entity;

@Entity
public class RegistrationToken extends AbstractTokenEntity {
    private static final int EXPIRATION_IN_HOURS = 24;

    public RegistrationToken() {
        //empty constructor required by JPA
    }

    public RegistrationToken(String token, UserEntity userEntity) {
        super(token, userEntity, EXPIRATION_IN_HOURS);
    }
}
