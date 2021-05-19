package com.healthy.gym.account.configuration.tests;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"password.change.success", "Hasło zostało pomyślnie zmienione."},
                {"field.required", "Pole jest wymagane."},
                {"field.password.failure", "Hasło powinno mieć od 8 do 24 znaków."},
                {"field.password.match.failure", "Podane hasła powinny być identyczne."},
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"password.change.success", "Password changed successfully."},
                {"field.required", "Field is required."},
                {"field.password.failure", "Password should have from 8 to 24 characters."},
                {"field.password.match.failure", "Provided passwords should match."},
                {"exception.access.denied", "You are not allowed to perform this operation."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
