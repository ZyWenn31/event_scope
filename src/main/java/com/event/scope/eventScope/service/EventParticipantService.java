package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventParticipant;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.repository.EventParticipantRepository;
import com.event.scope.eventScope.repository.EventRepository;
import com.event.scope.eventScope.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventParticipantService {
    private final EventParticipantRepository eventParticipantRepository;
    private final UserRepository userRepository;
    private final EventRepository  eventRepository;


    public EventParticipantService(EventParticipantRepository eventParticipantRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public EventParticipant findById(Long id) {
        return eventParticipantRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Event participant with id " + id + " not found"));
    }

    public List<EventParticipant> findAll() {
        return eventParticipantRepository.findAll();
    }

    public EventParticipant save(EventParticipant eventParticipant) {
        return eventParticipantRepository.save(eventParticipant);
    }

    public void deleteById(Long id) {
        eventParticipantRepository.deleteById(id);
    }

    public EventParticipant update(Long id, EventParticipant updatedParticipant) {
        EventParticipant existing = eventParticipantRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Event participant with id " + id + " not found"));


        existing.setUser(updatedParticipant.getUser());
        existing.setEvent(updatedParticipant.getEvent());
        existing.setCreatedAt(updatedParticipant.getCreatedAt());

        return eventParticipantRepository.save(existing);
    }

    public void addParticipant(Long eventId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        Event event = eventRepository.findById(eventId)
                .orElseThrow();

        if (eventParticipantRepository.existsByUserAndEvent(user, event)) {
            return;
        }

        EventParticipant participant = new EventParticipant();
        participant.setUser(user);
        participant.setEvent(event);
        participant.setCreatedAt(LocalDateTime.now());

        eventParticipantRepository.save(participant);
    }

    public void removeParticipant(Long eventId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();

        eventParticipantRepository.findByUserAndEvent(user, event)
                .ifPresent(eventParticipantRepository::delete);
    }

    public Set<Long> getUserParticipantsEvent(User user){
        Set<Long> userEventIds = new HashSet<>();

        if (user != null) {
            userEventIds = eventParticipantRepository.findAllByUser(user)
                    .stream()
                    .map(ep -> ep.getEvent().getId())
                    .collect(Collectors.toSet());
        }

        return userEventIds;
    }
}
