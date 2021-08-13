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
    private UserDocument from;

    @DBRef
    private UserDocument to;

    @LastModifiedBy
    private UserDocument lastModifiedBy;

    private String title;
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModified;

    private boolean markAsRead;

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

    public UserDocument getFrom() {
        return from;
    }

    public void setFrom(UserDocument from) {
        this.from = from;
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

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
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
                && Objects.equals(from, that.from)
                && Objects.equals(to, that.to)
                && Objects.equals(lastModifiedBy, that.lastModifiedBy)
                && Objects.equals(title, that.title)
                && Objects.equals(content, that.content)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(lastModified, that.lastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                notificationId,
                from,
                to,
                lastModifiedBy,
                title,
                content,
                createdAt,
                lastModified,
                markAsRead
        );
    }

    @Override
    public String toString() {
        return "NotificationDocument{" +
                "id='" + id + '\'' +
                ", notificationId='" + notificationId + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", lastModifiedBy=" + lastModifiedBy +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                ", markAsRead=" + markAsRead +
                '}';
    }
}
