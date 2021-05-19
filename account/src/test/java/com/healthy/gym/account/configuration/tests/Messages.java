package com.healthy.gym.account.configuration.tests;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"delete.account.success", "Konto zostało pomyślnie usunięte."},
                {"exception.account.not.found", "Nie ma w bazie takiego użytkownika."},
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."},
                {"field.required", "Pole jest wymagane."},
                {"field.password.failure", "Hasło powinno mieć od 8 do 24 znaków."},
                {"field.password.match.failure", "Podane hasła powinny być identyczne."},
                {"password.change.success", "Hasło zostało pomyślnie zmienione."},
                {"request.failure", "Podczas przetwarzania żądania wystąpił błąd."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"delete.account.success", "Account has been removed successfully."},
                {"exception.account.not.found", "There is no such user in the database."},
                {"exception.access.denied", "You are not allowed to perform this operation."},
                {"field.required", "Field is required."},
                {"field.password.failure", "Password should have from 8 to 24 characters."},
                {"field.password.match.failure", "Provided passwords should match."},
                {"password.change.success", "Password changed successfully."},
                {"request.failure", "An error occurred while processing your request."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
