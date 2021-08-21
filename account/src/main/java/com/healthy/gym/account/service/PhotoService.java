package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
    byte[] getAvatar(String userId) throws UserAvatarNotFoundException;

    PhotoDocument removeAvatar(String userId) throws UserAvatarNotFoundException;

    String setAvatar(String userId, MultipartFile multipartFile) throws PhotoSavingException, IOException;
}
