package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.dto.DetailUserInfoDTO;
import com.healthy.gym.account.exception.ResponseBindException;
import com.healthy.gym.account.exception.UserNotFoundException;
import com.healthy.gym.account.pojo.request.ChangeUserRolesRequest;
import com.healthy.gym.account.pojo.response.ChangeUserRolesResponse;
import com.healthy.gym.account.service.ManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
public class ManagerController {

    private final Translator translator;
    private final ManagerService managerService;

    public ManagerController(Translator translator, ManagerService managerService) {
        this.translator = translator;
        this.managerService = managerService;
    }

    @PostMapping("/manager/user/{userId}/roles")
    public ResponseEntity<ChangeUserRolesResponse> changeUserRoles(
            @PathVariable final String userId,
            @RequestBody final ChangeUserRolesRequest request,
            final BindingResult bindingResult
    ) throws ResponseBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message = translator.toLocale("user.roles.changed");
            DetailUserInfoDTO user = managerService.changeUserRoles(userId, request.getRoles());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ChangeUserRolesResponse(message, user));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.user.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("request.failure");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
