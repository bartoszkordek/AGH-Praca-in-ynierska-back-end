package com.healthy.gym.account.pojo.response;

import com.healthy.gym.account.dto.UserNotificationDTO;

public class DeleteNotificationResponse extends AbstractResponse {
    private UserNotificationDTO notificationDTO;

    public DeleteNotificationResponse(String message, UserNotificationDTO notificationDTO) {
        super(message);
        this.notificationDTO = notificationDTO;
    }

    public UserNotificationDTO getNotificationDTO() {
        return notificationDTO;
    }

    public void setNotificationDTO(UserNotificationDTO notificationDTO) {
        this.notificationDTO = notificationDTO;
    }
}
