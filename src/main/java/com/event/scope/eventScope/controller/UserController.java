package com.event.scope.eventScope.controller;

import com.event.scope.eventScope.model.EventParticipant;
import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.model.OrganizerReview;
import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.service.OrganizerReviewService;
import com.event.scope.eventScope.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserController {
    private final UserService userService;
    private final OrganizerReviewService organizerReviewService;

    public UserController(UserService userService, OrganizerReviewService organizerReviewService) {
        this.userService = userService;
        this.organizerReviewService = organizerReviewService;
    }

    @GetMapping("/userProfile")
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
            double averageRating =
                    user.getReceivedReviews()
                            .stream()
                            .mapToInt(review -> review.getRating())
                            .average()
                            .orElse(0.0);

            model.addAttribute(
                    "organizerRating",
                    String.format("%.1f", averageRating)
            );

            model.addAttribute(
                    "organizerReviews",
                    user.getReceivedReviews()
            );
            model.addAttribute("isOrganizer", true);
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("organizedEventsIsNull", user.getOrganizedEvents().isEmpty());
            model.addAttribute("organizedEvents", user.getOrganizedEvents());

        } else {
            model.addAttribute("isOrganizer", false);
            boolean hasEmail = user.getEmail() != null && !user.getEmail().isBlank();
            model.addAttribute("hasEmail", hasEmail);
            if (hasEmail) {
                model.addAttribute("userEmail", user.getEmail());
            }
            boolean hasName = user.getName() != null && !user.getName().isBlank();
            model.addAttribute("hasName", hasName);
            if (hasName) {
                model.addAttribute("userName", user.getName());
            }
        }

        return "userProfile";
    }

    @GetMapping("/userProfile/{username}")
    public String publicProfile(
            @PathVariable String username,
            Model model,
            Principal principal
    ) {

        User user = userService.findByUsername(username);

        boolean isOrganizer = "ORGANIZER".equals(user.getRole());

        List<EventParticipant> allParticipations = user.getParticipations();

        List<EventParticipant> plannedParticipations = allParticipations.stream()
                .filter(p -> EventStatus.PLANNED.equals(p.getEvent().getStatus()))
                .toList();

        model.addAttribute("profileUser", user);
        model.addAttribute("isOrganizer", isOrganizer);
        model.addAttribute("allParticipations", allParticipations);
        model.addAttribute("plannedParticipations", plannedParticipations);

        if (isOrganizer) {

            double avgRating = user.getReceivedReviews().stream()
                    .mapToInt(r -> r.getRating())
                    .average()
                    .orElse(0.0);

            model.addAttribute("organizerRating", String.format("%.1f", avgRating));
            model.addAttribute("organizerReviews", user.getReceivedReviews());
            model.addAttribute("organizedEvents", user.getOrganizedEvents());
        }

        model.addAttribute("isAuth", principal != null);
        model.addAttribute("currentUsername", principal != null ? principal.getName() : "");

        boolean canReviewOrganizer = false;
        boolean alreadyReviewedOrganizer = false;

        if (principal != null && isOrganizer && !principal.getName().equals(username)) {
            User viewer = userService.findByUsername(principal.getName());

            long finishedAttended = user.getOrganizedEvents().stream()
                    .filter(e -> EventStatus.FINISHED.equals(e.getStatus()))
                    .filter(e -> e.getParticipants().stream()
                            .anyMatch(p -> p.getUser().getId().equals(viewer.getId())))
                    .count();

            if (finishedAttended >= 2) {
                alreadyReviewedOrganizer = organizerReviewService.existsByUserAndOrganizer(viewer, user);
                canReviewOrganizer = !alreadyReviewedOrganizer;
            }
        }

        model.addAttribute("canReviewOrganizer", canReviewOrganizer);
        model.addAttribute("alreadyReviewedOrganizer", alreadyReviewedOrganizer);

        return "publicUserProfile";
    }

    @PostMapping("/userProfile/{username}/review")
    public String submitOrganizerReview(
            @PathVariable String username,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            Principal principal
    ) {

        if (principal == null) {
            return "redirect:/login";
        }

        User organizer = userService.findByUsername(username);
        User viewer = userService.findByUsername(principal.getName());

        if (!"ORGANIZER".equals(organizer.getRole()) || principal.getName().equals(username)) {
            return "redirect:/userProfile/" + username;
        }

        long finishedAttended = organizer.getOrganizedEvents().stream()
                .filter(e -> EventStatus.FINISHED.equals(e.getStatus()))
                .filter(e -> e.getParticipants().stream()
                        .anyMatch(p -> p.getUser().getId().equals(viewer.getId())))
                .count();

        if (finishedAttended < 2 || organizerReviewService.existsByUserAndOrganizer(viewer, organizer)) {
            return "redirect:/userProfile/" + username;
        }

        OrganizerReview review = new OrganizerReview();
        review.setUser(viewer);
        review.setOrganizer(organizer);
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : null);
        review.setCreatedAt(LocalDateTime.now());

        organizerReviewService.save(review);

        return "redirect:/userProfile/" + username;
    }

    @GetMapping("/makeMeOrganizer")
    public String makeMeOrganizer(Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());

        if ("ORGANIZER".equals(user.getRole())) {
            return "redirect:/userProfile";
        }

        boolean hasName = user.getName() != null && !user.getName().isBlank();
        boolean hasEmail = user.getEmail() != null && !user.getEmail().isBlank();

        if (!hasName && !hasEmail) {
            return "redirect:/userProfile?orgError=both";
        }
        if (!hasName) {
            return "redirect:/userProfile?orgError=name";
        }
        if (!hasEmail) {
            return "redirect:/userProfile?orgError=email";
        }

        userService.promoteToOrganizer(user);

        return "redirect:/userProfile";
    }

    @PostMapping("/userProfile/setEmail")
    public String setEmail(
            @RequestParam String email,
            Principal principal
    ) {

        if (principal == null) {
            return "redirect:/login";
        }

        String trimmed = email == null ? "" : email.trim();

        if (!trimmed.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "redirect:/userProfile?emailError=invalid";
        }

        if (userService.isEmailTaken(trimmed)) {
            return "redirect:/userProfile?emailError=taken";
        }

        userService.setEmail(userService.findByUsername(principal.getName()), trimmed);

        return "redirect:/userProfile?emailSaved=true";
    }

    @PostMapping("/userProfile/setName")
    public String setName(
            @RequestParam String name,
            Principal principal
    ) {

        if (principal == null) {
            return "redirect:/login";
        }

        String trimmed = name == null ? "" : name.trim();

        if (trimmed.isBlank() || trimmed.length() < 5 || trimmed.length() > 60) {
            return "redirect:/userProfile?nameError=invalid";
        }

        userService.setName(userService.findByUsername(principal.getName()), trimmed);

        return "redirect:/userProfile?nameSaved=true";
    }

    @PostMapping("/userProfile/uploadAvatar")
    public String uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            Principal principal
    ) throws IOException {

        if (principal == null) {
            return "redirect:/login";
        }

        if (file.isEmpty() || file.getContentType() == null
                || !file.getContentType().startsWith("image/")) {
            return "redirect:/userProfile?avatarError=true";
        }

        User user = userService.findByUsername(principal.getName());

        String filename = user.getUsername() + ".png";
        Path savePath = Paths.get(
                System.getProperty("user.dir"),
                "src", "main", "resources", "static", "images", filename
        );
        Files.createDirectories(savePath.getParent());
        file.transferTo(savePath);

        user.setAvatarPath("/images/" + filename);
        userService.save(user);

        return "redirect:/userProfile?avatarSaved=true";
    }

}
