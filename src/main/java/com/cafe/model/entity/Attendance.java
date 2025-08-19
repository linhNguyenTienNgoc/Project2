package com.cafe.model.entity;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * Entity class cho bảng attendance
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class Attendance {
    
    private int attendanceId;
    private int userId;
    private Timestamp checkIn;
    private Timestamp checkOut;
    private LocalDate workDate;
    private double totalHours;
    private String notes;
    private Timestamp createdAt;
    
    // Additional fields for display
    private String userFullName;
    private String username;
    
    // Constants
    private static final double STANDARD_WORK_HOURS = 8.0;
    private static final double OVERTIME_THRESHOLD = 8.0;
    
    // Constructors
    public Attendance() {
        this.workDate = LocalDate.now();
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    public Attendance(int userId) {
        this();
        this.userId = userId;
    }
    
    public Attendance(int userId, Timestamp checkIn) {
        this(userId);
        this.checkIn = checkIn;
        this.workDate = checkIn.toLocalDateTime().toLocalDate();
    }
    
    public Attendance(int attendanceId, int userId, Timestamp checkIn, Timestamp checkOut, 
                     LocalDate workDate, double totalHours, String notes, Timestamp createdAt) {
        this.attendanceId = attendanceId;
        this.userId = userId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.workDate = workDate;
        this.totalHours = totalHours;
        this.notes = notes;
        this.createdAt = createdAt;
    }
    
    // Business Methods
    
    /**
     * Thực hiện check-in
     */
    public void performCheckIn() {
        this.checkIn = new Timestamp(System.currentTimeMillis());
        this.workDate = this.checkIn.toLocalDateTime().toLocalDate();
    }
    
    /**
     * Thực hiện check-out và tính tổng giờ làm
     */
    public void performCheckOut() {
        this.checkOut = new Timestamp(System.currentTimeMillis());
        calculateTotalHours();
    }
    
    /**
     * Tính tổng số giờ làm việc
     */
    public void calculateTotalHours() {
        if (checkIn != null && checkOut != null) {
            LocalDateTime checkInTime = checkIn.toLocalDateTime();
            LocalDateTime checkOutTime = checkOut.toLocalDateTime();
            
            Duration duration = Duration.between(checkInTime, checkOutTime);
            this.totalHours = duration.toMinutes() / 60.0;
        } else {
            this.totalHours = 0.0;
        }
    }
    
    /**
     * Kiểm tra đã check-in chưa
     */
    public boolean isCheckedIn() {
        return checkIn != null && checkOut == null;
    }
    
    /**
     * Kiểm tra đã hoàn thành ca làm chưa
     */
    public boolean isCompleted() {
        return checkIn != null && checkOut != null;
    }
    
    /**
     * Kiểm tra có làm overtime không
     */
    public boolean isOvertime() {
        return totalHours > OVERTIME_THRESHOLD;
    }
    
    /**
     * Tính số giờ overtime
     */
    public double getOvertimeHours() {
        if (isOvertime()) {
            return totalHours - OVERTIME_THRESHOLD;
        }
        return 0.0;
    }
    
    /**
     * Tính số giờ làm việc bình thường
     */
    public double getRegularHours() {
        return Math.min(totalHours, OVERTIME_THRESHOLD);
    }
    
    /**
     * Kiểm tra có đi muộn không (sau 8:00 AM)
     */
    public boolean isLate() {
        if (checkIn == null) return false;
        
        LocalDateTime checkInTime = checkIn.toLocalDateTime();
        LocalDateTime standardStartTime = workDate.atTime(8, 0); // 8:00 AM
        
        return checkInTime.isAfter(standardStartTime);
    }
    
    /**
     * Tính số phút đi muộn
     */
    public long getLateMinutes() {
        if (!isLate()) return 0;
        
        LocalDateTime checkInTime = checkIn.toLocalDateTime();
        LocalDateTime standardStartTime = workDate.atTime(8, 0);
        
        return Duration.between(standardStartTime, checkInTime).toMinutes();
    }
    
    /**
     * Kiểm tra có về sớm không (trước 5:00 PM)
     */
    public boolean isEarlyLeave() {
        if (checkOut == null) return false;
        
        LocalDateTime checkOutTime = checkOut.toLocalDateTime();
        LocalDateTime standardEndTime = workDate.atTime(17, 0); // 5:00 PM
        
        return checkOutTime.isBefore(standardEndTime);
    }
    
    /**
     * Tính số phút về sớm
     */
    public long getEarlyLeaveMinutes() {
        if (!isEarlyLeave()) return 0;
        
        LocalDateTime checkOutTime = checkOut.toLocalDateTime();
        LocalDateTime standardEndTime = workDate.atTime(17, 0);
        
        return Duration.between(checkOutTime, standardEndTime).toMinutes();
    }
    
    /**
     * Lấy trạng thái attendance
     */
    public String getAttendanceStatus() {
        if (!isCompleted()) {
            if (isCheckedIn()) {
                return "Working";
            } else {
                return "Not Started";
            }
        }
        
        if (isLate() && isEarlyLeave()) {
            return "Late & Early Leave";
        } else if (isLate()) {
            return "Late";
        } else if (isEarlyLeave()) {
            return "Early Leave";
        } else if (isOvertime()) {
            return "Overtime";
        } else {
            return "Normal";
        }
    }
    
    /**
     * Tính điểm attendance (0-100)
     */
    public double getAttendanceScore() {
        if (!isCompleted()) return 0;
        
        double score = 100;
        
        // Trừ điểm nếu đi muộn
        if (isLate()) {
            long lateMinutes = getLateMinutes();
            score -= Math.min(lateMinutes * 0.5, 20); // Tối đa trừ 20 điểm
        }
        
        // Trừ điểm nếu về sớm
        if (isEarlyLeave()) {
            long earlyMinutes = getEarlyLeaveMinutes();
            score -= Math.min(earlyMinutes * 0.5, 20); // Tối đa trừ 20 điểm
        }
        
        // Cộng điểm nếu làm overtime
        if (isOvertime()) {
            score += Math.min(getOvertimeHours() * 5, 10); // Tối đa cộng 10 điểm
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Format hiển thị thời gian check-in
     */
    public String getFormattedCheckIn() {
        return checkIn != null ? 
            checkIn.toLocalDateTime().toLocalTime().toString() : 
            "Not checked in";
    }
    
    /**
     * Format hiển thị thời gian check-out
     */
    public String getFormattedCheckOut() {
        return checkOut != null ? 
            checkOut.toLocalDateTime().toLocalTime().toString() : 
            "Not checked out";
    }
    
    /**
     * Format hiển thị tổng giờ làm
     */
    public String getFormattedTotalHours() {
        int hours = (int) totalHours;
        int minutes = (int) ((totalHours - hours) * 60);
        return String.format("%dh %02dm", hours, minutes);
    }
    
    // Getters and Setters
    public int getAttendanceId() {
        return attendanceId;
    }
    
    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public Timestamp getCheckIn() {
        return checkIn;
    }
    
    public void setCheckIn(Timestamp checkIn) {
        this.checkIn = checkIn;
        if (checkIn != null) {
            this.workDate = checkIn.toLocalDateTime().toLocalDate();
        }
    }
    
    public Timestamp getCheckOut() {
        return checkOut;
    }
    
    public void setCheckOut(Timestamp checkOut) {
        this.checkOut = checkOut;
        calculateTotalHours();
    }
    
    public LocalDate getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }
    
    public double getTotalHours() {
        return totalHours;
    }
    
    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }
    
    // Additional setters for AttendanceService
    public void setHoursWorked(double hoursWorked) {
        this.totalHours = hoursWorked;
    }
    
    public void setOvertimeHours(double overtimeHours) {
        // This is for compatibility with AttendanceService
        // Overtime is calculated automatically
    }
    
    public void setAttendanceDate(LocalDate attendanceDate) {
        this.workDate = attendanceDate;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkIn = Timestamp.valueOf(checkInTime);
        this.workDate = checkInTime.toLocalDate();
    }
    
    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOut = Timestamp.valueOf(checkOutTime);
        calculateTotalHours();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    // Display fields
    public String getUserFullName() {
        return userFullName;
    }
    
    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", userId=" + userId +
                ", userFullName='" + userFullName + '\'' +
                ", workDate=" + workDate +
                ", checkIn=" + getFormattedCheckIn() +
                ", checkOut=" + getFormattedCheckOut() +
                ", totalHours=" + getFormattedTotalHours() +
                ", status='" + getAttendanceStatus() + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Attendance that = (Attendance) obj;
        return attendanceId == that.attendanceId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(attendanceId);
    }
}