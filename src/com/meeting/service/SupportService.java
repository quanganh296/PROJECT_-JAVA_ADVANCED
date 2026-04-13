package com.meeting.service;

import com.meeting.dao.BookingDAO;
import com.meeting.dao.BookingEquipmentDAO;
import com.meeting.dao.BookingServiceDAO;
import com.meeting.model.Booking;
import com.meeting.model.BookingEquipment;
import com.meeting.model.BookingService;

import java.util.List;

public class SupportService {
    private BookingDAO bookingDAO = new BookingDAO();
    private BookingEquipmentDAO bookingEquipmentDAO = new BookingEquipmentDAO();
    private BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();

    // Lấy danh sách công việc được phân công cho nhân viên hỗ trợ
    public List<Booking> getAssignedTasks(int supportStaffId) {
        return bookingDAO.getAssignedTasks(supportStaffId);
    }

    // Cập nhật trạng thái chuẩn bị
    public boolean updatePrepStatus(int bookingId, String prepStatus) {
        return bookingDAO.updatePrepStatus(bookingId, prepStatus);
    }

    // Lấy danh sách thiết bị cho một booking
    public List<BookingEquipment> getBookingEquipments(int bookingId) {
        return bookingEquipmentDAO.getEquipmentsByBookingId(bookingId);
    }

    // Lấy danh sách dịch vụ cho một booking
    public List<BookingService> getBookingServices(int bookingId) {
        return bookingServiceDAO.getServicesByBookingId(bookingId);
    }
}
