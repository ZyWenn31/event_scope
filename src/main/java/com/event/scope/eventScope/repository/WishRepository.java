package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findDistinctByTags_IdIn(List<Long> tagIds);
    List<Wish> findAllByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Wish> findDistinctByTags_IdInAndCreatedAtBetween(
            List<Long> tagIds,
            LocalDateTime start,
            LocalDateTime end
    );
}
