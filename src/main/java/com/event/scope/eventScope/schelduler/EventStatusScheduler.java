package com.event.scope.eventScope.schelduler;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventStatusScheduler {

    private final EventRepository eventRepository;

    public EventStatusScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void finishPastEvents() {
        List<Event> inProgress = eventRepository.findAllByStatus(EventStatus.IN_PROGRESS);

        LocalDateTime now = LocalDateTime.now();

        List<Event> toFinish = inProgress.stream()
                .filter(e -> e.getEventEndDate() != null && e.getEventEndDate().isBefore(now))
                .toList();

        toFinish.forEach(e -> e.setStatus(EventStatus.FINISHED));

        eventRepository.saveAll(toFinish);
    }

    @Scheduled(cron = "0 * * * * *")
    public void markInProgressEvents() {
        List<Event> planned = eventRepository.findAllByStatus(EventStatus.PLANNED);

        LocalDateTime now = LocalDateTime.now();

        List<Event> toStart = planned.stream()
                .filter(e -> e.getEventDate().isBefore(now))
                .toList();

        toStart.forEach(e -> e.setStatus(EventStatus.IN_PROGRESS));

        eventRepository.saveAll(toStart);
    }
}
