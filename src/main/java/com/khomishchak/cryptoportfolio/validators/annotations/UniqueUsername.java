package com.khomishchak.cryptoportfolio.validators.annotations;

import com.khomishchak.cryptoportfolio.validators.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {

    String message() default "USERNAME_ALREADY_TAKEN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
