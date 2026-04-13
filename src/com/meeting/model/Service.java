package com.meeting.model;

public class Service {
    private int serviceId;
    private String serviceName;
    private double unitPrice; // Đơn giá (Dùng kiểu double để lưu số thập phân nếu cần)

    // Constructor không tham số
    public Service() {
    }

    // Constructor đầy đủ tham số
    public Service(int serviceId, String serviceName, double unitPrice) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.unitPrice = unitPrice;
    }

    // --- Getters & Setters ---
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}