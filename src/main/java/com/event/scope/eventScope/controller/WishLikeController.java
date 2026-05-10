package com.event.scope.eventScope.controller;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.service.UserService;
import com.event.scope.eventScope.service.WishLikeService;
import com.event.scope.eventScope.service.WishService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WishLikeController {
    private final WishLikeService wishLikeService;
    private final WishService wishService;
    private final UserService userService;

    public WishLikeController(WishLikeService wishLikeService,
                              WishService wishService,
                              UserService userService) {
        this.wishLikeService = wishLikeService;
        this.wishService = wishService;
        this.userService = userService;
    }

    @PostMapping("/wish/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestParam Long wishId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        Wish wish = wishService.findById(wishId);

        boolean liked = wishLikeService.toggle(user, wish);
        long count = wishLikeService.countLikes(wish);

        return ResponseEntity.ok(Map.of("liked", liked, "count", count));
    }
}
