package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.exception.notfound.ImageNotFoundException;

public interface PhotoService {

    byte[] getImage(String userId) throws ImageNotFoundException;
}
