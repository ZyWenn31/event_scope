package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByStatus(EventStatus status);
    List<Event> findAllByStatusIn(List<EventStatus> statuses);

    @Modifying
    @Query("UPDATE Event e SET e.status = :finished WHERE e.status IN (:planned, :inProgress) AND e.eventEndDate IS NOT NULL AND e.eventEndDate < :now")
    void markFinishedWhenEndDatePassed(
            @Param("finished") EventStatus finished,
            @Param("planned") EventStatus planned,
            @Param("inProgress") EventStatus inProgress,
            @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("UPDATE Event e SET e.status = :inProgress WHERE e.status = :planned AND e.eventDate < :now AND (e.eventEndDate IS NULL OR e.eventEndDate >= :now)")
    void markInProgressWhenStarted(
            @Param("inProgress") EventStatus inProgress,
            @Param("planned") EventStatus planned,
            @Param("now") LocalDateTime now
    );
}
