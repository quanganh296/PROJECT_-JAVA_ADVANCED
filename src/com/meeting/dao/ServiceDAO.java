package com.meeting.dao;

import com.meeting.model.Service;
import com.meeting.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    // 1. Lấy tất cả dịch vụ
    public List<Service> getAllServices() throws SQLException {
        List<Service> serviceList = new ArrayList<>();
        String sql = "SELECT * FROM services";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Service service = new Service();
                service.setServiceId(rs.getInt("service_id"));
                service.setServiceName(rs.getString("service_name"));
                service.setUnitPrice(rs.getDouble("unit_price"));
                serviceList.add(service);
            }
        }

        return serviceList;
    }

    // 2. Thêm dịch vụ
    public boolean addService(Service service) throws SQLException {
        String sql = "INSERT INTO services (service_name, unit_price) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, service.getServiceName());
            pstmt.setDouble(2, service.getUnitPrice());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 3. Cập nhật dịch vụ
    public boolean updateService(Service service) throws SQLException {
        String sql = "UPDATE services SET service_name = ?, unit_price = ? WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, service.getServiceName());
            pstmt.setDouble(2, service.getUnitPrice());
            pstmt.setInt(3, service.getServiceId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 4. Xóa dịch vụ
    public boolean deleteService(int serviceId) throws SQLException {
        String sql = "DELETE FROM services WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serviceId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // 5. Tìm theo ID
    public Service getServiceById(int serviceId) throws SQLException {
        String sql = "SELECT * FROM services WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Service service = new Service();
                service.setServiceId(rs.getInt("service_id"));
                service.setServiceName(rs.getString("service_name"));
                service.setUnitPrice(rs.getDouble("unit_price"));
                return service;
            }
        }

        return null;
    }
}