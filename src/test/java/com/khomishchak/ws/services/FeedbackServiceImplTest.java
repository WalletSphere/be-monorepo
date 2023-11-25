package com.khomishchak.ws.services;

import com.khomishchak.ws.model.Feedback;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.FeedbackType;
import com.khomishchak.ws.model.enums.RegistrationStatus;
import com.khomishchak.ws.model.requests.FeedbackRequest;
import com.khomishchak.ws.model.response.CreateFeedbackResp;
import com.khomishchak.ws.repositories.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long FEEDBACK_ID = 1L;

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private UserService userService;

    private FeedbackService feedbackService;

    User testUser;

    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackServiceImpl(feedbackRepository, userService);
    }

    @ParameterizedTest
    @EnumSource(FeedbackType.class)
    void shouldSaveFeedback_andReturnSuccessfulCreationResp(FeedbackType feedbackType) {
        // given
        testUser = new User();
        testUser.setId(USER_ID);

        Feedback createdFeedback = new Feedback();
        createdFeedback.setId(FEEDBACK_ID);

        String feedbackMessage = "feedbackMessage";
        FeedbackRequest feedbackRequest = new FeedbackRequest(feedbackType, feedbackMessage);

        when(userService.getUserById(eq(USER_ID))).thenReturn(testUser);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(createdFeedback);

        // when
        CreateFeedbackResp createFeedbackResp = feedbackService.saveFeedback(feedbackRequest, USER_ID);

        // then
        assertThat(createFeedbackResp.status()).isEqualTo(RegistrationStatus.SUCCESSFUL);
        assertThat(createFeedbackResp.feedbackId()).isEqualTo(FEEDBACK_ID);
        assertThat(createFeedbackResp.userId()).isEqualTo(USER_ID);
    }

    @ParameterizedTest
    @EnumSource(FeedbackType.class)
    void shouldGetAllFeedbacks(FeedbackType feedbackType) {
        // given
        Feedback feedback = new Feedback(FEEDBACK_ID, feedbackType, "message", new User());
        when(feedbackRepository.findAll()).thenReturn(List.of(feedback));

        // when
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();

        // then
        verify(feedbackRepository, times(1)).findAll();
        assertThat(feedbacks.get(0)).isEqualTo(feedback);
    }
}