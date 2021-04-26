package com.healthy.gym.user.controller;

import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.pojo.request.CreateUserRequest;
import com.healthy.gym.user.pojo.response.CreateUserResponse;
import com.healthy.gym.user.service.UserService;
import com.healthy.gym.user.shared.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final Translator translator;

    @Autowired
    public UserController(
            UserService userService,
            Translator translator
    ) {
        this.userService = userService;
        this.translator = translator;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateUserResponse> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            CreateUserResponse invalidCreateUserResponse = handleInvalidRegistration(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidCreateUserResponse);
        }

        if (hasUserAlreadyExist(createUserRequest)) {
            CreateUserResponse userExistsResponse = handleUserExistsResponse();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userExistsResponse);
        }

        CreateUserResponse response = handleValidRegistration(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private CreateUserResponse handleInvalidRegistration(BindingResult bindingResult) {

        Map<String, String> errors = new HashMap<>();
        String singUpFailureMessage = translator.toLocale("user.sing-up.failure");
        CreateUserResponse response =
                new CreateUserResponse(false, singUpFailureMessage, errors, null);

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

    private boolean hasUserAlreadyExist(CreateUserRequest request) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDTO userDTO = modelMapper.map(request, UserDTO.class);

        try {
            userService.loadUserByUsername(userDTO.getEmail());
        } catch (UsernameNotFoundException e) {
            return false;
        }
        return true;
    }

    private CreateUserResponse handleUserExistsResponse() {
        String userExistsMessage = translator.toLocale("user.sign-up.email.exists");
        return new CreateUserResponse(false, userExistsMessage, new HashMap<>(), null);
    }


    private CreateUserResponse handleValidRegistration(CreateUserRequest createUserRequest) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDTO userDTO = modelMapper.map(createUserRequest, UserDTO.class);
        UserDTO responseUserDTO = userService.createUser(userDTO);

        String singUpSuccessMessage = translator.toLocale("user.sing-up.success");

        return new CreateUserResponse(
                true,
                singUpSuccessMessage,
                new HashMap<>(),
                responseUserDTO.getUserId()
        );
    }

    @GetMapping("/status")
    public @ResponseBody
    String status() {
        return userService.status();
    }
}
