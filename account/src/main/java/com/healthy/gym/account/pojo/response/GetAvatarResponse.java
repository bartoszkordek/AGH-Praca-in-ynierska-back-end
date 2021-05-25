package com.healthy.gym.account.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class GetAvatarResponse extends AbstractResponse {
    @JsonProperty("avatar")
    public final String imageBase64Encoded;

    public GetAvatarResponse(String message, String imageBase64Encoded) {
        super(message);
        this.imageBase64Encoded = imageBase64Encoded;
    }

    public String getImageBase64Encoded() {
        return imageBase64Encoded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GetAvatarResponse that = (GetAvatarResponse) o;
        return Objects.equals(imageBase64Encoded, that.imageBase64Encoded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), imageBase64Encoded);
    }

    @Override
    public String toString() {
        return "GetAvatarResponse{" +
                "imageBase64Encoded='" + imageBase64Encoded + '\'' +
                "} " + super.toString();
    }
}
