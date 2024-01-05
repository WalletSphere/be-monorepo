package com.khomishchak.ws.controllers;

import com.khomishchak.ws.model.Feedback;
import com.khomishchak.ws.model.requests.FeedbackRequest;
import com.khomishchak.ws.model.response.CreateFeedbackResp;
import com.khomishchak.ws.services.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<CreateFeedbackResp> createFeedback(@RequestBody FeedbackRequest feedbackRequest,
                                                             @RequestHeader("UserId") long userId) {
        return new ResponseEntity<>(feedbackService.saveFeedback(feedbackRequest, userId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return new ResponseEntity<>(feedbackService.getAllFeedbacks(), HttpStatus.OK);
    }
}
