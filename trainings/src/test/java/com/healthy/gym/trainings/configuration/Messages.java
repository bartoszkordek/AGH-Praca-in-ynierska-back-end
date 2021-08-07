package com.healthy.gym.trainings.configuration;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."},
                {"exception.already.accepted.individual.training",
                        "Zapytanie o trening indywidualny zostało już zaakceptowane."},
                {"exception.location.not.found", "Nie znaleziono określonej lokalizacji."},
                {"exception.constraint.violation", "Nieprawidłowy format danych."},
                {"exception.create.group.training.location.occupied", "Sala jest niedostępna we wskazanym czasie."},
                {"exception.create.group.training.trainer.not.found", "Nie znaleziono wskazanego trenera."},
                {"exception.create.group.training.trainer.occupied", "Trener jest niedostępny we wskazanym czasie."},
                {"exception.create.group.training.trainingType.not.found", "Nie znaleziono określonego typu treningu."},
                {"exception.date.or.hour.parse", "Błędna data lub godzina."},
                {"exception.duplicated.location.name", "Podana nazwa lokalizacji już istnieje."},
                {"exception.duplicated.training.type", "Podana nazwa treningu już istnieje."},
                {"exception.duration.format", "Niepoprawny format czasu trwania zajęć."},
                {"exception.email.sending", "Nie można wysłać maila."},
                {"exception.group.training.enrollment", "Nie można się zapisać na trening grupowy."},
                {"exception.group.training.enrollment.remove",
                        "Nie odnaleziono użytkownika na liście uczestników zajęć grupowych."},
                {"exception.group.training.remove", "Nie można usunąć istniejącego treningu grupowego."},
                {"exception.group.training.update", "Nie udało się edytować istniejącego treningu grupowego."},
                {"exception.group.training.not.found", "Nie znaleziono wskazanego treningu grupowego."},
                {"exception.internal.error", "Podczas przetwarzania żądania wystąpił błąd."},
                {"exception.invalid.id.format", "Nieprawidłowy format id. Zastosuj format UUID."},
                {"exception.invalid.date.format", "Nieprawidłowy format daty. Zastosuj format 'YYYY-MM-dd'."},
                {"exception.invalid.date.time.format",
                        "Nieprawidłowy format daty i czasu. Zastosuj format 'YYYY-MM-dd'T'HH:mm'."},
                {"exception.invalid.page.size.format", "Nieprawidłowy format. Zastosuj jedną z liczb: 5, 10, 20, 50 or 100."},
                {"exception.invalid.page.number.format",
                        "Nieprawidłowy format. Zastosuj liczbę całkowitą z przedziału od 1 do 2147483647."},
                {"exception.multipart.body", "Źle wypełniony formularz."},
                {"exception.no.individual.training.found", "Brak indywidualnych treningów do wyświetlenia."},
                {"exception.not.existing.individual.training", "Nie znaleziono określonego treningu indywidualnego."},
                {"exception.not.found.review.id", "Błędne id recenzji"},
                {"exception.not.found.training.id", "Blędne id treningu."},
                {"exception.not.found.training.type.all", "Brak typów treningów do wyświetlenia."},
                {"exception.not.found.training.type", "Nie znaleziono podanego typu treningu."},
                {"exception.not.found.user.id", "Nie znaleziono wskazanego użytkownika."},
                {"exception.review.stars.out.of.range", "Liczba gwazdek poza zakresem."},
                {"exception.start.date.after.end.date", "Data rozpoczęcia powinna przypadać przed datą zakończenia."},
                {"exception.past.date", "Data rozpoczęcia nie może być datą przeszłą."},
                {"exception.past.date.enrollment", "Nie można się zapisać na zajęcia, które już się odbyły."},
                {"exception.past.date.enrollment.remove",
                        "Nie można zrezygnować z uczestnictwa z zajęć, które już się odbyły."},
                {"exception.unsupported.data.type", "Błąd. Dopuszczalny format pliku to JPEG lub PNG."},
                {"exception.user.already.enrolled.to.training", "Użytkownik jest już zapisany na dane zajęcia."},
                {"field.invalid.date.time.format", "Nieprawidłowy format daty lub czasu."},
                {"field.name.failure", "Nazwa powinna mieć od 2 do 20 znaków."},
                {"field.required", "Pole jest wymagane."},
                {"field.training.limit.min.value", "Minimalny limit osób na treningu to 1."},
                {"location.created", "Nowa lokalizacja została stworzona."},
                {"location.updated", "Lokalizacja została uaktualniona."},
                {"location.removed", "Lokalizacja została usunięta."},
                {"request.bind.exception", "Podano nieprawidłowy format danych."},
                {"request.create.training.success", "Pomyślnie dodano nowy trening do grafiku."},
                {"request.update.training.success", "Pomyślnie zaktualizowany trening w grafiku."},
                {"request.delete.training.success", "Pomyślnie usunięto trening z grafiku."},
                {"training.type.created", "Nowy typ treningu został stworzony."},
                {"training.type.failure", "Wystąpił błąd podczas tworzenia treningu."},
                {"training.type.removed", "Typ treningu został usunięty."},
                {"training.type.updated", "Typ treningu został zaktualizowany."},
                {"enrollment.success.basic.list", "Pomyślnie zapisano się na zajęcia."},
                {"enrollment.success.reserve.list", "Brak miejsc na liście podstawowej. Zapisano na listę rezerwową."},
                {"enrollment.success.individual", "Zapytanie o trening indywidualny zostało stworzone."},
                {"enrollment.remove", "Zrezygnowano z uczestnictwa w zajęciach."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"exception.access.denied", "You are not allowed to perform this operation."},
                {"exception.already.accepted.individual.training",
                        "Individual training request has been already accepted."},
                {"exception.location.not.found", "Specified location is not found."},
                {"exception.constraint.violation", "Invalid data format."},
                {"exception.create.group.training.location.occupied", "The location is unavailable at the indicated time."},
                {"exception.create.group.training.trainer.not.found", "Specified trainer is not found."},
                {"exception.create.group.training.trainer.occupied", "The trainer is unavailable at the indicated time."},
                {"exception.create.group.training.trainingType.not.found", "Specified training type is not found."},
                {"exception.date.or.hour.parse", "Incorrect date or hour."},
                {"exception.duplicated.location.name", "Provided location name already exists."},
                {"exception.duplicated.training.type", "Provided training type name already exists."},
                {"exception.duration.format", "Incorrect workout time duration format."},
                {"exception.email.sending", "Cannot send email."},
                {"exception.group.training.enrollment", "Cannot enroll to group training."},
                {"exception.group.training.enrollment.remove",
                        "User not found on the list of participants of group training."},
                {"exception.group.training.remove", "Cannot remove group training."},
                {"exception.group.training.update", "Cannot update group training."},
                {"exception.group.training.not.found", "Specified group training is not found."},
                {"exception.internal.error", "An error occurred while processing your request."},
                {"exception.invalid.id.format", "Invalid id format. Use UUID format."},
                {"exception.invalid.date.format", "Invalid date format. Use the 'YYYY-MM-dd' format."},
                {"exception.invalid.date.time.format",
                        "Invalid date and time format. Use the 'YYYY-MM-dd'T'HH:mm' format."},
                {"exception.invalid.page.size.format", "Invalid format. Use one of the numbers: 5, 10, 20, 50 or 100."},
                {"exception.invalid.page.number.format", "Invalid format. Use an integer between 1 and 2,147,483,647."},
                {"exception.multipart.body", "Incorrectly completed form."},
                {"exception.no.individual.training.found", "There are no individual workouts to display."},
                {"exception.not.existing.individual.training", "Specified individual training is not found."},
                {"exception.not.found.review.id", "Incorrect review id."},
                {"exception.not.found.training.id", "Incorrect training id."},
                {"exception.not.found.training.type.all", "There are no workout types to display."},
                {"exception.not.found.training.type", "Specified training type not found."},
                {"exception.not.found.user.id", "Specified user is not found."},
                {"exception.review.stars.out.of.range", "Stars out of range."},
                {"exception.start.date.after.end.date", "Starting date should be before ending date."},
                {"exception.past.date", "The start date cannot be in the past."},
                {"exception.past.date.enrollment", "You cannot enroll to past event."},
                {"exception.past.date.enrollment.remove",
                        "Participation in activities that have already taken place cannot be canceled."},
                {"exception.unsupported.data.type", "Error. The acceptable file format is JPEG or PNG."},
                {"exception.user.already.enrolled.to.training", "The user is already enrolled to the group training."},
                {"field.invalid.date.time.format", "Invalid date or time format."},
                {"field.name.failure", "The name should be 2 to 20 characters long."},
                {"field.required", "Field is required."},
                {"field.training.limit.min.value", "The minimum number of people in training is 1."},
                {"location.created", "New location has been created."},
                {"location.updated", "The location has been updated."},
                {"location.removed", "The location has been removed."},
                {"request.bind.exception", "An invalid data format has been provided."},
                {"request.create.training.success", "New training has been successfully added to the timetable."},
                {"request.update.training.success", "The training has been successfully updated in the timetable."},
                {"request.delete.training.success", "The training has been successfully removed from the timetable."},
                {"training.type.created", "New training type has been created."},
                {"training.type.failure", "An error occurred while creating new training type."},
                {"training.type.removed", "Training type has been removed."},
                {"training.type.updated", "Training type has been updated."},
                {"enrollment.success.basic.list", "Successfully enrolled to group training."},
                {"enrollment.success.reserve.list", "Basic list is full. You have been enrolled to reserve list."},
                {"enrollment.success.individual", "Individual training request has been created."},
                {"enrollment.remove", "Participation in the classes was resigned."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
