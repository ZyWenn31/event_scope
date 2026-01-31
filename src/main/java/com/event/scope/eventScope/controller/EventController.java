package com.event.scope.eventScope.controller;


import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.service.EventParticipantService;
import com.event.scope.eventScope.service.EventService;
import com.event.scope.eventScope.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final EventParticipantService eventParticipantService;

    public EventController(EventService eventService, UserService userService, EventParticipantService eventParticipantService) {
        this.eventService = eventService;
        this.userService = userService;
        this.eventParticipantService = eventParticipantService;
    }

    @GetMapping
    public String getAllEvents(Model model, Principal principal) {
        List<Event> events = eventService.findAll();
        model.addAttribute("events", events);
        model.addAttribute("isAuth", principal != null);


        if (principal != null){
            model.addAttribute("userEventIds", eventParticipantService.getUserParticipantsEvent(userService.findByUsername(principal.getName())));
            model.addAttribute("userId", userService.findByUsername(principal.getName()).getId());
        }
        else
            model.addAttribute("userId", -1);


        return "eventsPage";
    }
}
