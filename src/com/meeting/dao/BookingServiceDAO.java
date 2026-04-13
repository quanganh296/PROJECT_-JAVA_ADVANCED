package com.meeting.dao;

import com.meeting.model.BookingService;
import com.meeting.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceDAO {

    // 1. Thêm một dịch vụ vào đơn đặt phòng
    public boolean addServiceToBooking(BookingService bookingService) {
        String sql = "INSERT INTO booking_services (booking_id, service_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingService.getBookingId());
            pstmt.setInt(2, bookingService.getServiceId());
            pstmt.setInt(3, bookingService.getQuantity());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi thêm dịch vụ vào đơn đặt phòng: " + e.getMessage());
            return false;
        }
    }

    // 2. Lấy danh sách dịch vụ của một đơn đặt phòng cụ thể (Phục vụ cho Admin tính tiền hoặc Support chuẩn bị)
    public List<BookingService> getServicesByBookingId(int bookingId) {
        List<BookingService> list = new ArrayList<>();
        String sql = "SELECT * FROM booking_services WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BookingService bs = new BookingService();
                bs.setBookingId(rs.getInt("booking_id"));
                bs.setServiceId(rs.getInt("service_id"));
                bs.setQuantity(rs.getInt("quantity"));
                list.add(bs);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy chi tiết dịch vụ của đơn: " + e.getMessage());
        }
        return list;
    }

    // 3. Xóa dịch vụ khỏi đơn (Dùng khi cập nhật đơn)
    public boolean removeServiceFromBooking(int bookingId, int serviceId) {
        String sql = "DELETE FROM booking_services WHERE booking_id = ? AND service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, serviceId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi xóa dịch vụ khỏi đơn: " + e.getMessage());
            return false;
        }
    }

    // Get service details by booking id with names
    public List<String> getServiceDetailsByBookingId(int bookingId) {
        List<String> details = new ArrayList<>();
        String sql = "SELECT s.service_name, bs.quantity FROM booking_services bs JOIN services s ON bs.service_id = s.service_id WHERE bs.booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("service_name");
                int qty = rs.getInt("quantity");
                details.add(name + " (x" + qty + ")");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy chi tiết dịch vụ: " + e.getMessage());
        }
        return details;
    }
}