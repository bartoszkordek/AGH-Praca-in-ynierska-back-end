package com.healthy.gym.account.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicUserInfoDTO {
    private String userId;
    private String name;
    private String surname;
    @JsonProperty("avatar")
    private String avatarUrl;

    public BasicUserInfoDTO() {
    }

    public BasicUserInfoDTO(String userId, String name, String surname, String avatarUrl) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.avatarUrl = avatarUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicUserInfoDTO that = (BasicUserInfoDTO) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname)
                && Objects.equals(avatarUrl, that.avatarUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, surname, avatarUrl);
    }

    @Override
    public String toString() {
        return "BasicUserInfoDTO{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
