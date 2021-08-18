package com.healthy.gym.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNotificationDTO {

    private String notificationId;
    @JsonProperty("from")
    private BasicUserInfoDTO createdBy;
    private String title;
    private String content;
    @JsonProperty("created")
    private String createdAt;
    private boolean markAsRead;

    public UserNotificationDTO() {
    }

    public UserNotificationDTO(
            String notificationId,
            BasicUserInfoDTO createdBy,
            String title,
            String content,
            String createdAt,
            boolean markAsRead
    ) {
        this.notificationId = notificationId;
        this.createdBy = createdBy;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.markAsRead = markAsRead;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public BasicUserInfoDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BasicUserInfoDTO createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isMarkAsRead() {
        return markAsRead;
    }

    public void setMarkAsRead(boolean markAsRead) {
        this.markAsRead = markAsRead;
    }
}
