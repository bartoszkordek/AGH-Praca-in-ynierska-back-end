package com.healthy.gym.account.pojo.response;

import java.util.Objects;

public class AvatarResponse extends AbstractResponse {
    private final String avatar;

    public AvatarResponse(String message, String avatar) {
        super(message);
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AvatarResponse that = (AvatarResponse) o;
        return Objects.equals(avatar, that.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), avatar);
    }

    @Override
    public String
    toString() {
        return "AvatarResponse{" +
                "avatar='" + avatar + '\'' +
                "} " + super.toString();
    }
}
