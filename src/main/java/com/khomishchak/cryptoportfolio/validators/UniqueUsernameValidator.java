package com.khomishchak.cryptoportfolio.validators;

import com.khomishchak.cryptoportfolio.repositories.UserRepository;
import com.khomishchak.cryptoportfolio.validators.annotations.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;


public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String field, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByUsername(field).isEmpty();
    }
}
