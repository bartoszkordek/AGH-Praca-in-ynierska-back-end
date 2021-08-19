package com.healthy.gym.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNotificationDTO {

    @JsonProperty("from")
    private BasicUserInfoDTO createdBy;

    @JsonProperty("created")
    private String createdAt;

    private String title;
    private String content;
    private boolean markAsRead;
    private String notificationId;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotificationDTO that = (UserNotificationDTO) o;
        return markAsRead == that.markAsRead
                && Objects.equals(notificationId, that.notificationId)
                && Objects.equals(createdBy, that.createdBy)
                && Objects.equals(title, that.title)
                && Objects.equals(content, that.content)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, createdBy, title, content, createdAt, markAsRead);
    }

    @Override
    public String toString() {
        return "UserNotificationDTO{" +
                "notificationId='" + notificationId + '\'' +
                ", createdBy=" + createdBy +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", markAsRead=" + markAsRead +
                '}';
    }
}
