package com.readymon.calendarbooking.service;

import com.readymon.calendarbooking.config.GoogleCalendarProperties;
import com.readymon.calendarbooking.dto.BookingRequest;
import com.readymon.calendarbooking.exception.CalendarServiceException;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private Calendar calendar;

    @Mock
    private GoogleCalendarProperties properties;

    @Mock
    private Calendar.Events events;

    @Mock
    private Calendar.Events.List eventsList;

    @InjectMocks
    private CalendarService calendarService;

    @BeforeEach
    void setUp() {
        when(properties.getCalendarId()).thenReturn("test-calendar@example.com");
        when(properties.getTimeZone()).thenReturn("Asia/Kolkata");
        when(properties.getSlotDurationMinutes()).thenReturn(30);
    }

    @Test
    void testIsTimeSlotAvailable_WhenSlotIsFree() throws IOException {
        // Arrange
        Events emptyEvents = new Events();
        emptyEvents.setItems(Collections.emptyList());

        when(calendar.events()).thenReturn(events);
        when(events.list(anyString())).thenReturn(eventsList);
        when(eventsList.setTimeMin(any())).thenReturn(eventsList);
        when(eventsList.setTimeMax(any())).thenReturn(eventsList);
        when(eventsList.setSingleEvents(true)).thenReturn(eventsList);
        when(eventsList.setOrderBy(anyString())).thenReturn(eventsList);
        when(eventsList.execute()).thenReturn(emptyEvents);

        // Act
        boolean result = calendarService.isTimeSlotAvailable("2025-06-14", "15:30");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTimeSlotAvailable_WhenSlotIsOccupied() throws IOException {
        // Arrange
        Events occupiedEvents = new Events();
        occupiedEvents.setItems(List.of(new com.google.api.services.calendar.model.Event()));

        when(calendar.events()).thenReturn(events);
        when(events.list(anyString())).thenReturn(eventsList);
        when(eventsList.setTimeMin(any())).thenReturn(eventsList);
        when(eventsList.setTimeMax(any())).thenReturn(eventsList);
        when(eventsList.setSingleEvents(true)).thenReturn(eventsList);
        when(eventsList.setOrderBy(anyString())).thenReturn(eventsList);
        when(eventsList.execute()).thenReturn(occupiedEvents);

        // Act
        boolean result = calendarService.isTimeSlotAvailable("2025-06-14", "15:30");

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsTimeSlotAvailable_WhenIOException() throws IOException {
        // Arrange
        when(calendar.events()).thenReturn(events);
        when(events.list(anyString())).thenReturn(eventsList);
        when(eventsList.setTimeMin(any())).thenReturn(eventsList);
        when(eventsList.setTimeMax(any())).thenReturn(eventsList);
        when(eventsList.setSingleEvents(true)).thenReturn(eventsList);
        when(eventsList.setOrderBy(anyString())).thenReturn(eventsList);
        when(eventsList.execute()).thenThrow(new IOException("Calendar API error"));

        // Act & Assert
        assertThrows(CalendarServiceException.class, () -> {
            calendarService.isTimeSlotAvailable("2025-06-14", "15:30");
        });
    }

    @Test
    void testBookAppointment_Success() throws IOException {
        // Arrange
        BookingRequest request = new BookingRequest(
                "John Doe", "2025-06-14", "15:30",
                "+919876543210", "john@example.com", "Consultation"
        );

        Events emptyEvents = new Events();
        emptyEvents.setItems(Collections.emptyList());

        when(calendar.events()).thenReturn(events);
        when(events.list(anyString())).thenReturn(eventsList);
        when(eventsList.setTimeMin(any())).thenReturn(eventsList);
        when(eventsList.setTimeMax(any())).thenReturn(eventsList);
        when(eventsList.setSingleEvents(true)).thenReturn(eventsList);
        when(eventsList.setOrderBy(anyString())).thenReturn(eventsList);
        when(eventsList.execute()).thenReturn(emptyEvents);

        Calendar.Events.Insert insertRequest = mock(Calendar.Events.Insert.class);
        when(events.insert(anyString(), any())).thenReturn(insertRequest);
        when(insertRequest.execute()).thenReturn(new com.google.api.services.calendar.model.Event());

        // Act & Assert
        assertDoesNotThrow(() -> calendarService.bookAppointment(request));
    }
}
