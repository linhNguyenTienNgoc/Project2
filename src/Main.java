
import main.java.com.cafe.dao.base.AreaDAO;
import main.java.com.cafe.dao.base.AreaDAOImpl;
import main.java.com.cafe.model.entity.Area;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/cafe_management";
        String user = "root";
        String password = "12345678";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            AreaDAO areaDAO = new AreaDAOImpl(conn);

            // Thêm mới
            areaDAO.addArea(new Area("Khu A", "Khu bên trái", true));

            // Lấy và in danh sách
            for (Area a : areaDAO.getAllAreas()) {
                System.out.println(a.getAreaName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
