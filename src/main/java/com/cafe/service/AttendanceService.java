package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.model.entity.User;
import com.cafe.model.entity.Attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Attendance Service - Complete Employee Attendance Management System
 * Xử lý tất cả các nghiệp vụ liên quan đến chấm công nhân viên
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class AttendanceService {
    
    public AttendanceService() {
        // Connections will be managed per operation using try-with-resources
    }

    /**
     * Chấm công vào ca (check-in)
     */
    public boolean checkIn(int userId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Check if user already checked in today
            if (hasCheckedInToday(userId)) {
                System.err.println("User already checked in today: " + userId);
                return false;
            }

            String sql = "INSERT INTO attendance (user_id, check_in_time, attendance_date) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    System.out.println("✅ Check-in successful for user: " + userId);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error during check-in: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Chấm công ra ca (check-out)
     */
    public boolean checkOut(int userId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Find today's attendance record
            String findSql = "SELECT attendance_id, check_in_time FROM attendance WHERE user_id = ? AND attendance_date = ? AND check_out_time IS NULL";
            try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
                findStmt.setInt(1, userId);
                findStmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (rs.next()) {
                        int attendanceId = rs.getInt("attendance_id");
                        Timestamp checkInTime = rs.getTimestamp("check_in_time");
                        LocalDateTime checkOutTime = LocalDateTime.now();
                        
                        // Calculate hours worked
                        long hoursWorked = java.time.Duration.between(
                            checkInTime.toLocalDateTime(), 
                            checkOutTime
                        ).toHours();
                        
                        // Update record with check-out time and hours worked
                        String updateSql = "UPDATE attendance SET check_out_time = ?, hours_worked = ? WHERE attendance_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setTimestamp(1, Timestamp.valueOf(checkOutTime));
                            updateStmt.setDouble(2, hoursWorked);
                            updateStmt.setInt(3, attendanceId);
                            
                            int result = updateStmt.executeUpdate();
                            if (result > 0) {
                                System.out.println("✅ Check-out successful for user: " + userId + ", Hours worked: " + hoursWorked);
                                return true;
                            }
                        }
                    } else {
                        System.err.println("No check-in record found for today for user: " + userId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error during check-out: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra xem nhân viên đã chấm công vào hôm nay chưa
     */
    public boolean hasCheckedInToday(int userId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT COUNT(*) FROM attendance WHERE user_id = ? AND attendance_date = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error checking attendance: " + e.getMessage());
        }
        return false;
    }

    /**
     * Kiểm tra xem nhân viên đã chấm công ra hôm nay chưa
     */
    public boolean hasCheckedOutToday(int userId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT check_out_time FROM attendance WHERE user_id = ? AND attendance_date = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getTimestamp("check_out_time") != null;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error checking checkout status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lấy bản ghi chấm công của nhân viên theo ngày
     */
    public Optional<Attendance> getAttendanceByUserAndDate(int userId, LocalDate date) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM attendance WHERE user_id = ? AND attendance_date = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(date));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Attendance attendance = new Attendance();
                        attendance.setAttendanceId(rs.getInt("attendance_id"));
                        attendance.setUserId(rs.getInt("user_id"));
                        attendance.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
                        
                        Timestamp checkIn = rs.getTimestamp("check_in_time");
                        if (checkIn != null) {
                            attendance.setCheckInTime(checkIn.toLocalDateTime());
                        }
                        
                        Timestamp checkOut = rs.getTimestamp("check_out_time");
                        if (checkOut != null) {
                            attendance.setCheckOutTime(checkOut.toLocalDateTime());
                        }
                        
                        attendance.setHoursWorked(rs.getDouble("hours_worked"));
                        attendance.setOvertimeHours(rs.getDouble("overtime_hours"));
                        
                        return Optional.of(attendance);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting attendance: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Lấy tất cả bản ghi chấm công trong khoảng thời gian
     */
    public List<AttendanceReport> getAttendanceReport(LocalDate fromDate, LocalDate toDate) {
        List<AttendanceReport> reports = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            UserDAO userDAO = new UserDAOImpl(conn);
            List<User> users = userDAO.getAllUsers();
            
            String sql = "SELECT * FROM attendance WHERE user_id = ? AND attendance_date BETWEEN ? AND ? ORDER BY attendance_date";
            
            for (User user : users) {
                if (!"Admin".equals(user.getRole())) { // Skip admin users
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, user.getUserId());
                        stmt.setDate(2, java.sql.Date.valueOf(fromDate));
                        stmt.setDate(3, java.sql.Date.valueOf(toDate));
                        
                        try (ResultSet rs = stmt.executeQuery()) {
                            double totalHours = 0;
                            double totalOvertime = 0;
                            int totalDays = 0;
                            
                            while (rs.next()) {
                                totalHours += rs.getDouble("hours_worked");
                                totalOvertime += rs.getDouble("overtime_hours");
                                totalDays++;
                            }
                            
                            if (totalDays > 0) {
                                reports.add(new AttendanceReport(
                                    user.getFullName(),
                                    user.getRole(),
                                    totalDays,
                                    totalHours,
                                    totalOvertime,
                                    totalDays > 0 ? totalHours / totalDays : 0
                                ));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error generating attendance report: " + e.getMessage());
        }
        
        return reports;
    }

    /**
     * Tính overtime cho tất cả nhân viên trong ngày
     */
    public void calculateOvertimeForDate(LocalDate date) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "UPDATE attendance SET overtime_hours = CASE " +
                        "WHEN hours_worked > 8 THEN hours_worked - 8 " +
                        "ELSE 0 END " +
                        "WHERE attendance_date = ? AND check_out_time IS NOT NULL";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(date));
                int updated = stmt.executeUpdate();
                System.out.println("✅ Updated overtime for " + updated + " attendance records on " + date);
            }
        } catch (Exception e) {
            System.err.println("❌ Error calculating overtime: " + e.getMessage());
        }
    }

    /**
     * Lấy thống kê chấm công của nhân viên trong tháng
     */
    public AttendanceStatistics getMonthlyStatistics(int userId, int year, int month) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            
            String sql = "SELECT COUNT(*) as total_days, " +
                        "SUM(hours_worked) as total_hours, " +
                        "SUM(overtime_hours) as total_overtime, " +
                        "AVG(hours_worked) as avg_hours " +
                        "FROM attendance " +
                        "WHERE user_id = ? AND attendance_date BETWEEN ? AND ? AND check_out_time IS NOT NULL";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(startDate));
                stmt.setDate(3, java.sql.Date.valueOf(endDate));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new AttendanceStatistics(
                            rs.getInt("total_days"),
                            rs.getDouble("total_hours"),
                            rs.getDouble("total_overtime"),
                            rs.getDouble("avg_hours")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting monthly statistics: " + e.getMessage());
        }
        
        return new AttendanceStatistics(0, 0, 0, 0);
    }

    // Data Transfer Objects
    public static class AttendanceReport {
        private String employeeName;
        private String role;
        private int totalDays;
        private double totalHours;
        private double totalOvertime;
        private double avgHoursPerDay;

        public AttendanceReport(String employeeName, String role, int totalDays, 
                              double totalHours, double totalOvertime, double avgHoursPerDay) {
            this.employeeName = employeeName;
            this.role = role;
            this.totalDays = totalDays;
            this.totalHours = totalHours;
            this.totalOvertime = totalOvertime;
            this.avgHoursPerDay = avgHoursPerDay;
        }

        // Getters
        public String getEmployeeName() { return employeeName; }
        public String getRole() { return role; }
        public int getTotalDays() { return totalDays; }
        public double getTotalHours() { return totalHours; }
        public double getTotalOvertime() { return totalOvertime; }
        public double getAvgHoursPerDay() { return avgHoursPerDay; }
    }

    public static class AttendanceStatistics {
        private int totalDays;
        private double totalHours;
        private double totalOvertime;
        private double avgHours;

        public AttendanceStatistics(int totalDays, double totalHours, double totalOvertime, double avgHours) {
            this.totalDays = totalDays;
            this.totalHours = totalHours;
            this.totalOvertime = totalOvertime;
            this.avgHours = avgHours;
        }

        // Getters
        public int getTotalDays() { return totalDays; }
        public double getTotalHours() { return totalHours; }
        public double getTotalOvertime() { return totalOvertime; }
        public double getAvgHours() { return avgHours; }
    }
}


