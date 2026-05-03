package com.event.scope.eventScope.controller;


import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.model.Tag;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.service.EventParticipantService;
import com.event.scope.eventScope.service.EventService;
import com.event.scope.eventScope.service.TagService;
import com.event.scope.eventScope.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.event.scope.eventScope.model.EventParticipant;

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

    public EventController(EventService eventService, UserService userService, EventParticipantService eventParticipantService, TagService tagService) {
        this.eventService = eventService;
        this.userService = userService;
        this.eventParticipantService = eventParticipantService;
        this.tagService = tagService;
    }

    @GetMapping
    public String getAllEvents(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean onlyFuture,
            @RequestParam(required = false) Boolean onlyMyParticipantEvents,
            @RequestParam(required = false) List<Long> tagIds,
            Model model,
            Principal principal
    ) {

        boolean isAuth = principal != null;

        List<Event> events;

        if (isAuth) {

            Long myId = userService.findByUsername(principal.getName()).getId();

            events = eventService.findFiltered(
                    title,
                    onlyFuture,
                    onlyMyParticipantEvents,
                    tagIds,
                    myId
            );

            model.addAttribute(
                    "userEventIds",
                    eventParticipantService.getUserParticipantsEvent(
                            userService.findByUsername(principal.getName())
                    )
            );

            model.addAttribute("userId", myId);
            model.addAttribute("username", principal.getName());

        } else {

            events = eventService.findFiltered(title, onlyFuture, tagIds);

            model.addAttribute("userId", -1);
            model.addAttribute("username", "");
        }

        model.addAttribute("events", events);
        model.addAttribute("title", title);
        model.addAttribute("onlyFuture", onlyFuture != null && onlyFuture);
        model.addAttribute("onlyMyParticipantEvents", onlyMyParticipantEvents != null && onlyMyParticipantEvents);
        model.addAttribute("selectedTags", tagIds);
        model.addAttribute("tags", tagService.findAll());
        model.addAttribute("isAuth", isAuth);

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

        model.addAttribute("event", event);
        model.addAttribute("participants", participants);
        model.addAttribute("participantCount", participants.size());
        model.addAttribute("isCurrentUserOrganizer", isCurrentUserOrganizer);
        model.addAttribute("isAuth", principal != null);
        model.addAttribute("username", principal != null ? principal.getName() : "");

        return "eventDetailPage";
    }

    @GetMapping("/create")
    public String createEventPage(Model model) {

        model.addAttribute("event", new Event());

        model.addAttribute("tags", tagService.findAll());

        model.addAttribute(
                "minDateTime",
                LocalDate.now().plusDays(1)
                        .atStartOfDay()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
        );

        return "createEventPage";
    }

    @PostMapping("/create")
    public String createEvent(
            @Valid @ModelAttribute("event") Event event,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> tagIds,
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

        if (bindingResult.hasErrors()) {

            model.addAttribute("tags", tagService.findAll());

            model.addAttribute(
                    "minDateTime",
                    LocalDate.now().plusDays(1)
                            .atStartOfDay()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
            );

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

        eventService.save(event);

        return "redirect:/event";
    }
}
