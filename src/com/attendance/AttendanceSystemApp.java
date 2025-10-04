package com.attendance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class AttendanceSystemApp {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Attendance System Login ===");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.print("Enter Role (Admin/Employee): ");
        String role = scanner.nextLine();

        if (UserDAO.authenticate(username, password, role)) {
            System.out.println("Login successful as " + role);
            if (role.equalsIgnoreCase("Admin")) {
                adminMenu();
            } else {
                employeeMenu(username);
            }
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    // ----- Admin menu -----
    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add New User");
            System.out.println("2. Update User");
            System.out.println("3. Delete User");
            System.out.println("4. View All Users");
            System.out.println("5. Mark Attendance");
            System.out.println("6. Update Attendance Records");
            System.out.println("7. Approve/Reject Leave Requests");
            System.out.println("8. Approve/Reject Attendance Corrections");
            System.out.println("9. Generate Attendance & Leave Reports");
            System.out.println("10. Logout");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    addNewUser();
                    break;
                case 2:
                    updateUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                	UserDAO userDAO = new UserDAO();
                    viewAllUsers(userDAO);
                    break;
                case 5:
                    markAttendance();
                    break;
                case 6:
                    updateAttendanceRecords();
                    break;
                case 7:
                    approveRejectLeaveRequests();
                    break;
                case 8:
                    approveRejectAttendanceCorrections();
                    break;
                case 9:
                    generateReports();
                    break;
                case 10:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ----- Employee menu -----
    private static void employeeMenu(String username) {
        int userId = UserDAO.getUserIdByUsername(username);
        if (userId == -1) {
            System.out.println("User not found. Logging out.");
            return;
        }

        while (true) {
            System.out.println("\n--- Employee Menu ---");
            System.out.println("1. Request Leave");
            System.out.println("2. View Leave Status");
            System.out.println("3. View Attendance Summary");
            System.out.println("4. Request Attendance Correction");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    requestLeave(userId);
                    break;
                case 2:
                    viewLeaveStatus(userId);
                    break;
                case 3:
                    viewAttendanceSummary(userId);
                    break;
                case 4:
                    requestAttendanceCorrection(userId);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // --- Admin functions ---

    private static void addNewUser() {
        System.out.println("--- Add New User ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (Admin/Employee): ");
        String role = scanner.nextLine();
        System.out.print("Enter full name: ");
        String name = scanner.nextLine();
        System.out.print("Enter department: ");
        String department = scanner.nextLine();
        System.out.print("Enter designation: ");
        String designation = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contactNo = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        UserDAO.addUser(username, password, role, name, department, designation, contactNo, email);
    }

    private static void updateUser() {
        System.out.println("--- Update User ---");
        System.out.print("Enter user ID to update: ");
        int userId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();
        System.out.print("Enter new role (Admin/Employee): ");
        String role = scanner.nextLine();

        UserDAO.updateUser(userId, password, role);
    }

    private static void deleteUser() {
        System.out.println("--- Delete User ---");
        System.out.print("Enter user ID to delete: ");
        int userId = Integer.parseInt(scanner.nextLine());

        UserDAO.deleteUser(userId);
    }

    private static void viewAllUsers(UserDAO userDAO) {
        List<User> users = userDAO.getAllUsers();

        System.out.printf(
        	    "+----+---------------+----------+---------------+--------------------+-----------------------+------------+-------------------------+%n"
        	);
        	System.out.printf(
        	    "| %-2s | %-13s | %-8s | %-13s | %-18s | %-21s | %-10s | %-23s |%n",
        	    "ID", "Username", "Role", "Name", "Dept", "Designation", "Contact", "Email"
        	);
        	System.out.printf(
        	    "+----+---------------+----------+---------------+--------------------+-----------------------+------------+-------------------------+%n"
        	);

        	for (User u : users) {
        	    System.out.printf(
        	        "| %-2d | %-13s | %-8s | %-13s | %-18s | %-21s | %-10s | %-23s |%n",
        	        u.getUserId(),
        	        u.getUsername(),
        	        u.getRole(),
        	        u.getName(),
        	        u.getDepartment(),
        	        u.getDesignation(),
        	        u.getContactNo(),
        	        u.getEmail()
        	    );
        	}

        	System.out.printf(
        	    "+----+---------------+----------+---------------+--------------------+-----------------------+------------+-------------------------+%n"
        	);
    }


    private static void markAttendance() {
        System.out.println("--- Mark Attendance ---");
        System.out.print("Enter username of employee: ");
        String username = scanner.nextLine();
        int userId = UserDAO.getUserIdByUsername(username);
        if (userId == -1) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter date (YYYY-MM-DD): ");
        Date date = Date.valueOf(scanner.nextLine());
        System.out.print("Enter status (Present/Absent/Late): ");
        String status = scanner.nextLine();

        AttendanceDAO.markAttendance(userId, date, status);
    }

    private static void updateAttendanceRecords() {
        System.out.println("--- Update Attendance Records ---");
        // Similar to markAttendance: allow admin to update by attendance_id or by username/date
        System.out.print("Enter username of employee: ");
        String username = scanner.nextLine();
        int userId = UserDAO.getUserIdByUsername(username);
        if (userId == -1) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter date (YYYY-MM-DD) to update: ");
        Date date = Date.valueOf(scanner.nextLine());
        System.out.print("Enter new status (Present/Absent/Late): ");
        String status = scanner.nextLine();

        AttendanceDAO.markAttendance(userId, date, status);
    }

    private static void approveRejectLeaveRequests() {
        System.out.println("--- Pending Leave Requests ---");
        List<String> requests = LeaveRequestDAO.getAllPendingRequests();
        if (requests.isEmpty()) {
            System.out.println("No pending leave requests.");
            return;
        }
        for (String req : requests) {
            System.out.println(req);
        }
        System.out.print("Enter Leave ID to Approve/Reject (or 0 to cancel): ");
        int leaveId = Integer.parseInt(scanner.nextLine());
        if (leaveId == 0) return;

        System.out.print("Enter new status (Approved/Rejected): ");
        String newStatus = scanner.nextLine();

        if (!newStatus.equalsIgnoreCase("Approved") && !newStatus.equalsIgnoreCase("Rejected")) {
            System.out.println("Invalid status.");
            return;
        }
        LeaveRequestDAO.updateLeaveStatus(leaveId, newStatus);
    }

    private static void approveRejectAttendanceCorrections() {
        System.out.println("--- Pending Attendance Correction Requests ---");
        List<String> corrections = AttendanceCorrectionDAO.getAllPendingCorrections();
        if (corrections.isEmpty()) {
            System.out.println("No pending correction requests.");
            return;
        }
        for (String corr : corrections) {
            System.out.println(corr);
        }
        System.out.print("Enter Correction ID to Approve/Reject (or 0 to cancel): ");
        int correctionId = Integer.parseInt(scanner.nextLine());
        if (correctionId == 0) return;

        System.out.print("Enter new status (Approved/Rejected): ");
        String newStatus = scanner.nextLine();

        if (!newStatus.equalsIgnoreCase("Approved") && !newStatus.equalsIgnoreCase("Rejected")) {
            System.out.println("Invalid status.");
            return;
        }
        AttendanceCorrectionDAO.updateCorrectionStatus(correctionId, newStatus);
    }

    
    // --- Employee functions ---

    private static void requestLeave(int userId) {
        System.out.println("--- Request Leave ---");
        System.out.print("Enter start date (YYYY-MM-DD): ");
        Date startDate = Date.valueOf(scanner.nextLine());
        System.out.print("Enter end date (YYYY-MM-DD): ");
        Date endDate = Date.valueOf(scanner.nextLine());
        System.out.print("Enter reason for leave: ");
        String reason = scanner.nextLine();

        LeaveRequestDAO.requestLeave(userId, startDate, endDate, reason);
    }

    private static void viewLeaveStatus(int userId) {
        System.out.println("--- Your Leave Requests ---");
        List<String> leaves = LeaveRequestDAO.viewLeaveStatus(userId);
        if (leaves.isEmpty()) {
            System.out.println("No leave requests found.");
        } else {
            leaves.forEach(System.out::println);
        }
    }

    private static void viewAttendanceSummary(int userId) {
        System.out.println("--- Your Attendance Summary ---");
        List<String> attendance = AttendanceDAO.getAttendanceSummary(userId);
        if (attendance.isEmpty()) {
            System.out.println("No attendance records found.");
        } else {
            attendance.forEach(System.out::println);
        }
    }
    private static void generateReports() {
        System.out.println("\n=== Attendance Report ===");
        List<String[]> attendanceData = AttendanceDAO.getAllAttendanceRecordsAsArray();
        if (attendanceData.isEmpty()) {
            System.out.println("No attendance records found.");
        } else {
            System.out.printf("+----+---------------+------------+----------+%n");
            System.out.printf("| ID | Username      | Date       | Status   |%n");
            System.out.printf("+----+---------------+------------+----------+%n");
            for (String[] row : attendanceData) {
                System.out.printf("| %-2s | %-13s | %-10s | %-8s |%n", row[0], row[1], row[2], row[3]);
            }
            System.out.printf("+----+---------------+------------+----------+%n");
        }

        System.out.println("\n=== Leave Requests Report ===");
        List<String[]> leaveData = LeaveRequestDAO.getAllLeaveRequestsAsArray();
        if (leaveData.isEmpty()) {
            System.out.println("No leave requests found.");
        } else {
            System.out.printf("+----+------------+------------+------------+---------------------+%n");
            System.out.printf("| ID | Start Date | End Date   | Status     | Reason              |%n");
            System.out.printf("+----+------------+------------+------------+---------------------+%n");
            for (String[] row : leaveData) {
                System.out.printf("| %-2s | %-10s | %-10s | %-10s | %-19s |%n", row[0], row[1], row[2], row[3], row[4]);
            }
            System.out.printf("+----+------------+------------+------------+---------------------+%n");
        }

        System.out.println("\n=== Attendance Corrections Report ===");
        List<String[]> correctionData = AttendanceCorrectionDAO.getAllAttendanceCorrectionsAsArray();
        if (correctionData.isEmpty()) {
            System.out.println("No corrections found.");
        } else {
            System.out.printf("+----+---------------+------------+------------+---------------------+%n");
            System.out.printf("| ID | Username      | Date       | New Status | Reason              |%n");
            System.out.printf("+----+---------------+------------+------------+---------------------+%n");
            for (String[] row : correctionData) {
                System.out.printf("| %-2s | %-13s | %-10s | %-10s | %-19s |%n", row[0], row[1], row[2], row[3], row[4]);
            }
            System.out.printf("+----+---------------+------------+------------+---------------------+%n");
        }
    }

    
    private static void requestAttendanceCorrection(int userId) {
        System.out.println("--- Request Attendance Correction ---");
        System.out.print("Enter attendance date (YYYY-MM-DD) you want to correct: ");
        Date date = Date.valueOf(scanner.nextLine());

        // Find attendance_id for userId and date
        String sql = "SELECT attendance_id, status FROM attendance WHERE user_id = ? AND date = ?";
        int attendanceId = -1;
        String currentStatus = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                attendanceId = rs.getInt("attendance_id");
                currentStatus = rs.getString("status");
            } else {
                System.out.println("No attendance record found for that date.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error finding attendance: " + e.getMessage());
            return;
        }

        System.out.println("Current status on " + date + " is " + currentStatus);
        System.out.print("Enter requested status (Present/Absent/Late): ");
        String requestedStatus = scanner.nextLine();

        System.out.print("Enter reason for correction: ");
        String reason = scanner.nextLine();

        AttendanceCorrectionDAO.requestCorrection(userId, attendanceId, requestedStatus, reason);
    }
}
