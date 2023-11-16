package com.khomishchak.cryptoportfolio.model.requests;

import com.khomishchak.cryptoportfolio.model.enums.DeviceType;
import com.khomishchak.cryptoportfolio.validators.annotations.UniqueEmail;
import com.khomishchak.cryptoportfolio.validators.annotations.UniqueUsername;
import jakarta.validation.constraints.Email;

public record RegistrationRequest(@UniqueUsername String username, String password, @Email @UniqueEmail String email,
                                  boolean acceptTC, DeviceType deviceType) {
}
