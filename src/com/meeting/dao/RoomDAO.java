package com.meeting.dao;

import com.meeting.model.Room;
import com.meeting.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // 1. Lấy tất cả phòng
    public List<Room> getAllRooms() throws SQLException {
        List<Room> roomList = new ArrayList<>();
        String sql = "SELECT * FROM room";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setCapacity(rs.getInt("capacity"));
                room.setLocation(rs.getString("location"));
                room.setFixedEquipment(rs.getString("fixed_equipment"));
                roomList.add(room);
            }
        }

        return roomList;
    }

    // 2. Thêm phòng
    public boolean insertRoom(Room room) throws SQLException {
        String sql = "INSERT INTO room (room_name, capacity, location, fixed_equipment) VALUES (?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getRoomName());
            pstmt.setInt(2, room.getCapacity());
            pstmt.setString(3, room.getLocation());
            pstmt.setString(4, room.getFixedEquipment());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 3. Cập nhật phòng
    public boolean updateRoom(Room room) throws SQLException {
        String sql = "UPDATE room SET room_name = ?, capacity = ?, location = ?, fixed_equipment = ? WHERE room_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getRoomName());
            pstmt.setInt(2, room.getCapacity());
            pstmt.setString(3, room.getLocation());
            pstmt.setString(4, room.getFixedEquipment());
            pstmt.setInt(5, room.getRoomId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 4. Xóa phòng
    public boolean deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM room WHERE room_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;
        }
    }
//Lấy danh sách phòng trống
//    public List<Room> getAvailableRooms(LocalDateTime start, LocalDateTime end) {
//        List<Room> availableRooms = new ArrayList<>();
//        // SQL: Lấy tất cả phòng ngoại trừ những phòng có đơn đặt (không bị từ chối) trùng vào khoảng [start, end]
//        String sql = "SELECT * FROM room WHERE room_id NOT IN (" +
//                "    SELECT room_id FROM bookings " +
//                "    WHERE booking_status != 'REJECTED' " +
//                "    AND (start_time < ? AND end_time > ?)" +
//                ")";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setTimestamp(1, Timestamp.valueOf(end));
//            pstmt.setTimestamp(2, Timestamp.valueOf(start));
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                Room room = new Room();
//                room.setRoomId(rs.getInt("room_id"));
//                room.setRoomName(rs.getString("room_name"));
//                room.setCapacity(rs.getInt("capacity"));
//                room.setLocation(rs.getString("location"));
//                room.setFixedEquipment(rs.getString("fixed_equipment"));
//                availableRooms.add(room);
//            }
//        } catch (SQLException e) {
//            System.out.println("Lỗi lấy danh sách phòng trống: " + e.getMessage());
//        }
//        return availableRooms;
//    }

    //Tìm kiếm phòng theo tên
    public List<Room> searchRoomsByName(String keyword) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE room_name LIKE ?";

        try (Connection conn = com.meeting.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword.trim() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setCapacity(rs.getInt("capacity"));
                room.setLocation(rs.getString("location"));
                room.setFixedEquipment(rs.getString("fixed_equipment"));
                room.setStatus(rs.getString("status")); // LẤY TRẠNG THÁI TỪ DB
                list.add(room);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm: " + e.getMessage());
        }
        return list;
    }

//    public List<Room> searchRoomsById(int keyword) {
//        List<Room> list = new ArrayList<>();
//        String sql = "SELECT * FROM room WHERE room_id = ?";
//
//        try (Connection conn = com.meeting.util.DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, keyword);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                Room room = new Room();
//                room.setRoomId(rs.getInt("room_id"));
//                room.setRoomName(rs.getString("room_name"));
//                room.setCapacity(rs.getInt("capacity"));
//                room.setLocation(rs.getString("location"));
//                room.setFixedEquipment(rs.getString("fixed_equipment"));
//                room.setStatus(rs.getString("status")); // LẤY TRẠNG THÁI TỪ DB
//                list.add(room);
//            }
//        } catch (SQLException e) {
//            System.out.println("Lỗi tìm kiếm: " + e.getMessage());
//        }
//        return list;
//    }

    // Lấy thông tin chi tiết của một phòng theo ID
    public Room getRoomById(int roomId) throws SQLException {
        String sql = "SELECT * FROM room WHERE room_id = ?";

        // Sử dụng try-with-resources để tự động đóng kết nối
        try (Connection conn = com.meeting.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Truyền ID phòng vào dấu ?
            pstmt.setInt(1, roomId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Nếu tìm thấy phòng có ID tương ứng
                if (rs.next()) {
                    Room room = new Room();
                    room.setRoomId(rs.getInt("room_id"));
                    room.setRoomName(rs.getString("room_name"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setLocation(rs.getString("location"));
                    room.setStatus(rs.getString("status"));
                    room.setFixedEquipment(rs.getString("fixed_equipment"));

                    return room; // Trả về đối tượng Room
                }
            }
        } catch (SQLException e) {
            System.out.println("[LỖI DB] Không thể lấy thông tin phòng: " + e.getMessage());
        }

        return null; // Trả về null nếu không tìm thấy phòng hoặc xảy ra lỗi
    }
}