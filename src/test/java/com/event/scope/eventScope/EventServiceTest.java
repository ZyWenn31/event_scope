package com.event.scope.eventScope;

import com.event.scope.eventScope.model.Event;
import com.event.scope.eventScope.model.EventStatus;
import com.event.scope.eventScope.repository.EventRepository;
import com.event.scope.eventScope.service.EventService;
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

public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Description");
        event.setStatus(EventStatus.PLANNED);
        event.setEventDate(LocalDateTime.now());
    }

    @Test
    void testFindById_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        Event found = eventService.findById(1L);
        assertEquals(event.getTitle(), found.getTitle());
    }

    @Test
    void testFindById_NotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> eventService.findById(2L));
    }

    @Test
    void testFindAll() {
        List<Event> events = List.of(event);
        when(eventRepository.findAll()).thenReturn(events);
        List<Event> result = eventService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testSave() {
        when(eventRepository.save(event)).thenReturn(event);
        Event saved = eventService.save(event);
        assertNotNull(saved);
        assertEquals(event.getTitle(), saved.getTitle());
    }

    @Test
    void testDeleteById() {
        doNothing().when(eventRepository).deleteById(1L);
        eventService.deleteById(1L);
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdate_Success() {
        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated");
        updatedEvent.setStatus(EventStatus.FINISHED);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.update(updatedEvent, 1L);
        assertEquals("Updated", result.getTitle());
        assertEquals(EventStatus.FINISHED, result.getStatus());
    }

    @Test
    void testUpdate_NotFound() {
        Event updatedEvent = new Event();
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> eventService.update(updatedEvent, 2L));
    }

    @Test
    void testFindAllByStatus() {
        List<Event> events = List.of(event);
        when(eventRepository.findAllByStatus(EventStatus.PLANNED)).thenReturn(events);
        List<Event> result = eventService.findAllByStatus(EventStatus.PLANNED);
        assertEquals(1, result.size());
        assertEquals(EventStatus.PLANNED, result.get(0).getStatus());
    }

    @Test
    void testSave_NullEvent() {
        assertThrows(NullPointerException.class, () -> eventService.save(null));
    }

    @Test
    void testUpdate_AllFields() {
        Event updated = new Event();
        updated.setTitle("New Title");
        updated.setDescription("New Description");
        updated.setStatus(EventStatus.FINISHED);
        updated.setRating(5);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.update(updated, 1L);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals(EventStatus.FINISHED, result.getStatus());
        assertEquals(5, result.getRating());
    }

}
