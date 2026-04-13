package com.meeting.service;

import com.meeting.dao.BookingDAO;
import com.meeting.model.Booking;
import com.sun.net.httpserver.Authenticator;

import java.time.LocalDateTime;
import java.util.List;

public class BookingService {
    private final BookingDAO bookingDAO = new BookingDAO();


    //  Logic đặt phòng: Kiểm tra lịch trống trước khi thêm

    public String createBooking(Booking booking) {
        // 1. Kiểm tra logic thời gian cơ bản
        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            return "Thời gian bắt đầu không được ở quá khứ.";
        }
        if (booking.getEndTime().isBefore(booking.getStartTime())) {
            return "Thời gian kết thúc phải sau thời gian bắt đầu.";
        }

        // 2. Kiểm tra xung đột lịch phòng thông qua DAO
        boolean isAvailable = bookingDAO.isRoomAvailable(
                booking.getRoomId(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        if (!isAvailable) {
            return "Phòng đã có người đặt hoặc đang bận trong khoảng thời gian này.";
        }

        // 3. Tiến hành lưu đơn
        // Hứng kết quả bằng int và kiểm tra lớn hơn 0
        int generatedBookingId = bookingDAO.addBookingReturnId(booking);

        return (generatedBookingId > 0) ? "SUCCESS" : "Lỗi hệ thống khi lưu đơn đặt phòng.";
    }


     //Lấy danh sách việc làm cho nhân viên hỗ trợ

//    public List<Booking> getTasksForSupport(int staffId) {
//        return bookingDAO.getAssignedTasks(staffId);
//    }


     // Duyệt đơn và phân công nhân viên

//    public boolean approveAndAssign(int bookingId, int staffId) {
//        // Có thể thêm kiểm tra xem staffId đó có đúng là Role SUPPORT không ở đây
//        return bookingDAO.updateBookingStatusAndAssign(bookingId, "APPROVED", staffId);
//    }
}