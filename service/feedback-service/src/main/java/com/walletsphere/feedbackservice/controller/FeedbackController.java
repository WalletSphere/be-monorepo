package com.walletsphere.feedbackservice.controller;

import com.walletsphere.feedbackservice.model.Feedback;
import com.walletsphere.feedbackservice.model.request.FeedbackRequest;
import com.walletsphere.feedbackservice.model.response.CreateFeedbackResp;
import com.walletsphere.feedbackservice.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
