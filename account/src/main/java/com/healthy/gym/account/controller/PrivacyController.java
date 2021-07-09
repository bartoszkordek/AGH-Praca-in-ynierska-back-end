package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.exception.ResponseBindException;
import com.healthy.gym.account.exception.UserPrivacyNotUpdatedException;
import com.healthy.gym.account.pojo.request.ChangePrivacyRequest;
import com.healthy.gym.account.pojo.response.ChangePrivacyResponse;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.shared.UserPrivacyDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/{id}/privacy", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrivacyController {

    private final AccountService accountService;
    private final Translator translator;
    private final ModelMapper modelMapper;

    @Autowired
    public PrivacyController(AccountService accountService, Translator translator) {
        this.accountService = accountService;
        this.translator = translator;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or principal==#userId")
    @GetMapping
    public ResponseEntity<ChangePrivacyResponse> getUserPrivacy(@PathVariable("id") String userId) {
        try {
            UserPrivacyDTO userPrivacyDTO = accountService.getUserPrivacy(userId);
            ChangePrivacyResponse response = modelMapper.map(userPrivacyDTO, ChangePrivacyResponse.class);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale("exception.account.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("request.failure");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("principal==#userId")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChangePrivacyResponse> changeUserPrivacy(
            @PathVariable("id") String userId,
            @Valid @RequestBody ChangePrivacyRequest request,
            BindingResult bindingResult
    ) throws ResponseBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            UserPrivacyDTO userPrivacyDTO = modelMapper.map(request, UserPrivacyDTO.class);
            UserPrivacyDTO userPrivacyDTOUpdated = accountService.changeUserPrivacy(userPrivacyDTO, userId);
            String message = translator.toLocale("account.change.user.data.success");

            ChangePrivacyResponse response = modelMapper.map(userPrivacyDTOUpdated, ChangePrivacyResponse.class);
            response.setMessage(message);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale("exception.account.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserPrivacyNotUpdatedException exception) {
            String reason = translator.toLocale("account.change.user.data.failure");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("request.failure");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
