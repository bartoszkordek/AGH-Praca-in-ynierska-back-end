package com.healthy.gym.trainings.component;

import org.springframework.stereotype.Component;

@Component
public class EmailMessageManagerImpl implements EmailMessageManager {
    @Override
    public String getCancelGroupTrainingMessageSubject() {
        return null;
    }

    @Override
    public String getCancelGroupTrainingMessageText() {
        return null;
    }

    @Override
    public String getChangeStartTimeOfGroupTrainingMessageSubject() {
        return null;
    }

    @Override
    public String getChangeStartTimeOfGroupTrainingMessageText() {
        return null;
    }
}
