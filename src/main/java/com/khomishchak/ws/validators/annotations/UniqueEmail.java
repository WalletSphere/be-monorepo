package com.khomishchak.ws.validators.annotations;

import com.khomishchak.ws.validators.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {

    String message() default "EMAIL_ALREADY_TAKEN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
