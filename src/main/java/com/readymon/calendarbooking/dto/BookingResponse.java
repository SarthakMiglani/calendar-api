package com.readymon.calendarbooking.dto;

public class BookingResponse {
    private String status;
    private String message;

    // Constructors
    public BookingResponse() {}

    public BookingResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}