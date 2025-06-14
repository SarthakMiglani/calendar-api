package com.readymon.calendarbooking.service;

import com.readymon.calendarbooking.config.GoogleCalendarProperties;
import com.readymon.calendarbooking.dto.BookingRequest;
import com.readymon.calendarbooking.exception.CalendarServiceException;
import com.readymon.calendarbooking.exception.TimeSlotUnavailableException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

    private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);

    @Autowired
    private Calendar calendar;

    @Autowired
    private GoogleCalendarProperties properties;

    public boolean isTimeSlotAvailable(String date, String time) {
        try {
            ZonedDateTime requestedDateTime = parseDateTime(date, time);
            ZonedDateTime endDateTime = requestedDateTime.plusMinutes(properties.getSlotDurationMinutes());

            logger.info("Checking availability for {} to {}", requestedDateTime, endDateTime);

            Events events = calendar.events().list(properties.getCalendarId())
                    .setTimeMin(new DateTime(requestedDateTime.toInstant().toEpochMilli()))
                    .setTimeMax(new DateTime(endDateTime.toInstant().toEpochMilli()))
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .execute();

            List<Event> items = events.getItems();
            boolean isAvailable = items.isEmpty();

            logger.info("Time slot availability: {}, Found {} conflicting events",
                    isAvailable, items.size());

            return isAvailable;

        } catch (IOException e) {
            logger.error("Error checking calendar availability", e);
            throw new CalendarServiceException("Failed to check calendar availability", e);
        }
    }

    public List<String> findAlternateTimeSlots(String date) {
        List<String> alternateSlots = new ArrayList<>();

        try {
            LocalDate localDate = LocalDate.parse(date);
            ZoneId zoneId = ZoneId.of(properties.getTimeZone());

            // Check slots from 9 AM to 6 PM
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(18, 0);

            LocalTime currentTime = startTime;
            while (currentTime.isBefore(endTime)) {
                ZonedDateTime slotDateTime = ZonedDateTime.of(localDate, currentTime, zoneId);

                if (isTimeSlotAvailable(date, currentTime.format(DateTimeFormatter.ofPattern("HH:mm")))) {
                    alternateSlots.add(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                }

                currentTime = currentTime.plusMinutes(properties.getSlotDurationMinutes());

                // Limit to 5 alternate slots
                if (alternateSlots.size() >= 5) {
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Error finding alternate time slots for date: {}", date, e);
        }

        return alternateSlots;
    }

    public void bookAppointment(BookingRequest request) {
        try {
            // Double-check availability to prevent race conditions
            if (!isTimeSlotAvailable(request.getDate(), request.getTime())) {
                throw new TimeSlotUnavailableException("Time slot is no longer available");
            }

            ZonedDateTime startDateTime = parseDateTime(request.getDate(), request.getTime());
            ZonedDateTime endDateTime = startDateTime.plusMinutes(properties.getSlotDurationMinutes());

            Event event = new Event()
                    .setSummary("Appointment: " + request.getName())
                    .setDescription(buildEventDescription(request));

            EventDateTime start = new EventDateTime()
                    .setDateTime(new DateTime(startDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                    .setTimeZone(properties.getTimeZone())
                    .setTimeZone(properties.getTimeZone());
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(new DateTime(startDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                    .setTimeZone(properties.getTimeZone())
                    .setTimeZone(properties.getTimeZone());
            event.setEnd(end);

            event = calendar.events().insert(properties.getCalendarId(), event).execute();

            logger.info("Successfully created calendar event with ID: {}", event.getId());

        } catch (TimeSlotUnavailableException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Error creating calendar event", e);
            throw new CalendarServiceException("Failed to create calendar event", e);
        }
    }

    private ZonedDateTime parseDateTime(String date, String time) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalTime localTime = LocalTime.parse(time);
            ZoneId zoneId = ZoneId.of(properties.getTimeZone());

            return ZonedDateTime.of(localDate, localTime, zoneId);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date/time: {} {}", date, time, e);
            throw new IllegalArgumentException("Invalid date or time format", e);
        }
    }

    private String buildEventDescription(BookingRequest request) {
        return String.format(
                "Appointment Details:\n" +
                        "Name: %s\n" +
                        "Phone: %s\n" +
                        "Email: %s\n" +
                        "Purpose: %s\n" +
                        "Date: %s\n" +
                        "Time: %s",
                request.getName(),
                request.getPhoneNum(),
                request.getEmail(),
                request.getPurposeOfVisit(),
                request.getDate(),
                request.getTime()
        );
    }
}
