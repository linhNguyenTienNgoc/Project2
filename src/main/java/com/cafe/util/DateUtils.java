package com.cafe.util;

import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class để xử lý ngày tháng
 */
public class DateUtils {

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    private static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
    private static final String SQL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Tạo StringConverter cho DatePicker với format mặc định
     */
    public static StringConverter<LocalDate> createDateConverter() {
        return createDateConverter(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    /**
     * Tạo StringConverter cho DatePicker với format tùy chỉnh
     */
    public static StringConverter<LocalDate> createDateConverter(DateTimeFormatter formatter) {
        return new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    try {
                        return LocalDate.parse(string, formatter);
                    } catch (DateTimeParseException e) {
                        // Thử parse với format khác nếu format chính thất bại
                        return tryParseDate(string);
                    }
                }
                return null;
            }
        };
    }

    /**
     * Thử parse date với nhiều format khác nhau
     */
    private static LocalDate tryParseDate(String dateString) {
        String[] formats = {
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "yyyy-MM-dd",
            "dd.MM.yyyy",
            "dd/MM/yy",
            "dd-MM-yy"
        };

        for (String format : formats) {
            try {
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
            } catch (DateTimeParseException e) {
                // Tiếp tục thử format tiếp theo
            }
        }

        throw new DateTimeParseException("Cannot parse date: " + dateString, dateString, 0);
    }

    /**
     * Format LocalDate thành String
     */
    public static String formatDate(LocalDate date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * Format LocalDate thành String với format tùy chỉnh
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Format LocalDateTime thành String
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * Format LocalDateTime thành String với format tùy chỉnh
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parse String thành LocalDate
     */
    public static LocalDate parseDate(String dateString) {
        return parseDate(dateString, DEFAULT_DATE_FORMAT);
    }

    /**
     * Parse String thành LocalDate với format tùy chỉnh
     */
    public static LocalDate parseDate(String dateString, String pattern) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parse String thành LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return parseDateTime(dateTimeString, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * Parse String thành LocalDateTime với format tùy chỉnh
     */
    public static LocalDateTime parseDateTime(String dateTimeString, String pattern) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Chuyển đổi LocalDate thành java.sql.Date
     */
    public static java.sql.Date toSqlDate(LocalDate localDate) {
        if (localDate == null) return null;
        return java.sql.Date.valueOf(localDate);
    }

    /**
     * Chuyển đổi LocalDateTime thành java.sql.Timestamp
     */
    public static java.sql.Timestamp toSqlTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return java.sql.Timestamp.valueOf(localDateTime);
    }

    /**
     * Chuyển đổi java.sql.Date thành LocalDate
     */
    public static LocalDate fromSqlDate(java.sql.Date sqlDate) {
        if (sqlDate == null) return null;
        return sqlDate.toLocalDate();
    }

    /**
     * Chuyển đổi java.sql.Timestamp thành LocalDateTime
     */
    public static LocalDateTime fromSqlTimestamp(java.sql.Timestamp sqlTimestamp) {
        if (sqlTimestamp == null) return null;
        return sqlTimestamp.toLocalDateTime();
    }

    /**
     * Kiểm tra xem date có hợp lệ không
     */
    public static boolean isValidDate(String dateString) {
        try {
            parseDate(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy ngày hiện tại
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Lấy thời gian hiện tại
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Tính số ngày giữa hai ngày
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Kiểm tra xem date có phải là ngày trong quá khứ không
     */
    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    /**
     * Kiểm tra xem date có phải là ngày trong tương lai không
     */
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }
}
