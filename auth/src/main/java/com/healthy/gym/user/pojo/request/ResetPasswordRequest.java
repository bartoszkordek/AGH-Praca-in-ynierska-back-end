package com.healthy.gym.user.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.user.validation.ValidEmail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {
    @NotNull(message = "{field.required}")
    @NotBlank(message = "{field.required}")
    @ValidEmail(message = "{field.email.failure}")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
