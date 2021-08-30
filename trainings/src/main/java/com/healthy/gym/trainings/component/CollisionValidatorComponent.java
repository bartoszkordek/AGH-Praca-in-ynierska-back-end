package com.healthy.gym.trainings.component;

import com.healthy.gym.trainings.utils.CollisionValidator;

import java.time.LocalDateTime;

public interface CollisionValidatorComponent {
    CollisionValidator getCollisionValidator(LocalDateTime startDateTime, LocalDateTime endDateTime);

    CollisionValidator getCollisionValidator(LocalDateTime startDateTime, LocalDateTime endDateTime, String trainingId);
}
