package com.meeting.presentation;

import com.meeting.dao.*;
import com.meeting.model.*;
import com.meeting.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeMenu {
    private final RoomDAO roomDAO = new RoomDAO();
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final BookingEquipmentDAO bookingEquipmentDAO = new BookingEquipmentDAO();
    private final BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();

    private final int currentUserId;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EmployeeMenu(int userId) {
        this.currentUserId = userId;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("   HỆ THỐNG ĐẶT PHÒNG HỌP (NHÂN VIÊN)    ");
            System.out.println("=========================================");
            System.out.println("1. Đặt phòng họp mới");
            System.out.println("2. Xem lịch sử đặt phòng của tôi");
            System.out.println("3. Xem lịch họp sắp tới (Đã duyệt)");
            System.out.println("0. Đăng xuất");
            System.out.println("=========================================");

            int choice = ValidationUtil.getInt("Nhập lựa chọn của bạn: ", "Vui lòng nhập số!");

            switch (choice) {
                case 1:
                    bookNewRoom();
                    break;
                case 2:
                    viewMyBookings();
                    break;
                case 3:
                    viewUpcomingMeetings();
                    break;
                case 0:
                    System.out.println("[THÔNG BÁO] Đã đăng xuất.");
                    return;
                default:
                    System.out.println("[LỖI] Lựa chọn không hợp lệ!");
            }
        }
    }

    private void bookNewRoom() {
        System.out.println("\n--- ĐẶT PHÒNG HỌP MỚI ---");
        try {
            // 1. Hiển thị danh sách phòng họp
            List<Room> rooms = roomDAO.getAllRooms();
            System.out.println("\n--- DANH SÁCH PHÒNG HỌP ---");
            System.out.printf("| %-5s | %-20s | %-10s |\n", "ID", "Tên Phòng", "Sức chứa");
            System.out.println("--------------------------------------------------");
            for (Room r : rooms) {
                System.out.printf("| %-5d | %-20s | %-10d |\n", r.getRoomId(), r.getRoomName(), r.getCapacity());
            }

            int roomId = ValidationUtil.getInt("Nhập ID phòng muốn chọn: ", "Lỗi nhập số!");
            LocalDateTime start = ValidationUtil.getDateTime("Giờ bắt đầu (dd/MM/yyyy HH:mm): ");
            LocalDateTime end = ValidationUtil.getDateTime("Giờ kết thúc (dd/MM/yyyy HH:mm): ");
            int count = ValidationUtil.getInt("Số lượng người tham gia: ", "Lỗi nhập số!");

            // 2. Kiểm tra phòng trống
            if (!bookingDAO.isRoomAvailable(roomId, start, end)) {
                System.out.println("[LỖI] Rất tiếc, phòng đã có lịch trong khoảng thời gian này!");
                return;
            }

            // 3. Tạo đơn đặt phòng (Lưu vào DB trước để lấy ID)
            Booking newBooking = new Booking();
            newBooking.setRoomId(roomId);
            newBooking.setEmployeeId(currentUserId);
            newBooking.setStartTime(start);
            newBooking.setEndTime(end);
            newBooking.setParticipantsCount(count);

            int realBookingId = bookingDAO.insertBooking(newBooking);

            if (realBookingId != -1) {
                System.out.println("\n[HỆ THỐNG] Đã khởi tạo đơn hàng #" + realBookingId);

                // 4. Chọn thiết bị đi kèm
                addEquipmentsToBooking(realBookingId);

                // 5. Chọn dịch vụ đi kèm
                addServicesToBooking(realBookingId);

                // 6. THÔNG BÁO THÀNH CÔNG TỔNG KẾT
                System.out.println("\n****************************************************");
                System.out.println("* *");
                System.out.println("* [CHÚC MỪNG] ĐẶT PHÒNG THÀNH CÔNG!           *");
                System.out.println("* *");
                System.out.printf("* Mã đơn hàng: #%-34d *\n", realBookingId);
                System.out.printf("* Thời gian: %-36s *\n", start.format(dtf) + " -> " + end.getHour() + ":" + end.getMinute());
                System.out.println("* Trạng thái: ĐANG CHỜ PHÊ DUYỆT                  *");
                System.out.println("* *");
                System.out.println("****************************************************");
                System.out.println("-> Bạn có thể kiểm tra trạng thái ở mục 'Lịch sử đặt phòng'.");

            } else {
                System.out.println("[LỖI] Không thể lưu đơn đặt phòng. Vui lòng thử lại!");
            }
        } catch (Exception e) {
            System.out.println("[LỖI] Hệ thống gặp sự cố: " + e.getMessage());
        }
    }

    private void addEquipmentsToBooking(int bookingId) {
        try {
            List<Equipment> equipmentList = equipmentDAO.getAllEquipments();
            if (equipmentList.isEmpty()) return;

            System.out.println("\n--- DANH SÁCH THIẾT BỊ HỖ TRỢ ---");
            System.out.printf("| %-5s | %-20s | %-10s |\n", "ID", "Tên thiết bị", "Sẵn có");
            System.out.println("--------------------------------------------------");
            for (Equipment e : equipmentList) {
                System.out.printf("| %-5d | %-20s | %-10d |\n", e.getEquipmentId(), e.getEquipmentName(), e.getAvailableQuantity());
            }

            while (true) {
                int eqId = ValidationUtil.getInt("Nhập ID thiết bị (Nhập 0 để xong): ", "Lỗi!");
                if (eqId == 0) break;
                int qty = ValidationUtil.getInt("Số lượng mượn: ", "Lỗi!");

                BookingEquipment be = new BookingEquipment();
                be.setBookingId(bookingId);
                be.setEquipmentId(eqId);
                be.setQuantity(qty);

                if (bookingEquipmentDAO.addEquipmentToBooking(be)) {
                    System.out.println(" -> Đã thêm " + qty + " thiết bị vào đơn.");
                } else {
                    System.out.println(" -> [LỖI] Không thể thêm. Kiểm tra lại ID hoặc số lượng kho.");
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi chọn thiết bị: " + e.getMessage());
        }
    }

    private void addServicesToBooking(int bookingId) {
        try {
            // Giả sử bạn có ServiceDAO
            List<Service> services = serviceDAO.getAllServices();
            if (services.isEmpty()) return;

            System.out.println("\n--- DANH SÁCH DỊCH VỤ ĐI KÈM ---");
            System.out.printf("| %-5s | %-20s | %-10s |\n", "ID", "Tên dịch vụ", "Đơn giá");
            System.out.println("--------------------------------------------------");
            for (Service s : services) {
                System.out.printf("| %-5d | %-20s | %-10.0f |\n", s.getServiceId(), s.getServiceName(), s.getUnitPrice());
            }

            while (true) {
                int svId = ValidationUtil.getInt("Nhập ID dịch vụ (Nhập 0 để xong): ", "Lỗi!");
                if (svId == 0) break;
                int qty = ValidationUtil.getInt("Số lượng/Suất: ", "Lỗi!");

                BookingService bs = new BookingService();
                bs.setBookingId(bookingId);
                bs.setServiceId(svId);
                bs.setQuantity(qty);

                if (bookingServiceDAO.addServiceToBooking(bs)) {
                    System.out.println(" -> Đã thêm dịch vụ thành công.");
                } else {
                    System.out.println(" -> [LỖI] Không thể thêm dịch vụ.");
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi chọn dịch vụ: " + e.getMessage());
        }
    }

    private void viewMyBookings() {
        List<Booking> list = bookingDAO.getBookingsByEmployeeId(currentUserId);
        displayBookingTable(list, "LỊCH SỬ ĐẶT PHÒNG CỦA TÔI");
    }

    private void viewUpcomingMeetings() {
        List<Booking> list = bookingDAO.getUpcomingApprovedBookings(currentUserId);
        displayBookingTable(list, "LỊCH HỌP SẮP TỚI (ĐÃ DUYỆT)");
    }

    private void displayBookingTable(List<Booking> list, String title) {
        if (list.isEmpty()) {
            System.out.println("[THÔNG BÁO] Không có dữ liệu hiển thị.");
            return;
        }
        System.out.println("\n--- " + title + " ---");
        String header = String.format("| %-5s | %-15s | %-16s | %-10s | %-12s |", "ID", "Phòng", "Bắt đầu", "Duyệt", "Chuẩn bị");
        System.out.println("-".repeat(header.length()));
        System.out.println(header);
        System.out.println("-".repeat(header.length()));
        for (Booking b : list) {
            try {
                String roomName = roomDAO.getRoomById(b.getRoomId()).getRoomName();
                System.out.printf("| %-5d | %-15s | %-16s | %-10s | %-12s |\n",
                        b.getBookingId(), roomName, b.getStartTime().format(dtf),
                        b.getBookingStatus(), (b.getPrepStatus() == null ? "N/A" : b.getPrepStatus()));
            } catch (SQLException e) { /**/ }
        }
        System.out.println("-".repeat(header.length()));
    }
}