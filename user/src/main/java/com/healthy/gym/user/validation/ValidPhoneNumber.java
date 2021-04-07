package com.healthy.gym.user.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy =PhoneNumberValidator.class )
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface ValidPhoneNumber {
    String message() default "Numer telefonu powininien być 9 cyfrowy.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
