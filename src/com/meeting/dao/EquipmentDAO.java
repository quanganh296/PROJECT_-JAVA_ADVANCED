package com.meeting.dao;

import com.meeting.model.Equipment;
import com.meeting.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    // 1. Lấy danh sách tất cả thiết bị
    public List<Equipment> getAllEquipments() throws SQLException {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT * FROM equipments";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setEquipmentId(rs.getInt("equipment_id"));
                equipment.setEquipmentName(rs.getString("equipment_name"));
                equipment.setTotalQuantity(rs.getInt("total_quantity"));
                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
                equipment.setStatus(rs.getString("status"));
                equipmentList.add(equipment);
            }
        }

        return equipmentList;
    }

    // 2. Thêm thiết bị mới
    public boolean addEquipment(Equipment equipment) throws SQLException {
        String sql = "INSERT INTO equipments(equipment_name, total_quantity, available_quantity, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipment.getEquipmentName());
            pstmt.setInt(2, equipment.getTotalQuantity());
            pstmt.setInt(3, equipment.getAvailableQuantity());
            pstmt.setString(4, equipment.getStatus());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 3. Xóa thiết bị
    public boolean deleteEquipment(int equipmentId) throws SQLException {
        String sql = "DELETE FROM equipments WHERE equipment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipmentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // 4. Cập nhật thiết bị
    public boolean updateEquipment(Equipment equipment) throws SQLException {
        String sql = "UPDATE equipments SET equipment_name = ?, total_quantity = ?, available_quantity = ?, status = ? WHERE equipment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipment.getEquipmentName());
            pstmt.setInt(2, equipment.getTotalQuantity());
            pstmt.setInt(3, equipment.getAvailableQuantity());
            pstmt.setString(4, equipment.getStatus());
            pstmt.setInt(5, equipment.getEquipmentId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 5. Tìm thiết bị theo ID
    public Equipment getEquipmentById(int equipmentId) throws SQLException {
        String sql = "SELECT * FROM equipments WHERE equipment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setEquipmentId(rs.getInt("equipment_id"));
                equipment.setEquipmentName(rs.getString("equipment_name"));
                equipment.setTotalQuantity(rs.getInt("total_quantity"));
                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
                equipment.setStatus(rs.getString("status"));

                return equipment;
            }
        }

        return null;
    }
    // 5. Tìm thiết bị theo Name
//    public Equipment getEquipmentByName(String equipmentName) throws SQLException {
//        String sql = "SELECT * FROM equipments WHERE equipment_id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, equipmentName);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                Equipment equipment = new Equipment();
//                equipment.setEquipmentId(rs.getInt("equipment_id"));
//                equipment.setEquipmentName(rs.getString("equipment_name"));
//                equipment.setTotalQuantity(rs.getInt("total_quantity"));
//                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
//                equipment.setStatus(rs.getString("status"));
//
//                return equipment;
//            }
//        }
//
//        return null;
//    }
}