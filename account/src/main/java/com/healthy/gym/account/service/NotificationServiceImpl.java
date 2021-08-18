package com.healthy.gym.account.service;

import com.healthy.gym.account.data.repository.NotificationDAO;
import com.healthy.gym.account.shared.UserNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;

    @Autowired
    public NotificationServiceImpl(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public UserNotificationDTO getRecentUserNotifications(String userId, String pageNumber, String pageSize) {
        return null;
    }
}
