package com.healthy.gym.gympass.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IDFormatValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface ValidIDFormat {
    String message() default "{exception.invalid.id.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
