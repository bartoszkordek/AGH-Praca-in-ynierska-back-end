package com.healthy.gym.trainings.configuration;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."},
                {"exception.create.group.training.location.not.found", "Nie znaleziono określonej lokalizacji."},
                {"exception.create.group.training.location.occupied", "Sala jest niedostępna we wskazanym czasie."},
                {"exception.create.group.training.trainer.not.found", "Nie znaleziono wskazanego trenera."},
                {"exception.create.group.training.trainer.occupied", "Trener jest niedostępny we wskazanym czasie."},
                {"exception.create.group.training.trainingType.not.found", "Nie znaleziono określonego typu treningu."},
                {"exception.duplicated.training.type", "Podana nazwa treningu już istnieje."},
                {"exception.duration.format", "Niepoprawny format czasu trwania zajęć."},
                {"exception.internal.error", "Podczas przetwarzania żądania wystąpił błąd."},
                {"exception.invalid.id.format", "Nieprawidłowy format id."},
                {"exception.multipart.body", "Źle wypełniony formularz."},
                {"exception.not.found.training.type", "Nie znaleziono podanego typu treningu."},
                {"exception.not.found.training.type.all", "Brak typów treningów do wyświetlenia."},
                {"exception.unsupported.data.type", "Błąd. Dopuszczalny format pliku to JPEG lub PNG."},
                {"field.required", "Pole jest wymagane."},
                {"field.training.limit.min.value", "The minimum number of people in training is 1."},
                {"request.bind.exception", "Podano nieprawidłowy format danych."},
                {"request.create.training.success", "Pomyślnie dodano nowy trening do grafiku."},
                {"training.type.created", "Nowy typ treningu został stworzony."},
                {"training.type.failure", "Wystąpił błąd podczas tworzenia treningu."},
                {"training.type.removed", "Typ treningu został usunięty."},
                {"training.type.updated", "Typ treningu został zaktualizowany."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"exception.access.denied", "You are not allowed to perform this operation."},
                {"exception.create.group.training.location.not.found", "Specified location is not found."},
                {"exception.create.group.training.location.occupied", "The location is unavailable at the indicated time."},
                {"exception.create.group.training.trainer.not.found", "Specified trainer is not found."},
                {"exception.create.group.training.trainer.occupied", "The trainer is unavailable at the indicated time."},
                {"exception.create.group.training.trainingType.not.found", "Specified training type is not found."},
                {"exception.duplicated.training.type", "Provided training type name already exists."},
                {"exception.duration.format", "Incorrect workout time duration format."},
                {"exception.internal.error", "An error occurred while processing your request."},
                {"exception.invalid.id.format", "Invalid id format."},
                {"exception.multipart.body", "Incorrectly completed form."},
                {"exception.not.found.training.type", "Specified training type not found."},
                {"exception.not.found.training.type.all", "There are no workout types to display."},
                {"exception.unsupported.data.type", "Error. The acceptable file format is JPEG or PNG."},
                {"field.required", "Field is required."},
                {"field.training.limit.min.value", "Minimalny limit osób na treningu to 1."},
                {"request.bind.exception", "An invalid data format has been provided."},
                {"request.create.training.success", "New training has been successfully added to the timetable."},
                {"training.type.created", "New training type has been created."},
                {"training.type.failure", "An error occurred while creating new training type."},
                {"training.type.removed", "Training type has been removed."},
                {"training.type.updated", "Training type has been updated."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
