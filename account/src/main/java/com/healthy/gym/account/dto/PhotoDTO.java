package com.healthy.gym.account.dto;

import com.healthy.gym.account.pojo.Image;

import java.util.Objects;

public class PhotoDTO {
    private String userId;
    private String title;
    private Image image;

    public PhotoDTO() {
    }

    public PhotoDTO(String userId, String title, Image image) {
        this.userId = userId;
        this.title = title;
        this.image = image;
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
        PhotoDTO photoDTO = (PhotoDTO) o;
        return Objects.equals(userId, photoDTO.userId)
                && Objects.equals(title, photoDTO.title)
                && Objects.equals(image, photoDTO.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, title, image);
    }

    @Override
    public String toString() {
        return "PhotoDTO{" +
                "userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", image=" + image +
                '}';
    }
}
