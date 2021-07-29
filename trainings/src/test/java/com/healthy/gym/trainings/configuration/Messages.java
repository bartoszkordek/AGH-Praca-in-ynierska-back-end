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
                {"exception.date.or.hour.parse", "Błędna data lub godzina."},
                {"exception.duplicated.training.type", "Podana nazwa treningu już istnieje."},
                {"exception.duration.format", "Niepoprawny format czasu trwania zajęć."},
                {"exception.email.sending", "Nie można wysłać maila."},
                {"exception.group.training.enrollment", "Nie można się zapisać na trening grupowy."},
                {"exception.group.training.remove", "Nie można usunąć istniejącego treningu grupowego."},
                {"exception.group.training.update", "Nie udało się edytować istniejącego treningu grupowego."},
                {"exception.internal.error", "Podczas przetwarzania żądania wystąpił błąd."},
                {"exception.invalid.id.format", "Nieprawidłowy format id."},
                {"exception.multipart.body", "Źle wypełniony formularz."},
                {"exception.not.found.review.id", "Błędne id recenzji"},
                {"exception.not.found.training.id", "Blędne id treningu."},
                {"exception.not.found.training.type.all", "Brak typów treningów do wyświetlenia."},
                {"exception.not.found.training.type", "Nie znaleziono podanego typu treningu."},
                {"exception.not.found.user.id", "Błędny numer użytkownika."},
                {"exception.review.stars.out.of.range", "Liczba gwazdek poza zakresem."},
                {"exception.start.date.after.end.date", "Data rozpoczęcia powinna przypadać przed datą zakończenia."},
                {"exception.unsupported.data.type", "Błąd. Dopuszczalny format pliku to JPEG lub PNG."},
                {"field.invalid.date.time.format", "Nieprawidłowy format daty lub czasu."},
                {"field.required", "Pole jest wymagane."},
                {"field.training.limit.min.value", "Minimalny limit osób na treningu to 1."},
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
                {"exception.date.or.hour.parse", "Incorrect date or hour."},
                {"exception.duplicated.training.type", "Provided training type name already exists."},
                {"exception.duration.format", "Incorrect workout time duration format."},
                {"exception.email.sending", "Cannot send email."},
                {"exception.group.training.enrollment", "Cannot enroll to group training."},
                {"exception.group.training.remove", "Cannot remove group training."},
                {"exception.group.training.update", "Cannot update group training."},
                {"exception.internal.error", "An error occurred while processing your request."},
                {"exception.invalid.id.format", "Invalid id format."},
                {"exception.multipart.body", "Incorrectly completed form."},
                {"exception.not.found.review.id", "Incorrect review id."},
                {"exception.not.found.training.id", "Incorrect training id."},
                {"exception.not.found.training.type.all", "There are no workout types to display."},
                {"exception.not.found.training.type", "Specified training type not found."},
                {"exception.not.found.user.id", "Not found user id."},
                {"exception.review.stars.out.of.range", "Stars out of range."},
                {"exception.start.date.after.end.date", "Starting date should be before ending date."},
                {"exception.unsupported.data.type", "Error. The acceptable file format is JPEG or PNG."},
                {"field.invalid.date.time.format", "Invalid date or time format."},
                {"field.required", "Field is required."},
                {"field.training.limit.min.value", "The minimum number of people in training is 1."},
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
