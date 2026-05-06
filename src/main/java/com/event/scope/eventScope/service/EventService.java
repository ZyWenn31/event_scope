package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.model.Tag;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.event.scope.eventScope.model.EventStatus.IN_PROGRESS;
import static com.event.scope.eventScope.model.EventStatus.PLANNED;

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
            List<Long> tagIds,
            User user,
            String sortBy,
            String organizer,
            LocalDate eventDate,
            boolean inProgress
    ) {

        List<Event> events = inProgress
                ? eventRepository.findAllByStatusIn(List.of(IN_PROGRESS))
                : eventRepository.findAllByStatusIn(List.of(PLANNED, IN_PROGRESS));

        if (title != null && !title.isBlank()) {
            events = events.stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .toList();
        }

        if (organizer != null && !organizer.isBlank()) {
            events = events.stream()
                    .filter(e -> e.getOrganizer().getName() != null &&
                            e.getOrganizer().getName().toLowerCase().contains(organizer.toLowerCase()))
                    .toList();
        }

        if (eventDate != null) {
            LocalDateTime from = eventDate.atStartOfDay();
            LocalDateTime to = eventDate.atTime(23, 59, 59);
            events = events.stream()
                    .filter(e -> !e.getEventDate().isBefore(from) && !e.getEventDate().isAfter(to))
                    .toList();
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            events = events.stream()
                    .filter(e -> e.getTags().stream().anyMatch(tag -> tagIds.contains(tag.getId())))
                    .toList();
        }

        events = events.stream().filter(e -> !e.getOrganizer().getId().equals(user.getId())).toList();

        Set<Long> userTagIds = user.getParticipations().stream()
                .flatMap(p -> p.getEvent().getTags().stream())
                .map(Tag::getId)
                .collect(Collectors.toSet());

        if (!userTagIds.isEmpty()) {
            events = sortByTagOverlap(events, userTagIds);
        } else {
            events = applySorting(events, sortBy);
        }

        return events;
    }

    public List<Event> findFiltered(
            String title,
            List<Long> tagIds,
            String sortBy,
            String organizer,
            LocalDate eventDate,
            boolean inProgress
    ) {

        List<Event> events = inProgress
                ? eventRepository.findAllByStatusIn(List.of(IN_PROGRESS))
                : eventRepository.findAllByStatusIn(List.of(PLANNED, IN_PROGRESS));

        if (title != null && !title.isBlank()) {
            events = events.stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .toList();
        }

        if (organizer != null && !organizer.isBlank()) {
            events = events.stream()
                    .filter(e -> e.getOrganizer().getName() != null &&
                            e.getOrganizer().getName().toLowerCase().contains(organizer.toLowerCase()))
                    .toList();
        }

        if (eventDate != null) {
            LocalDateTime from = eventDate.atStartOfDay();
            LocalDateTime to = eventDate.atTime(23, 59, 59);
            events = events.stream()
                    .filter(e -> !e.getEventDate().isBefore(from) && !e.getEventDate().isAfter(to))
                    .toList();
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            events = events.stream()
                    .filter(e -> e.getTags().stream().anyMatch(tag -> tagIds.contains(tag.getId())))
                    .toList();
        }

        events = applySorting(events, sortBy);

        return events;
    }

    private List<Event> sortByTagOverlap(List<Event> events, Set<Long> userTagIds) {
        return events.stream()
                .sorted(Comparator
                        .<Event, Long>comparing(
                                e -> e.getTags().stream()
                                        .filter(t -> userTagIds.contains(t.getId()))
                                        .count(),
                                Comparator.reverseOrder())
                        .thenComparing(
                                e -> e.getParticipants() != null ? e.getParticipants().size() : 0,
                                Comparator.reverseOrder())
                        .thenComparing(Event::getCreatedAt, Comparator.reverseOrder()))
                .toList();
    }

    private List<Event> applySorting(List<Event> events, String sortBy) {
        return events.stream()
                .sorted(Comparator
                        .<Event, Integer>comparing(
                                e -> e.getParticipants() != null ? e.getParticipants().size() : 0,
                                Comparator.reverseOrder())
                        .thenComparing(Event::getCreatedAt, Comparator.reverseOrder()))
                .toList();
    }
}
