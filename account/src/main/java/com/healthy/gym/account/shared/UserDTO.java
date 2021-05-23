package com.healthy.gym.account.shared;

import com.healthy.gym.account.enums.GymRole;

import java.util.Collection;
import java.util.Objects;

public class UserDTO {
    private String userId;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String password;
    private String encryptedPassword;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private Collection<GymRole> gymRoles;

    public UserDTO() {
        // default constructor for object mapper
    }

    public UserDTO(
            String userId,
            String name,
            String surname,
            String email,
            String phoneNumber,
            String password,
            String encryptedPassword
    ) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.encryptedPassword = encryptedPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return enabled == userDTO.enabled
                && accountNonExpired == userDTO.accountNonExpired
                && credentialsNonExpired == userDTO.credentialsNonExpired
                && accountNonLocked == userDTO.accountNonLocked
                && Objects.equals(userId, userDTO.userId)
                && Objects.equals(name, userDTO.name)
                && Objects.equals(surname, userDTO.surname)
                && Objects.equals(email, userDTO.email)
                && Objects.equals(phoneNumber, userDTO.phoneNumber)
                && Objects.equals(password, userDTO.password)
                && Objects.equals(encryptedPassword, userDTO.encryptedPassword)
                && Objects.equals(gymRoles, userDTO.gymRoles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                userId,
                name,
                surname,
                email,
                phoneNumber,
                password,
                encryptedPassword,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                gymRoles
        );
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", enabled=" + enabled +
                ", accountNonExpired=" + accountNonExpired +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", gymRoles=" + gymRoles +
                '}';
    }
}
