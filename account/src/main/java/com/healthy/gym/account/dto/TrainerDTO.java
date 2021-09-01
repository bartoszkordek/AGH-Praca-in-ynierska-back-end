package com.healthy.gym.account.dto;

import java.util.List;
import java.util.Objects;

public class TrainerDTO {

    private String userId;
    private String name;
    private String surname;
    private List<String> images;
    private String avatar;
    private DescriptionDTO description;

    public TrainerDTO() {}

    public TrainerDTO(
            String userId,
            String name,
            String surname,
            List<String> images,
            String avatar,
            DescriptionDTO description
    ) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.images = images;
        this.avatar = avatar;
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public List<String> getImages() {
        return images;
    }

    public String getAvatar() {
        return avatar;
    }

    public DescriptionDTO getDescription() {
        return description;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDescription(DescriptionDTO description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TrainerDTO{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", images=" + images +
                ", avatar='" + avatar + '\'' +
                ", description=" + description +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainerDTO that = (TrainerDTO) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(images, that.images) &&
                Objects.equals(avatar, that.avatar) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                userId,
                name,
                surname,
                images,
                avatar,
                description
        );
    }
}
