package com.healthy.gym.account.pojo.response;

import com.healthy.gym.account.dto.UserNotificationDTO;

public class DeleteNotificationResponse extends AbstractResponse {
    private UserNotificationDTO notification;

    public DeleteNotificationResponse(String message, UserNotificationDTO notification) {
        super(message);
        this.notification = notification;
    }

    public UserNotificationDTO getNotification() {
        return notification;
    }

    public void setNotification(UserNotificationDTO notification) {
        this.notification = notification;
    }
}
