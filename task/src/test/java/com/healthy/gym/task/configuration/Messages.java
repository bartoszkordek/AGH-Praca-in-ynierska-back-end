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
                {"exception.retro.due.date", "Przeszła spodziewana data zakończenia."}

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
                {"exception.retro.due.date", "Retro due date."}

        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
