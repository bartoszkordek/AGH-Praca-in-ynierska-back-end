package com.healthy.gym.trainings.data.document;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "notifications")
public class NotificationDocument {

    @Id
    private String id;
    private String notificationId;

    @CreatedBy
    @DBRef
    private UserDocument createdBy;

    @DBRef
    private UserDocument to;

    @LastModifiedBy
    @DBRef
    private UserDocument lastModifiedBy;

    private String title;
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    private boolean markAsRead;


    public NotificationDocument() {
        //empty constructor required by spring data mapper
    }

    public NotificationDocument(
            String notificationId,
            UserDocument to,
            String title,
            String content
    ) {
        this.notificationId = notificationId;
        this.to = to;
        this.title = title;
        this.content = content;
    }

    public NotificationDocument(
            String notificationId,
            UserDocument to,
            String title,
            String content,
            boolean markAsRead
    ) {
        this.notificationId = notificationId;
        this.to = to;
        this.title = title;
        this.content = content;
        this.markAsRead = markAsRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public UserDocument getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDocument createdBy) {
        this.createdBy = createdBy;
    }

    public UserDocument getTo() {
        return to;
    }

    public void setTo(UserDocument to) {
        this.to = to;
    }

    public UserDocument getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(UserDocument lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
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
        NotificationDocument that = (NotificationDocument) o;
        return markAsRead == that.markAsRead
                && Objects.equals(id, that.id)
                && Objects.equals(notificationId, that.notificationId)
                && Objects.equals(createdBy, that.createdBy)
                && Objects.equals(to, that.to)
                && Objects.equals(lastModifiedBy, that.lastModifiedBy)
                && Objects.equals(title, that.title)
                && Objects.equals(content, that.content)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(lastModifiedAt, that.lastModifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                notificationId,
                createdBy,
                to,
                lastModifiedBy,
                title,
                content,
                createdAt,
                lastModifiedAt,
                markAsRead
        );
    }

    @Override
    public String toString() {
        return "NotificationDocument{" +
                "id='" + id + '\'' +
                ", notificationId='" + notificationId + '\'' +
                ", from=" + createdBy +
                ", to=" + to +
                ", lastModifiedBy=" + lastModifiedBy +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModifiedAt +
                ", markAsRead=" + markAsRead +
                '}';
    }
}
