package com.event.scope.eventScope;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.repository.UserRepository;
import com.event.scope.eventScope.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@mail.com");
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void findById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void findById_notFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.findById(2L));
    }

    @Test
    void findAll_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.findAll();

        assertEquals(1, users.size());
        assertEquals(user.getUsername(), users.get(0).getUsername());
    }

    @Test
    void save_success() {
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.save(user);

        assertNotNull(saved);
        assertEquals("testUser", saved.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByUsername_success() {
        when(userRepository.findByUsername("testUser"))
                .thenReturn(Optional.of(user));

        User result = userService.findByUsername("testUser");

        assertEquals("testUser", result.getUsername());
    }
}
