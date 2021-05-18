package com.healthy.gym.account.controller;

import com.healthy.gym.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
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

    @GetMapping("/deleteAccount")
    public String deleteAccount() {
        return "ok";
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
