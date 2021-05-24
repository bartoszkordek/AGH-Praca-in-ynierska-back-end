package com.healthy.gym.account.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ChangeUserDataResponse extends AbstractResponse {

    private String name;
    private String surname;
    private String email;
    @JsonProperty("phone")
    private String phoneNumber;

    public ChangeUserDataResponse() {
    }

    public ChangeUserDataResponse(String message) {
        super(message);
    }

    public ChangeUserDataResponse(
            String message,
            String name,
            String surname,
            String email,
            String phoneNumber
    ) {
        super(message);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ChangeUserDataResponse response = (ChangeUserDataResponse) o;
        return Objects.equals(name, response.name)
                && Objects.equals(surname, response.surname)
                && Objects.equals(email, response.email)
                && Objects.equals(phoneNumber, response.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, surname, email, phoneNumber);
    }

    @Override
    public String toString() {
        return "ChangeUserDataResponse{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                "} " + super.toString();
    }
}
