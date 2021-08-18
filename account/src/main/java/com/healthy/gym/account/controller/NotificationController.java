package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.dto.UserNotificationDTO;
import com.healthy.gym.account.exception.NoNotificationFoundException;
import com.healthy.gym.account.exception.NotificationNotFoundException;
import com.healthy.gym.account.exception.UserNotFoundException;
import com.healthy.gym.account.service.NotificationService;
import com.healthy.gym.account.validation.ValidIDFormat;
import com.healthy.gym.account.validation.ValidPageNumber;
import com.healthy.gym.account.validation.ValidPageSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class NotificationController {

    private final Translator translator;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(Translator translator, NotificationService notificationService) {
        this.translator = translator;
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasRole('ADMIN') or principal==#userId")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNotificationDTO>> getRecentUserNotifications(
            @ValidIDFormat @PathVariable String userId,
            @ValidPageNumber @RequestParam String pageNumber,
            @ValidPageSize @RequestParam String pageSize
    ) {
        try {
            var notifications = notificationService
                    .getRecentUserNotifications(userId, Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
            return ResponseEntity.status(HttpStatus.OK).body(notifications);

        } catch (NoNotificationFoundException exception) {
            String reason = translator.toLocale("exception.no.notification.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.user.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or principal==#userId")
    @PostMapping("/{notificationId}/user/{userId}")
    public ResponseEntity<UserNotificationDTO> markNotificationAsRead(
            @ValidIDFormat @PathVariable String notificationId,
            @ValidIDFormat @PathVariable String userId
    ) {
        try {
            UserNotificationDTO notificationDTO = notificationService
                    .markNotificationAsRead(notificationId, userId);
            return ResponseEntity.status(HttpStatus.OK).body(notificationDTO);

        } catch (NotificationNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.notification");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.user.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
