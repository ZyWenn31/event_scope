package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.OrganizerReview;
import com.event.scope.eventScope.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerReviewRepository extends JpaRepository<OrganizerReview, Long> {

    boolean existsByUserAndOrganizer(User user, User organizer);
}
