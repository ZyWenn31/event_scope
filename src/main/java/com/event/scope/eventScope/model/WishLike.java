package com.event.scope.eventScope.model;

import jakarta.persistence.*;

@Entity
@Table(name = "t_wish_like",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "wish_id"})})
public class WishLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "wish_id", nullable = false)
    private Wish wish;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Wish getWish() {
        return wish;
    }

    public void setWish(Wish wish) {
        this.wish = wish;
    }
}
