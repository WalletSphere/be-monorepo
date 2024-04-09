package com.khomishchak.feedbackservice.service;

import com.khomishchak.feedbackservice.model.Feedback;
import com.khomishchak.feedbackservice.model.request.FeedbackRequest;
import com.khomishchak.feedbackservice.model.response.CreateFeedbackResp;

import java.util.List;

public interface FeedbackService {

    CreateFeedbackResp saveFeedback(FeedbackRequest feedbackRequest, long userId);

    List<Feedback> getAllFeedbacks();
}
