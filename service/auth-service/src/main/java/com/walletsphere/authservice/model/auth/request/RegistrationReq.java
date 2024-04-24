package com.walletsphere.authservice.model.auth.request;

import com.walletsphere.authservice.model.auth.DeviceType;
import jakarta.validation.constraints.Email;

public record RegistrationReq(String username,
                              String password,
                              @Email String email,
                              boolean acceptTC,
                              DeviceType deviceType) {
}
