package com.innim.okkycopy.global.common.validation;

import com.innim.okkycopy.global.common.validation.annotation.MentionId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MentionIdValidator implements ConstraintValidator<MentionId, Long> {

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null || value >= 1L) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "validation fail, mentionId should be null or bigger than 0")
            .addConstraintViolation();
        return false;
    }
}
