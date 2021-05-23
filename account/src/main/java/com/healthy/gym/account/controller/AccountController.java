package com.healthy.gym.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.exception.IdenticalOldAndNewPasswordException;
import com.healthy.gym.account.exception.OldPasswordDoesNotMatchException;
import com.healthy.gym.account.pojo.request.ChangePasswordRequest;
import com.healthy.gym.account.pojo.response.ChangePasswordResponse;
import com.healthy.gym.account.pojo.response.DeleteAccountResponse;
import com.healthy.gym.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PreAuthorize("principal==#userId")
    @PutMapping(
            value = "/changePassword/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @PathVariable("id") String userId,
            @Valid @RequestBody ChangePasswordRequest request,
            BindingResult bindingResult
    ) throws JsonProcessingException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            accountService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            String message = translator.toLocale("password.change.success");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ChangePasswordResponse(message));

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale("exception.account.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (IdenticalOldAndNewPasswordException exception) {
            String reason = translator.toLocale("password.exception.old.identical.with.new.password");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (OldPasswordDoesNotMatchException exception) {
            String reason = translator.toLocale("password.exception.old.password.does.not.match");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (BindException exception) {
            String reason = getBindExceptionErrorMessages(exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("request.failure");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    private String getBindExceptionErrorMessages(BindException exception) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> errorMessages = exception.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return objectMapper.writeValueAsString(errorMessages);
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
            String reason = translator.toLocale("exception.account.not.found");
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
