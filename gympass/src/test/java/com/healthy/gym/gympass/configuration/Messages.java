package com.healthy.gym.gympass.configuration;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"exception.internal.error", "Podczas przetwarzania żądania wystąpił błąd."},
                {"exception.no.offers", "Brak ofert."},
                {"offer.created", "Nowa oferta zostala utworzona."},
                {"offer.updated", "Oferta zostala zaktualizowana."},
                {"field.required", "Pole jest wymagane."},
                {"field.name.failure", "Nazwa powinna mieć od 2 do 20 znaków."},
                {"field.subheader.failure", "Opis powinnien mieć od 2 do 60 znaków."},
                {"field.synopsis.failure", "Opis powinnien mieć od 2 do 60 znaków."},
                {"field.features.failure", "Oferta może maksymalnie zawierać 20 szczegółów."},
                {"request.bind.exception", "Podano nieprawidłowy format danych."},
                {"exception.duplicated.offers", "Zduplikowana nazwa oferty."},
                {"exception.invalid.offer.id", "Błędne ID oferty."}

        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"exception.internal.error", "An error occurred while processing your request."},
                {"exception.no.offers", "No offers."},
                {"offer.created", "New offer has been created."},
                {"offer.updated", "Offer has been updated."},
                {"field.required", "Field is required"},
                {"field.name.failure", "The name should be 2 to 20 characters long."},
                {"field.subheader.failure", "The subheader should be 2 to 60 characters long."},
                {"field.synopsis.failure", "The synopsis should be 2 to 60 characters long."},
                {"field.features.failure", "Offer should contain max 20 features."},
                {"request.bind.exception", "An invalid data format has been provided."},
                {"exception.duplicated.offers", "Duplicated offer name."},
                {"exception.invalid.offer.id", "Invalid offer ID."}

        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
