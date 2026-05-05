package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.model.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    // --- без фильтра по пользователю ---
    List<Wish> findDistinctByTags_IdIn(List<Long> tagIds);
    List<Wish> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Wish> findDistinctByTags_IdInAndCreatedAtBetween(List<Long> tagIds, LocalDateTime start, LocalDateTime end);

    // --- только пожелания конкретного пользователя ---
    List<Wish> findAllByUser(User user);
    List<Wish> findDistinctByUserAndTags_IdIn(User user, List<Long> tagIds);
    List<Wish> findAllByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    List<Wish> findDistinctByUserAndTags_IdInAndCreatedAtBetween(User user, List<Long> tagIds, LocalDateTime start, LocalDateTime end);
}
