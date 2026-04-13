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
    private final BookingDAO bookingDAO = new BookingDAO(); // Sẽ mở khóa khi tạo xong BookingDAO
    private final BookingEquipmentDAO bookingEquipmentDAO = new BookingEquipmentDAO();
    private final BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();

    private final UserDAO userDAO = new UserDAO();

    private final int currentUserId; // Lưu ID của nhân viên đang đăng nhập

    // Constructor nhận vào ID của user khi họ đăng nhập thành công
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
            System.out.println("3. Hủy đơn đặt phòng");
            System.out.println("4. Quản lý hồ sơ cá nhân");
            System.out.println("0. Đăng xuất");
            System.out.println("-----------------------------------------");

            int choice = ValidationUtil.getInt("Nhập lựa chọn của bạn (0-4): ", "Lựa chọn phải là số nguyên!");

            switch (choice) {
                case 1:
                    bookNewRoom();
                    break;
                case 2:
                    viewMyBookings();
                    break;
                case 3:
                    cancelPendingBooking();
                    break;
                case 4: handleViewMyBookings();
                    break;
                case 5: manageProfile();
                    break;
                case 0:
                    System.out.println("[THÔNG BÁO] Đã đăng xuất thành công!");
                    return; // Thoát vòng lặp, quay lại AuthMenu
                default:
                    System.out.println("[LỖI] Lựa chọn không hợp lệ. Vui lòng thử lại!");
            }
        }
    }

    // 1. Xem danh sách phòng
    private void viewAvailableRooms() {
        System.out.println("\n--- DANH SÁCH PHÒNG HỌP ---");
        List<Room> rooms;
        try {
            rooms = roomDAO.getAllRooms();
        } catch (Exception e) {
            System.out.println("[LỖI] Không thể tải danh sách phòng: " + e.getMessage());
            return;
        }
        if (rooms.isEmpty()) {
            System.out.println("[CẢNH BÁO] Hiện chưa có phòng họp nào trên hệ thống.");
            return;
        }

        System.out.printf("%-5s | %-20s | %-10s | %-20s\n", "ID", "Tên phòng", "Sức chứa", "Vị trí");
        System.out.println("-----------------------------------------------------------------");
        for (Room r : rooms) {
            System.out.printf("%-5d | %-20s | %-10d | %-20s\n",
                    r.getRoomId(), r.getRoomName(), r.getCapacity(), r.getLocation());
        }
    }

    // 2. Chức năng đăng ký đặt phòng
    private void bookNewRoom() {

        System.out.println("DANH SÁCH PHÒNG TRỐNG");
        viewAvailableRooms();
        System.out.println("\n--- ĐẶT PHÒNG HỌP MỚI ---");

        // Nhập ID phòng và kiểm tra phòng
        int roomId;
        Room selectedRoom = null;
        while (true) {
            roomId = ValidationUtil.getInt("Nhập ID phòng muốn đặt: ", "Vui lòng nhập số!");
            try {
                selectedRoom = roomDAO.getRoomById(roomId);
                if (selectedRoom == null) {
                    System.out.println("[LỖI] Không tìm thấy phòng với ID này. Vui lòng nhập lại!");
                } else {
                    break;
                }
            } catch (SQLException e) {
                System.out.println("[LỖI DB] Lỗi khi kiểm tra phòng: " + e.getMessage());
                return;
            }
        }

        // Nhập số lượng người và kiểm tra sức chứa
        int participantsCount;
        while (true) {
            participantsCount = ValidationUtil.getInt("Nhập số lượng người tham gia: ", "Vui lòng nhập số!");
            if (participantsCount <= 0) {
                System.out.println("[LỖI] Số lượng người tham gia phải lớn hơn 0!");
            } else if (participantsCount > selectedRoom.getCapacity()) {
                System.out.println("[LỖI] Sức chứa của phòng này chỉ tối đa " + selectedRoom.getCapacity() + " người!");
                System.out.println("Vui lòng nhập lại số lượng phù hợp nhé.");
            } else {
                break;
            }
        }

        // Nhập thời gian và chặn đặt ở quá khứ
        LocalDateTime startTime;
        while (true) {
            startTime = ValidationUtil.getDateTime("Nhập thời gian bắt đầu (dd/MM/yyyy HH:mm): ");
            if (startTime.isBefore(LocalDateTime.now())) {
                System.out.println("[LỖI] Thời gian bắt đầu không được ở trong quá khứ. Vui lòng nhập lại!");
            } else {
                break;
            }
        }

        LocalDateTime endTime;
        while (true) {
            endTime = ValidationUtil.getDateTime("Nhập thời gian kết thúc (dd/MM/yyyy HH:mm): ");
            if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
                System.out.println("[LỖI] Thời gian kết thúc phải diễn ra sau thời gian bắt đầu. Vui lòng nhập lại!");
            } else {
                break;
            }
        }

        // Kiểm tra trùng lịch
        if (!bookingDAO.isRoomAvailable(roomId, startTime, endTime)) {
            System.out.println("[LỖI] Phòng đã được đặt trong khoảng thời gian này. Vui lòng chọn giờ/phòng khác!");
            return;
        }

        // Lưu đơn đặt phòng (Mặc định trạng thái PENDING)
        Booking newBooking = new Booking();
        newBooking.setRoomId(roomId);
        newBooking.setEmployeeId(currentUserId);
        newBooking.setStartTime(startTime);
        newBooking.setEndTime(endTime);
        newBooking.setParticipantsCount(participantsCount);
        newBooking.setBookingStatus("PENDING");

        int bookingId = bookingDAO.addBookingReturnId(newBooking);

        if (bookingId > 0) {
            System.out.println("\n[THÀNH CÔNG] Đã tạo đơn đặt phòng (ID: " + bookingId + ").");

            // Chọn thiết bị đi kèm
            // Chọn thiết bị đi kèm
            System.out.println("\n--- CHỌN THIẾT BỊ DI ĐỘNG ---");
            try {
                // Hiển thị danh sách thiết bị có sẵn
                List<Equipment> equipmentList = equipmentDAO.getAllEquipments(); // Đảm bảo EquipmentDAO của bạn có hàm này
                if (equipmentList != null && !equipmentList.isEmpty()) {
                    System.out.printf("| %-5s | %-25s |\n", "ID", "Tên thiết bị");
                    System.out.println("-------------------------------------");
                    for (Equipment eq : equipmentList) {
                        System.out.printf("| %-5d | %-25s |\n", eq.getEquipmentId(), eq.getEquipmentName());
                    }
                    System.out.println("-------------------------------------");
                } else {
                    System.out.println("[THÔNG BÁO] Hiện hệ thống chưa có thiết bị di động nào.");
                }
            } catch (SQLException e) {
                System.out.println("[LỖI DB] Không thể tải danh sách thiết bị: " + e.getMessage());
            }

            // Tiến hành nhập ID thiết bị
            while (true) {
                int eqId = ValidationUtil.getInt("Nhập ID thiết bị (Nhập 0 để bỏ qua/hoàn tất): ", "Vui lòng nhập số!");
                if (eqId == 0) break;
                int qty = ValidationUtil.getInt("Nhập số lượng: ", "Vui lòng nhập số!");
                bookingEquipmentDAO.addEquipmentToBooking(new BookingEquipment());
                System.out.println("Đã thêm thiết bị vào đơn!");
            }

            // Chọn dịch vụ đi kèm
            System.out.println("\n--- CHỌN DỊCH VỤ ĐI KÈM ---");
            try {
                // Hiển thị danh sách dịch vụ có sẵn
                List<Service> serviceList = serviceDAO.getAllServices(); // Đảm bảo ServiceDAO có hàm này
                if (serviceList != null && !serviceList.isEmpty()) {
                    System.out.printf("| %-5s | %-25s |\n", "ID", "Tên dịch vụ");
                    System.out.println("-------------------------------------");
                    for (Service sv : serviceList) {
                        System.out.printf("| %-5d | %-25s |\n", sv.getServiceId(), sv.getServiceName());
                    }
                    System.out.println("-------------------------------------");
                } else {
                    System.out.println("[THÔNG BÁO] Hiện hệ thống chưa có dịch vụ đi kèm nào.");
                }
            } catch (SQLException e) {
                System.out.println("[LỖI DB] Không thể tải danh sách dịch vụ: " + e.getMessage());
            }

            // Tiến hành nhập ID dịch vụ
            while (true) {
                int svId = ValidationUtil.getInt("Nhập ID dịch vụ (Nhập 0 để bỏ qua/hoàn tất): ", "Vui lòng nhập số!");
                if (svId == 0) break;
                int qty = ValidationUtil.getInt("Nhập số lượng: ", "Vui lòng nhập số!");
                bookingServiceDAO.addServiceToBooking(new BookingService(bookingId, svId, qty));
                System.out.println("Đã thêm dịch vụ vào đơn!");
            }

            System.out.println("\n[HOÀN TẤT] Yêu cầu đặt phòng #" + bookingId + " đã được gửi tới Admin chờ duyệt!");
        } else {
            System.out.println("[LỖI] Đặt phòng thất bại do lỗi CSDL.");
        }
    }
    // 3. Xem lịch sử đặt phòng của bản thân
    private void viewMyBookings() {
        System.out.println("\n--- LỊCH SỬ ĐẶT PHÒNG CỦA TÔI ---");
        List<Booking> myBookings = bookingDAO.getBookingsByEmployeeId(currentUserId);

        if (myBookings.isEmpty()) {
            System.out.println("Bạn chưa có đơn đặt phòng nào.");
            return;
        }

        for (Booking b : myBookings) {
            System.out.println("=========================================================================================");
            System.out.printf("ID Đơn: %-5d | Phòng ID: %-5d | Thời gian: %s đến %s\n",
                    b.getBookingId(), b.getRoomId(), b.getStartTime(), b.getEndTime());
            System.out.printf("Trạng thái duyệt: [%s] | Trạng thái chuẩn bị: [%s]\n",
                    b.getBookingStatus(), b.getPrepStatus());

            // --- Hiển thị danh sách Thiết bị đã đặt ---
            List<com.meeting.model.BookingEquipment> eqList = bookingEquipmentDAO.getEquipmentsByBookingId(b.getBookingId());
            if (eqList != null && !eqList.isEmpty()) {
                System.out.println("  [+] Thiết bị đã mượn:");
                for (com.meeting.model.BookingEquipment eq : eqList) {
                    // Nếu bạn có viết hàm lấy tên thiết bị trong EquipmentDAO thì có thể gọi để in tên thay vì in ID
                    System.out.println("      - ID Thiết bị: " + eq.getEquipmentId() + " | Tên thiết bị: " + eq.getEquipmentByName() + " | Số lượng: " + eq.getQuantity());
                }
            } else {
                System.out.println("  [-] Không đặt thêm thiết bị.");
            }

            // --- Hiển thị danh sách Dịch vụ đã đặt ---
            List<com.meeting.model.BookingService> svList = bookingServiceDAO.getServicesByBookingId(b.getBookingId());
            if (svList != null && !svList.isEmpty()) {
                System.out.println("  [+] Dịch vụ đã gọi:");
                for (com.meeting.model.BookingService sv : svList) {
                    // Tương tự, có thể lấy tên dịch vụ từ ServiceDAO nếu muốn hiển thị chi tiết hơn
                    System.out.println("      - ID Dịch vụ: " + sv.getServiceId() + " | Số lượng: " + sv.getQuantity());
                }
            } else {
                System.out.println("  [-] Không đặt thêm dịch vụ.");
            }
        }
        System.out.println("=========================================================================================\n");
    }

    // 4. Hủy đơn đặt phòng (Chỉ cho phép hủy nếu đơn vẫn đang PENDING)
    private void cancelPendingBooking() {
        System.out.println("\n--- HỦY ĐƠN ĐẶT PHÒNG ---");
        System.out.println(("\n Danh sách các đơn đặt phòng đang chờ duyệt (PENDING) của bạn:"));
        viewMyBookings();
        int bookingId = ValidationUtil.getInt("Nhập ID đơn đặt phòng muốn hủy: ", "ID phải là số!");

        // Kiểm tra xem đơn này có phải của nhân viên này không và trạng thái có phải PENDING không
        Booking booking;
        try {
            booking = bookingDAO.getBookingById(bookingId);
        } catch (Exception e) {
            System.out.println("[LỖI] Không thể tải thông tin đơn: " + e.getMessage());
            return;
        }

        if (booking == null || booking.getEmployeeId() != currentUserId) {
            System.out.println("[LỖI] Không tìm thấy đơn hoặc bạn không có quyền hủy đơn này!");
            return;
        }

        if (!"PENDING".equalsIgnoreCase(booking.getBookingStatus())) {
            System.out.println("[LỖI] Chỉ có thể hủy đơn đang ở trạng thái PENDING (Chờ duyệt)!");
            return;
        }

        String confirm = ValidationUtil.getString("Bạn chắc chắn muốn hủy đơn ID " + bookingId + " không? (Y/N): ");
        if (confirm.equalsIgnoreCase("Y")) {
            // Cập nhật trạng thái thành công CANCELLED trong DAO
            try {
                if (bookingDAO.updateBookingStatus(bookingId, "CANCELLED")) {
                    System.out.println("[THÀNH CÔNG] Đã hủy đơn đặt phòng thành công.");
                } else {
                    System.out.println("[LỖI] Hủy thất bại do lỗi hệ thống.");
                }
            } catch (Exception e) {
                System.out.println("[LỖI] Không thể hủy đơn: " + e.getMessage());
            }
        } else {
            System.out.println("Đã hủy thao tác.");
        }
    }


    private void manageProfile() {
        try {
            // 1. Lấy thông tin user hiện tại từ UserDAO
            User currentUser = userDAO.getUserById(currentUserId);
            if (currentUser == null) {
                System.out.println("[LỖI] Không tìm thấy thông tin người dùng.");
                return;
            }

            System.out.println("\n--- HỒ SƠ CÁ NHÂN ---");
            System.out.println("Họ tên: " + currentUser.getFullName());
            System.out.println("Tên đăng nhập: " + currentUser.getUsername());
            System.out.println("1. Email hiện tại: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Chưa có"));
            System.out.println("2. Phòng ban: " + (currentUser.getDepartment() != null ? currentUser.getDepartment() : "Chưa có"));
            System.out.println("3. Số điện thoại nội bộ: " + (currentUser.getPhone() != null ? currentUser.getPhone() : "Chưa có"));
            System.out.println("0. Thoát");
            System.out.println("---------------------");

            int choice = ValidationUtil.getInt("Chọn thông tin bạn muốn cập nhật (1-3): ", "Vui lòng nhập số!");

            switch (choice) {
                case 1:
                    String newEmail = ValidationUtil.getString("Nhập Email mới: ");
                    currentUser.setEmail(newEmail);
                    break;
                case 2:
                    String newDept = ValidationUtil.getString("Nhập Phòng ban mới: ");
                    currentUser.setDepartment(newDept);
                    break;
                case 3:
                    String newPhone = ValidationUtil.getString("Nhập Số điện thoại nội bộ mới: ");
                    currentUser.setPhone(newPhone);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("[THÔNG BÁO] Không có thay đổi nào được thực hiện.");
                    return;
            }

            // 2. Gọi DAO để cập nhật vào Database
            // Lưu ý: Đảm bảo UserDAO của bạn đã có hàm updateUserInfo(User user)
            if (userDAO.updateUserInfo(currentUser)) {
                System.out.println("[THÀNH CÔNG] Thông tin cá nhân đã được cập nhật!");
            } else {
                System.out.println("[LỖI] Cập nhật thất bại. Vui lòng thử lại sau.");
            }

        } catch (SQLException e) {
            System.out.println("[LỖI HỆ THỐNG] Lỗi khi truy cập hồ sơ: " + e.getMessage());
        }
    }
    private void handleViewMyBookings() {
        try {
            List<Booking> bookings = bookingDAO.getBookingsByEmployeeId(currentUserId);
            if (bookings.isEmpty()) {
                System.out.println("[THÔNG BÁO] Bạn chưa có đơn đặt phòng nào.");
                return;
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            System.out.println("\n--- LỊCH HỌP CỦA TÔI ---");
            String header = String.format("| %-5s | %-20s | %-16s | %-16s | %-12s | %-18s |",
                    "ID", "Phòng", "Bắt đầu", "Kết thúc", "Duyệt", "Chuẩn bị");
            System.out.println("-".repeat(header.length()));
            System.out.println(header);
            System.out.println("-".repeat(header.length()));

            for (Booking b : bookings) {
                String roomName = roomDAO.getRoomById(b.getRoomId()).getRoomName();
                System.out.printf("| %-5d | %-20s | %-16s | %-16s | %-12s | %-18s |\n",
                        b.getBookingId(),
                        roomName,
                        b.getStartTime().format(dtf),
                        b.getEndTime().format(dtf),
                        b.getBookingStatus(),
                        (b.getPrepStatus() == null ? "N/A" : b.getPrepStatus()));
            }
            System.out.println("-".repeat(header.length()));
        } catch (SQLException e) {
            System.out.println("[LỖI] Không thể tải lịch họp: " + e.getMessage());
        }
    }
}
