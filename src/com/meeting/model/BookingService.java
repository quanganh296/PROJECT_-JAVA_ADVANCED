package com.meeting.model;

public class BookingService {
    private int bookingId;
    private int serviceId;
    private int quantity; // Số lượng phần dịch vụ được đặt (VD: 10 chai nước)

    public BookingService() {
    }

    public BookingService(int bookingId, int serviceId, int quantity) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.quantity = quantity;
    }

    // --- Getters & Setters ---
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}