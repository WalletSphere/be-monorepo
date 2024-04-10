package com.khomishchak.authservice.model.auth.dto;

import jakarta.validation.constraints.Email;

public record CreateUserRequestDTO(String username,
                                   String password,
                                   @Email String email,
                                   boolean acceptTC) {
}
