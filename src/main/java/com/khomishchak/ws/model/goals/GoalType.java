package com.khomishchak.ws.model.goals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public enum GoalType {
    DAILY_DEPOSIT_GOAL {
        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
        }

        @Override
        public LocalDateTime getStartTime(int amountOfDaysAgo) {
            return getEndTime().minusDays(amountOfDaysAgo);
        }

        @Override
        public CommonGoalType getCommonType() {
            return CommonGoalType.DEPOSIT_GOAL;
        }
    },
    WEEKLY_DEPOSIT_GOAL {
        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY)), LocalTime.MIDNIGHT);
        }

        @Override
        public LocalDateTime getStartTime(int amountOfWeeksAgo) {
            return getEndTime().minusDays(7L * amountOfWeeksAgo);
        }

        @Override
        public CommonGoalType getCommonType() {
            return CommonGoalType.DEPOSIT_GOAL;
        }
    },
    MONTHLY_DEPOSIT_GOAL {
        @Override
        public LocalDateTime getEndTime() {
            return LocalDateTime.of(LocalDate.now().plusMonths(1).withDayOfMonth(1), LocalTime.MIDNIGHT);
        }

        @Override
        public LocalDateTime getStartTime(int amountOfMonthsAgo) {
            return getEndTime().minusMonths(amountOfMonthsAgo);
        }

        @Override
        public CommonGoalType getCommonType() {
            return CommonGoalType.DEPOSIT_GOAL;
        }
    };

    public abstract LocalDateTime getEndTime();

    // 1 - start time of current goal; 2 - start time of previous goal
    public abstract LocalDateTime getStartTime(int amountOfPeriodsAgo);

    public abstract CommonGoalType getCommonType();
}
