package com.event.scope.eventScope.controller;


import com.event.scope.eventScope.service.EventService;
import com.event.scope.eventScope.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllEvents(Model model, Principal principal) {
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("isAuth", principal != null);

        if (principal != null)
            model.addAttribute("userId", userService.findByUsername(principal.getName()).getId());
        else
            model.addAttribute("userId", -1);
        return "eventsPage";
    }
}
