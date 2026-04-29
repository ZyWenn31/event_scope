package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByStatus(EventStatus status);
}
