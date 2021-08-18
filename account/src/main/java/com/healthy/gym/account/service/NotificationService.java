package com.healthy.gym.account.service;

import com.healthy.gym.account.shared.UserNotificationDTO;

public interface NotificationService {
    UserNotificationDTO getRecentUserNotifications(String userId, String pageNumber, String pageSize);
}
