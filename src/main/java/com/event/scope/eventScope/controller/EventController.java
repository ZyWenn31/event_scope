package com.event.scope.eventScope.controller;


import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.service.EventParticipantService;
import com.event.scope.eventScope.service.EventService;
import com.event.scope.eventScope.service.TagService;
import com.event.scope.eventScope.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

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

        List<Event> events =
                eventService.findFiltered(
                        title,
                        onlyFuture,
                        onlyMyParticipantEvents,
                        tagIds
                );

        model.addAttribute("events", events);

        model.addAttribute("title", title);

        model.addAttribute("onlyFuture",
                onlyFuture != null && onlyFuture);

        model.addAttribute("onlyMyParticipantEvents",
                onlyMyParticipantEvents != null && onlyMyParticipantEvents);

        model.addAttribute("selectedTags", tagIds);

        model.addAttribute("tags",
                tagService.findAll());

        model.addAttribute("isAuth",
                principal != null);

        if (principal != null){

            model.addAttribute(
                    "userEventIds",

                    eventParticipantService
                            .getUserParticipantsEvent(

                                    userService.findByUsername(
                                            principal.getName()
                                    )
                            )
            );

            model.addAttribute(
                    "userId",

                    userService.findByUsername(
                            principal.getName()
                    ).getId()
            );
        }
        else {
            model.addAttribute("userId", -1);
        }

        return "eventsPage";
    }
}
