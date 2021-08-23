package com.healthy.gym.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.account.enums.GymRole;

import java.util.Collection;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailUserInfoDTO {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String userId;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private Collection<GymRole> gymRoles;
    @JsonProperty("avatar")
    private String avatarUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Collection<GymRole> getGymRoles() {
        return gymRoles;
    }

    public void setGymRoles(Collection<GymRole> gymRoles) {
        this.gymRoles = gymRoles;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailUserInfoDTO that = (DetailUserInfoDTO) o;
        return enabled == that.enabled
                && accountNonExpired == that.accountNonExpired
                && credentialsNonExpired == that.credentialsNonExpired
                && accountNonLocked == that.accountNonLocked
                && Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname)
                && Objects.equals(email, that.email)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(userId, that.userId)
                && Objects.equals(gymRoles, that.gymRoles)
                && Objects.equals(avatarUrl, that.avatarUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                surname,
                email,
                phoneNumber,
                userId,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                gymRoles,
                avatarUrl
        );
    }

    @Override
    public String toString() {
        return "DetailUserInfoDTO{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userId='" + userId + '\'' +
                ", enabled=" + enabled +
                ", accountNonExpired=" + accountNonExpired +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", gymRoles=" + gymRoles +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
