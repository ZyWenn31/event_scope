package com.event.scope.eventScope.controller;

import com.event.scope.eventScope.model.Tag;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.service.TagService;
import com.event.scope.eventScope.service.UserService;
import com.event.scope.eventScope.service.WishService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/wish")
public class WishController {
    private final WishService wishService;
    private final TagService tagService;
    private final UserService userService;

    public WishController(WishService wishService,
                          TagService tagService, UserService userService) {

        this.wishService = wishService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllWishes(
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) LocalDate createdDate,
            Model model,
            Principal principal
    ) {

        List<Wish> wishes =
                wishService.findAllFiltered(tagIds, createdDate);

        List<Tag> tags = tagService.findAll();

        model.addAttribute("wishes", wishes);
        model.addAttribute("tags", tags);

        model.addAttribute("selectedTags", tagIds);
        model.addAttribute("selectedDate", createdDate);

        model.addAttribute("isAuth", principal != null);
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

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
}