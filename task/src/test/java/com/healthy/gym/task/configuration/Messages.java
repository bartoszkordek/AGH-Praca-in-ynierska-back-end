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
                {"task.updated", "Zadanie zostało zaktualizowane."},
                {"task.removed", "Zadanie zostało usunięte."},
                {"task.approved.employee", "Zadanie zostało zaakceptowane przez pracownika."},
                {"task.declined.employee", "Zadanie zostało odrzucone przez pracownika."},
                {"task.status.not.updated.employee", "Status zadania nie został zaktualizowany."},
                {"report.sent", "Raport został wysłany."},
                {"report.approved.manager", "Raport został zaakceptowany przez managera."},
                {"report.declined.manager", "Raport został odrzucony przez managera."},
                {"request.bind.exception", "Podano nieprawidłowy format danych."},
                {"exception.task.not.found", "Nie znaleziono zadania."},
                {"exception.manager.not.found", "Nie znaleziono managera."},
                {"exception.employee.not.found", "Nie znaleziono pracownika."},
                {"exception.declined.employee", "Zadanie jest odrzucone. Proszę zaakceptować przed wysłaniem raportu."},
                {"exception.retro.due.date", "Przeszła spodziewana data zakończenia."},
                {"exception.invalid.status", "Nieprawidłowy status."},
                {"field.required", "Pole jest wymagane."},
                {"field.title.failure", "Tytuł powinnien mieć od 2 do 20 znaków."},
                {"field.description.failure", "Opis powinnien mieć od 2 do 200 znaków."},
                {"field.result.failure", "Rezultat powinnien mieć od 2 do 500 znaków."},
                {"field.employee.comment", "Komentarz powinnien mieć od 2 do 200 znaków."},
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
                {"task.updated", "Task has been updated."},
                {"task.removed", "Task has been removed."},
                {"task.approved.employee", "Task has been approved by employee."},
                {"task.declined.employee", "Task has been declined by employee."},
                {"task.status.not.updated.employee", "Task status has not been updated."},
                {"report.sent", "Report has been sent."},
                {"report.approved.manager", "Report has been approved by manager."},
                {"report.declined.manager", "Report has been declined by manager."},
                {"request.bind.exception", "An invalid data format has been provided."},
                {"exception.task.not.found", "Task not found."},
                {"exception.manager.not.found", "Manager not found."},
                {"exception.employee.not.found", "Employee not found."},
                {"exception.declined.employee", "Task is declined. Please approve it before sending the report."},
                {"exception.retro.due.date", "Retro due date."},
                {"exception.invalid.status", "Invalid status."},
                {"field.required", "Field is required."},
                {"field.title.failure", "The title should be 2 to 20 characters long."},
                {"field.description.failure", "The description should be 2 to 200 characters long."},
                {"field.result.failure", "The result should be 2 to 500 characters long."},
                {"field.employee.comment", "The comment should be 2 to 200 characters long."},
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
