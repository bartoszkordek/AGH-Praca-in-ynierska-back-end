package com.healthy.gym.user.controller;

import com.healthy.gym.user.pojo.request.CreateUserRequest;
import com.healthy.gym.user.pojo.response.CreateUserResponse;
import com.healthy.gym.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateUserResponse> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest, BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            CreateUserResponse invalidCreateUserResponse = handleInvalidRegistration(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidCreateUserResponse);
        }

        CreateUserResponse response = handleValidRegistration(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private CreateUserResponse handleInvalidRegistration(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        CreateUserResponse response =
                new CreateUserResponse(false, "Rejestracja zakończona niepowodzeniem.", errors);

        bindingResult.getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                String fieldName = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                String errorMessage = error.getDefaultMessage();
                errors.put("matchingPassword", errorMessage);
            }
        });

        return response;
    }

    private CreateUserResponse handleValidRegistration(CreateUserRequest createUserRequest) {
        return new CreateUserResponse(true, "Użytkownik został zarejestrowany.", new HashMap<>());
    }

    @GetMapping("/status")
    public @ResponseBody
    String status() {
        return userService.status();
    }
}
