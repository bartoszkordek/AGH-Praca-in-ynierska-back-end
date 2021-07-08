package com.healthy.gym.account.data.document;

import com.healthy.gym.account.pojo.Image;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "photos")
public class PhotoDocument {

    @Id
    private String id;
    private String userId;
    private String title;
    private Image image;

    public PhotoDocument() {
    }

    public PhotoDocument(String userId, String title, Image image) {
        this.userId = userId;
        this.title = title;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoDocument that = (PhotoDocument) o;
        return Objects.equals(id, that.id)
                && Objects.equals(userId, that.userId)
                && Objects.equals(title, that.title)
                && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, title, image);
    }

    @Override
    public String toString() {
        return "PhotoDocument{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", image=" + image +
                '}';
    }
}
