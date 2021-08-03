package com.healthy.gym.gympass.controller;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.dto.Description;
import com.healthy.gym.gympass.dto.Price;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OfferController {

    @GetMapping("/offer")
    public ResponseEntity<List<GymPassDocument>> getGymPassOffer() {
        List<GymPassDocument> response = new ArrayList<>();

        response.add(
                new GymPassDocument(
                        null,
                        "ffad7844-79a4-47d4-a646-4d6aa928631c",
                        "Wejście jednorazowe",
                        null,
                        new Price(19.99, "zł", "wejście"),
                        false,
                        new Description(
                                "Gdy potrzebujesz skorzystać jednorazowo z naszej siłowni",
                                List.of(
                                        "dostęp do każdego sprzętu fitness",
                                        "dowolne godziny wejścia",
                                        "nieograniczony czas wejścia",
                                        "dostęp do sauny"
                                )
                        )
                )
        );
        response.add(
                new GymPassDocument(
                        null,
                        "ffad7844-79a4-47d4-a646-4d6aa928631c",
                        "Karnet 4 wejścia",
                        "Najpopularniejszy dla ograniczonej liczby wejść",
                        new Price(69.99, "zł", "4 wejścia"),
                        true,
                        new Description(
                                "Karnet idealny dla osób o nieregularnym trybie życia",
                                List.of(
                                        "dostęp do każdego sprzętu fitness",
                                        "grupowe i indywidualne zajęcia fitness",
                                        "dowolne godziny wejścia",
                                        "nieograniczony czas wejścia",
                                        "nieograniczona ważność karnetu",
                                        "dostęp do sauny"
                                )
                        )
                )
        );
        response.add(
                new GymPassDocument(
                        null,
                        "c62e98a5-fef8-47a3-81ef-c9c89da8da2e",
                        "Karnet 8 wejść",
                        null,
                        new Price(108.99, "zł", "8 wejść"),
                        false,
                        new Description(
                                "Karnet idealny dla osób o nieregularnym trybie życia",
                                List.of(
                                        "dostęp do każdego sprzętu fitness",
                                        "grupowe i indywidualne zajęcia fitness",
                                        "dowolne godziny wejścia",
                                        "nieograniczony czas wejścia",
                                        "nieograniczona ważność karnetu",
                                        "dostęp do sauny"
                                )
                        )
                )
        );
        response.add(
                new GymPassDocument(
                        null,
                        "6f2298fb-8d05-4e86-8dce-4a448e1da14f",
                        "Standardowy",
                        "Najpopularniejszy",
                        new Price(139.99, "zł", "miesiąc"),
                        true,
                        new Description(
                                "Najlepszy wybór dla osób regularnie ćwiczących",
                                List.of(
                                        "dostęp do każdego sprzętu fitness",
                                        "grupowe i indywidualne zajęcia fitness",
                                        "dowolne godziny wejścia",
                                        "nieograniczony czas wejścia",
                                        "nieograniczona liczba wejść",
                                        "ważność 30 dni",
                                        "dostęp do sauny"
                                )
                        )
                )
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
