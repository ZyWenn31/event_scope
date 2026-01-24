package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event findById(Long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Event with id " + id +"not found"));
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    public Event update(Event event, Long id) {
        Event existing = eventRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Event with id " + id +"not found"));

        existing.setCreatedAt(event.getCreatedAt());
        existing.setDescription(event.getDescription());
        existing.setEventDate(event.getEventDate());
        existing.setOrganizer(event.getOrganizer());
        existing.setParticipants(event.getParticipants());
        existing.setRating(event.getRating());
        existing.setReviews(event.getReviews());
        existing.setStatus(event.getStatus());
        existing.setTags(event.getTags());
        existing.setTitle(event.getTitle());

        return eventRepository.save(existing);
    }

    public List<Event> findAllByStatus(String status) {
        return eventRepository.findAllByStatus(status);
    }
}
