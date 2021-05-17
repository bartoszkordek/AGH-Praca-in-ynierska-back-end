package com.healthy.gym.auth.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogInUserRequest {
    @NotNull(message = "{field.required}")
    @NotBlank(message = "{field.required}")
    @Email(message = "{field.email.failure}")
    private String email;

    @NotNull(message = "{field.required}")
    @NotBlank(message = "{field.required}")
    @Size(min = 8, max = 24, message = "{field.password.failure}")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
