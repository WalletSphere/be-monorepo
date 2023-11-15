package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.model.Feedback;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.FeedbackType;
import com.khomishchak.cryptoportfolio.model.enums.RegistrationStatus;
import com.khomishchak.cryptoportfolio.model.requests.FeedbackRequest;
import com.khomishchak.cryptoportfolio.model.response.CreateFeedbackResp;
import com.khomishchak.cryptoportfolio.repositories.FeedbackRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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