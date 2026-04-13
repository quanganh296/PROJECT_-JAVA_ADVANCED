package com.meeting.dao;

import com.meeting.model.BookingEquipment;
import com.meeting.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingEquipmentDAO {

    // 1. Thêm một thiết bị vào đơn đặt phòng
    public boolean addEquipmentToBooking(BookingEquipment bookingEquipment) {
        String sql = "INSERT INTO booking_equipments (booking_id, equipment_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingEquipment.getBookingId());
            pstmt.setInt(2, bookingEquipment.getEquipmentId());
            pstmt.setInt(3, bookingEquipment.getQuantity());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi thêm thiết bị vào đơn đặt phòng: " + e.getMessage());
            return false;
        }
    }

    // 2. Lấy danh sách thiết bị được mượn của một đơn đặt phòng cụ thể (dành cho Support Staff xem)
    public List<BookingEquipment> getEquipmentsByBookingId(int bookingId) {
        List<BookingEquipment> list = new ArrayList<>();
        String sql = "SELECT be.booking_id, be.equipment_id, be.quantity, e.equipment_name " +
                "FROM booking_equipments be " +
                "JOIN equipments e ON be.equipment_id = e.equipment_id " +
                "WHERE be.booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BookingEquipment be = new BookingEquipment();
                be.setBookingId(rs.getInt("booking_id"));
                be.setEquipmentId(rs.getInt("equipment_id"));
                be.setQuantity(rs.getInt("quantity"));
                be.setEquipmentByName(rs.getString("equipment_name"));
                list.add(be);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy chi tiết thiết bị của đơn: " + e.getMessage());
        }
        return list;
    }

    // 3. Xóa thiết bị khỏi đơn (Dùng trong trường hợp Nhân viên muốn cập nhật/hủy booking)
    public boolean removeEquipmentFromBooking(int bookingId, int equipmentId) {
        String sql = "DELETE FROM booking_equipments WHERE booking_id = ? AND equipment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, equipmentId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi xóa thiết bị khỏi đơn: " + e.getMessage());
            return false;
        }
    }

    // Get equipment details by booking id with names
    public List<String> getEquipmentDetailsByBookingId(int bookingId) {
        List<String> details = new ArrayList<>();
        String sql = "SELECT e.equipment_name, be.quantity FROM booking_equipments be JOIN equipments e ON be.equipment_id = e.equipment_id WHERE be.booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("equipment_name");
                int qty = rs.getInt("quantity");
                details.add(name + " (x" + qty + ")");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy chi tiết thiết bị: " + e.getMessage());
        }
        return details;
    }
}