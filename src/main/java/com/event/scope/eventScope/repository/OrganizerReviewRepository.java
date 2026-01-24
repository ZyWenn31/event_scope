package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.OrganizerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerReviewRepository extends JpaRepository<OrganizerReview, Long> {
}
