package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.model.Feedback;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.requests.FeedbackRequest;
import com.khomishchak.cryptoportfolio.repositories.FeedbackRepository;

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
    public Feedback saveFeedback(FeedbackRequest feedbackRequest) {
        User user = userService.getUserById(feedbackRequest.userId());

        Feedback newFeedback = Feedback.builder()
                .feedbackType(feedbackRequest.feedbackType())
                .message(feedbackRequest.message())
                .user(user)
                .build();

        user.getFeedbacks().add(newFeedback);

        return feedbackRepository.save(newFeedback);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }
}
