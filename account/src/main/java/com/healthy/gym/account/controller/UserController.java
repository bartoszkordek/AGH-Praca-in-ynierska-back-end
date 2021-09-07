package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.dto.DetailUserInfoDTO;
import com.healthy.gym.account.dto.StatsDTO;
import com.healthy.gym.account.exception.NoUserFound;
import com.healthy.gym.account.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final String REQUEST_FAILURE = "request.failure";
    private final UserService userService;
    private final Translator translator;

    public UserController(UserService userService, Translator translator) {
        this.userService = userService;
        this.translator = translator;
    }

    @GetMapping("/users")
    public ResponseEntity<List<DetailUserInfoDTO>> getAllUsersInSystem() {
        try {
            return ResponseEntity.ok(userService.getAllUsersInSystem());

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.user.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/trainers")
    public ResponseEntity<List<DetailUserInfoDTO>> getAllTrainers() {
        try {
            return ResponseEntity.ok(userService.getAllTrainersInSystem());

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.trainer.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<List<DetailUserInfoDTO>> getAllEmployees() {
        try {
            return ResponseEntity.ok(userService.getAllEmployeesInSystem());

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.employee.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/managers")
    public ResponseEntity<List<DetailUserInfoDTO>> getAllManagers() {
        try {
            return ResponseEntity.ok(userService.getAllManagersInSystem());

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.manager.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsDTO>> getStatsForLastWeek() {
        try {
            return ResponseEntity.ok(userService.getLastWeekStats());

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
