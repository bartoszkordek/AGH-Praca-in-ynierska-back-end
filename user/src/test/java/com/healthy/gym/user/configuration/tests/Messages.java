package com.healthy.gym.user.configuration.tests;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"user.sing-up.failure", "Rejestracja zakończona niepowodzeniem."},
                {"user.sing-up.success", "Użytkownik został zarejestrowany."},
                {"field.required", "Pole jest wymagane."},
                {"field.name.failure", "Imię powinno mieć od 2 do 60 znaków."},
                {"field.surname.failure", "Nazwisko powinno mieć od 2 do 60 znaków."},
                {"field.email.failure", "Proszę podać poprawny adres email."},
                {"field.phone.number.failure", "Niepoprawny format numeru telefonu."},
                {"field.password.failure", "Hasło powinno mieć od 8 do 24 znaków."},
                {"field.password.match.failure", "Podane hasła powinny być identyczne."},
                {"user.sign-up.email.exists", "Podany adres email jest już zajęty."},
                {"user.log-in.fail", "Nieprawidłowy email lub hasło."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"user.sing-up.failure", "Registration failed."},
                {"user.sing-up.success", "Registration successful."},
                {"field.required", "Field is required."},
                {"field.name.failure", "Name should have from 2 to 60 characters."},
                {"field.surname.failure", "Surname should have from 2 to 60 characters."},
                {"field.email.failure", "Provide valid email address."},
                {"field.phone.number.failure", "Invalid phone number format."},
                {"field.password.failure", "Password should have from 8 to 24 characters."},
                {"field.password.match.failure", "Provided passwords should match."},
                {"user.sign-up.email.exists", "Provided email already exists."},
                {"user.log-in.fail", "Invalid email or password."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
