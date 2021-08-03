package com.healthy.gym.gympass.controller;


import com.healthy.gym.gympass.component.Translator;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class OfferController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private Translator translator;
    private final OfferService offerService;

    @Autowired
    public OfferController(
            Translator translator,
            OfferService offerService
    ){
        this.translator = translator;
        this.offerService = offerService;
    }


    @GetMapping("/offer")
    public ResponseEntity<List<GymPassDTO>> getGymPassOffer() {

        try{
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(offerService.getGymPassOffer());
        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
