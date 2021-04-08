package com.healthy.gym.user.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.user.validation.FieldsValueMatch;
import com.healthy.gym.user.validation.ValidPhoneNumber;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "password",
                fieldToMatch = "matchingPassword",
                message = "{password.matching.failure}"
        )
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 60, message = "{field.name.failure}")
    private String name;

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 60, message = "{field.surname.failure}")
    private String surname;

    @NotNull(message = "{field.required}")
    @Email(message = "{field.email.failure}")
    private String email;

    @ValidPhoneNumber(message = "{field.phone.number.failure}")
    @JsonProperty("phone")
    private String phoneNumber;

    @NotNull(message = "{field.required}")
    @Size(min = 8, max = 24, message = "{field.password.failure}")
    private String password;

    @NotNull(message = "{field.required}")
    @Size(min = 8, max = 24, message = "{field.password.match.failure}")
    private String matchingPassword;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
