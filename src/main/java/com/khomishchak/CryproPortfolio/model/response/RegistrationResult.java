package com.khomishchak.CryproPortfolio.model.response;

import com.khomishchak.CryproPortfolio.model.enums.UserRole;

public record RegistrationResult(String username, String password, UserRole userRole) {
}