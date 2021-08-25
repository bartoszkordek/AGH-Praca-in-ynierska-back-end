package com.healthy.gym.gympass.controller.offer;


import com.healthy.gym.gympass.component.Translator;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.exception.OfferNotFoundException;
import com.healthy.gym.gympass.exception.RequestBindException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import com.healthy.gym.gympass.pojo.response.GymPassOfferResponse;
import com.healthy.gym.gympass.service.OfferService;
import com.healthy.gym.gympass.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
        value = "/offer",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OfferController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private final Translator translator;
    private final OfferService offerService;

    @Autowired
    public OfferController(
            Translator translator,
            OfferService offerService
    ) {
        this.translator = translator;
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<List<GymPassDTO>> getGymPassOffers() {

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(offerService.getGymPassOffers());

        } catch (NoOffersException exception) {
            String reason = translator.toLocale("exception.no.offers");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<GymPassOfferResponse> createGymPassOffer(
            @Valid @RequestBody final GymPassOfferRequest request,
            final BindingResult bindingResult
    ) throws RequestBindException {

        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message = translator.toLocale("offer.created");

            GymPassDTO createdGymPassOffer = offerService.createGymPassOffer(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new GymPassOfferResponse(
                            message,
                            createdGymPassOffer
                    ));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (DuplicatedOffersException exception) {
            String reason = translator.toLocale("exception.duplicated.offers");
            throw new ResponseStatusException(HttpStatus.CONFLICT, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<GymPassOfferResponse> updateGymPassOffer(
            @PathVariable("id") @ValidIDFormat final String id,
            @Valid @RequestBody final GymPassOfferRequest request,
            final BindingResult bindingResult
    ) throws RequestBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message = translator.toLocale("offer.updated");

            GymPassDTO updatedGymPassOffer = offerService.updateGymPassOffer(id, request);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GymPassOfferResponse(
                            message,
                            updatedGymPassOffer
                    ));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (OfferNotFoundException exception) {
            String reason = translator.toLocale("exception.invalid.offer.id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (DuplicatedOffersException exception) {
            String reason = translator.toLocale("exception.duplicated.offers");
            throw new ResponseStatusException(HttpStatus.CONFLICT, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GymPassOfferResponse> deleteGymPassOffer(
            @PathVariable("id") @ValidIDFormat final String id
    ) {
        try {
            String message = translator.toLocale("offer.removed");
            GymPassDTO removedGymPassOffer = offerService.deleteGymPassOffer(id);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GymPassOfferResponse(
                            message,
                            removedGymPassOffer
                    ));
        } catch (OfferNotFoundException exception) {
            String reason = translator.toLocale("exception.invalid.offer.id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
