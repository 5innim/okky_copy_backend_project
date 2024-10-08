package com.innim.okkycopy.global.common.validation;

import com.innim.okkycopy.global.common.validation.annotation.ListSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ListSizeValidator implements ConstraintValidator<ListSize, List<?>> {

    private int min;
    private int max;

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value.size() <= max && value.size() >= min) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "validation fail for size of list, min: " + min + ", max: " + max)
                .addConstraintViolation();
            return false;
        }
    }


    @Override
    public void initialize(ListSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

}
