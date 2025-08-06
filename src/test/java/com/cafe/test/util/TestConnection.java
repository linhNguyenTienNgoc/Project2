package test.java.com.cafe.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        // Thông tin kết nối
        String jdbcURL = "jdbc:mysql://localhost:3306/cafe_management"; // thay đổi nếu DB tên khác
        String dbUser = "root";      // tài khoản MySQL
        String dbPassword = "12345678"; // mật khẩu MySQL

        try {
            // Kết nối tới DB
            Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
            System.out.println("✅ Kết nối thành công tới cơ sở dữ liệu!");
            connection.close();
        } catch (SQLException e) {
            System.out.println("❌ Kết nối thất bại!");
            e.printStackTrace();
        }
    }
}
