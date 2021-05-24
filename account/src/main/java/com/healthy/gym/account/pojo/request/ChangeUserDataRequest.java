package com.healthy.gym.account.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.account.validation.ValidEmail;
import com.healthy.gym.account.validation.ValidPhoneNumber;

import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeUserDataRequest {

    @Size(min = 2, max = 60, message = "{field.name.failure}")
    private String name;

    @Size(min = 2, max = 60, message = "{field.surname.failure}")
    private String surname;

    @ValidEmail(message = "{field.email.failure}")
    private String email;

    @JsonProperty("phone")
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
}
