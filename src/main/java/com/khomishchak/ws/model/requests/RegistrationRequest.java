package com.khomishchak.ws.model.requests;

import com.khomishchak.ws.model.enums.DeviceType;
import com.khomishchak.ws.validators.annotations.UniqueEmail;
import com.khomishchak.ws.validators.annotations.UniqueUsername;
import jakarta.validation.constraints.Email;

public record RegistrationRequest(@UniqueUsername String username, String password, @Email @UniqueEmail String email,
                                  boolean acceptTC, DeviceType deviceType) {
}
