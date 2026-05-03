package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if (event == null) {
            throw new NullPointerException("Event is null");
        }
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

    public List<Event> findAllByStatus(EventStatus status) {
        return eventRepository.findAllByStatus(status);
    }

    public List<Event> findFiltered(
            String title,
            Boolean onlyFuture,
            Boolean onlyMyParticipantEvents,
            List<Long> tagIds,
            Long myId
    ) {

        List<Event> events =
                eventRepository.findAllByStatus(
                        EventStatus.PLANNED
                );

        if (title != null && !title.isBlank()) {

            events = events.stream()

                    .filter(event ->
                            event.getTitle()
                                    .toLowerCase()
                                    .contains(title.toLowerCase()))

                    .toList();
        }

        if (onlyFuture != null && onlyFuture) {

            events = events.stream()

                    .filter(event ->
                            event.getEventDate()
                                    .isAfter(LocalDateTime.now()))

                    .toList();
        }

        if (tagIds != null && !tagIds.isEmpty()) {

            events = events.stream()

                    .filter(event ->

                            event.getTags()
                                    .stream()

                                    .anyMatch(tag ->
                                            tagIds.contains(tag.getId()))
                    )

                    .toList();
        }

        events = events.stream().filter(event -> event.getOrganizer().getId() != myId).toList();

        return events;
    }

    public List<Event> findFiltered(
            String title,
            Boolean onlyFuture,
            List<Long> tagIds
    ) {

        List<Event> events =
                eventRepository.findAllByStatus(
                        EventStatus.PLANNED
                );

        if (title != null && !title.isBlank()) {

            events = events.stream()

                    .filter(event ->
                            event.getTitle()
                                    .toLowerCase()
                                    .contains(title.toLowerCase()))

                    .toList();
        }

        if (onlyFuture != null && onlyFuture) {

            events = events.stream()

                    .filter(event ->
                            event.getEventDate()
                                    .isAfter(LocalDateTime.now()))

                    .toList();
        }

        if (tagIds != null && !tagIds.isEmpty()) {

            events = events.stream()

                    .filter(event ->

                            event.getTags()
                                    .stream()

                                    .anyMatch(tag ->
                                            tagIds.contains(tag.getId()))
                    )

                    .toList();
        }

        return events;
    }
}
