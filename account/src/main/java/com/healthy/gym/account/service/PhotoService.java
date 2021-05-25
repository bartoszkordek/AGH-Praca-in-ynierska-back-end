package com.healthy.gym.account.service;

import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.shared.PhotoDTO;

public interface PhotoService {
    PhotoDTO getAvatar(String userId) throws UserAvatarNotFoundException;

    PhotoDTO setAvatar(String userId, PhotoDTO avatar) throws PhotoSavingException;
}
