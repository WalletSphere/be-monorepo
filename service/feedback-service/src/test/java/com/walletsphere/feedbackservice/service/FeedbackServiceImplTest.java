package com.walletsphere.feedbackservice.service;

import com.walletsphere.feedbackservice.model.Feedback;
import com.walletsphere.feedbackservice.model.FeedbackType;
import com.walletsphere.feedbackservice.model.request.FeedbackRequest;
import com.walletsphere.feedbackservice.model.response.CreateFeedbackResp;
import com.walletsphere.feedbackservice.repository.FeedbackRepository;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mock;


import java.util.List;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long FEEDBACK_ID = 1L;

    @Mock
    private FeedbackRepository feedbackRepository;

    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackServiceImpl(feedbackRepository);
    }

    @ParameterizedTest
    @EnumSource(FeedbackType.class)
    void shouldSaveFeedback_andReturnSuccessfulCreationResp(FeedbackType feedbackType) {
        // given
        Feedback createdFeedback = new Feedback();
        createdFeedback.setId(FEEDBACK_ID);

        String feedbackMessage = "feedbackMessage";
        FeedbackRequest feedbackRequest = new FeedbackRequest(feedbackType, feedbackMessage);

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(createdFeedback);

        // when
        CreateFeedbackResp createFeedbackResp = feedbackService.saveFeedback(feedbackRequest, USER_ID);

        // then
        assertEquals(FEEDBACK_ID, createFeedbackResp.feedbackId());
        assertEquals(USER_ID, createFeedbackResp.userId());
    }

    @ParameterizedTest
    @EnumSource(FeedbackType.class)
    void shouldGetAllFeedbacks(FeedbackType feedbackType) {
        // given
        Feedback feedback = new Feedback(FEEDBACK_ID, feedbackType, "message", USER_ID);
        when(feedbackRepository.findAll()).thenReturn(List.of(feedback));

        // when
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();

        // then
        verify(feedbackRepository, times(1)).findAll();
        assertEquals(feedback, feedbacks.get(0));
    }
}