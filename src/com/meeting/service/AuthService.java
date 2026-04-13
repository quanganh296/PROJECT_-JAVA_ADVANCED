package com.meeting.service;

import com.meeting.dao.UserDAO;
import com.meeting.model.User;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();


    //Xử lý logic đăng nhập
    //return Đối tượng User nếu thành công, null nếu thất bại

    public User login(String username, String password) {
        // Bạn có thể thêm logic kiểm tra định dạng username/password ở đây trước khi gọi DAO
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("[LỖI] Tên đăng nhập hoặc mật khẩu không được để trống.");
            return null;
        }

        User user;
        try {
            user = userDAO.login(username, password);
        } catch (SQLException e) {
            System.out.println("[LỖI] Lỗi hệ thống khi đăng nhập: " + e.getMessage());
            return null;
        }

        if (user != null) {
            System.out.println("[THÀNH CÔNG] Chào mừng " + user.getFullName() + "!");
        } else {
            System.out.println("[THẤT BẠI] Sai tên đăng nhập hoặc mật khẩu.");
        }

        return user;
    }

    /**
     * Logic đăng ký tài khoản mới (Mặc định là EMPLOYEE)
     */
    public boolean register(User newUser) {
        // Kiểm tra xem username đã tồn tại chưa (có thể thêm hàm checkUsername vào UserDAO)
        // Mặc định gán role là EMPLOYEE để bảo mật
        newUser.setRole("EMPLOYEE");
        try {
            return userDAO.register(newUser);
        } catch (SQLException e) {
            System.out.println("[LỖI] Lỗi hệ thống khi đăng ký: " + e.getMessage());
            return false;
        }
    }
}