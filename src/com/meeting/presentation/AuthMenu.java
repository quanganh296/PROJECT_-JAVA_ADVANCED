package com.meeting.presentation;

import com.meeting.dao.UserDAO;
import com.meeting.model.User;
import com.meeting.service.AuthService;
import com.meeting.util.ValidationUtil;

import java.sql.SQLException;

public class AuthMenu {
    private final UserDAO userDAO = new UserDAO();
    private final AuthService authService = new AuthService();

    public void displayMenu() {
        while (true) {
            try {
                System.out.println("\n=========================================");
                System.out.println("   CHÀO MỪNG ĐẾN VỚI HỆ THỐNG ĐẶT PHÒNG  ");
                System.out.println("=========================================");
                System.out.println("1. Đăng nhập");
                System.out.println("2. Đăng ký tài khoản (Nhân viên)");
                System.out.println("0. Thoát chương trình");
                System.out.println("-----------------------------------------");

                int choice = ValidationUtil.getInt("Nhập lựa chọn của bạn (0-2): ", "Lựa chọn phải là số nguyên!");

                switch (choice) {
                    case 1:
                        handleLogin();
                        break;
                    case 2:
                        handleRegister();
                        break;
                    case 0:
                        System.out.println("[THÔNG BÁO] Cảm ơn bạn đã sử dụng hệ thống. Tạm biệt!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("[LỖI] Lựa chọn không hợp lệ!");
                }
            } catch (SQLException e) {
                System.out.println("[LỖI HỆ THỐNG] Có lỗi xảy ra với cơ sở dữ liệu: " + e.getMessage());
            }
        }
    }

    private void handleLogin() throws SQLException {
        System.out.println("\n--- ĐĂNG NHẬP ---");
        // Sử dụng .readPassword() nếu bạn đã triển khai trong ValidationUtil để bảo mật
        String username = ValidationUtil.getString("Username: ");
        String password = ValidationUtil.getString("Password: ");

        // Sử dụng authService để xử lý logic đăng nhập
        User authenticatedUser = authService.login(username, password);

        if (authenticatedUser != null) {
            routeUserToMenu(authenticatedUser);
        }
    }

    private void handleRegister() {
        System.out.println("\n--- ĐĂNG KÝ TÀI KHOẢN MỚI ---");
        String username = ValidationUtil.getString("Nhập tên đăng nhập: ");

        try {
            if (userDAO.isUsernameExists(username)) {
                System.out.println("[LỖI] Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác!");
                return;
            }
        } catch (SQLException e) {
            System.out.println("[LỖI] Lỗi hệ thống khi kiểm tra tên đăng nhập: " + e.getMessage());
            return;
        }

        String password = ValidationUtil.getString("Nhập mật khẩu: ");
        String fullName = ValidationUtil.getString("Nhập họ và tên: ");
        String email = ValidationUtil.getEmail("Nhập email: ");
        String phone = ValidationUtil.getPhone("Nhập số điện thoại: ");
        String department = ValidationUtil.getString("Nhập phòng ban (VD: IT, HR, Sales...): ");

        User newUser = new User(0, username, password, "EMPLOYEE", department, fullName, email, phone);

        try {
            if (userDAO.register(newUser)) {
                System.out.println("[THÀNH CÔNG] Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
            } else {
                System.out.println("[LỖI] Đăng ký thất bại. Vui lòng thử lại sau.");
            }
        } catch (SQLException e) {
            System.out.println("[LỖI] Lỗi hệ thống khi đăng ký: " + e.getMessage());
        }
    }

    private void routeUserToMenu(User user) throws SQLException {
        String role = user.getRole().toUpperCase();

        switch (role) {
            case "ADMIN":
                AdminMenu adminMenu = new AdminMenu();
                adminMenu.displayMenu();
                break;
            case "EMPLOYEE":
                EmployeeMenu employeeMenu = new EmployeeMenu(user.getUserId());
                employeeMenu.displayMenu();
                break;
            case "SUPPORT":
                SupportMenu supportMenu = new SupportMenu(user.getUserId());
                // SỬA LỖI TẠI ĐÂY: Phải gọi displayMenu() thay vì handleTaskUpdate()
                supportMenu.displayMenu();
                break;
            default:
                System.out.println("[LỖI] Vai trò tài khoản không hợp lệ!");
        }
    }
}