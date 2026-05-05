package com.event.scope.eventScope.controller;

import com.event.scope.eventScope.model.Tag;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.service.TagService;
import com.event.scope.eventScope.service.UserService;
import com.event.scope.eventScope.service.WishLikeService;
import com.event.scope.eventScope.service.WishService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/wish")
public class WishController {
    private final WishService wishService;
    private final TagService tagService;
    private final UserService userService;
    private final WishLikeService wishLikeService;

    public WishController(WishService wishService,
                          TagService tagService,
                          UserService userService,
                          WishLikeService wishLikeService) {

        this.wishService = wishService;
        this.tagService = tagService;
        this.userService = userService;
        this.wishLikeService = wishLikeService;
    }

    @GetMapping
    public String getAllWishes(
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) LocalDate createdDate,
            @RequestParam(required = false, defaultValue = "false") boolean onlyMine,
            Model model,
            Principal principal
    ) {

        boolean isAuth = principal != null;
        boolean isOrganizer = false;
        User currentUser = null;
        Set<Long> likedWishIds = new HashSet<>();

        if (isAuth) {
            currentUser = userService.findByUsername(principal.getName());
            isOrganizer = "ORGANIZER".equals(currentUser.getRole());
            model.addAttribute("username", principal.getName());
        }

        User filterUser = (onlyMine && isAuth) ? currentUser : null;

        List<Wish> wishes = wishService.findAllFiltered(tagIds, createdDate, filterUser);

        Map<Long, Long> likeCounts = new HashMap<>();
        Map<Long, String> daysAgoMap = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        for (Wish wish : wishes) {
            likeCounts.put(wish.getId(), wishLikeService.countLikes(wish));
            daysAgoMap.put(wish.getId(), formatDaysAgo(ChronoUnit.DAYS.between(wish.getCreatedAt(), now)));
        }

        if (isAuth) {
            final User u = currentUser;
            likedWishIds = wishes.stream()
                    .filter(w -> wishLikeService.isLiked(u, w))
                    .map(Wish::getId)
                    .collect(Collectors.toSet());
        }

        model.addAttribute("wishes", wishes);
        model.addAttribute("tags", tagService.findAll());
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("daysAgoMap", daysAgoMap);
        model.addAttribute("likedWishIds", likedWishIds);

        model.addAttribute("selectedTags", tagIds);
        model.addAttribute("selectedDate", createdDate);
        model.addAttribute("onlyMine", onlyMine);

        model.addAttribute("isAuth", isAuth);
        model.addAttribute("isOrganizer", isOrganizer);

        return "wishesPage";
    }

    @GetMapping("/create")
    public String createWishPage(Model model, Principal principal) {

        model.addAttribute("wish",
                new Wish());

        model.addAttribute("tags",
                tagService.findAll());



        return "createWishPage";
    }


    @PostMapping("/create")
    public String createWish(
            @Valid @ModelAttribute("wish") Wish wish,
            BindingResult bindingResult,
            @RequestParam(required = false)
            List<Long> tagIds,
            Principal principal,
            Model model
    ) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "tags",
                    tagService.findAll()
            );

            return "createWishPage";
        }

        User user =
                userService.findByUsername(
                        principal.getName()
                );

        wish.setUser(user);

        wish.setCreatedAt(
                LocalDateTime.now()
        );

        Set<Tag> tags = new HashSet<>();

        if (tagIds != null) {

            tags = tagService
                    .findAllByIds(tagIds);
        }

        wish.setTags(tags);

        wishService.save(wish);

        model.addAttribute("username", principal.getName());

        return "redirect:/wish";
    }

    private String formatDaysAgo(long days) {
        if (days == 0) return "Сегодня";
        if (days == 1) return "Вчера";
        long mod10 = days % 10;
        long mod100 = days % 100;
        String word;
        if (mod100 >= 11 && mod100 <= 19) {
            word = "дней";
        } else if (mod10 == 1) {
            word = "день";
        } else if (mod10 >= 2 && mod10 <= 4) {
            word = "дня";
        } else {
            word = "дней";
        }
        return days + " " + word + " назад";
    }
}