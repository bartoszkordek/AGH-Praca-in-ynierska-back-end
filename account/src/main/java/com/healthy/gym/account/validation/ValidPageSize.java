package com.healthy.gym.account.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PageSizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidPageSize {
    String message() default "{exception.invalid.page.size.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
