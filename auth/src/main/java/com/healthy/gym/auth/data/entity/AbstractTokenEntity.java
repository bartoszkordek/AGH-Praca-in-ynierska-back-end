package com.healthy.gym.auth.data.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class AbstractTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private UserEntity userEntity;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    private boolean wasUsed;

    public AbstractTokenEntity() {
        //empty constructor required by JPA
    }

    public AbstractTokenEntity(String token, UserEntity userEntity, int expirationInHours) {
        this.token = token;
        this.userEntity = userEntity;
        expiryDate = calculateExpiryDate(expirationInHours);
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

    public boolean wasUsed() {
        return wasUsed;
    }

    public void setWasUsed(boolean wasUsed) {
        this.wasUsed = wasUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTokenEntity that = (AbstractTokenEntity) o;
        return id == that.id && wasUsed == that.wasUsed && Objects.equals(token, that.token) && Objects.equals(userEntity, that.userEntity) && Objects.equals(expiryDate, that.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, userEntity, expiryDate, wasUsed);
    }

    @Override
    public String toString() {
        return "AbstractTokenEntity{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", userEntity=" + userEntity +
                ", expiryDate=" + expiryDate +
                ", wasUsed=" + wasUsed +
                '}';
    }
}
