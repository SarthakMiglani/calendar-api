package com.readymon.calendarbooking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.calendar")
public class GoogleCalendarProperties {
    private String calendarId;
    private String serviceAccountKeyPath;
    private String timeZone = "Asia/Kolkata";
    private int slotDurationMinutes = 30;
    private String applicationName = "Calendar Booking API";

    // Getters and setters
    public String getCalendarId() { return calendarId; }
    public void setCalendarId(String calendarId) { this.calendarId = calendarId; }

    public String getServiceAccountKeyPath() { return serviceAccountKeyPath; }
    public void setServiceAccountKeyPath(String serviceAccountKeyPath) {
        this.serviceAccountKeyPath = serviceAccountKeyPath;
    }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}