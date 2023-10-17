package com.khomishchak.cryptoportfolio.model.requests;

import com.khomishchak.cryptoportfolio.model.enums.FeedbackType;

public record FeedbackRequest (Long userId, FeedbackType feedbackType, String message) {
}
