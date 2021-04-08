package com.healthy.gym.user.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldsValueMatchValidator
        implements ConstraintValidator<FieldsValueMatch,Object> {

    private String field;
    private String fieldToMatch;

    @Override
    public void initialize(FieldsValueMatch constraintAnnotation) {
        this.field=constraintAnnotation.field();
        this.fieldToMatch=constraintAnnotation.fieldToMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        Object fieldValue=new BeanWrapperImpl(value).getPropertyValue(field);
        Object fieldToMatchValue=new BeanWrapperImpl(value).getPropertyValue(fieldToMatch);

        if(fieldValue==null) return fieldToMatchValue==null;

        return fieldValue.equals(fieldToMatchValue);
    }
}
