package com.meeting.model;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int roomId;
    private int employeeId;
    private Integer supportStaffId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int participantsCount;
    private String bookingStatus;
    private String prepStatus;

    public Booking() {
    }

    public Booking(int roomId, int employeeId, Integer supportStaffId, LocalDateTime startTime, LocalDateTime endTime, int participantsCount, String bookingStatus, String prepStatus, int bookingId) {
        this.roomId = roomId;
        this.employeeId = employeeId;
        this.supportStaffId = supportStaffId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantsCount = participantsCount;
        this.bookingStatus = bookingStatus;
        this.prepStatus = prepStatus;
        this.bookingId = bookingId;
    }
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getSupportStaffId() {
        return supportStaffId;
    }

    public void setSupportStaffId(Integer supportStaffId) {
        this.supportStaffId = supportStaffId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPrepStatus() {
        return prepStatus;
    }

    public void setPrepStatus(String prepStatus) {
        this.prepStatus = prepStatus;
    }

}
