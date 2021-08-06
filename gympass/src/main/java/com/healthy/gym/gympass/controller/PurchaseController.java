package com.healthy.gym.gympass.controller;

import com.healthy.gym.gympass.component.Translator;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;
import com.healthy.gym.gympass.pojo.response.PurchasedGymPassResponse;
import com.healthy.gym.gympass.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/offer/purchase",
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
    ){

        return null;
    }


}
