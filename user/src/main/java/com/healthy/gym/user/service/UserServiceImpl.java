package com.healthy.gym.user.service;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Override
    public String status() {
        return "OK";
    }
}
