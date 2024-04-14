package com.walletsphere.feedbackservice.model.request;

import com.walletsphere.feedbackservice.model.FeedbackType;

public record FeedbackRequest (FeedbackType feedbackType, String message) {}
