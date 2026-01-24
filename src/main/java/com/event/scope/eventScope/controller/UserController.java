package com.event.scope.eventScope.controller;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/userProfile")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userProfile(Principal principal, Model model) {

        if  (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("userAvatar", user.getAvatarPath());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userRole", user.getRole());
        model.addAttribute("userParticipantEvents", user.getParticipations());
        model.addAttribute("participationsIsNull", user.getParticipations().isEmpty());


        if (user.getRole().equals("ORGANIZER")) {
            model.addAttribute("isOrganizer", true);
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("organizedEventsIsNull", user.getOrganizedEvents().isEmpty());
            model.addAttribute("organizedEvents", user.getOrganizedEvents());

        } else {
            model.addAttribute("isOrganizer", false);
        }

        return "userProfile";
    }
}
