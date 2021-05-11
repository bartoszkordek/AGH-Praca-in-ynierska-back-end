package com.healthy.gym.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.exceptions.token.ExpiredTokenException;
import com.healthy.gym.user.exceptions.token.InvalidTokenException;
import com.healthy.gym.user.pojo.request.ConfirmResetPasswordRequest;
import com.healthy.gym.user.pojo.request.ResetPasswordRequest;
import com.healthy.gym.user.pojo.response.ConfirmationResponse;
import com.healthy.gym.user.service.TokenService;
import com.healthy.gym.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PasswordController {

    private final UserService userService;
    private final Translator translator;
    private final TokenService tokenService;

    @Autowired
    public PasswordController(UserService userService, Translator translator, TokenService tokenService) {
        this.userService = userService;
        this.translator = translator;
        this.tokenService = tokenService;
    }

    @PostMapping(
            value = "/resetPassword",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ConfirmationResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            userService.resetPassword(request.getEmail());

            String message = translator.toLocale("reset.password");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ConfirmationResponse(message, new HashMap<>(), true));
        } catch (AccountStatusException exception) {
            String reason = null;

            if (exception instanceof AccountExpiredException) {
                reason = translator.toLocale("reset.password.exception.account.expired");

            } else if (exception instanceof CredentialsExpiredException) {
                reason = translator.toLocale("reset.password.exception.credentials.expired");

            } else if (exception instanceof DisabledException) {
                reason = translator.toLocale("reset.password.exception.account.disabled");

            } else if (exception instanceof LockedException) {
                reason = translator.toLocale("reset.password.exception.account.locked");

            } else {
                reason = translator.toLocale("reset.password.error");
                exception.printStackTrace();
            }

            throw new ResponseStatusException(HttpStatus.FORBIDDEN, reason, exception);

        } catch (BindException | UsernameNotFoundException exception) {
            String reason = translator.toLocale("field.email.failure");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("reset.password.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PostMapping(
            value = "/confirmNewPassword",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ConfirmationResponse> confirmResettingPassword(
            @RequestParam("token") String token,
            @Valid @RequestBody ConfirmResetPasswordRequest request,
            BindingResult bindingResult
    ) throws JsonProcessingException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            tokenService.verifyTokenAndResetPassword(token, request.getPassword());
            String message = translator.toLocale("reset.password.confirmation.token.valid");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ConfirmationResponse(message, new HashMap<>(), true));

        } catch (ExpiredTokenException exception) {
            String reason = translator.toLocale("reset.password.confirmation.token.expired");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, reason, exception);

        } catch (InvalidTokenException exception) {
            String reason = translator.toLocale("reset.password.confirmation.token.invalid");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (BindException exception) {
            String reason = getBindExceptionErrorMessages(exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("reset.password.error");
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
}
