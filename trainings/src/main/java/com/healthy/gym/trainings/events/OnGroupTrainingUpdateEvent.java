package com.healthy.gym.trainings.events;

import org.springframework.context.ApplicationEvent;

import java.util.Collection;
import java.util.List;

public class OnGroupTrainingUpdateEvent extends ApplicationEvent {
    private final String title;
    private final String content;
    private final Collection<String> emails;

    public OnGroupTrainingUpdateEvent(
            Object source,
            String title,
            String content,
            List<String> emails
    ) {
        super(source);
        this.title = title;
        this.content = content;
        this.emails = emails;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Collection<String> getEmails() {
        return emails;
    }
}
