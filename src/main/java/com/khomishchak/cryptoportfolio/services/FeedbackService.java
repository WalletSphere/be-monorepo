package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.model.Feedback;
import com.khomishchak.cryptoportfolio.model.response.CreateFeedbackResp;
import com.khomishchak.cryptoportfolio.model.requests.FeedbackRequest;

import java.util.List;

public interface FeedbackService {

    CreateFeedbackResp saveFeedback(FeedbackRequest feedbackRequest, long userId);

    List<Feedback> getAllFeedbacks();
}
