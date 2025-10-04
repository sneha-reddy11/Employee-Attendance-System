package com.attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    public static void requestLeave(int userId, Date startDate, Date endDate, String reason) {
        String sql = "INSERT INTO leave_requests (user_id, start_date, end_date, reason) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.setString(4, reason);
            ps.executeUpdate();
            System.out.println("Leave request submitted.");
        } catch (SQLException e) {
            System.out.println("Error submitting leave request: " + e.getMessage());
        }
    }

    public static List<String> viewLeaveStatus(int userId) {
        List<String> leaves = new ArrayList<>();
        String sql = "SELECT leave_id, start_date, end_date, reason, status FROM leave_requests WHERE user_id = ? ORDER BY leave_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String rec = String.format("Leave ID: %d, From: %s, To: %s, Reason: %s, Status: %s",
                        rs.getInt("leave_id"), rs.getDate("start_date"), rs.getDate("end_date"),
                        rs.getString("reason"), rs.getString("status"));
                leaves.add(rec);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching leave requests: " + e.getMessage());
        }
        return leaves;
    }

    public static List<String> getAllPendingRequests() {
        List<String> requests = new ArrayList<>();
        String sql = "SELECT l.leave_id, u.username, l.start_date, l.end_date, l.reason, l.status FROM leave_requests l JOIN users u ON l.user_id = u.user_id WHERE l.status = 'Pending' ORDER BY l.leave_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String rec = String.format("Leave ID: %d, User: %s, From: %s, To: %s, Reason: %s, Status: %s",
                        rs.getInt("leave_id"), rs.getString("username"), rs.getDate("start_date"),
                        rs.getDate("end_date"), rs.getString("reason"), rs.getString("status"));
                requests.add(rec);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pending leave requests: " + e.getMessage());
        }
        return requests;
    }

    public static void updateLeaveStatus(int leaveId, String newStatus) {
        String sql = "UPDATE leave_requests SET status = ? WHERE leave_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, leaveId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Leave request updated.");
            } else {
                System.out.println("Leave request ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating leave status: " + e.getMessage());
        }
    }
    public static List<String[]> getAllLeaveRequestsAsArray() {
        List<String[]> records = new ArrayList<>();
        String sql = "SELECT lr.leave_id, lr.start_date, lr.end_date, lr.status, lr.reason " +
                     "FROM leave_requests lr " +
                     "ORDER BY lr.leave_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("leave_id"));
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                String status = rs.getString("status");
                String reason = rs.getString("reason");

                // Order matches: ID, Start Date, End Date, Status, Reason
                records.add(new String[]{id, startDate, endDate, status, reason});
            }
        } catch (Exception e) {
            System.out.println("Error fetching leave requests: " + e.getMessage());
        }
        return records;
    }


}

