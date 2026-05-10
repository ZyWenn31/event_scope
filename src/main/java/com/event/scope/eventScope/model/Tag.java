package com.event.scope.eventScope.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "t_tag", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Event> events;

    @ManyToMany(mappedBy = "tags")
    private Set<Wish> wishes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<Wish> getWishes() {
        return wishes;
    }

    public void setWishes(Set<Wish> wishes) {
        this.wishes = wishes;
    }
}

