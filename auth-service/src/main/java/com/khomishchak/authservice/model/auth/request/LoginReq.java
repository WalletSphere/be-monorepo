package com.khomishchak.authservice.model.auth.request;

import com.khomishchak.authservice.model.auth.DeviceType;

public record LoginReq(String username, String password, DeviceType deviceType) {
}
