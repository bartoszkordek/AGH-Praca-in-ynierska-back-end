package com.healthy.gym.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @GetMapping
    public ResponseEntity<Object> getRecentUserNotifications(
            @PathVariable String userId,
            @RequestParam String pageNumber,
            @RequestParam String pageSize
    ) {

        return null;
    }
}
