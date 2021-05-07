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
                {"user.log-in.fail", "Nieprawidłowy email lub hasło."},
                {"user.logout.fail", "Wystąpił błąd podczas wylogowywania."},
                {"user.logout.success", "Zostałeś pomyślnie wylogowany."},
                {"user.logout.token.expired", "Twoja sesja wygasła i nastąpiło automaczne wylogowanie użytkownika."},
                {"user.logout.invalid.token", "Niepoprawny token uwierzytelniający."},
                {"mail.registration.confirmation.subject", "Potwierdzenie rerjestracji nowego użytkownika."},
                {"mail.registration.confirmation.message", "Aby potwierdzić rejestrację w serwisie kliknij w poniższy link:"},
                {"registration.confirmation.token.expired", "Wygasł token potwierdzający rejestrację. Zarejestruj się jeszcze raz."},
                {"registration.confirmation.token.invalid", "Wystąpił błąd. Nieprawidłowy token potwierdzający rejestrację."},
                {"registration.confirmation.token.valid", "Poprawnie potwierdzono rejestrację. Możesz się zalogować."},
                {"mail.registration.confirmation.expiration", "Link wygasa:"}
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
                {"user.log-in.fail", "Invalid email or password."},
                {"user.logout.fail", "Error occurred while logout."},
                {"user.logout.success", "You were successfully logged out."},
                {"user.logout.token.expired", "Your session has expired and the user has been logged out automatically."},
                {"user.logout.invalid.token", "Invalid token."},
                {"mail.registration.confirmation.subject", "Confirmation of registration a new user."},
                {"mail.registration.confirmation.message", "To confirm registration in service click the following link:"},
                {"registration.confirmation.token.expired", "The token confirming the registration has expired. Register again."},
                {"registration.confirmation.token.invalid", "Error occurred. Invalid token confirming registration."},
                {"registration.confirmation.token.valid", "Registration successfully confirmed. You can log in."},
                {"mail.registration.confirmation.expiration", "Link expires:"}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
