package com.healthy.gym.account.shared;

import java.util.Arrays;
import java.util.Objects;

public class PhotoDTO {
    private String userId;
    private String title;
    private byte[] image;

    public PhotoDTO() {
    }

    public PhotoDTO(String userId, String title, byte[] image) {
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoDTO photoDTO = (PhotoDTO) o;
        return Objects.equals(userId, photoDTO.userId)
                && Objects.equals(title, photoDTO.title)
                && Arrays.equals(image, photoDTO.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(userId, title);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
