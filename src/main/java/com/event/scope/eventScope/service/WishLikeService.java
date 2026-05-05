package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.model.WishLike;
import com.event.scope.eventScope.repository.WishLikeRepository;
import org.springframework.stereotype.Service;

@Service
public class WishLikeService {
    private final WishLikeRepository wishLikeRepository;

    public WishLikeService(WishLikeRepository wishLikeRepository) {
        this.wishLikeRepository = wishLikeRepository;
    }

    public boolean isLiked(User user, Wish wish) {
        return wishLikeRepository.existsByUserAndWish(user, wish);
    }

    public long countLikes(Wish wish) {
        return wishLikeRepository.countByWish(wish);
    }

    public void like(User user, Wish wish) {
        if (!wishLikeRepository.existsByUserAndWish(user, wish)) {
            WishLike like = new WishLike();
            like.setUser(user);
            like.setWish(wish);
            wishLikeRepository.save(like);
        }
    }

    public void unlike(User user, Wish wish) {
        wishLikeRepository.findByUserAndWish(user, wish)
                .ifPresent(wishLikeRepository::delete);
    }

    public boolean toggle(User user, Wish wish) {
        if (wishLikeRepository.existsByUserAndWish(user, wish)) {
            unlike(user, wish);
            return false;
        } else {
            like(user, wish);
            return true;
        }
    }
}
