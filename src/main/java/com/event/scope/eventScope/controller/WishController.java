package com.event.scope.eventScope.controller;

import com.event.scope.eventScope.model.Tag;
import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.service.TagService;
import com.event.scope.eventScope.service.WishService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/wish")
public class WishController {
    private final WishService wishService;
    private final TagService tagService;

    public WishController(WishService wishService,
                          TagService tagService) {

        this.wishService = wishService;
        this.tagService = tagService;
    }

    @GetMapping
    public String getAllWishes(
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) LocalDate createdDate,
            Model model,
            Principal principal
    ) {

        List<Wish> wishes =
                wishService.findAllFiltered(tagId, createdDate);

        List<Tag> tags = tagService.findAll();

        model.addAttribute("wishes", wishes);
        model.addAttribute("tags", tags);

        model.addAttribute("selectedTagId", tagId);
        model.addAttribute("selectedDate", createdDate);

        model.addAttribute("isAuth", principal != null);

        return "wishesPage";
    }
}