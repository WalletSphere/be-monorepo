package com.walletsphere.goalsservice.service.impl;

import com.walletsphere.goalsservice.model.CommonGoalType;
import com.walletsphere.goalsservice.model.SelfGoal;
import com.walletsphere.goalsservice.service.SelfGoalValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class DepositSelfGoalValidator implements SelfGoalValidator {

    private String getDepositAmountUrl;

    private final static CommonGoalType GOAL_TYPE = CommonGoalType.DEPOSIT_GOAL;

    @Value("${ws.exchanger.deposit.amount.url:http://localhost:8080/balances/transactions-history/period}")
    public void setGetDepositAmountUrl(String getDepositAmountUrl) {
        this.getDepositAmountUrl = getDepositAmountUrl;
    }

    private final RestTemplate restTemplate;

    public DepositSelfGoalValidator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public CommonGoalType getCommonGoalType() {
        return GOAL_TYPE;
    }

    @Override
    public boolean isAchieved(SelfGoal goal) {
        double depositValue = getDepositValueForPeriod(goal.getUserId(), goal.getTicker());
        goal.setCurrentAmount(depositValue);
        return depositValue > goal.getGoalAmount();
    }

    private Double getDepositValueForPeriod(long userId, String ticker) {
        return restTemplate.getForObject(getDepositAmountUrl, Double.class, userId, ticker);
    }


}
