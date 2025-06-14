package com.readymon.calendarbooking.dto;

import java.util.List;

public class AvailabilityResponse {
    private String confirmation;
    private List<String> alternateTimeSlots;

    // Constructors
    public AvailabilityResponse() {}

    public AvailabilityResponse(String confirmation, List<String> alternateTimeSlots) {
        this.confirmation = confirmation;
        this.alternateTimeSlots = alternateTimeSlots;
    }

    // Getters and setters
    public String getConfirmation() { return confirmation; }
    public void setConfirmation(String confirmation) { this.confirmation = confirmation; }

    public List<String> getAlternateTimeSlots() { return alternateTimeSlots; }
    public void setAlternateTimeSlots(List<String> alternateTimeSlots) {
        this.alternateTimeSlots = alternateTimeSlots;
    }
}