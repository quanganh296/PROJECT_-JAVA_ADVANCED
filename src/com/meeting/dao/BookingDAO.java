package com.meeting.dao;

import com.meeting.model.Booking;
import com.meeting.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // ==========================================
    // CÁC HÀM DÙNG CHUNG & NHÂN VIÊN (EMPLOYEE)
    // ==========================================

    // Kiểm tra xem phòng có bị trùng lịch hay không
    public boolean isRoomAvailable(int roomId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE room_id = ? AND booking_status != 'REJECTED' AND (start_time < ? AND end_time > ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            pstmt.setTimestamp(2, Timestamp.valueOf(endTime));
            pstmt.setTimestamp(3, Timestamp.valueOf(startTime));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0; // Trả về true (trống) nếu đếm được 0 đơn trùng lịch
            }

        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra lịch phòng: " + e.getMessage());
        }
        return false;
    }

    // Tạo đơn đặt phòng mới và TRẢ VỀ ID của đơn vừa tạo (Hỗ trợ thêm thiết bị/dịch vụ)
    public int addBookingReturnId(Booking booking) {
        String sql = "INSERT INTO bookings (room_id, employee_id, start_time, end_time, participants_count, booking_status) " +
                "VALUES (?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, booking.getRoomId());
            pstmt.setInt(2, booking.getEmployeeId());
            pstmt.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            pstmt.setInt(5, booking.getParticipantsCount());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về booking_id tự sinh
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tạo đơn đặt phòng: " + e.getMessage());
        }
        return -1;
    }

    // Lấy thông tin một đơn đặt phòng dựa vào ID
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBookingFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy thông tin đơn bằng ID: " + e.getMessage());
        }
        return null;
    }

    // Lấy danh sách toàn bộ đơn đặt phòng của MỘT nhân viên cụ thể
    public List<Booking> getBookingsByEmployeeId(int employeeId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE employee_id = ? AND booking_status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách đơn của nhân viên: " + e.getMessage());
        }
        return list;
    }

    // Cập nhật trạng thái của đơn đặt phòng (Dùng khi Hủy đơn - CANCELLED)
    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET booking_status = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.toUpperCase());
            pstmt.setInt(2, bookingId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật trạng thái đơn: " + e.getMessage());
        }
        return false;
    }


    // ==========================================
    // CÁC HÀM DÀNH CHO ADMIN
    // ==========================================

    // Lấy danh sách các đơn đang chờ duyệt (PENDING)
    public List<Booking> getPendingBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE booking_status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách đơn PENDING: " + e.getMessage());
        }
        return list;
    }

    // Cập nhật trạng thái duyệt đơn và Giao việc cho nhân viên hỗ trợ (Support Staff)
    public boolean updateBookingStatusAndAssign(int bookingId, String status, Integer supportStaffId) {
        String sql = "UPDATE bookings SET booking_status = ?, support_staff_id = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.toUpperCase());
            if (supportStaffId != null) {
                pstmt.setInt(2, supportStaffId);
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.setInt(3, bookingId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi duyệt và phân công đơn: " + e.getMessage());
        }
        return false;
    }


    // ==========================================
    // CÁC HÀM DÀNH CHO SUPPORT STAFF
    // ==========================================

    // Lấy danh sách công việc được phân công cho một nhân viên Support cụ thể (CHƯA HOÀN TẤT & THEO NGÀY)
    public List<Booking> getAssignedTasks(int supportStaffId) {
        List<Booking> list = new ArrayList<>();
        // ĐÃ NÂNG CẤP: Lọc bỏ trạng thái READY và sắp xếp theo ngày giờ gần nhất
        String sql = "SELECT * FROM bookings WHERE support_staff_id = ? AND booking_status = 'APPROVED' " +
                "AND (prep_status IS NULL OR prep_status != 'READY') ORDER BY start_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supportStaffId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách công việc: " + e.getMessage());
        }
        return list;
    }

    // Cập nhật trạng thái tiến độ chuẩn bị (SETUP_COMPLETED, PREPARING...)
    public boolean updatePrepStatus(int bookingId, String prepStatus) {
        String sql = "UPDATE bookings SET prep_status = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, prepStatus.toUpperCase());
            pstmt.setInt(2, bookingId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật trạng thái chuẩn bị: " + e.getMessage());
        }
        return false;
    }


    // ==========================================
    // HÀM HỖ TRỢ (HELPER)
    // ==========================================

    // Hàm dùng chung để chuyển đổi từ ResultSet của Database thành Object Booking
    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setEmployeeId(rs.getInt("employee_id"));

        // Xử lý support_staff_id vì nó có thể null trong DB
        int supportId = rs.getInt("support_staff_id");
        if (!rs.wasNull()) {
            booking.setSupportStaffId(supportId);
        }

        booking.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        booking.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        booking.setParticipantsCount(rs.getInt("participants_count"));
        booking.setBookingStatus(rs.getString("booking_status"));
        booking.setPrepStatus(rs.getString("prep_status"));
        return booking;
    }

    public boolean approveAndAssignBooking(int bookingId, int supportId) {
        String sql = "UPDATE bookings SET booking_status = 'APPROVED', support_staff_id = ?, prep_status = 'PREPARING' WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supportId);
            pstmt.setInt(2, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi DB: " + e.getMessage());
            return false;
        }
    }

    // 1. Lấy danh sách tất cả các đơn đã được duyệt (dành cho Admin)
    public List<Booking> getAllApprovedBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE booking_status = 'APPROVED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách đơn đã duyệt: " + e.getMessage());
        }
        return list;
    }

    // 2. Xóa đơn đặt phòng (Admin)
    public boolean deleteBooking(int bookingId) {
        // Lưu ý: Nếu DB không để ON DELETE CASCADE, bạn cần xóa ở bảng booking_equipment và booking_services trước
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa đơn đặt phòng: " + e.getMessage());
            return false;
        }
    }

    // 3. Lấy lịch họp sắp tới đã được duyệt (dành cho Employee)
    public List<Booking> getUpcomingApprovedBookings(int employeeId) {
        List<Booking> list = new ArrayList<>();
        // Lấy các đơn APPROVED và thời gian bắt đầu lớn hơn hiện tại
        String sql = "SELECT * FROM bookings WHERE employee_id = ? AND booking_status = 'APPROVED' AND start_time > NOW() ORDER BY start_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy lịch họp sắp tới: " + e.getMessage());
        }
        return list;
    }
}