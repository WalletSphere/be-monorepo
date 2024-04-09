package com.khomishchak.feedbackservice.model.request;

import com.khomishchak.feedbackservice.model.FeedbackType;

public record FeedbackRequest (FeedbackType feedbackType, String message) {}
