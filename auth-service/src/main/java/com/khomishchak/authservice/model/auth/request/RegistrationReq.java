package com.khomishchak.authservice.model.auth.request;

import com.khomishchak.authservice.model.auth.DeviceType;
import jakarta.validation.constraints.Email;

public record RegistrationReq(String username,
                              String password,
                              @Email String email,
                              boolean acceptTC,
                              DeviceType deviceType) {
}
