package ru.practicum.ewm.event.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdsArrayValidator implements ConstraintValidator<PositiveInArray, Number[]> {

    @Override
    public void initialize(PositiveInArray constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Number[] ids, ConstraintValidatorContext constraintValidatorContext) {
        if (ids == null) {
            return true;
        }
        for (Number id : ids) {
            if (id.longValue() <= 0) {
                return false;
            }
        }
        return true;
    }
}
