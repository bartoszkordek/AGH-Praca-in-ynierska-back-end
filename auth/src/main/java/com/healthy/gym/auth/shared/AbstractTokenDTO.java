package com.healthy.gym.auth.shared;

import com.healthy.gym.auth.data.document.UserDocument;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractTokenDTO {
    private String id;
    private String token;
    private UserDocument userDocument;
    private LocalDateTime expiryDate;
    private boolean wasUsed;

    protected AbstractTokenDTO() {
    }

    protected AbstractTokenDTO(
            String id,
            String token,
            UserDocument userDocument,
            LocalDateTime expiryDate,
            boolean wasUsed
    ) {
        this.id = id;
        this.token = token;
        this.userDocument = userDocument;
        this.expiryDate = expiryDate;
        this.wasUsed = wasUsed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        AbstractTokenDTO that = (AbstractTokenDTO) o;
        return wasUsed == that.wasUsed
                && Objects.equals(id, that.id)
                && Objects.equals(token, that.token)
                && Objects.equals(userDocument, that.userDocument)
                && Objects.equals(expiryDate, that.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, userDocument, expiryDate, wasUsed);
    }

    @Override
    public String toString() {
        return "AbstractTokenDTO{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", userDocument=" + userDocument +
                ", expiryDate=" + expiryDate +
                ", wasUsed=" + wasUsed +
                '}';
    }
}
