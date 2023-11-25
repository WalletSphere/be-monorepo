package com.khomishchak.ws.model.response;

import com.khomishchak.ws.model.enums.UserRole;

public record RegistrationResult(String username, String email, UserRole userRole, String jwt) {
}