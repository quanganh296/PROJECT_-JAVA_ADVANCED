package com.meeting.model;

public class Room {

    private int roomId;
    private String roomName;
    private int capacity;
    private String location;
    private String fixedEquipment;
    private String status;

    public Room() {
    }

    public Room(int roomId, String roomName, String location, int capacity, String fixedEquipment, String status) {
         this.status = status;
        this.roomId = roomId;
        this.roomName = roomName;
        this.location = location;
        this.capacity = capacity;
        this.fixedEquipment = fixedEquipment;
        this.status = status;
    }


    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomname) {
        this.roomName = roomname;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFixedEquipment() {
        return fixedEquipment;
    }

    public void setFixedEquipment(String fixedEquipment) {
        this.fixedEquipment = fixedEquipment;
    }





}
