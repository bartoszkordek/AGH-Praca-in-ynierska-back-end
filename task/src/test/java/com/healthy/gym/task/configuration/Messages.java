package com.healthy.gym.task.configuration;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"exception.internal.error", "Podczas przetwarzania żądania wystąpił błąd."},
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."},
                {"task.created", "Nowe zadanie zaostało utworzone."},
                {"request.bind.exception", "Podano nieprawidłowy format danych."},
                {"exception.manager.not.found", "Nie znaleziono managera."},
                {"exception.employee.not.found", "Nie znaleziono pracownika."},
                {"exception.retro.due.date", "Przeszła spodziewana data zakończenia."},
                {"field.required", "Pole jest wymagane."},
                {"field.title.failure", "Tytuł powinnien mieć od 2 do 20 znaków."},
                {"field.description.failure", "Opis powinnien mieć od 2 do 200 znaków."},
                {"field.result.failure", "Rezultat powinnien mieć od 2 do 500 znaków."},
                {"exception.invalid.id.format", "Nieprawidłowy format id. Zastosuj format UUID."},
                {"exception.invalid.date.format", "Nieprawidłowy format daty. Zastosuj format 'YYYY-MM-dd'."},
                {"exception.invalid.date.time.format", "Nieprawidłowy format daty i czasu. Zastosuj format 'YYYY-MM-dd'T'HH:mm'."}

        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"exception.internal.error", "An error occurred while processing your request."},
                {"exception.access.denied", "You are not allowed to perform this operation."},
                {"task.created", "New task has been created."},
                {"request.bind.exception", "An invalid data format has been provided."},
                {"exception.manager.not.found", "Manager not found."},
                {"exception.employee.not.found", "Employee not found."},
                {"exception.retro.due.date", "Retro due date."},
                {"field.required", "Field is required."},
                {"field.title.failure", "The title should be 2 to 20 characters long."},
                {"field.description.failure", "The description should be 2 to 200 characters long."},
                {"field.result.failure", "The result should be 2 to 500 characters long."},
                {"exception.invalid.id.format", "Invalid id format. Use UUID format."},
                {"exception.invalid.date.format", "Invalid date format. Use the 'YYYY-MM-dd' format."},
                {"exception.invalid.date.time.format", "Invalid date and time format. Use the 'YYYY-MM-dd'T'HH:mm' format."}

        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
