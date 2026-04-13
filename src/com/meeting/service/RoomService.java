package com.meeting.service;

import com.meeting.dao.BookingDAO;
import com.meeting.dao.RoomDAO;
import com.meeting.model.Room;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RoomService {
    private RoomDAO roomDAO = new RoomDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    // Lấy tất cả phòng
    public List<Room> getAllRooms() {
        try {
            return roomDAO.getAllRooms();
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách phòng: " + e.getMessage());
            return null;
        }
    }

    // Thêm phòng mới
    public boolean addRoom(Room room) {
        try {
            return roomDAO.insertRoom(room);
        } catch (SQLException e) {
            System.out.println("Lỗi thêm phòng: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật phòng
    public boolean updateRoom(Room room) {
        try {
            return roomDAO.updateRoom(room);
        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật phòng: " + e.getMessage());
            return false;
        }
    }

    // Xóa phòng
    public boolean deleteRoom(int roomId) {
        try {
            return roomDAO.deleteRoom(roomId);
        } catch (SQLException e) {
            System.out.println("Lỗi xóa phòng: " + e.getMessage());
            return false;
        }
    }

    // Kiểm tra phòng có trống không
    public boolean isRoomAvailable(int roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingDAO.isRoomAvailable(roomId, startTime, endTime);
    }
}
