package com.healthy.gym.account.service;

import com.healthy.gym.account.dto.UserNotificationDTO;
import com.healthy.gym.account.exception.NoNotificationFoundException;
import com.healthy.gym.account.exception.NotificationNotFoundException;
import com.healthy.gym.account.exception.UserNotFoundException;

import java.util.List;

public interface NotificationService {
    List<UserNotificationDTO> getRecentUserNotifications(String userId, int pageNumber, int pageSize)
            throws NoNotificationFoundException, UserNotFoundException;

    UserNotificationDTO markNotificationAsRead(String notificationId, String userId)
            throws NotificationNotFoundException, UserNotFoundException;
}
