package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findAllByTags_Id(Long tagId);
    List<Wish> findAllByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Wish> findAllByTags_IdAndCreatedAtBetween(
            Long tagId,
            LocalDateTime start,
            LocalDateTime end
    );
}
