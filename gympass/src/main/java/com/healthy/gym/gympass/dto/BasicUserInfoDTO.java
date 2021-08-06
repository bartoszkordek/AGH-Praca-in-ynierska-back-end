package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicUserInfoDTO {
    private String userId;
    private String name;
    private String surname;

    public BasicUserInfoDTO() {
    }

    public BasicUserInfoDTO(
            String userId,
            String name,
            String surname
    ) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    @Override
    public String toString() {
        return "BasicUserInfoDTO{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicUserInfoDTO that = (BasicUserInfoDTO) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                userId,
                name,
                surname
        );
    }
}
