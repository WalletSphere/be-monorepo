package com.khomishchak.cryptoportfolio.services.scheduled;

import org.springframework.scheduling.annotation.Scheduled;

public interface ScheduledService {

    void doAtTheStartOfTheDay();
}
