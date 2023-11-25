package com.khomishchak.ws.model.requests;

import com.khomishchak.ws.model.enums.DeviceType;

public record LoginRequest(String username, String password, DeviceType deviceType) {
}
