package com.khomishchak.cryptoportfolio.model.response;

import com.khomishchak.cryptoportfolio.model.enums.UserRole;

public record RegistrationResult(String username, String email, UserRole userRole, String jwt) {
}