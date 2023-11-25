package com.khomishchak.ws.services;

import com.khomishchak.ws.model.Feedback;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.RegistrationStatus;
import com.khomishchak.ws.model.requests.FeedbackRequest;
import com.khomishchak.ws.model.response.CreateFeedbackResp;
import com.khomishchak.ws.repositories.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserService userService;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, UserService userService) {
        this.feedbackRepository = feedbackRepository;
        this.userService = userService;
    }

    @Override
    public CreateFeedbackResp saveFeedback(FeedbackRequest feedbackRequest, long userId) {
        User user = userService.getUserById(userId);

        Feedback newFeedback = Feedback.builder()
                .feedbackType(feedbackRequest.feedbackType())
                .message(feedbackRequest.message())
                .user(user)
                .build();

        user.getFeedbacks().add(newFeedback);

        return new CreateFeedbackResp(user.getId(), feedbackRepository.save(newFeedback).getId(), RegistrationStatus.SUCCESSFUL);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }
}
