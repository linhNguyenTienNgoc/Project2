package main.java.com.cafe.dao.base;

import main.java.com.cafe.model.entity.TableCafe;

import java.util.List;

public interface TableDAO {
    boolean addTable(TableCafe table);
    boolean updateTable(TableCafe table);
    boolean deleteTable(int tableId);
    TableCafe getTableById(int tableId);
    List<TableCafe> getAllTables();
    List<TableCafe> getTablesByArea(int areaId);
}