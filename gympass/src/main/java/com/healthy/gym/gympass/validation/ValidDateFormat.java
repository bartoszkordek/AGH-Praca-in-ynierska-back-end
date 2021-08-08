package com.healthy.gym.gympass.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateFormatValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidDateFormat {

    String message() default "{exception.invalid.date.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
