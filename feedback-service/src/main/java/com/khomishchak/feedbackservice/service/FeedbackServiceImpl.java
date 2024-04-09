package com.khomishchak.feedbackservice.service;

import com.khomishchak.feedbackservice.model.Feedback;
import com.khomishchak.feedbackservice.model.request.FeedbackRequest;
import com.khomishchak.feedbackservice.model.response.CreateFeedbackResp;
import com.khomishchak.feedbackservice.repository.FeedbackRepository;
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
