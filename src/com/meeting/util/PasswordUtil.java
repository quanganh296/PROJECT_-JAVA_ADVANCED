package com.meeting.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtil {

    // 1. Hàm mã hóa mật khẩu (Dùng khi Đăng ký)
    public static String hashPassword(String password) {
        try {
            // Sử dụng thuật toán SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Biến mật khẩu thành mảng byte
            byte[] hashBytes = md.digest(password.getBytes());

            // Chuyển mảng byte thành chuỗi Base64 để dễ lưu vào Database
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("[LỖI] Hệ thống không hỗ trợ thuật toán mã hóa này: " + e.getMessage());
            return null;
        }
    }

    // 2. Hàm kiểm tra mật khẩu (Dùng khi Đăng nhập)
    public static boolean checkPassword(String plainPassword, String hashedPasswordFromDB) {
        // Mã hóa mật khẩu người dùng vừa nhập
        String newHash = hashPassword(plainPassword);

        // So sánh với mật khẩu đã mã hóa lưu trong Database
        if (newHash != null && newHash.equals(hashedPasswordFromDB)) {
            return true; // Khớp mật khẩu
        }
        return false; // Sai mật khẩu
    }
}