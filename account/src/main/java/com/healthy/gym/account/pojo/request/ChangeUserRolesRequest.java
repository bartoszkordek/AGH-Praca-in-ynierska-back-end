package com.healthy.gym.account.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.account.validation.ValidRoles;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeUserRolesRequest {

    @NotNull(message = "{field.required}")
    @ValidRoles
    private List<String> roles;

    public ChangeUserRolesRequest(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeUserRolesRequest that = (ChangeUserRolesRequest) o;
        return Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roles);
    }
}
