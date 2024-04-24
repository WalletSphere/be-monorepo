package com.walletsphere.feedbackservice.service;

import com.walletsphere.feedbackservice.model.Feedback;
import com.walletsphere.feedbackservice.model.request.FeedbackRequest;
import com.walletsphere.feedbackservice.model.response.CreateFeedbackResp;

import java.util.List;

public interface FeedbackService {

    CreateFeedbackResp saveFeedback(FeedbackRequest feedbackRequest, long userId);

    List<Feedback> getAllFeedbacks();
}
