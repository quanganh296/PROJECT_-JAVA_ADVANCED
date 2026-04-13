package com.meeting.presentation;

import com.meeting.model.Booking;
import com.meeting.model.BookingEquipment;
import com.meeting.model.BookingService;
import com.meeting.service.SupportService;
import com.meeting.util.ValidationUtil;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SupportMenu {

    private final SupportService supportService = new SupportService();
    private final int currentUserId;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public SupportMenu(int userId) {
        this.currentUserId = userId;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("  HỆ THỐNG QUẢN LÝ (NHÂN VIÊN HỖ TRỢ)    ");
            System.out.println("=========================================");
            System.out.println("1. Danh sách booking được phân công");
            System.out.println("0. Đăng xuất");
            System.out.println("=========================================");

            int choice = ValidationUtil.getInt("Nhập lựa chọn của bạn: ", "Vui lòng nhập số!");

            switch (choice) {
                case 1:
                    handleTaskUpdate();
                    break;
                case 0:
                    System.out.println("[THÔNG BÁO] Đã đăng xuất khỏi tài khoản Support.");
                    return;
                default:
                    System.out.println("[LỖI] Lựa chọn không hợp lệ!");
            }
        }
    }

    private void handleTaskUpdate() {
        // 1. Lấy danh sách task từ Service
        List<Booking> allTasks = supportService.getAssignedTasks(currentUserId);

        // 2. Lọc: Chỉ lấy đơn CHƯA HOÀN TẤT (prepStatus khác 'READY') và Sắp xếp theo thời gian bắt đầu
        List<Booking> incompleteTasks = allTasks.stream()
                .filter(b -> !"READY".equalsIgnoreCase(b.getPrepStatus()))
                .sorted(Comparator.comparing(Booking::getStartTime))
                .collect(Collectors.toList());

        if (incompleteTasks.isEmpty()) {
            System.out.println("\n[THÔNG BÁO] Tuyệt vời! Bạn không có công việc nào chưa hoàn tất.");
            return;
        }

        // 3. Hiển thị danh sách dạng bảng
        System.out.println("\n--- DANH SÁCH CÔNG VIỆC CHƯA HOÀN TẤT (SẮP XẾP THEO NGÀY) ---");
        String header = String.format("| %-5s | %-15s | %-16s | %-16s | %-18s |",
                "ID", "Phòng (ID)", "Bắt đầu", "Kết thúc", "Trạng thái chuẩn bị");
        System.out.println("-".repeat(header.length()));
        System.out.println(header);
        System.out.println("-".repeat(header.length()));

        for (Booking b : incompleteTasks) {
            System.out.printf("| %-5d | Phòng %-9d | %-16s | %-16s | %-18s |\n",
                    b.getBookingId(),
                    b.getRoomId(),
                    b.getStartTime().format(dtf),
                    b.getEndTime().format(dtf),
                    (b.getPrepStatus() == null ? "CHƯA XỬ LÝ" : b.getPrepStatus()));
        }
        System.out.println("-".repeat(header.length()));

        // 4. Chọn đơn để cập nhật
        int bookingId = ValidationUtil.getInt("\nNhập ID đơn để xem chi tiết & cập nhật (Nhập 0 để quay lại): ", "Vui lòng nhập số!");
        if (bookingId == 0) return;

        // Kiểm tra ID nhập vào có nằm trong danh sách được phân công không
        Booking selected = incompleteTasks.stream()
                .filter(b -> b.getBookingId() == bookingId)
                .findFirst()
                .orElse(null);

        if (selected == null) {
            System.out.println("[LỖI] ID đơn không hợp lệ hoặc đơn này đã hoàn tất!");
            return;
        }

        showDetailAndWriteStatus(bookingId);
    }

    private void showDetailAndWriteStatus(int bookingId) {
        System.out.println("\n--- CHI TIẾT YÊU CẦU TRANG THIẾT BỊ & DỊCH VỤ ---");

        // Hiển thị thiết bị
        System.out.println("[1] THIẾT BỊ DI ĐỘNG:");
        List<BookingEquipment> eqs = supportService.getBookingEquipments(bookingId);
        if (eqs.isEmpty()) System.out.println("    - Không có thiết bị yêu cầu.");
        else eqs.forEach(e -> System.out.println("    + Mã thiết bị: " + e.getEquipmentId() + " | Số lượng: " + e.getQuantity()));

        // Hiển thị dịch vụ
        System.out.println("[2] DỊCH VỤ ĐI KÈM:");
        List<BookingService> svs = supportService.getBookingServices(bookingId);
        if (svs.isEmpty()) System.out.println("    - Không có dịch vụ yêu cầu.");
        else svs.forEach(s -> System.out.println("    + Mã dịch vụ: " + s.getServiceId() + " | Số lượng: " + s.getQuantity()));

        // 5. Cập nhật trạng thái chuẩn bị
        System.out.println("\n--- CẬP NHẬT TIẾN ĐỘ ---");
        System.out.println("1. Đang thực hiện (PREPARING)");
        System.out.println("2. Đã sẵn sàng (READY) - Hoàn tất");
        System.out.println("3. Thiếu thiết bị (MISSING_EQUIPMENT)");
        System.out.println("0. Quay lại");

        int choice = ValidationUtil.getInt("Chọn trạng thái mới (0-3): ", "Vui lòng chọn số!");
        String status;
        switch (choice) {
            case 1: status = "PREPARING"; break;
            case 2: status = "READY"; break;
            case 3: status = "MISSING_EQUIPMENT"; break;
            default: return;
        }

        if (supportService.updatePrepStatus(bookingId, status)) {
            System.out.println("[THÀNH CÔNG] Đã cập nhật trạng thái đơn #" + bookingId);
        } else {
            System.out.println("[LỖI] Không thể cập nhật trạng thái.");
        }
    }
}