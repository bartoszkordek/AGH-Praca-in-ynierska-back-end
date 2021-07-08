package com.healthy.gym.account.pojo.response;

import com.healthy.gym.account.shared.ImageDTO;

import java.util.Objects;

public class AvatarResponse extends AbstractResponse {
    private final ImageDTO avatar;

    public AvatarResponse(String message, ImageDTO avatar) {
        super(message);
        this.avatar = avatar;
    }

    public ImageDTO getAvatar() {
        return avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AvatarResponse response = (AvatarResponse) o;
        return Objects.equals(avatar, response.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), avatar);
    }

    @Override
    public String toString() {
        return "GetAvatarResponse{" +
                "avatar=" + avatar +
                "} " + super.toString();
    }
}
