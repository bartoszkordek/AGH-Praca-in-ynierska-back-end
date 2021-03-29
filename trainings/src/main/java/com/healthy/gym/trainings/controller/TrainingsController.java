package com.healthy.gym.trainings.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainings")
public class TrainingsController {
    
    @GetMapping("/status")
    public String status(){
        return "OK";
    }
}
