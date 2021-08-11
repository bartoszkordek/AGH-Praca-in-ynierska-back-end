package com.healthy.gym.gympass.controller;

import com.healthy.gym.gympass.component.Translator;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;
import com.healthy.gym.gympass.dto.PurchasedUserGymPassDTO;
import com.healthy.gym.gympass.exception.*;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;
import com.healthy.gym.gympass.pojo.response.PurchasedGymPassResponse;
import com.healthy.gym.gympass.pojo.response.ValidationGymPassResponse;
import com.healthy.gym.gympass.service.PurchaseService;
import com.healthy.gym.gympass.validation.ValidDateFormat;
import com.healthy.gym.gympass.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(
        value = "/purchase",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PurchaseController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private final Translator translator;
    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(
            Translator translator,
            PurchaseService purchaseService
    ){
        this.translator = translator;
        this.purchaseService = purchaseService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<PurchasedGymPassResponse> purchaseGymPass(
            @Valid @RequestBody final PurchasedGymPassRequest request,
            final BindingResult bindingResult
    ) throws RequestBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message = translator.toLocale("gympass.purchased");

            PurchasedGymPassDTO purchasedGymPass = purchaseService.purchaseGymPass(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new PurchasedGymPassResponse(
                            message,
                            purchasedGymPass
                    ));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (OfferNotFoundException exception) {
                String reason = translator.toLocale("exception.offer.not.found");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale("exception.user.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (RetroPurchasedException exception) {
            String reason = translator.toLocale("exception.retro.purchased");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.after.end");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NotSpecifiedGymPassTypeException exception) {
            String reason = translator.toLocale("exception.gympass.type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @PutMapping("/{id}/suspend/{suspensionDate}")
    public ResponseEntity<PurchasedGymPassResponse> suspendGymPass(
            @PathVariable("id") @ValidIDFormat final String id,
            @PathVariable @ValidDateFormat final String suspensionDate
    ) {
        try{
            String message = translator.toLocale("gympass.suspended");

            PurchasedGymPassDTO suspendedPurchasedGymPass  = purchaseService.suspendGymPass(id, suspensionDate);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new PurchasedGymPassResponse(
                            message,
                            suspendedPurchasedGymPass
                    ));

        } catch (GymPassNotFoundException exception) {
            String reason = translator.toLocale("exception.gympass.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (AlreadySuspendedGymPassException exception) {
            String reason = translator.toLocale("exception.gympass.already.suspended");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (RetroSuspensionDateException exception) {
            String reason = translator.toLocale("exception.retro.date.suspension");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (SuspensionDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.suspension.after.end");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/status/{id}")
    public ResponseEntity<ValidationGymPassResponse> checkGymPassValidityStatus(
            @PathVariable("id") @ValidIDFormat final String id
    ){
        try{
            PurchasedGymPassStatusValidationResultDTO result  = purchaseService.checkGymPassValidityStatus(id);
            String message = validationStatusMessage(result);

            if(result.isValid()){
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
            String reason = translator.toLocale("exception.gympass.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/page/{page}")
    public ResponseEntity<List<PurchasedGymPassDTO>> getGymPasses(
            @ValidDateFormat @RequestParam(value = "purchaseStartDate",required = false) final String purchaseStartDate,
            @ValidDateFormat @RequestParam(value = "purchaseEndDate", required = false) final String purchaseEndDate,
            @RequestParam(defaultValue = "10", required = false) final int size,
            @PathVariable final int page
    ){
        try{
            Pageable paging = PageRequest.of(page, size);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(purchaseService.getGymPasses(
                            purchaseStartDate,
                            purchaseEndDate,
                            paging)
                    );

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.after.end");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<PurchasedUserGymPassDTO>> getUserGymPasses(
            @PathVariable("id") @ValidIDFormat final String id,
            @ValidDateFormat @RequestParam(value = "startDate",required = false) final String startDate,
            @ValidDateFormat @RequestParam(value = "endDate", required = false) final String endDate
    ){
        try{
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(purchaseService.getUserGymPasses(id, startDate, endDate));

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale("exception.user.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.after.end");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NoGymPassesException exception){
            String reason = translator.toLocale("exception.no.gympasses");
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<PurchasedGymPassResponse> deleteGymPasses(
            @PathVariable("id") @ValidIDFormat final String id
    ){
        try{
            String message = translator.toLocale("gympass.removed");
            PurchasedGymPassDTO removedGymPass = purchaseService.deleteGymPass(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new PurchasedGymPassResponse(
                            message,
                            removedGymPass
                    ));

        } catch (GymPassNotFoundException exception) {
            String reason = translator.toLocale("exception.gympass.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }

    }

    private String validationStatusMessage(PurchasedGymPassStatusValidationResultDTO result){
        String validGymPassMessage = translator.toLocale("gympass.valid");
        String notValidRetroEndDateGymPassMessage = translator.toLocale("gympass.not.valid.retro.end.date");
        String notValidNoEntriesGymPassMessage = translator.toLocale("gympass.not.valid.no.entries");
        String notValidSuspendedGymPassMessage = translator.toLocale("gympass.not.valid.suspended");
        LocalDate now = LocalDate.now();
        String endDate = result.getEndDate();
        int entries = result.getEntries();
        String suspensionDate = result.getSuspensionDate();
        if(now.isAfter(LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)))
            return notValidRetroEndDateGymPassMessage;
        if(entries < 1)
            return notValidNoEntriesGymPassMessage;
        if(suspensionDate != null && now.isBefore(LocalDate.parse(suspensionDate, DateTimeFormatter.ISO_LOCAL_DATE)))
            return notValidSuspendedGymPassMessage;

        return validGymPassMessage;
    }

}
