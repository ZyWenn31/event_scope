package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.EventReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {
}
