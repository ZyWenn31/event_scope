package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.EventParticipant;
import com.event.scope.eventScope.repository.EventParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventParticipantService {
    private final EventParticipantRepository eventParticipantRepository;

    public EventParticipantService(EventParticipantRepository eventParticipantRepository) {
        this.eventParticipantRepository = eventParticipantRepository;
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
}
