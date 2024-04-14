package com.walletsphere.feedbackservice.service;

import com.walletsphere.feedbackservice.model.Feedback;
import com.walletsphere.feedbackservice.model.request.FeedbackRequest;
import com.walletsphere.feedbackservice.model.response.CreateFeedbackResp;
import com.walletsphere.feedbackservice.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public CreateFeedbackResp saveFeedback(FeedbackRequest feedbackRequest, long userId) {
        Feedback newFeedback = Feedback.builder()
                .feedbackType(feedbackRequest.feedbackType())
                .message(feedbackRequest.message())
                .userId(userId)
                .build();

        return new CreateFeedbackResp(userId, feedbackRepository.save(newFeedback).getId());
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }
}
