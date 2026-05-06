package com.event.scope.eventScope.controller;


import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventReview;
import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.model.Tag;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.model.Wish;
import com.event.scope.eventScope.service.EventParticipantService;
import com.event.scope.eventScope.service.EventReviewService;
import com.event.scope.eventScope.service.EventService;
import com.event.scope.eventScope.service.TagService;
import com.event.scope.eventScope.service.UserService;
import com.event.scope.eventScope.service.WishLikeService;
import com.event.scope.eventScope.service.WishService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.event.scope.eventScope.model.EventParticipant;

import org.springframework.format.annotation.DateTimeFormat;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final EventParticipantService eventParticipantService;
    private final TagService tagService;
    private final EventReviewService eventReviewService;
    private final WishService wishService;
    private final WishLikeService wishLikeService;

    public EventController(EventService eventService, UserService userService, EventParticipantService eventParticipantService, TagService tagService, EventReviewService eventReviewService, WishService wishService, WishLikeService wishLikeService) {
        this.eventService = eventService;
        this.userService = userService;
        this.eventParticipantService = eventParticipantService;
        this.tagService = tagService;
        this.eventReviewService = eventReviewService;
        this.wishService = wishService;
        this.wishLikeService = wishLikeService;
    }

    @GetMapping
    public String getAllEvents(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String organizer,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate eventDate,
            @RequestParam(required = false, defaultValue = "false") boolean inProgress,
            Model model,
            Principal principal
    ) {

        boolean isAuth = principal != null;

        List<Event> events;

        if (isAuth) {

            User currentUser = userService.findByUsername(principal.getName());

            events = eventService.findFiltered(
                    title,
                    tagIds,
                    currentUser,
                    sortBy,
                    organizer,
                    eventDate,
                    inProgress
            );

            model.addAttribute(
                    "userEventIds",
                    eventParticipantService.getUserParticipantsEvent(currentUser)
            );

            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("username", principal.getName());

        } else {

            events = eventService.findFiltered(title, tagIds, sortBy, organizer, eventDate, inProgress);

            model.addAttribute("userId", -1);
            model.addAttribute("username", "");
        }

        model.addAttribute("events", events);
        model.addAttribute("title", title);
        model.addAttribute("selectedTags", tagIds);
        model.addAttribute("tags", tagService.findAll());
        model.addAttribute("isAuth", isAuth);
        model.addAttribute("selectedSortBy", sortBy != null ? sortBy : "DATE_ASC");
        model.addAttribute("organizer", organizer);
        model.addAttribute("eventDate", eventDate);
        model.addAttribute("inProgress", inProgress);

        return "eventsPage";
    }

    @GetMapping("/{id}")
    public String eventDetail(
            @PathVariable Long id,
            Model model,
            Principal principal
    ) {

        Event event = eventService.findById(id);

        List<EventParticipant> participants =
                eventParticipantService.getEventParticipants(event);

        boolean isCurrentUserOrganizer = principal != null &&
                event.getOrganizer().getUsername().equals(principal.getName());

        boolean canReview = false;
        boolean alreadyReviewed = false;

        if (principal != null && EventStatus.FINISHED.equals(event.getStatus())) {
            User currentUser = userService.findByUsername(principal.getName());
            boolean isParticipant = participants.stream()
                    .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()));
            if (isParticipant) {
                alreadyReviewed = eventReviewService.existsByUserAndEvent(currentUser, event);
                canReview = !alreadyReviewed;
            }
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(event.getCreatedAt(), LocalDateTime.now());

        if (event.getWish() != null) {
            model.addAttribute("wishLikeCount", wishLikeService.countLikes(event.getWish()));
            long wishDays = java.time.temporal.ChronoUnit.DAYS.between(event.getWish().getCreatedAt(), LocalDateTime.now());
            model.addAttribute("wishDaysAgo", formatDaysAgo(wishDays));
        }

        model.addAttribute("event", event);
        model.addAttribute("createdDaysAgo", formatDaysAgo(days));
        model.addAttribute("participants", participants);
        model.addAttribute("participantCount", participants.size());
        model.addAttribute("isCurrentUserOrganizer", isCurrentUserOrganizer);
        model.addAttribute("isAuth", principal != null);
        model.addAttribute("username", principal != null ? principal.getName() : "");
        model.addAttribute("canReview", canReview);
        model.addAttribute("alreadyReviewed", alreadyReviewed);

        return "eventDetailPage";
    }

    @PostMapping("/{id}/review")
    public String submitReview(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            Principal principal
    ) {

        if (principal == null) {
            return "redirect:/login";
        }

        Event event = eventService.findById(id);
        User user = userService.findByUsername(principal.getName());

        if (!EventStatus.FINISHED.equals(event.getStatus())) {
            return "redirect:/event/" + id;
        }

        boolean isParticipant = eventParticipantService.getEventParticipants(event).stream()
                .anyMatch(p -> p.getUser().getId().equals(user.getId()));

        if (!isParticipant || eventReviewService.existsByUserAndEvent(user, event)) {
            return "redirect:/event/" + id;
        }

        EventReview review = new EventReview();
        review.setUser(user);
        review.setEvent(event);
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : null);
        review.setCreatedAt(LocalDateTime.now());

        eventReviewService.save(review);

        Event updatedEvent = eventService.findById(id);
        double avg = updatedEvent.getReviews().stream()
                .mapToInt(EventReview::getRating)
                .average()
                .orElse(0.0);
        updatedEvent.setRating((int) Math.round(avg));
        eventService.save(updatedEvent);

        return "redirect:/event/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelEvent(@PathVariable Long id, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Event event = eventService.findById(id);

        if (!event.getOrganizer().getUsername().equals(principal.getName())) {
            return "redirect:/event/" + id;
        }

        if (!EventStatus.PLANNED.equals(event.getStatus())) {
            return "redirect:/event/" + id;
        }

        event.setStatus(EventStatus.CANCELED);
        eventService.save(event);

        return "redirect:/event/" + id;
    }

    @GetMapping("/create")
    public String createEventPage(
            @RequestParam(required = false) Long wishId,
            Model model
    ) {

        model.addAttribute("event", new Event());
        model.addAttribute("tags", tagService.findAll());
        model.addAttribute(
                "minDateTime",
                LocalDate.now().plusDays(1)
                        .atStartOfDay()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
        );
        model.addAttribute(
                "minEndDateTime",
                LocalDate.now().plusDays(1)
                        .atStartOfDay()
                        .plusHours(1)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
        );

        if (wishId != null) {
            var wish = wishService.findById(wishId);
            model.addAttribute("sourceWish", wish);
            model.addAttribute("wishId", wishId);
            model.addAttribute("wishLikeCount", wishLikeService.countLikes(wish));
            long wishDays = java.time.temporal.ChronoUnit.DAYS.between(wish.getCreatedAt(), LocalDateTime.now());
            model.addAttribute("wishDaysAgo", formatDaysAgo(wishDays));
        }

        return "createEventPage";
    }

    @PostMapping("/create")
    public String createEvent(
            @Valid @ModelAttribute("event") Event event,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) Long wishId,
            Principal principal,
            Model model
    ) {

        if (event.getEventDate() != null &&
                event.getEventDate().toLocalDate()
                        .isBefore(LocalDate.now().plusDays(1))) {

            bindingResult.rejectValue(
                    "eventDate",
                    "eventDate.tooSoon",
                    "Мероприятие можно запланировать не раньше завтрашнего дня"
            );
        }

        if (event.getEventDate() != null && event.getEventEndDate() != null &&
                event.getEventEndDate().isBefore(event.getEventDate().plusHours(1))) {

            bindingResult.rejectValue(
                    "eventEndDate",
                    "eventEndDate.tooSoon",
                    "Дата окончания должна быть не раньше чем через час от начала"
            );
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("tags", tagService.findAll());
            model.addAttribute(
                    "minDateTime",
                    LocalDate.now().plusDays(1)
                            .atStartOfDay()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
            );
            model.addAttribute(
                    "minEndDateTime",
                    LocalDate.now().plusDays(1)
                            .atStartOfDay()
                            .plusHours(1)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
            );
            if (wishId != null) {
                model.addAttribute("sourceWish", wishService.findById(wishId));
                model.addAttribute("wishId", wishId);
            }

            return "createEventPage";
        }

        User organizer = userService.findByUsername(principal.getName());

        event.setOrganizer(organizer);
        event.setCreatedAt(LocalDateTime.now());
        event.setStatus(EventStatus.PLANNED);
        event.setRating(0);

        Set<Tag> tags = new HashSet<>();
        if (tagIds != null) {
            tags = tagService.findAllByIds(tagIds);
        }
        event.setTags(tags);

        if (wishId != null) {
            event.setWish(wishService.findById(wishId));
        }

        eventService.save(event);

        return "redirect:/event";
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
