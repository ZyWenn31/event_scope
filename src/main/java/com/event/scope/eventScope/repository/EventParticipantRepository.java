package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventParticipant;
import com.event.scope.eventScope.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    boolean existsByUserAndEvent(User user, Event event);
    Optional<EventParticipant> findByUserAndEvent(User user, Event event);

    List<EventParticipant> findAllByUser(User user);
}
