package com.meeting.dao;

import com.meeting.model.User;
import com.meeting.util.DBConnection;
import com.meeting.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("user_name"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setFullName(rs.getString("full_name"));
                user.setDepartment(rs.getString("department"));
                user.setPhone(rs.getString("phone"));
                return user;
            }
        }

        return null;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        // Lưu ý: Đảm bảo đường dẫn DBConnection của bạn là chuẩn xác
        try (Connection conn = com.meeting.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("user_name"));
                    user.setFullName(rs.getString("full_name"));

                    // Lấy thêm các thông tin mới
                    user.setEmail(rs.getString("email"));
                    user.setDepartment(rs.getString("department"));
                    user.setPhone(rs.getString("phone"));

                    return user; // Trả về đối tượng user chứa đầy đủ thông tin
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy ID này trong DB
    }

    public boolean isUsernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE user_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (user_name, password, full_name, email, phone, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getRole());

            return pstmt.executeUpdate() > 0;
        }
    }

    public User login(String username, String plainPassword) throws SQLException {
        // 1. CHỈ tìm kiếm theo user_name, bỏ phần AND password = ? đi
        String sql = "SELECT * FROM users WHERE user_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            // Bỏ luôn dòng setString thứ 2 vì SQL giờ chỉ có 1 dấu chấm hỏi

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 2. Lấy mật khẩu đã mã hóa từ Database lên
                    String hashedPassword = rs.getString("password");

                    // 3. Dùng PasswordUtil để kiểm tra mật khẩu người dùng nhập có khớp với Hash không
                    if (PasswordUtil.checkPassword(plainPassword, hashedPassword)) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        // Sửa lại setUsername cho đúng với các hàm khác của bạn
                        user.setUsername(rs.getString("user_name"));
                        user.setPassword(hashedPassword);
                        user.setFullName(rs.getString("full_name"));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setRole(rs.getString("role"));
                        user.setDepartment(rs.getString("department")); // Bổ sung thêm cho đầy đủ

                        return user; // Đăng nhập thành công
                    }
                }
            }
        }
        return null; // Sai tài khoản hoặc mật khẩu
    }

    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (user_name, password, role, department, full_name, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getDepartment());
            pstmt.setString(5, user.getFullName());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getPhone());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateUserInfo(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, department = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = com.meeting.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getDepartment());
            pstmt.setString(3, user.getPhone());
            pstmt.setInt(4, user.getUserId());
            return pstmt.executeUpdate() > 0;
        }
    }

    // Lấy danh sách tất cả người dùng
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("user_name"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setFullName(rs.getString("full_name"));
                user.setDepartment(rs.getString("department"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                userList.add(user);
            }
        }

        return userList;
    }


    // Lấy danh sách người dùng theo Vai trò (Role)
    public List<User> getUsersByRole(String role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";

        try (Connection conn = com.meeting.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.toUpperCase());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("user_name"));
                user.setFullName(rs.getString("full_name"));
                user.setDepartment(rs.getString("department"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                // Chú ý: Đảm bảo class User của bạn có setRole, nếu không có thể bỏ qua dòng dưới
                // user.setRole(rs.getString("role"));

                list.add(user);
            }
        } catch (SQLException e) {
            System.out.println(" [LỖI DB] Không thể lấy danh sách user: " + e.getMessage());
        }
        return list;
    }
}