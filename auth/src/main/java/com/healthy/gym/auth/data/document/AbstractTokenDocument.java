package com.healthy.gym.auth.data.document;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public abstract class AbstractTokenDocument {

    private String token;

    @DBRef
    private UserDocument userDocument;
    private LocalDateTime expiryDate;
    private boolean wasUsed;

    protected AbstractTokenDocument() {
    }

    protected AbstractTokenDocument(String token, UserDocument userDocument, int expirationInHours) {
        this.token = token;
        this.userDocument = userDocument;
        expiryDate = calculateExpiryDate(expirationInHours);
    }

    private LocalDateTime calculateExpiryDate(int expirationInHours) {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusHours(expirationInHours);
        localDateTime = localDateTime.truncatedTo(ChronoUnit.MILLIS);
        return localDateTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDocument getUserDocument() {
        return userDocument;
    }

    public void setUserDocument(UserDocument userDocument) {
        this.userDocument = userDocument;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isWasUsed() {
        return wasUsed;
    }

    public void setWasUsed(boolean wasUsed) {
        this.wasUsed = wasUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTokenDocument that = (AbstractTokenDocument) o;
        return wasUsed == that.wasUsed
                && Objects.equals(token, that.token)
                && Objects.equals(userDocument, that.userDocument)
                && Objects.equals(expiryDate, that.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, userDocument, expiryDate, wasUsed);
    }

    @Override
    public String toString() {
        return "AbstractTokenDocument{" +
                "token='" + token + '\'' +
                ", userDocument=" + userDocument +
                ", expiryDate=" + expiryDate +
                ", wasUsed=" + wasUsed +
                '}';
    }
}
