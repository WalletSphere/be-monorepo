package com.khomishchak.ws.model.response;

import com.khomishchak.ws.model.enums.RegistrationStatus;

// TODO: instead of RegistrationStatus status add list of Field class containing FieldType, FieldMessageType, FieldMessage
public record RegisterApiKeysResp(Long userId, Long apiKeysSettingsId, RegistrationStatus status) {
}
