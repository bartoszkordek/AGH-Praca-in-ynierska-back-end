package com.healthy.gym.gympass.configuration;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"exception.internal.error", "Podczas przetwarzania żądania wystąpił błąd."},
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."},
                {"exception.no.offers", "Brak ofert."},
                {"offer.created", "Nowa oferta została utworzona."},
                {"offer.updated", "Oferta zostala zaktualizowana."},
                {"offer.removed", "Oferta zostala usunięta."},
                {"field.required", "Pole jest wymagane."},
                {"field.name.failure", "Nazwa powinna mieć od 2 do 20 znaków."},
                {"field.subheader.failure", "Opis powinnien mieć od 2 do 60 znaków."},
                {"field.period.failure", "Okres powinien mieć od 2 do 20 znaków."},
                {"field.synopsis.failure", "Opis powinnien mieć od 2 do 60 znaków."},
                {"field.features.failure", "Oferta może maksymalnie zawierać 20 szczególów."},
                {"request.bind.exception", "Podano nieprawidłowy format danych."},
                {"exception.duplicated.offers", "Zduplikowana nazwa oferty."},
                {"exception.invalid.offer.id", "Blędne ID oferty."},
                {"exception.gympass.not.found", "Karnet nie istnieje."},
                {"gympass.purchased", "Karnet został zakupiony."},
                {"gympass.suspended", "Karnet został zawieszony."},
                {"gympass.valid", "Karnet jest ważny."},
                {"gympass.not.valid", "Karnet nie jest ważny."},
                {"exception.offer.not.found", "Nie znaleziono oferty."},
                {"exception.user.not.found", "Nie znaleziono użytkownika."},
                {"exception.gympass.type","Nie sprecyzowany typ karnetu."},
                {"exception.retro.purchased", "Nie można zakupić karnetu z datą wsteczną."},
                {"exception.retro.date.suspension", "Nie można zawiesić karnetu z datą wsteczną."},
                {"exception.start.after.end", "Data rozpoczęcia po dacie zakończenia."},
                {"exception.suspension.after.end", "Data zawieszenia nie może być późniejsza niż data zakończenia."},
                {"exception.gympass.already.suspended", "Karnet został już zawieszony."},
                {"exception.invalid.id.format", "Nieprawidłowy format id. Zastosuj format UUID."},
                {"exception.invalid.date.format", "Nieprawidłowy format daty. Zastosuj format 'YYYY-MM-dd'."},
                {"exception.invalid.date.time.format",
                        "Nieprawidłowy format daty i czasu. Zastosuj format 'YYYY-MM-dd'T'HH:mm'."}

        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"exception.internal.error", "An error occurred while processing your request."},
                {"exception.access.denied", "You are not allowed to perform this operation."},
                {"exception.no.offers", "No offers."},
                {"offer.created", "New offer has been created."},
                {"offer.updated", "Offer has been updated."},
                {"offer.removed", "Offer has been removed."},
                {"field.required", "Field is required."},
                {"field.name.failure", "The name should be 2 to 20 characters long."},
                {"field.subheader.failure", "The subheader should be 2 to 60 characters long."},
                {"field.period.failure", "The period should be 2 to 20 characters long."},
                {"field.synopsis.failure", "The synopsis should be 2 to 60 characters long."},
                {"field.features.failure", "Offer should contain max 20 features."},
                {"request.bind.exception", "An invalid data format has been provided."},
                {"exception.duplicated.offers", "Duplicated offer name."},
                {"exception.invalid.offer.id", "Invalid offer ID."},
                {"exception.gympass.not.found", "Gympass not found."},
                {"gympass.purchased", "Gympass has been purchased."},
                {"gympass.suspended", "Gympass has been suspended."},
                {"gympass.valid", "Gympass is valid."},
                {"gympass.not.valid", "Gympass is not valid."},
                {"exception.offer.not.found", "Offer not found."},
                {"exception.user.not.found", "User not found."},
                {"exception.gympass.type", "Not specified gympass type."},
                {"exception.retro.purchased", "Cannot purchase gympass with retro date."},
                {"exception.retro.date.suspension", "Cannot suspend gympass with retro date."},
                {"exception.suspension.after.end", "Suspension date after end date."},
                {"exception.start.after.end", "Start date after end date."},
                {"exception.gympass.already.suspended" ,"Gympass has already been already suspended."},
                {"exception.invalid.id.format", "Invalid id format. Use UUID format."},
                {"exception.invalid.date.format","Invalid date format. Use the 'YYYY-MM-dd' format."},
                {"exception.invalid.date.time.format",
                        "Invalid date and time format. Use the 'YYYY-MM-dd'T'HH:mm' format."}

        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
