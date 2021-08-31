package com.healthy.gym.equipment.service;

import com.healthy.gym.equipment.exception.ImageNotFoundException;

public interface PhotoService {

    byte[] getImage(String userId) throws ImageNotFoundException;
}
