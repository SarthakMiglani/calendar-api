package com.readymon.calendarbooking.controller;

import com.readymon.calendarbooking.dto.AvailabilityRequest;
import com.readymon.calendarbooking.dto.BookingRequest;
import com.readymon.calendarbooking.service.CalendarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalendarService calendarService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCheckAvailability_Available() throws Exception {
        // Arrange
        AvailabilityRequest request = new AvailabilityRequest("2025-06-14", "15:30");
        when(calendarService.isTimeSlotAvailable(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/check-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmation").value("yes"))
                .andExpect(jsonPath("$.alternateTimeSlots").isEmpty());
    }

    @Test
    void testCheckAvailability_NotAvailable() throws Exception {
        // Arrange
        AvailabilityRequest request = new AvailabilityRequest("2025-06-14", "15:30");
        when(calendarService.isTimeSlotAvailable(anyString(), anyString())).thenReturn(false);
        when(calendarService.findAlternateTimeSlots(anyString()))
                .thenReturn(Arrays.asList("14:00", "16:00", "17:30"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/check-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmation").value("no"))
                .andExpect(jsonPath("$.alternateTimeSlots").isArray())
                .andExpect(jsonPath("$.alternateTimeSlots[0]").value("14:00"));
    }

    @Test
    void testBookAppointment_Success() throws Exception {
        // Arrange
        BookingRequest request = new BookingRequest(
                "John Doe", "2025-06-14", "15:30",
                "+919876543210", "john@example.com", "Consultation"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Booked"))
                .andExpect(jsonPath("$.message").value(
                        "Your appointment has been successfully scheduled for 2025-06-14 at 15:30"));
    }

    @Test
    void testBookAppointment_ValidationError() throws Exception {
        // Arrange - Invalid request with missing required fields
        BookingRequest request = new BookingRequest();

        // Act & Assert
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}