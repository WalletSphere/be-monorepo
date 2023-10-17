package com.khomishchak.cryptoportfolio.model.requests;

import com.khomishchak.cryptoportfolio.model.enums.DeviceType;

public record LoginRequest(String username, String password, DeviceType deviceType) {
}
