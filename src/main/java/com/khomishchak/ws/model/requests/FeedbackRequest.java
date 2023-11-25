package com.khomishchak.ws.model.requests;

import com.khomishchak.ws.model.enums.FeedbackType;

public record FeedbackRequest (FeedbackType feedbackType, String message) {
}
