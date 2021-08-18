package com.healthy.gym.account.service;

import com.healthy.gym.account.data.repository.NotificationDAO;
import com.healthy.gym.account.dto.UserNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;

    @Autowired
    public NotificationServiceImpl(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public List<UserNotificationDTO> getRecentUserNotifications(String userId, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public UserNotificationDTO markNotificationAsRead(String notificationId, String userId) {
        return null;
    }
}
