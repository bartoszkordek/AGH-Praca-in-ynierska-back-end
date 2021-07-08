package com.healthy.gym.account.service;

import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.shared.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
    ImageDTO getAvatar(String userId) throws UserAvatarNotFoundException;

    ImageDTO removeAvatar(String userId) throws UserAvatarNotFoundException;

    ImageDTO setAvatar(String userId, MultipartFile multipartFile) throws PhotoSavingException, IOException;
}
