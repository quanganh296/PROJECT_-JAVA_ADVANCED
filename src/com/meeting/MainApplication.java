package com.meeting;

import com.meeting.dao.UserDAO;
import com.meeting.model.User;
import com.meeting.presentation.AuthMenu;

import java.sql.SQLException;

public class MainApplication {
    public static void main(String[] args) throws SQLException {
        System.out.println("[THÔNG BÁO] Đang khởi động Hệ thống Quản lý Đặt phòng họp...");

        // Tạo các tài khoản mặc định để test nếu chưa có trong Database
        createDefaultAdmin();
        createDefaultEmployee();
        createDefaultSupport();

        // Khởi tạo và hiển thị Menu Đăng nhập/Đăng ký
        AuthMenu authMenu = new AuthMenu();
        authMenu.displayMenu();
    }

//    // 1. Hàm tạo Admin tự động
    private static void createDefaultAdmin() {
        try {
            UserDAO userDAO = new UserDAO();
            String adminUsername = "admin";

            if (!userDAO.isUsernameExists(adminUsername)) {
                User adminUser = new User(0, adminUsername, "admin123", "ADMIN", "Ban Giám Đốc", "Quản Trị Viên", "admin@congty.com", "0999999999");
                if (userDAO.register(adminUser)) {
                    System.out.println("=========================================");
                    System.out.println("[THÀNH CÔNG] ĐÃ TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH!");
                    System.out.println("Tên đăng nhập: admin | Mật khẩu: admin123");
                    System.out.println("=========================================\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống khi tạo Admin: " + e.getMessage());
        }
    }

//     2. Hàm tạo Nhân viên (Employee) tự động
    private static void createDefaultEmployee() {
        try {
            UserDAO userDAO = new UserDAO();
            String empUsername = "nv01";

            if (!userDAO.isUsernameExists(empUsername)) {
                User empUser = new User(0, empUsername, "123", "EMPLOYEE", "Phòng IT", "Nhân Viên Test 01", "nv01@congty.com", "0123456789");
                if (userDAO.register(empUser)) {
                    System.out.println("=========================================");
                    System.out.println("[THÀNH CÔNG] ĐÃ TẠO TÀI KHOẢN NHÂN VIÊN MẶC ĐỊNH!");
                    System.out.println("Tên đăng nhập: nv01 | Mật khẩu: 123");
                    System.out.println("=========================================\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống khi tạo Employee: " + e.getMessage());
        }
    }

    // 3. Hàm tạo Nhân viên Hỗ trợ (Support Staff) tự động
    private static void createDefaultSupport() {
        try {
            UserDAO userDAO = new UserDAO();
            String supUsername = "sp01";

            if (!userDAO.isUsernameExists(supUsername)) {
                User supUser = new User(0, supUsername, "123", "SUPPORT", "Support Team", "Nhân Viên Hỗ Trợ 01", "sp01@congty.com", "0987654321");
                if (userDAO.register(supUser)) {
                    System.out.println("=========================================");
                    System.out.println("[THÀNH CÔNG] ĐÃ TẠO TÀI KHOẢN SUPPORT MẶC ĐỊNH!");
                    System.out.println("Tên đăng nhập: sp01 | Mật khẩu: 123");
                    System.out.println("=========================================\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống khi tạo Support: " + e.getMessage());
        }
    }
}