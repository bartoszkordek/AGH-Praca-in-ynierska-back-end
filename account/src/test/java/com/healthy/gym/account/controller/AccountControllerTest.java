package com.healthy.gym.account.controller;

import com.healthy.gym.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AccountControllerTest {

    @Autowired
    private AccountController accountController;

    @MockBean
    private AccountService accountService;


    @Test
    void name() {
    }
}