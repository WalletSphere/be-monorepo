package com.walletsphere.authservice.model.auth.request;

import com.walletsphere.authservice.model.auth.DeviceType;

public record LoginReq(String username, String password, DeviceType deviceType) {
}
