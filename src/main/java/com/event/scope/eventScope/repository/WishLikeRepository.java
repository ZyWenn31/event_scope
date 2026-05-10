package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.model.WishLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishLikeRepository extends JpaRepository<WishLike, Long> {
    boolean existsByUserAndWish(User user, Wish wish);

    Optional<WishLike> findByUserAndWish(User user, Wish wish);

    long countByWish(Wish wish);
}
