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
                message = "Podane hasła się nie zgadzają"
        )
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserRequest {

    @NotNull(message = "Pole imię jest wymagane.")
    @Size(min = 2, max = 60, message = "Pole imię powinno mieć od 2 do 60 znaków.")
    private String name;

    @NotNull(message = "Pole nazwisko jest wymagane.")
    @Size(min = 2, max = 60, message = "Pole nazwisko powinno mieć od 2 do 60 znaków.")
    private String surname;

    @NotNull(message = "Pole email jest wymagane.")
    @Email(message = "Proszę podać poprawny adres email.")
    private String email;

    @ValidPhoneNumber(message = "Podany numer telefonu ma niepoprawny format")
    @JsonProperty("phone")
    private String phoneNumber;

    @NotNull(message = "Pole hasło jest wymagane.")
    @Size(min = 8, max = 24, message = "Pole hasło powinno mieć od 8 do 24 znaków.")
    private String password;

    @NotNull(message = "Pole powtórz hasło jest wymagane.")
    @Size(min = 8, max = 24, message = "Pole powtórz hasło powinno mieć od 8 do 24 znaków.")
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
