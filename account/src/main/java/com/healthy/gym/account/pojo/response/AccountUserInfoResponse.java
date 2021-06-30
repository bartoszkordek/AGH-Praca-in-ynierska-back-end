package com.healthy.gym.account.pojo.response;

import java.util.Objects;

public class AccountUserInfoResponse extends AbstractResponse {

    private final String name;
    private final String surname;
    private final String email;
    private final String phone;

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
        this.phone = phone;
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
        this.phone = phone;
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
                && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, surname, email, phone);
    }

    @Override
    public String toString() {
        return "AccountUserInfoResponse{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                "} " + super.toString();
    }
}
