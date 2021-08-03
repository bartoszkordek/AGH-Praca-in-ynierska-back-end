package com.healthy.gym.gympass.controller;


import com.healthy.gym.gympass.component.Translator;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import com.healthy.gym.gympass.pojo.response.GymPassOfferResponse;
import com.healthy.gym.gympass.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
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
        } catch (NoOffersException exception){
            String reason = translator.toLocale("exception.no.offers");
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, reason, exception);
        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/offer")
    public ResponseEntity<GymPassOfferResponse> createGymPassOffer(
            @Valid @RequestBody GymPassOfferRequest request,
            BindingResult bindingResult
    ){

        try{

            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message = translator.toLocale("offer.created");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body( new GymPassOfferResponse(
                            message,
                            offerService.createGymPassOffer()
                    ));

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
