package com.event.scope.eventScope.schelduler;

import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class EventStatusScheduler {

    private final EventRepository eventRepository;

    public EventStatusScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void updateEventStatuses() {
        LocalDateTime now = LocalDateTime.now();

        eventRepository.markPlannedFinished(EventStatus.FINISHED, EventStatus.PLANNED, now);
        eventRepository.markInProgressFinished(EventStatus.FINISHED, EventStatus.IN_PROGRESS, now);
        eventRepository.markInProgressWhenStarted(EventStatus.IN_PROGRESS, EventStatus.PLANNED, now);
    }
}
