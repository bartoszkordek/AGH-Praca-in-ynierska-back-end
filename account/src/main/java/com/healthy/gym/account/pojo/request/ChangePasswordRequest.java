package com.healthy.gym.account.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.account.validation.FieldsValueMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "newPassword",
                fieldToMatch = "matchingNewPassword",
                message = "{field.password.match.failure}"
        )
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 8, max = 24, message = "{field.password.failure}")
    private String oldPassword;

    @NotNull(message = "{field.required}")
    @Size(min = 8, max = 24, message = "{field.password.failure}")
    private String newPassword;

    @NotNull(message = "{field.required}")
    @Size(min = 8, max = 24, message = "{field.password.failure}")
    private String matchingNewPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getMatchingNewPassword() {
        return matchingNewPassword;
    }

    public void setMatchingNewPassword(String matchingNewPassword) {
        this.matchingNewPassword = matchingNewPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangePasswordRequest that = (ChangePasswordRequest) o;
        return Objects.equals(oldPassword, that.oldPassword)
                && Objects.equals(newPassword, that.newPassword)
                && Objects.equals(matchingNewPassword, that.matchingNewPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldPassword, newPassword, matchingNewPassword);
    }
}
