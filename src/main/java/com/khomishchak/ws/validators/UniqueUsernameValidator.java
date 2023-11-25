package com.khomishchak.ws.validators;

import com.khomishchak.ws.repositories.UserRepository;
import com.khomishchak.ws.validators.annotations.UniqueUsername;
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
