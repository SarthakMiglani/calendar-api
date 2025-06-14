package com.readymon.calendarbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AvailabilityRequest {

    @NotBlank(message = "Date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in YYYY-MM-DD format")
    private String date;

    @NotBlank(message = "Time is required")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "Time must be in HH:MM format")
    private String time;

    // Constructors
    public AvailabilityRequest() {}

    public AvailabilityRequest(String date, String time) {
        this.date = date;
        this.time = time;
    }

    // Getters and setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}