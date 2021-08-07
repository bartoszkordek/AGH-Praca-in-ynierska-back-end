package com.healthy.gym.trainings.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidSize {
    String message() default "{exception.invalid.size.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
