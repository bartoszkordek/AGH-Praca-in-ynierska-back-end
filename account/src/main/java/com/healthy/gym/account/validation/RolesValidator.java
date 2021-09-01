package com.healthy.gym.account.validation;

import com.healthy.gym.account.enums.GymRole;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class RolesValidator implements ConstraintValidator<ValidRoles, List<String>> {

    @Override
    public boolean isValid(List<String> roles, ConstraintValidatorContext context) {

        try {
            if (roles == null || roles.isEmpty()) throw new IllegalArgumentException();
            for (String role : roles) {
                String trimmedRole = role.trim().toUpperCase();
                GymRole.valueOf(trimmedRole);
            }
        } catch (Exception exception) {
            return false;
        }

        return true;
    }
}
