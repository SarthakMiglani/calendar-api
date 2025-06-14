package com.readymon.calendarbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BookingRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in YYYY-MM-DD format")
    private String date;

    @NotBlank(message = "Time is required")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "Time must be in HH:MM format")
    private String time;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\+\\d{10,15}", message = "Phone number must start with + and contain 10-15 digits")
    private String phoneNum;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Purpose of visit is required")
    @Size(min = 5, max = 200, message = "Purpose must be between 5 and 200 characters")
    private String purposeOfVisit;

    // Constructors
    public BookingRequest() {}

    public BookingRequest(String name, String date, String time, String phoneNum,
                          String email, String purposeOfVisit) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.phoneNum = phoneNum;
        this.email = email;
        this.purposeOfVisit = purposeOfVisit;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPurposeOfVisit() { return purposeOfVisit; }
    public void setPurposeOfVisit(String purposeOfVisit) { this.purposeOfVisit = purposeOfVisit; }
}