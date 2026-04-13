package com.meeting.presentation;

import com.meeting.model.Booking;
import com.meeting.service.SupportService;
import com.meeting.util.ValidationUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupportMenu {

    private final SupportService supportService = new SupportService();
    private final int currentUserId;

    public SupportMenu(int userId) {
        this.currentUserId = userId;
    }

    // 1. HÀM HIỂN THỊ MENU (Vòng lặp giữ user ở lại)
    public void displayMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("  HỆ THỐNG QUẢN LÝ (NHÂN VIÊN HỖ TRỢ)    ");
            System.out.println("=========================================");
            System.out.println("1. Xem danh sách công việc & Cập nhật trạng thái");
            System.out.println("0. Đăng xuất");
            System.out.println("=========================================");

            int choice = ValidationUtil.getInt("Nhập lựa chọn của bạn: ", "Vui lòng nhập số!");

            switch (choice) {
                case 1:
                    handleTaskUpdate(); // Gọi hàm xử lý công việc
                    break;
                case 0:
                    System.out.println("[THÔNG BÁO] Đã đăng xuất khỏi tài khoản Support.");
                    return; // Thoát vòng lặp, quay về AuthMenu
                default:
                    System.out.println("[LỖI] Lựa chọn không hợp lệ!");
            }
        }
    }

    // 2. HÀM XỬ LÝ CÔNG VIỆC
    private void handleTaskUpdate() {
        System.out.println("\n--- DANH SÁCH CÔNG VIỆC ĐƯỢC PHÂN CÔNG ---");

        // 1. Lấy danh sách task
        List<Booking> tasks = supportService.getAssignedTasks(currentUserId);
        if (tasks.isEmpty()) {
            System.out.println("[THÔNG BÁO] Bạn không có công việc nào chưa hoàn tất.");
            return;
        }

        // 2. Hiển thị bảng danh sách (Rút gọn để dễ nhìn)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s |\n", "ID", "Phòng", "Thời gian bắt đầu", "Trạng thái hiện tại");
        System.out.println("--------------------------------------------------------------------------------");
        for (Booking b : tasks) {
            System.out.printf("| %-5d | %-15s | %-20s | %-15s |\n",
                    b.getBookingId(), "ID: " + b.getRoomId(), b.getStartTime().format(dtf), b.getPrepStatus());
        }
        System.out.println("--------------------------------------------------------------------------------");

        // 3. Chọn đơn để cập nhật
        int bookingId = ValidationUtil.getInt("\nNhập ID đơn muốn cập nhật (Nhập 0 để quay lại): ", "ID phải là số!");
        if (bookingId == 0) return;

        // KIỂM TRA BẢO MẬT: Đảm bảo ID nhập vào nằm trong danh sách tasks của nhân viên này
        Booking selectedBooking = tasks.stream()
                .filter(t -> t.getBookingId() == bookingId)
                .findFirst()
                .orElse(null);

        if (selectedBooking == null) {
            System.out.println("[LỖI] ID đơn không hợp lệ hoặc không thuộc phạm vi quản lý của bạn!");
            return;
        }

        // 4. Hiển thị chi tiết thiết bị & dịch vụ cần chuẩn bị
        System.out.println("\n>>> CHI TIẾT YÊU CẦU CHO ĐƠN #" + bookingId);
        System.out.println("-------------------------------------------");

        System.out.println("[1] THIẾT BỊ DI ĐỘNG CẦN MANG ĐẾN:");
        var equipments = supportService.getBookingEquipments(bookingId);
        if (equipments.isEmpty()) System.out.println("    - Không có thiết bị yêu cầu.");
        else equipments.forEach(e -> System.out.println("    + Mã thiết bị: " + e.getEquipmentId() + " | Số lượng: " + e.getQuantity()));

        System.out.println("[2] DỊCH VỤ ĐI KÈM:");
        var services = supportService.getBookingServices(bookingId);
        if (services.isEmpty()) System.out.println("    - Không có dịch vụ yêu cầu.");
        else services.forEach(s -> System.out.println("    + Mã dịch vụ: " + s.getServiceId() + " | Số lượng: " + s.getQuantity()));

        // 5. Cập nhật trạng thái chuẩn bị (Phòng & Thiết bị)
        System.out.println("\n--- CẬP NHẬT TIẾN ĐỘ CHUẨN BỊ ---");
        System.out.println("1. Đang thực hiện (PREPARING)");
        System.out.println("2. Đã sẵn sàng (READY) - Đã xong cả phòng và thiết bị");
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

        // 6. Lưu vào DB
        if (supportService.updatePrepStatus(bookingId, status)) {
            System.out.println("[THÀNH CÔNG] Đã cập nhật trạng thái đơn #" + bookingId + " thành: " + status);
        } else {
            System.out.println("[THẤT BẠI] Lỗi kết nối Database.");
        }
    }
}