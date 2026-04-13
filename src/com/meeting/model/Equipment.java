package com.meeting.model;

public class Equipment {

    private int equipmentId;
    private String equipmentName;
    private int totalQuantity;
    private int availableQuantity;
    private String Status;

    public Equipment() {
    }

    public Equipment(int equipmentId, String status, int availableQuantity, int totalQuantity, String equipmentName) {
        this.equipmentId = equipmentId;
        Status = status;
        this.availableQuantity = availableQuantity;
        this.totalQuantity = totalQuantity;
        this.equipmentName = equipmentName;
    }


    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }



}
