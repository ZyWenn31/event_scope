package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with id " + id +" not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User update(User user, Long id) {
        User existing = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with id " + id +" not found"));


        existing.setOrganizerReviews(user.getOrganizerReviews());
        existing.setCreatedAt(user.getCreatedAt());
        existing.setEmail(user.getEmail());
        existing.setName(user.getName());
        existing.setEnabled(user.isEnabled());
        existing.setOrganizedEvents(user.getOrganizedEvents());
        existing.setTelegramAccount(user.getTelegramAccount());
        existing.setParticipation(user.getParticipations());
        existing.setRole(user.getRole());
        existing.setPassword(user.getPassword());
        existing.setUsername(user.getUsername());
        existing.setWishes(user.getWishes());
        existing.setEventReviews(user.getEventReviews());

        return userRepository.save(existing);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with username " + username +" not found")
                );
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public void setEmail(User user, String email) {
        user.setEmail(email);
        userRepository.save(user);
    }

    public void setName(User user, String name) {
        user.setName(name);
        userRepository.save(user);
    }
}
