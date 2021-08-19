package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.NotificationDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.NotificationDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.dto.UserNotificationDTO;
import com.healthy.gym.account.exception.NoNotificationFoundException;
import com.healthy.gym.account.exception.NotificationNotFoundException;
import com.healthy.gym.account.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public NotificationServiceImpl(NotificationDAO notificationDAO, UserDAO userDAO) {
        this.notificationDAO = notificationDAO;
        this.userDAO = userDAO;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<UserNotificationDTO> getRecentUserNotifications(String userId, int pageNumber, int pageSize)
            throws UserNotFoundException, NoNotificationFoundException {
        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UserNotFoundException();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<NotificationDocument> notifications = notificationDAO.findAllByToEquals(userDocument, pageable);

        List<NotificationDocument> notificationDocumentList = notifications.getContent();
        if (notificationDocumentList.isEmpty()) throw new NoNotificationFoundException();

        return notificationDocumentList
                .stream()
                .map(notificationDocument -> modelMapper.map(notificationDocument, UserNotificationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserNotificationDTO markNotificationAsRead(String notificationId, String userId)
            throws NotificationNotFoundException, UserNotFoundException {
        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UserNotFoundException();

        Optional<NotificationDocument> notificationDocument = notificationDAO.findByNotificationId(notificationId);
        NotificationDocument notification = notificationDocument.orElseThrow(NotificationNotFoundException::new);

        notification.setMarkAsRead(true);
        NotificationDocument updatedNotification = notificationDAO.save(notification);

        return modelMapper.map(updatedNotification, UserNotificationDTO.class);
    }
}
