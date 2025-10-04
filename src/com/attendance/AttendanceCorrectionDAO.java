package com.attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceCorrectionDAO {

    public static void requestCorrection(int userId, int attendanceId, String requestedStatus, String reason) {
        String sql = "INSERT INTO attendance_corrections (user_id, attendance_id, requested_status, reason) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, attendanceId);
            ps.setString(3, requestedStatus);
            ps.setString(4, reason);
            ps.executeUpdate();
            System.out.println("Attendance correction request submitted.");
        } catch (SQLException e) {
            System.out.println("Error submitting attendance correction: " + e.getMessage());
        }
    }

    public static List<String> getAllPendingCorrections() {
        List<String> corrections = new ArrayList<>();
        String sql = "SELECT ac.correction_id, u.username, a.date, ac.requested_status, ac.reason, ac.status " +
                     "FROM attendance_corrections ac " +
                     "JOIN users u ON ac.user_id = u.user_id " +
                     "JOIN attendance a ON ac.attendance_id = a.attendance_id " +
                     "WHERE ac.status = 'Pending' ORDER BY ac.correction_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String rec = String.format("Correction ID: %d, User: %s, Date: %s, Requested Status: %s, Reason: %s, Status: %s",
                        rs.getInt("correction_id"), rs.getString("username"), rs.getDate("date"),
                        rs.getString("requested_status"), rs.getString("reason"), rs.getString("status"));
                corrections.add(rec);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pending corrections: " + e.getMessage());
        }
        return corrections;
    }

    public static void updateCorrectionStatus(int correctionId, String newStatus) {
        String sql = "UPDATE attendance_corrections SET status = ? WHERE correction_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, correctionId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Attendance correction status updated.");
            } else {
                System.out.println("Correction ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating correction status: " + e.getMessage());
        }
    }
    public static List<String[]> getAllAttendanceCorrectionsAsArray() {
        List<String[]> records = new ArrayList<>();
        String sql = "SELECT ac.correction_id, u.username, a.date, ac.requested_status, ac.reason, ac.status " +
                     "FROM attendance_corrections ac " +
                     "JOIN users u ON ac.user_id = u.user_id " +
                     "JOIN attendance a ON ac.attendance_id = a.attendance_id " +
                     "ORDER BY ac.correction_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("correction_id"));
                String username = rs.getString("username");
                String date = String.valueOf(rs.getDate("date"));
                String requestedStatus = rs.getString("requested_status");
                String reason = rs.getString("reason");
                String status = rs.getString("status");
                records.add(new String[]{id, username, date, requestedStatus, reason, status});
            }
        } catch (Exception e) {
            System.out.println("Error fetching attendance corrections: " + e.getMessage());
        }
        return records;
    }


}

