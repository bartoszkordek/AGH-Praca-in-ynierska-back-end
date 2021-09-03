package com.healthy.gym.gympass.controller.purchase;

import com.healthy.gym.gympass.component.Translator;
import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.pojo.response.ValidationGymPassResponse;
import com.healthy.gym.gympass.service.PurchaseService;
import com.healthy.gym.gympass.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(
        value = "/purchase",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GeneralPurchaseController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private static final String GYMPASS_NOT_FOUND_EXCEPTION = "exception.gympass.not.found";
    private final Translator translator;
    private final PurchaseService purchaseService;

    @Autowired
    public GeneralPurchaseController(
            Translator translator,
            PurchaseService purchaseService
    ) {
        this.translator = translator;
        this.purchaseService = purchaseService;
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE') or hasRole('USER')")
    @GetMapping("/{id}/status")
    public ResponseEntity<ValidationGymPassResponse> checkGymPassValidityStatus(
            @PathVariable("id") @ValidIDFormat final String id
    ) {
        try {
            PurchasedGymPassStatusValidationResultDTO result = purchaseService.checkGymPassValidityStatus(id);
            String message = validationStatusMessage(result);

            if (result.isValid()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ValidationGymPassResponse(
                                message,
                                result
                        ));
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ValidationGymPassResponse(
                            message,
                            result
                    ));


        } catch (GymPassNotFoundException exception) {
            String reason = translator.toLocale(GYMPASS_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }


    private String validationStatusMessage(PurchasedGymPassStatusValidationResultDTO result) {
        String validGymPassMessage = translator.toLocale("gympass.valid");
        String notValidRetroEndDateGymPassMessage = translator.toLocale("gympass.not.valid.retro.end.date");
        String notValidNoEntriesGymPassMessage = translator.toLocale("gympass.not.valid.no.entries");
        String notValidSuspendedGymPassMessage = translator.toLocale("gympass.not.valid.suspended");
        var now = LocalDate.now();
        String endDate = result.getEndDate();
        int entries = result.getEntries();
        String suspensionDate = result.getSuspensionDate();
        if (now.isAfter(LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)))
            return notValidRetroEndDateGymPassMessage;
        if (entries < 1)
            return notValidNoEntriesGymPassMessage;
        if (suspensionDate != null && now.isBefore(LocalDate.parse(suspensionDate, DateTimeFormatter.ISO_LOCAL_DATE)))
            return notValidSuspendedGymPassMessage;

        return validGymPassMessage;
    }

}
