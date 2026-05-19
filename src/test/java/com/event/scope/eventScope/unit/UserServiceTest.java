package com.event.scope.eventScope.unit;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.repository.UserRepository;
import com.event.scope.eventScope.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ========== CRUD ==========

    @Test
    void findById_success() {
        User user = userWithId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_notFound_throwsEntityNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(2L));
    }

    @Test
    void findAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userWithId(1L), userWithId(2L)));

        assertThat(userService.findAll()).hasSize(2);
    }

    @Test
    void save_callsRepositoryAndReturnsUser() {
        User user = userWithId(1L);
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.save(user);

        assertNotNull(saved);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByUsername_whenExists_returnsUser() {
        User user = userWithId(1L);
        user.setUsername("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("alice");

        assertEquals("alice", result.getUsername());
    }

    // ========== domain logic ==========

    @Test
    void promoteToOrganizer_setsRoleToOrganizer() {
        User user = userWithId(1L);
        user.setRole("USER");

        userService.promoteToOrganizer(user);

        assertThat(user.getRole()).isEqualTo("ORGANIZER");
    }

    @Test
    void promoteToOrganizer_callsSave() {
        User user = userWithId(1L);

        userService.promoteToOrganizer(user);

        verify(userRepository).save(user);
    }

    @Test
    void isEmailTaken_whenEmailExists_returnsTrue() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThat(userService.isEmailTaken("taken@example.com")).isTrue();
    }

    @Test
    void isEmailTaken_whenEmailFree_returnsFalse() {
        when(userRepository.existsByEmail("free@example.com")).thenReturn(false);

        assertThat(userService.isEmailTaken("free@example.com")).isFalse();
    }

    @Test
    void isEmailTakenByOther_whenEmailBelongsToAnotherUser_returnsTrue() {
        when(userRepository.existsByEmailAndIdNot("shared@example.com", 1L)).thenReturn(true);

        assertThat(userService.isEmailTakenByOther("shared@example.com", 1L)).isTrue();
    }

    @Test
    void isEmailTakenByOther_whenEmailBelongsToCurrentUser_returnsFalse() {
        when(userRepository.existsByEmailAndIdNot("mine@example.com", 1L)).thenReturn(false);

        assertThat(userService.isEmailTakenByOther("mine@example.com", 1L)).isFalse();
    }

    @Test
    void findByUsername_whenUserNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("ghost"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ========== helpers ==========

    private User userWithId(long id) {
        User u = new User();
        u.setId(id);
        u.setCreatedAt(LocalDateTime.now());
        return u;
    }
}
