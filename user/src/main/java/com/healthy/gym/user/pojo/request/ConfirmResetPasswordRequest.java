package com.healthy.gym.user.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.user.validation.FieldsValueMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "password",
                fieldToMatch = "matchingPassword",
                message = "{field.password.match.failure}"
        )
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfirmResetPasswordRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 8, max = 24, message = "{field.password.failure}")
    private String password;

    @NotNull(message = "{field.required}")
    @Size(min = 8, max = 24, message = "{field.password.failure}")
    private String matchingPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
