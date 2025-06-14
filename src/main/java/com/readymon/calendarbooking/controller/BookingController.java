package com.readymon.calendarbooking.controller;

import com.readymon.calendarbooking.dto.*;
import com.readymon.calendarbooking.exception.CalendarServiceException;
import com.readymon.calendarbooking.exception.TimeSlotUnavailableException;
import com.readymon.calendarbooking.service.CalendarService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private CalendarService calendarService;

    @PostMapping("/check-availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @Valid @RequestBody AvailabilityRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors in availability request: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest()
                    .body(new AvailabilityResponse("error", Collections.emptyList()));
        }

        try {
            logger.info("Checking availability for date: {}, time: {}",
                    request.getDate(), request.getTime());

            boolean isAvailable = calendarService.isTimeSlotAvailable(
                    request.getDate(), request.getTime());

            if (isAvailable) {
                return ResponseEntity.ok(new AvailabilityResponse("yes", Collections.emptyList()));
            } else {
                List<String> alternateSlots = calendarService.findAlternateTimeSlots(request.getDate());
                return ResponseEntity.ok(new AvailabilityResponse("no", alternateSlots));
            }

        } catch (CalendarServiceException e) {
            logger.error("Calendar service error during availability check", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AvailabilityResponse("error", Collections.emptyList()));
        } catch (Exception e) {
            logger.error("Unexpected error during availability check", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AvailabilityResponse("error", Collections.emptyList()));
        }
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookAppointment(
            @Valid @RequestBody BookingRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors in booking request: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest()
                    .body(new BookingResponse("Failed", "Invalid request data"));
        }

        try {
            logger.info("Processing booking request for {} on {} at {}",
                    request.getName(), request.getDate(), request.getTime());

            calendarService.bookAppointment(request);

            String message = String.format(
                    "Your appointment has been successfully scheduled for %s at %s",
                    request.getDate(), request.getTime());

            return ResponseEntity.ok(new BookingResponse("Booked", message));

        } catch (TimeSlotUnavailableException e) {
            logger.warn("Time slot unavailable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new BookingResponse("Failed",
                            "The selected time slot is no longer available. Please try another time."));
        } catch (CalendarServiceException e) {
            logger.error("Calendar service error during booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookingResponse("Failed",
                            "Unable to process your booking at this time. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error during booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookingResponse("Failed",
                            "An unexpected error occurred. Please try again later."));
        }
    }
}