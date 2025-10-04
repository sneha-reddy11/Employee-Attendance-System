package com.attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public static void markAttendance(int userId, Date date, String status) {
        String sqlCheck = "SELECT attendance_id FROM attendance WHERE user_id = ? AND date = ?";
        String sqlInsert = "INSERT INTO attendance (user_id, date, status) VALUES (?, ?, ?)";
        String sqlUpdate = "UPDATE attendance SET status = ? WHERE attendance_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Check if attendance exists for this user and date
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setInt(1, userId);
            psCheck.setDate(2, date);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // Update existing attendance
                int attendanceId = rs.getInt("attendance_id");
                PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setString(1, status);
                psUpdate.setInt(2, attendanceId);
                psUpdate.executeUpdate();
                System.out.println("Attendance updated.");
            } else {
                // Insert new attendance
                PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
                psInsert.setInt(1, userId);
                psInsert.setDate(2, date);
                psInsert.setString(3, status);
                psInsert.executeUpdate();
                System.out.println("Attendance marked.");
            }
        } catch (SQLException e) {
            System.out.println("Error marking attendance: " + e.getMessage());
        }
    }

    public static List<String> getAttendanceSummary(int userId) {
        List<String> summaries = new ArrayList<>();
        String sql = "SELECT date, status FROM attendance WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String record = String.format("Date: %s, Status: %s", rs.getDate("date"), rs.getString("status"));
                summaries.add(record);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching attendance summary: " + e.getMessage());
        }
        return summaries;
    }

    // For Admin: view all attendance records (optionally extend this later)
    public static List<String[]> getAllAttendanceRecordsAsArray() {
        List<String[]> records = new ArrayList<>();
        String query = """
            SELECT a.attendance_id, u.username, a.date, a.status
            FROM attendance a
            JOIN users u ON a.user_id = u.user_id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getInt("attendance_id")),
                    rs.getString("username"),
                    rs.getString("date"),
                    rs.getString("status")
                };
                records.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }


}
