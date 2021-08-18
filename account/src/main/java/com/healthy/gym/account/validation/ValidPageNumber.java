package com.healthy.gym.account.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PageNumberValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidPageNumber {
    String message() default "{exception.invalid.page.number.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
