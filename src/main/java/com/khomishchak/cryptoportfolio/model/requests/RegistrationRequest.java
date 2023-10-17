package com.khomishchak.cryptoportfolio.model.requests;

import com.khomishchak.cryptoportfolio.model.enums.DeviceType;

public record RegistrationRequest(String username, String password, String email, boolean acceptTC, DeviceType deviceType) {
}
