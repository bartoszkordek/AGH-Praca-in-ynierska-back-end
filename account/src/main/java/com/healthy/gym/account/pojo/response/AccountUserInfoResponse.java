package com.healthy.gym.account.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AccountUserInfoResponse extends AbstractResponse {

    private String name;
    private String surname;
    private String email;
    @JsonProperty("phone")
    private String phoneNumber;

    public AccountUserInfoResponse() {
    }

    public AccountUserInfoResponse(
            String message,
            String name,
            String surname,
            String email,
            String phone
    ) {
        super(message);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phone;
    }

    public AccountUserInfoResponse(
            String name,
            String surname,
            String email,
            String phone
    ) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phone;
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
        AccountUserInfoResponse that = (AccountUserInfoResponse) o;
        return Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname)
                && Objects.equals(email, that.email)
                && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, surname, email, phoneNumber);
    }

    @Override
    public String toString() {
        return "AccountUserInfoResponse{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phoneNumber + '\'' +
                "} " + super.toString();
    }
}
