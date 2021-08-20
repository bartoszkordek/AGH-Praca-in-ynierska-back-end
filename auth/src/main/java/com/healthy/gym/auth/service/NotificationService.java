package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.document.NotificationDocument;
import com.healthy.gym.auth.data.document.UserDocument;

public interface NotificationService {
    NotificationDocument createWelcomeNotification(UserDocument userDocument);
}
