package com.healthy.gym.account.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.account.validation.ValidEmailNullable;
import com.healthy.gym.account.validation.ValidPhoneNumber;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeUserDataRequest {

    @Nullable
    @Size(min = 2, max = 60, message = "{field.name.failure}")
    private String name;

    @Nullable
    @Size(min = 2, max = 60, message = "{field.surname.failure}")
    private String surname;

    @ValidEmailNullable(message = "{field.email.failure}")
    private String email;

    @ValidPhoneNumber(message = "{field.phone.number.failure}")
    private String phoneNumber;

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
        ChangeUserDataRequest that = (ChangeUserDataRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname)
                && Objects.equals(email, that.email)
                && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email, phoneNumber);
    }

    @Override
    public String toString() {
        return "ChangeUserDataRequest{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
