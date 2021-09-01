package com.healthy.gym.gympass.controller.purchase;

import com.healthy.gym.gympass.component.Translator;
import com.healthy.gym.gympass.dto.PurchasedUserGymPassDTO;
import com.healthy.gym.gympass.exception.NoGymPassesException;
import com.healthy.gym.gympass.exception.StartDateAfterEndDateException;
import com.healthy.gym.gympass.exception.UserNotFoundException;
import com.healthy.gym.gympass.service.PurchaseService;
import com.healthy.gym.gympass.validation.ValidDateFormat;
import com.healthy.gym.gympass.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(
        value = "/purchase",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserPurchaseController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private static final String USER_NOT_FOUND_EXCEPTION = "exception.user.not.found";
    private static final String START_DATE_AFTER_END_DATE_EXCEPTION = "exception.start.after.end";
    private static final String NO_GYMPASSES_TO_DISPLAY_EXCEPTION = "exception.no.gympasses";
    private final Translator translator;
    private final PurchaseService purchaseService;

    @Autowired
    public UserPurchaseController(
            Translator translator,
            PurchaseService purchaseService
    ) {
        this.translator = translator;
        this.purchaseService = purchaseService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE') or principal==#userId")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchasedUserGymPassDTO>> getUserGymPasses(
            @PathVariable("userId") @ValidIDFormat final String userId,
            @ValidDateFormat @RequestParam(value = "startDate", required = false) final String startDate,
            @ValidDateFormat @RequestParam(value = "endDate", required = false) final String endDate
    ) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(purchaseService.getUserGymPasses(userId, startDate, endDate));

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale(USER_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale(START_DATE_AFTER_END_DATE_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NoGymPassesException exception) {
            String reason = translator.toLocale(NO_GYMPASSES_TO_DISPLAY_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE') or principal==#userId")
    @GetMapping("/user/{userId}/latest")
    public PurchasedUserGymPassDTO getUserLatestGymPass(
            @PathVariable("userId") @ValidIDFormat final String userId
    ) {
        try {
            return purchaseService.getUserLatestGympass(userId);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale(USER_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NoGymPassesException exception) {
            String reason = translator.toLocale(NO_GYMPASSES_TO_DISPLAY_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

}
