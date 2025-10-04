package com.attendance;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String name;
    private String department;
    private String designation;
    private String contactNo;
    private String email;

    public User(int userId, String username, String password, String role, String name,
                String department, String designation, String contactNo, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.contactNo = contactNo;
        this.email = email;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public String getContactNo() { return contactNo; }
    public String getEmail() { return email; }
}
