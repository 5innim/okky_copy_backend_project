package com.innim.okkycopy.global.commons.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListSizeValidator.class)
public @interface ListSize {
    String message() default  "";
    int max() default 2;
    int min() default 0;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
