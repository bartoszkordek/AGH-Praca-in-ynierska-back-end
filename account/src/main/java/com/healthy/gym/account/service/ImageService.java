package com.healthy.gym.account.service;

import com.healthy.gym.account.exception.ImageNotFoundException;

public interface ImageService {

    byte[] getImage(String imageId) throws ImageNotFoundException;
}
