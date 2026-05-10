package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.OrganizerReview;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.repository.OrganizerReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerReviewService {
    private final OrganizerReviewRepository organizerReviewRepository;

    public OrganizerReviewService(OrganizerReviewRepository organizerReviewRepository) {
        this.organizerReviewRepository = organizerReviewRepository;
    }

    public OrganizerReview findById(Long id) {
        return organizerReviewRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Organizer review with id " + id + "not found"));
    }

    public List<OrganizerReview> findAll() {
        return organizerReviewRepository.findAll();
    }

    public boolean existsByUserAndOrganizer(User user, User organizer) {
        return organizerReviewRepository.existsByUserAndOrganizer(user, organizer);
    }

    public OrganizerReview save(OrganizerReview organizerReview) {
        return organizerReviewRepository.save(organizerReview);
    }

    public void deleteById(Long id) {
        organizerReviewRepository.deleteById(id);
    }

    public OrganizerReview update(OrganizerReview organizerReview, Long id) {
        OrganizerReview existing = organizerReviewRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Organizer review with id " + id + "not found"));


        existing.setOrganizer(organizerReview.getOrganizer());
        existing.setCreatedAt(organizerReview.getCreatedAt());
        existing.setUser(organizerReview.getUser());
        existing.setRating(organizerReview.getRating());
        existing.setComment(organizerReview.getComment());

        return organizerReviewRepository.save(existing);
    }
}
