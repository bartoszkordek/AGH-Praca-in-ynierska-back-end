package com.healthy.gym.equipment.component;

import com.healthy.gym.equipment.exception.MultipartBodyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class MultipartFileValidatorImpl implements MultipartFileValidator {

    private final Translator translator;

    @Autowired
    public MultipartFileValidatorImpl(Translator translator) {
        this.translator = translator;
    }

    @Override
    public <T> boolean validateBody(T body) throws MultipartBodyException {

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> errors = validator.validate(body);

        Map<String, String> errorMap = new HashMap<>();

        for (ConstraintViolation<T> error : errors) {
            String errorName = error.getPropertyPath().toString();
            String errorMessage = getErrorMessage(error);
            errorMap.put(errorName, errorMessage);
        }

        if (errorMap.size() != 0) throw new MultipartBodyException(errorMap);

        return true;
    }

    private <T> String getErrorMessage(ConstraintViolation<T> error) {
        String message = error.getMessage();
        String messageEdited = message
                .replace("{", "")
                .replace("}", "");
        return translator.toLocale(messageEdited);
    }
}
