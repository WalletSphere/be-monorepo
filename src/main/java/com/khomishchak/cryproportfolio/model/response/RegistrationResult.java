package com.khomishchak.cryproportfolio.model.response;

import com.khomishchak.cryproportfolio.model.enums.UserRole;

public record RegistrationResult(String username, String email, UserRole userRole) {
}