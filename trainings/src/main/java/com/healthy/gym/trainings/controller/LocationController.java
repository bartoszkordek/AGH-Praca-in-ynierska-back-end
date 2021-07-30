package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.ResponseBindException;
import com.healthy.gym.trainings.exception.duplicated.DuplicatedLocationNameException;
import com.healthy.gym.trainings.model.request.CreateLocationRequest;
import com.healthy.gym.trainings.model.response.LocationResponse;
import com.healthy.gym.trainings.service.LocationService;
import com.healthy.gym.trainings.shared.LocationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@RestController
@RequestMapping(
        value = "/location",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class LocationController {

    private final LocationService locationService;
    private final Translator translator;

    public LocationController(LocationService locationService, Translator translator) {
        this.locationService = locationService;
        this.translator = translator;
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody CreateLocationRequest request,
            BindingResult bindingResult
    ) throws ResponseBindException {

        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            LocationDTO createdLocation = locationService.createLocation(request);
            String message = translator.toLocale("location.created");
            LocationResponse response = new LocationResponse(message, createdLocation);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (DuplicatedLocationNameException exception) {
            String reason = translator.toLocale("exception.duplicated.location.name");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }


}
