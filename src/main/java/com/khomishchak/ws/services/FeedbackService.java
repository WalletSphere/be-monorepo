package com.khomishchak.ws.services;

import com.khomishchak.ws.model.Feedback;
import com.khomishchak.ws.model.requests.FeedbackRequest;
import com.khomishchak.ws.model.response.CreateFeedbackResp;

import java.util.List;

public interface FeedbackService {

    CreateFeedbackResp saveFeedback(FeedbackRequest feedbackRequest, long userId);

    List<Feedback> getAllFeedbacks();
}
