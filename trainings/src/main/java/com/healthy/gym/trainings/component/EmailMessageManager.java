package com.healthy.gym.trainings.component;

public interface EmailMessageManager {
    String getCancelGroupTrainingMessageSubject();

    String getCancelGroupTrainingMessageText();

    String getChangeStartTimeOfGroupTrainingMessageSubject();

    String getChangeStartTimeOfGroupTrainingMessageText();
}
