package com.event.scope.eventScope.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "t_user", uniqueConstraints = {@UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, max = 25, message = "Имя должно быть от 5 до 25 символов")
    private String username;

    @Size(min = 6, message = "Пароль должен быть от 6 до 30 символов")
    private String password;

    private String role = "USER";

    private LocalDateTime createdAt;

    private boolean enabled = true;

    @Email(message = "Некорректный формат email")
    private String email;

    private String name;

    private String avatarPath;

    @OneToMany(mappedBy = "organizer")
    private List<Event> organizedEvents;

    @OneToMany(mappedBy = "user")
    private List<EventParticipant> participations;

    @OneToMany(mappedBy = "user")
    private List<EventReview> eventReviews;

    @OneToMany(mappedBy = "user")
    private List<OrganizerReview> organizerReviews;

    @OneToMany(mappedBy = "organizer")
    private List<OrganizerReview> receivedReviews;

    @OneToMany(mappedBy = "user")
    private List<Wish> wishes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    public void setOrganizedEvents(List<Event> organizedEvents) {
        this.organizedEvents = organizedEvents;
    }

    public List<EventParticipant> getParticipations() {
        return participations;
    }

    public void setParticipation(List<EventParticipant> participations) {
        this.participations = participations;
    }

    public List<EventReview> getEventReviews() {
        return eventReviews;
    }

    public void setEventReviews(List<EventReview> eventReviews) {
        this.eventReviews = eventReviews;
    }

    public List<OrganizerReview> getOrganizerReviews() {
        return organizerReviews;
    }

    public void setOrganizerReviews(List<OrganizerReview> organizerReviews) {
        this.organizerReviews = organizerReviews;
    }

    public List<OrganizerReview> getReceivedReviews() {
        return receivedReviews;
    }

    public void setReceivedReviews(List<OrganizerReview> receivedReviews) {
        this.receivedReviews = receivedReviews;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public void setWishes(List<Wish> wishes) {
        this.wishes = wishes;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
