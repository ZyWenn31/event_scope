package com.event.scope.eventScope.controller;

import com.event.scope.eventScope.service.EventParticipantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventParticipationController {
    private final EventParticipantService eventParticipantService;

    public EventParticipationController(EventParticipantService eventParticipantService) {
        this.eventParticipantService = eventParticipantService;
    }

    // Присоединиться к событию
    @PostMapping("/join")
    public ResponseEntity<Void> joinEvent(
            @RequestParam Long eventId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        eventParticipantService.addParticipant(eventId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // Отменить участие
    @PostMapping("/leave")
    public ResponseEntity<Void> leaveEvent(
            @RequestParam Long eventId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        eventParticipantService.removeParticipant(eventId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
