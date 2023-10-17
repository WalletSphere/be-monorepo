package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.model.Feedback;
import com.khomishchak.cryptoportfolio.model.requests.FeedbackRequest;

import java.util.List;

public interface FeedbackService {

    Feedback saveFeedback(FeedbackRequest feedbackRequest);

    List<Feedback> getAllFeedbacks();
}
