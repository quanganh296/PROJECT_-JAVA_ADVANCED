package com.meeting.model;

public class BookingEquipment {

    private int bookingId;
    private int equipmentId;
    private int quantity;
    private String equipmentName; // Tên thiết bị để hiển thị

    public BookingEquipment() {

    }

    public BookingEquipment(int bookingId, int equipmentId, int quantity, String equipmentName) {
        this.bookingId = bookingId;
        this.equipmentId = equipmentId;
        this.quantity = quantity;
        this.equipmentName = equipmentName;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentByName() {
        return equipmentName;
    }

    public void setEquipmentByName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
