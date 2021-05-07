package com.healthy.gym.user.data.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class RegistrationToken {
    private static final int EXPIRATION_IN_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private UserEntity userEntity;

    private LocalDateTime expiryDate;


    public RegistrationToken() {
        //empty constructor required by JPA
    }

    public RegistrationToken(String token, UserEntity userEntity) {
        this.token = token;
        this.userEntity = userEntity;
        expiryDate = calculateExpiryDate(EXPIRATION_IN_HOURS);
    }

    private LocalDateTime calculateExpiryDate(int expirationInHours) {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusHours(expirationInHours);
        return localDateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationToken that = (RegistrationToken) o;
        return id == that.id
                && Objects.equals(token, that.token)
                && Objects.equals(userEntity, that.userEntity)
                && Objects.equals(expiryDate, that.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, userEntity, expiryDate);
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", userEntity=" + userEntity +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
