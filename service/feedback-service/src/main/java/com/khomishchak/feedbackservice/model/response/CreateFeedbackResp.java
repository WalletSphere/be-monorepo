package com.khomishchak.feedbackservice.model.response;

// TODO: instead of RegistrationStatus status add list of Field class containing FieldType, FieldMessageType, FieldMessage
public record CreateFeedbackResp(Long userId, Long feedbackId) {
}
