package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.pojo.response.DeleteAccountResponse;
import com.healthy.gym.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AccountController {

    private final AccountService accountService;
    private final Translator translator;

    @Autowired
    public AccountController(
            AccountService accountService,
            Translator translator
    ) {
        this.accountService = accountService;
        this.translator = translator;
    }

    @GetMapping("/status")
    public String getStatus() {
        return "ok";
    }

    @PostMapping(
            value = "/changePassword",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String changePassword() {
        return "ok";
    }

    @PostMapping(
            value = "/changeUserPersonalData",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String changeUserPersonalData() {
        return "ok";
    }

    @PreAuthorize("hasRole('ADMIN') or principal==#userId")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteAccountResponse> deleteAccount(@PathVariable("id") String userId) {
        try {
            accountService.deleteAccount(userId);

            String message = translator.toLocale("delete.account.success");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new DeleteAccountResponse(message));

        } catch (UsernameNotFoundException exception) {
            String reason=translator.toLocale("exception.account.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("request.failure");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }


    @PostMapping(
            value = "/changeUserSettings",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String changeUserSettings() {
        return "ok";
    }

}
