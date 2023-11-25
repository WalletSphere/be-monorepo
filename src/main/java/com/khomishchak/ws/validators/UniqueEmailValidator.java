package com.khomishchak.ws.validators;

import com.khomishchak.ws.repositories.UserRepository;
import com.khomishchak.ws.validators.annotations.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String field, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByEmail(field).isEmpty();
    }
}
