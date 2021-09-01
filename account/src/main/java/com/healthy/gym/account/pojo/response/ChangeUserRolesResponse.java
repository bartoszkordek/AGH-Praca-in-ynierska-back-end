package com.healthy.gym.account.pojo.response;

import com.healthy.gym.account.dto.DetailUserInfoDTO;

import java.util.Objects;

public class ChangeUserRolesResponse extends AbstractResponse {
    private DetailUserInfoDTO user;

    public ChangeUserRolesResponse(String message, DetailUserInfoDTO user) {
        super(message);
        this.user = user;
    }

    public DetailUserInfoDTO getUser() {
        return user;
    }

    public void setUser(DetailUserInfoDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ChangeUserRolesResponse that = (ChangeUserRolesResponse) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }

    @Override
    public String toString() {
        return "ChangeUserRolesResponse{" +
                "user=" + user +
                "} " + super.toString();
    }
}
