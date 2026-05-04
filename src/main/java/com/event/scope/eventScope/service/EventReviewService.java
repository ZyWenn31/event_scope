package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventReview;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.repository.EventReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventReviewService {
    private final EventReviewRepository eventReviewRepository;

    public EventReviewService(EventReviewRepository eventReviewRepository) {
        this.eventReviewRepository = eventReviewRepository;
    }

    public EventReview findById(Long id) {
        return eventReviewRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Event review with id " + id + " not found"));
    }

    public List<EventReview> findAll() {
        return eventReviewRepository.findAll();
    }

    public EventReview save(EventReview eventReview) {
        return eventReviewRepository.save(eventReview);
    }

    public boolean existsByUserAndEvent(User user, Event event) {
        return eventReviewRepository.existsByUserAndEvent(user, event);
    }

    public void deleteById(Long id) {
        eventReviewRepository.deleteById(id);
    }

    public EventReview update(EventReview eventReview, Long id) {
        EventReview existing = eventReviewRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Event review with id " + id + " not found"));

        existing.setEvent(eventReview.getEvent());
        existing.setCreatedAt(eventReview.getCreatedAt());
        existing.setUser(eventReview.getUser());
        existing.setComment(eventReview.getComment());
        existing.setRating(eventReview.getRating());

        return eventReviewRepository.save(existing);
    }
}
