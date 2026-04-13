package com.meeting.util;

import java.io.Console;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Scanner sc = new Scanner(System.in);

    //Ktra định dạng
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^0\\d{9}$";

    //Định dạng  ngày giờ
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Khong được bỏ trống
    public static String getString(String prompt) {
        String input;
        while(true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if(input.isEmpty()) {
                System.out.println("Lỗi: không được để trống. Vui lòng nhập lại");
            } else {
                return input;
            }
        }
    }

    //Nhập số nguyên dương(Sức chứa, Số lượng,..)
    public static int getInt(String prompt, String errorMessage) {
        while(true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                if(value < 0) {
                    System.out.println("Lỗi: Phải nhập số >=0. Vui lòng nhập lại");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println(" " + errorMessage);
            }
        }
    }

    //Nhập số thập phân
    public static double getDouble(String prompt, String errorMessage) {
        while(true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(sc.nextLine().trim());
                if(value < 0) {
                    System.out.println("Lỗi: Giá trị không được phép âm. Vui lòng nhập lại");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println(" " + errorMessage);
            }
        }
    }

    //Nhập Email
    public static String getEmail(String prompt) {
        while(true) {
            String email = getString(prompt);
            if(Pattern.matches(EMAIL_REGEX, email)) {
                return email;
            } else {
                System.out.println("Lỗi: Định dạng email khng hợp lệ. Vui lòng nhập lại");
            }
        }
    }
    //Nhập số đt
    //Lấy Số điện thoại hợp lệ
    public static String getPhone(String message) {
        while (true) {
            String input = getString(message);
            // Regex kiểm tra SĐT Việt Nam (Bắt đầu bằng 0, theo sau là 9 số)
            if (input.matches("^0[0-9]{9}$")) {
                return input;
            }
            System.out.println("[LỖI] Số điện thoại không hợp lệ (Phải bao gồm 10 chữ số và bắt đầu bằng số 0).");
        }
    }

    //Nhập ngày
    public static LocalDateTime getFutureDateTime(String prompt) {
        while(true) {
            System.out.print(prompt + " (Định dạng: yyy-mm-dd (VD:2026-05-09): ");
            String input = sc.nextLine().trim();

            try {
                LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
                if(dateTime.isBefore(LocalDateTime.now())) {
                    System.out.println("Lỗi: Thời gian không được nằm trong quá khứ");
                } else {
                    return dateTime;
                }
            } catch(DateTimeParseException e ) {
                System.out.println("Lỗi: Định dạng ngày giờ không hợp lệ. Vui lòng nhập lại");
            }
        }
    }

    // Hàm nhập mật khẩu ẩn ký tự
//    public static String getPassword(String message) {
//        Console console = System.console();
//
//        if (console == null) {
//            // Nếu chạy trong IDE, console sẽ null.
//            // Ta phải dùng Scanner (hiện chữ) để chương trình không bị crash.
//            System.out.print(message + " (IDE mode - hiện chữ): ");
//            Scanner sc = new Scanner(System.in);
//            return sc.nextLine();
//        }
//
//        // Nếu chạy bằng CMD/Terminal thật:
//        // Mật khẩu sẽ ẩn hoàn toàn khi gõ (không hiện cả dấu *)
//        char[] passwordArray = console.readPassword(message);
//        return new String(passwordArray);
//    }

    public static LocalDateTime getDateTime(String prompt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Lỗi: Không được để trống. Vui lòng nhập lại!");
                continue;
            }
            try {
                // Ép kiểu chuỗi người dùng nhập thành LocalDateTime
                return LocalDateTime.parse(input, dtf);
            } catch (DateTimeParseException e) {
                System.out.println("Lỗi: Định dạng ngày giờ không hợp lệ!");
                System.out.println("Vui lòng nhập đúng định dạng dd/MM/yyyy HH:mm (Ví dụ: 15/05/2026 14:30)");
            }
        }
    }
}
