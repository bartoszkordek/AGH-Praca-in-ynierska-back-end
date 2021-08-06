package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.LocationDTO;
import com.healthy.gym.trainings.exception.ResponseBindException;
import com.healthy.gym.trainings.exception.duplicated.DuplicatedLocationNameException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.model.request.LocationRequest;
import com.healthy.gym.trainings.model.response.LocationResponse;
import com.healthy.gym.trainings.service.LocationService;
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

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@RestController
@RequestMapping(
        value = "/location",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class LocationController {

    private static final String EXCEPTION_INTERNAL_ERROR = "exception.internal.error";

    private final LocationService locationService;
    private final Translator translator;

    public LocationController(LocationService locationService, Translator translator) {
        this.locationService = locationService;
        this.translator = translator;
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody LocationRequest request,
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
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping
    public List<LocationDTO> getLocations() {
        return locationService.getAllLocations();
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable String locationId,
            @Valid @RequestBody LocationRequest locationRequest,
            BindingResult bindingResult
    ) throws ResponseBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            LocationDTO updatedLocation = locationService.updateLocationById(locationId, locationRequest);
            String message = translator.toLocale("location.updated");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new LocationResponse(message, updatedLocation));

        } catch (LocationNotFoundException exception) {
            String reason = translator.toLocale("exception.location.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (DuplicatedLocationNameException exception) {
            String reason = translator.toLocale("exception.duplicated.location.name");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<LocationResponse> deleteLocation(@PathVariable String locationId) {
        try {
            LocationDTO removedLocation = locationService.removeLocationById(locationId);
            String message = translator.toLocale("location.removed");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new LocationResponse(message, removedLocation));

        } catch (LocationNotFoundException exception) {
            String reason = translator.toLocale("exception.location.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

}
