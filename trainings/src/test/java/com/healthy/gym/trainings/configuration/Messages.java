package com.healthy.gym.trainings.configuration;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."},
                {"exception.duplicated.training.type", "Podana nazwa treningu już istnieje."},
                {"exception.multipart.body", "Źle wypełniony formularz."},
                {"exception.unsupported.data.type", "Błąd. Dopuszczalny format pliku to JPEG lub PNG."},
                {"exception.internal.error", "Podczas przetwarzania żądania wystąpił błąd."},
                {"field.required", "Pole jest wymagane."},
                {"training.type.created", "Nowy typ treningu został stworzony."},
                {"training.type.failure", "Wystąpił błąd podczas tworzenia treningu."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"exception.access.denied", "You are not allowed to perform this operation."},
                {"exception.duplicated.training.type", "Provided training type name already exists."},
                {"exception.multipart.body", "Incorrectly completed form."},
                {"exception.unsupported.data.type", "Error. The acceptable file format is JPEG or PNG."},
                {"exception.internal.error", "An error occurred while processing your request."},
                {"field.required", "Field is required."},
                {"training.type.created", "New training type has been created."},
                {"training.type.failure", "An error occurred while creating new training type."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
