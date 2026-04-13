package com.meeting.presentation;

import com.meeting.dao.*;
import com.meeting.model.*;
import com.meeting.service.BookingService;
import com.meeting.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class AdminMenu {
    private RoomDAO roomDAO = new RoomDAO();
    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private final UserDAO userDAO = new UserDAO();
    private final BookingService bookingService = new BookingService();

    //Hàm hiển thị menu chính của admin
    public void displayMenu() throws SQLException {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("    HỆ THỐNG QUẢN LÝ DÀNH CHO ADMIN      ");
            System.out.println("=========================================");
            System.out.println("1. Quản lý Phòng họp");
            System.out.println("2. Quản lý Thiết bị di động");
            System.out.println("3. Quản lý Dịch vụ đi kèm");
            System.out.println("4. Duyệt & Phân công đơn đặt phòng");
            System.out.println("5. Tìm kiếm phòng họp theo tên");
            System.out.println("6. Quản lý người dùng");
            System.out.println("0. Đăng xuất");
            System.out.println("=========================================");

            int choice = ValidationUtil.getInt("Nhập lựa chọn của bạn: ", "Lựa chọn phải là số nguyên!");

            switch (choice) {
                case 1:
                    manageRooms();
                    break;
                case 2:
                    manageEquipments();
                    break;
                case 3:
                    manageServices();
                    break;
                case 4:
                    manageBookings();
                    break;
                case 5: searchRoomByName();
                    break;
                case 6:
                    manageUsers(); // GỌI HÀM TẠO TÀI KHOẢN
                    break;
                case 0:
                    System.out.println("Đã đăng xuất khỏi tài khoản Admin!");
                    return; // Thoát khỏi vòng lặp, quay lại AuthMenu (Menu đăng nhập)
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại!");
            }
        }
    }

    private void manageRooms() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ PHÒNG HỌP ---");
            System.out.println("1. Xem danh sách phòng");
            System.out.println("2. Thêm phòng mới");
            System.out.println("3. Cập nhật thông tin phòng");
            System.out.println("4. Xóa phòng");
            System.out.println("0. Quay lại menu chính");

            int choice = ValidationUtil.getInt("Chọn chức năng (0-4): ", "Vui lòng nhập số nguyên!");

            switch (choice) {
                case 1:
                    showAllRooms();
                    break;
                case 2:
                    addNewRoom();
                    break;
                case 3:
                    updateRoom();
                    break;
                case 4:
                    deleteRoom();
                    break;
                case 0:
                    return; // Thoát Sub-menu
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    // 1. Hiển thị danh sách phòng
    private void showAllRooms() {
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            if (rooms.isEmpty()) {
                System.out.println("Hệ thống hiện chưa có phòng họp nào.");
                return;
            }

            System.out.println("\nDANH SÁCH PHÒNG HỌP:");
            System.out.printf("%-5s | %-20s | %-10s | %-20s | %-30s\n", "ID", "Tên phòng", "Sức chứa", "Vị trí", "Thiết bị cố định");
            System.out.println("-----------------------------------------------------------------------------------------");
            for (Room r : rooms) {
                System.out.printf("%-5d | %-20s | %-10d | %-20s | %-30s\n",
                        r.getRoomId(), r.getRoomName(), r.getCapacity(), r.getLocation(), r.getFixedEquipment());
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    // 2. Thêm phòng mới
    private void addNewRoom() {
        System.out.println("\n[THÊM PHÒNG MỚI]");
        // Nhờ ValidationUtil, code lấy dữ liệu siêu ngắn và không sợ crash!
        String name = ValidationUtil.getString("Nhập tên phòng: ");
        int capacity = ValidationUtil.getInt("Nhập sức chứa: ", "Sức chứa phải là số nguyên!");
        String location = ValidationUtil.getString("Nhập vị trí (VD: Tầng 1, Tòa A): ");
        String equipment = ValidationUtil.getString("Nhập thiết bị cố định (VD: Máy chiếu, Bảng trắng): ");

        Room newRoom = new Room(0, name, location, capacity, equipment, "Ready");

        try {
            if (roomDAO.insertRoom(newRoom)) {
                System.out.println("Thêm phòng họp thành công!");
            } else {
                System.out.println("Thêm phòng thất bại (Có thể do trùng tên phòng).");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    // 3. Cập nhật phòng
    private void updateRoom() {
        System.out.println("\n[CẬP NHẬT PHÒNG HỌP]");
        showAllRooms(); // Show cho người ta biết ID mà sửa

        int roomId = ValidationUtil.getInt("Nhập ID phòng cần sửa (Nhập 0 để hủy): ", "ID phải là số nguyên!");
        if (roomId == 0) return;

        // Bắt nhập thông tin mới
        String newName = ValidationUtil.getString("Nhập tên phòng MỚI: ");
        int newCapacity = ValidationUtil.getInt("Nhập sức chứa MỚI: ", "Sức chứa phải là số nguyên!");
        String newLocation = ValidationUtil.getString("Nhập vị trí MỚI: ");
        String newEquipment = ValidationUtil.getString("Nhập thiết bị cố định MỚI: ");
        String newStatus = ValidationUtil.getString("Nhập trạng thái MỚI: ");

        Room roomToUpdate = new Room(roomId, newName, newLocation, newCapacity, newEquipment, newStatus);

        try {
            if (roomDAO.updateRoom(roomToUpdate)) {
                System.out.println("Cập nhật thông tin phòng thành công!");
            } else {
                System.out.println("Cập nhật thất bại (Kiểm tra lại ID phòng có tồn tại không).");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    // 4. Xóa phòng
    private void deleteRoom() {
        System.out.println("\n[XÓA PHÒNG HỌP]");
        int roomId = ValidationUtil.getInt("Nhập ID phòng cần xóa: ", "ID phải là số nguyên!");

        String confirm = ValidationUtil.getString("Bạn có chắc chắn muốn xóa phòng này? (Y/N): ");
        if (confirm.equalsIgnoreCase("Y")) {
            try {
                if (roomDAO.deleteRoom(roomId)) {
                    System.out.println("Xóa phòng thành công!");
                } else {
                    System.out.println("Xóa thất bại (Phòng đang được sử dụng trong đơn đặt phòng hoặc không tồn tại).");
                }
            } catch (SQLException e) {
                System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
            }
        } else {
            System.out.println("Đã hủy thao tác xóa.");
        }
    }

    // --- SUB-MENU: QUẢN LÝ THIẾT BỊ DI ĐỘNG ---
    private void manageEquipments() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ THIẾT BỊ DI ĐỘNG ---");
            System.out.println("1. Xem danh sách thiết bị");
            System.out.println("2. Thêm thiết bị mới");
            System.out.println("3. Cập nhật thông tin thiết bị");
            System.out.println("4. Xóa thiết bị");
            System.out.println("0. Quay lại menu chính");

            int choice = ValidationUtil.getInt("Chọn chức năng (0-4): ", "Vui lòng nhập số nguyên!");

            switch (choice) {
                case 1:
                    showAllEquipments();
                    break;
                case 2:
                    addNewEquipment();
                    break;
                case 3:
                    updateEquipment();
                    break;
                case 4:
                    deleteEquipment();
                    break;
                case 0:
                    return; // Thoát Sub-menu
                default:
                    System.out.println("[LỖI] Lựa chọn không hợp lệ!");
            }
        }
    }

    private void showAllEquipments() {
        try {
            List<Equipment> list = equipmentDAO.getAllEquipments();
            if (list.isEmpty()) {
                System.out.println("[CẢNH BÁO] Hệ thống hiện chưa có thiết bị nào.");
                return;
            }

            System.out.println("\nDANH SÁCH THIẾT BỊ DI ĐỘNG:");
            System.out.printf("%-5s | %-25s | %-15s | %-15s | %-20s\n", "ID", "Tên thiết bị", "Tổng số lượng", "Khả dụng", "Trạng thái");
            System.out.println("---------------------------------------------------------------------------------------------");
            for (Equipment e : list) {
                System.out.printf("%-5d | %-25s | %-15d | %-15d | %-20s\n",
                        e.getEquipmentId(), e.getEquipmentName(), e.getTotalQuantity(), e.getAvailableQuantity(), e.getStatus());
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void addNewEquipment() {
        System.out.println("\n[THÊM THIẾT BỊ MỚI]");
        String name = ValidationUtil.getString("Nhập tên thiết bị (VD: Máy chiếu, Loa kéo): ");
        int total = ValidationUtil.getInt("Nhập tổng số lượng: ", "Số lượng phải là số nguyên!");
        int available = ValidationUtil.getInt("Nhập số lượng khả dụng ban đầu: ", "Số lượng phải là số nguyên!");
        String status = ValidationUtil.getString("Nhập trạng thái (VD: Mới, Hoạt động tốt): ");

        Equipment newEquipment = new Equipment(0, name, total, available, status);

        try {
            if (equipmentDAO.addEquipment(newEquipment)) {
                System.out.println("[THÀNH CÔNG] Thêm thiết bị thành công!");
            } else {
                System.out.println("[LỖI] Thêm thất bại (Có thể do trùng tên thiết bị).");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void updateEquipment() {
        System.out.println("\n[CẬP NHẬT THIẾT BỊ]");
        showAllEquipments();

        int id = ValidationUtil.getInt("Nhập ID thiết bị cần sửa (Nhập 0 để hủy): ", "ID phải là số nguyên!");
        if (id == 0) return;

        try {
            Equipment oldEquipment = equipmentDAO.getEquipmentById(id);
            if (oldEquipment == null) {
                System.out.println("[LỖI] Không tìm thấy thiết bị với ID = " + id);
                return;
            }

            System.out.println("Đang sửa thiết bị: " + oldEquipment.getEquipmentName());
            String newName = ValidationUtil.getString("Nhập tên thiết bị MỚI: ");
            int newTotal = ValidationUtil.getInt("Nhập tổng số lượng MỚI: ", "Vui lòng nhập số!");
            int newAvailable = ValidationUtil.getInt("Nhập số lượng khả dụng MỚI: ", "Vui lòng nhập số!");
            String newStatus = ValidationUtil.getString("Nhập trạng thái MỚI: ");

            Equipment updatedEquipment = new Equipment(id, newName, newTotal, newAvailable, newStatus);

            if (equipmentDAO.updateEquipment(updatedEquipment)) {
                System.out.println("[THÀNH CÔNG] Cập nhật thông tin thiết bị thành công!");
            } else {
                System.out.println("[LỖI] Cập nhật thất bại.");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void deleteEquipment() {
        System.out.println("\n[XÓA THIẾT BỊ]");
        int id = ValidationUtil.getInt("Nhập ID thiết bị cần xóa (Nhập 0 để hủy): ", "ID phải là số nguyên!");
        if (id == 0) return;

        String confirm = ValidationUtil.getString("Bạn có chắc chắn muốn xóa thiết bị này? (Y/N): ");
        if (confirm.equalsIgnoreCase("Y")) {
            try {
                if (equipmentDAO.deleteEquipment(id)) {
                    System.out.println("[THÀNH CÔNG] Xóa thiết bị thành công!");
                } else {
                    System.out.println("[LỖI] Xóa thất bại (Thiết bị này có thể đang được mượn trong 1 đơn đặt phòng).");
                }
            } catch (SQLException e) {
                System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
            }
        } else {
            System.out.println("[THÔNG BÁO] Đã hủy thao tác xóa.");
        }
    }

    // --- SUB-MENU: QUẢN LÝ DỊCH VỤ ĐI KÈM ---
    private void manageServices() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ DỊCH VỤ ĐI KÈM ---");
            System.out.println("1. Xem danh sách dịch vụ");
            System.out.println("2. Thêm dịch vụ mới");
            System.out.println("3. Cập nhật thông tin dịch vụ");
            System.out.println("4. Xóa dịch vụ");
            System.out.println("0. Quay lại menu chính");

            int choice = ValidationUtil.getInt("Chọn chức năng (0-4): ", "Vui lòng nhập số nguyên!");

            switch (choice) {
                case 1:
                    showAllServices();
                    break;
                case 2:
                    addNewService();
                    break;
                case 3:
                    updateService();
                    break;
                case 4:
                    deleteService();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("[LỖI] Lựa chọn không hợp lệ!");
            }
        }
    }

    private void showAllServices() {
        try {
            List<Service> list = serviceDAO.getAllServices();
            if (list.isEmpty()) {
                System.out.println("[CẢNH BÁO] Hệ thống hiện chưa có dịch vụ nào.");
                return;
            }

            System.out.println("\nDANH SÁCH DỊCH VỤ:");
            System.out.printf("%-5s | %-30s | %-15s\n", "ID", "Tên dịch vụ", "Đơn giá (VNĐ)");
            System.out.println("---------------------------------------------------------");
            for (Service s : list) {
                System.out.printf("%-5d | %-30s | %-15.2f\n", s.getServiceId(), s.getServiceName(), s.getUnitPrice());
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void addNewService() {
        System.out.println("\n[THÊM DỊCH VỤ MỚI]");
        String name = ValidationUtil.getString("Nhập tên dịch vụ (VD: Nước suối, Bánh ngọt): ");
        double price = ValidationUtil.getDouble("Nhập đơn giá: ", "Đơn giá phải là số!");

        Service newService = new Service(0, name, price);

        try {
            if (serviceDAO.addService(newService)) {
                System.out.println("[THÀNH CÔNG] Thêm dịch vụ thành công!");
            } else {
                System.out.println("[LỖI] Thêm thất bại (Có thể do trùng tên dịch vụ).");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void updateService() {
        System.out.println("\n[CẬP NHẬT DỊCH VỤ]");
        showAllServices();

        int id = ValidationUtil.getInt("Nhập ID dịch vụ cần sửa (Nhập 0 để hủy): ", "ID phải là số nguyên!");
        if (id == 0) return;

        try {
            Service oldService = serviceDAO.getServiceById(id);
            if (oldService == null) {
                System.out.println("[LỖI] Không tìm thấy dịch vụ với ID = " + id);
                return;
            }

            System.out.println("Đang sửa dịch vụ: " + oldService.getServiceName());
            String newName = ValidationUtil.getString("Nhập tên dịch vụ MỚI: ");
            double newPrice = ValidationUtil.getDouble("Nhập đơn giá MỚI: ", "Vui lòng nhập số!");

            Service updatedService = new Service(id, newName, newPrice);

            if (serviceDAO.updateService(updatedService)) {
                System.out.println("[THÀNH CÔNG] Cập nhật thông tin dịch vụ thành công!");
            } else {
                System.out.println("[LỖI] Cập nhật thất bại.");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void deleteService() {
        System.out.println("\n[XÓA DỊCH VỤ]");
        int id = ValidationUtil.getInt("Nhập ID dịch vụ cần xóa (Nhập 0 để hủy): ", "ID phải là số nguyên!");
        if (id == 0) return;

        String confirm = ValidationUtil.getString("Bạn có chắc chắn muốn xóa dịch vụ này? (Y/N): ");
        if (confirm.equalsIgnoreCase("Y")) {
            try {
                if (serviceDAO.deleteService(id)) {
                    System.out.println("[THÀNH CÔNG] Xóa dịch vụ thành công!");
                } else {
                    System.out.println("[LỖI] Xóa thất bại (Dịch vụ này có thể đang nằm trong 1 đơn đặt phòng).");
                }
            } catch (SQLException e) {
                System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
            }
        } else {
            System.out.println("[THÔNG BÁO] Đã hủy thao tác xóa.");
        }
    }

    // --- SUB-MENU: DUYỆT & PHÂN CÔNG ĐƠN ĐẶT PHÒNG ---
    private void manageBookings() {
        System.out.println("\n--- DANH SÁCH ĐƠN CHỜ DUYỆT ---");
        try {
            // 1. Xem danh sách yêu cầu đặt phòng (Chỉ lấy PENDING)
            List<Booking> pendingList = bookingDAO.getPendingBookings();
            if (pendingList.isEmpty()) {
                System.out.println("[THÔNG BÁO] Hiện không có đơn đặt phòng nào cần duyệt.");
                return;
            }

            System.out.println("-----------------------------------------------------------------------------------------");
            System.out.printf("| %-5s | %-10s | %-10s | %-20s | %-20s |\n", "ID", "Phòng ID", "User ID", "Bắt đầu", "Kết thúc");
            System.out.println("-----------------------------------------------------------------------------------------");
            for (Booking b : pendingList) {
                System.out.printf("| %-5d | %-10d | %-10d | %-20s | %-20s |\n",
                        b.getBookingId(), b.getRoomId(), b.getEmployeeId(), b.getStartTime(), b.getEndTime());
            }

            // 2. Chọn đơn để xử lý
            int bookingId = ValidationUtil.getInt("\nNhập ID đơn muốn xử lý (Nhập 0 để thoát): ", "ID phải là số!");
            if (bookingId == 0) return;

            System.out.println("1. Phê duyệt (Approve) & Phân công Support");
            System.out.println("2. Từ chối (Reject) do xung đột lịch/lý do khác");
            System.out.println("0. Quay lại");
            int choice = ValidationUtil.getInt("Lựa chọn của bạn: ", "Vui lòng nhập số hợp lệ!");

            if (choice == 1) {
                // 3. Phân công nhân viên hỗ trợ
                System.out.println("\n--- DANH SÁCH NHÂN VIÊN HỖ TRỢ ---");
                List<User> supportStaffs = userDAO.getUsersByRole("SUPPORT");
                if (supportStaffs.isEmpty()) {
                    System.out.println("[LỖI] Hệ thống chưa có nhân viên Support nào! Không thể phân công.");
                    return;
                }
                for (User u : supportStaffs) {
                    System.out.println("- ID: " + u.getUserId() + " | Tên: " + u.getFullName() + " | Phòng ban: " + u.getDepartment());
                }

                int supportId = ValidationUtil.getInt("Nhập ID nhân viên hỗ trợ muốn phân công: ", "ID phải là số!");

                // Cập nhật DB: Trạng thái = APPROVED, support_staff_id = supportId, prep_status = PREPARING
                if (bookingDAO.approveAndAssignBooking(bookingId, supportId)) {
                    System.out.println(" [THÀNH CÔNG] Đã duyệt đơn và phân công cho nhân viên ID: " + supportId);
                } else {
                    System.out.println(" [LỖI] Không thể duyệt đơn. Vui lòng kiểm tra lại ID đơn.");
                }

            } else if (choice == 2) {
                // Từ chối đơn
                if (bookingDAO.updateBookingStatus(bookingId, "REJECTED")) {
                    System.out.println(" [THÀNH CÔNG] Đã TỪ CHỐI đơn đặt phòng.");
                } else {
                    System.out.println(" [LỖI] Xử lý thất bại.");
                }
            } else {
                System.out.println("[THÔNG BÁO] Đã hủy thao tác.");
            }
        } catch (Exception e) {
            System.out.println(" [LỖI HỆ THỐNG]: " + e.getMessage());
        }
    }

    // MENU QUẢN LÝ NGƯỜI DÙNG
    // ==========================================
    private void manageUsers() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ NGƯỜI DÙNG ---");
            System.out.println("1. Xem danh sách người dùng");
            System.out.println("2. Tạo tài khoản Support Staff mới");
            System.out.println("3. Tạo tài khoản Admin mới");
            System.out.println("0. Quay lại menu chính");

            int choice = ValidationUtil.getInt("Nhập lựa chọn của bạn (0-3): ", "Lựa chọn phải là số nguyên!");

            switch (choice) {
                case 1:
                    displayUserList();
                    break;
                case 2:
                    createSupportStaff();
                    break;
                case 3:
                    createAdminAccount();
                    break;
                case 0:
                    return; // Thoát menu con, quay lại menu chính
                default:
                    System.out.println("[LỖI] Lựa chọn không hợp lệ!");
            }
        }
    }

    // 1. Hàm tạo tài khoản Support Staff
    private void createSupportStaff() {
        System.out.println("\n--- TẠO TÀI KHOẢN SUPPORT STAFF ---");

        String username = ValidationUtil.getString("Nhập tên đăng nhập: ");
        try {
            if (userDAO.isUsernameExists(username)) {
                System.out.println("[LỖI] Tên đăng nhập này đã tồn tại trong hệ thống!");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
            return;
        }

        String password = ValidationUtil.getString("Nhập mật khẩu: ");
        String fullName = ValidationUtil.getString("Nhập họ và tên nhân viên hỗ trợ: ");
        String email = ValidationUtil.getString("Nhập email: "); // Dùng getString nếu chưa có hàm getEmail
        String phone = ValidationUtil.getPhone("Nhập số điện thoại: ");
        String department = "Support Team"; // Mặc định phòng ban

        User newSupport = new User(0, username, password, "SUPPORT", department, fullName, email, phone);

        try {
            if (userDAO.register(newSupport)) {
                System.out.println("[THÀNH CÔNG] Đã tạo tài khoản Support Staff thành công!");
            } else {
                System.out.println("[LỖI] Tạo tài khoản thất bại do lỗi hệ thống.");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    // 2. Hàm tạo tài khoản Admin
    private void createAdminAccount() {
        System.out.println("\n--- TẠO TÀI KHOẢN ADMIN MỚI ---");

        String username = ValidationUtil.getString("Nhập tên đăng nhập (Username): ");
        try {
            if (userDAO.isUsernameExists(username)) {
                System.out.println("[LỖI] Tên đăng nhập này đã tồn tại. Vui lòng chọn tên khác!");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
            return;
        }

        String password = ValidationUtil.getString("Nhập mật khẩu: ");
        String fullName = ValidationUtil.getString("Nhập họ và tên: ");
        String email = ValidationUtil.getString("Nhập Email: ");
        String phone = ValidationUtil.getPhone("Nhập số điện thoại: ");
        String department = ValidationUtil.getString("Nhập bộ phận/chức vụ (VD: Ban Giám Đốc): ");

        User newAdmin = new User(0, username, password, "ADMIN", department, fullName, email, phone);

        try {
            if (userDAO.register(newAdmin)) {
                System.out.println("[THÀNH CÔNG] Đã tạo tài khoản Admin mới thành công!");
                System.out.println("Tài khoản: " + username + " | Chức vụ: Quản trị viên (ADMIN)");
            } else {
                System.out.println("[LỖI] Tạo tài khoản thất bại do lỗi hệ thống.");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }
    // 3. Hàm hiển thị danh sách người dùng hiện có (Admin + Support)
    private void displayUserList() {
        try {
            List<User> users = userDAO.getAllUsers();
            if(users.isEmpty()) {
                System.out.println("[CẢNH BÁO] Hệ thống hiện chưa có người dùng nào.");
            } else {
                System.out.println("\nDANH SÁCH NGƯỜI DÙNG HIỆN CÓ:");
                System.out.printf("%-5s | %-15s | %-10s | %-20s | %-30s\n", "ID", "Username", "Role", "Full Name", "Email");
                System.out.println("-----------------------------------------------------------------------------------------");
                for (User u : users) {
                    System.out.printf("%-5d | %-15s | %-10s | %-20s | %-30s\n",
                            u.getUserId(), u.getUsername(), u.getRole(), u.getFullName(), u.getEmail());
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }
    private void searchRoomByName() {
        System.out.println("\n--- TÌM KIẾM PHÒNG HỌP ---");
        String keyword = ValidationUtil.getString("Nhập tên phòng muốn tìm: ");


//        int keyword = ValidationUtil.getInt("Nhập ID phòng muốn tìm: ", "ID phải là số nguyên!");


        List<Room> results = roomDAO.searchRoomsByName(keyword);

        if (results.isEmpty()) {
            System.out.println("[THÔNG BÁO] Không tìm thấy phòng nào.");
        } else {
            System.out.println("\nKẾT QUẢ TÌM KIẾM:");
            // Tăng chiều dài đường kẻ ngang lên ~115 để chứa thêm cột
            String separator = "-----------------------------------------------------------------------------------------------------------------------";
            System.out.println(separator);
            // Thêm cột %-15s cho Trạng thái
            System.out.printf("| %-5s | %-20s | %-10s | %-20s | %-15s | %-25s |\n",
                    "ID", "Tên Phòng", "Sức chứa", "Vị trí", "Trạng thái", "Thiết bị cố định");
            System.out.println(separator);

            for (Room r : results) {
                System.out.printf("| %-5d | %-20s | %-10d | %-20s | %-15s | %-25s |\n",
                        r.getRoomId(),
                        r.getRoomName(),
                        r.getCapacity(),
                        r.getLocation(),
                        r.getStatus(), // HIỂN THỊ TRẠNG THÁI
                        (r.getFixedEquipment() != null ? r.getFixedEquipment() : "Trống"));
            }
            System.out.println(separator);
        }
    }
}
