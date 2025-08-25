package com.cafe.util;

import com.cafe.controller.admin.ReportController.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting reports to Excel
 */
public class ExcelExporter {

    public void exportSalesReport(List<Object> salesList, LocalDate startDate, LocalDate endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo doanh thu");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Create title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO DOANH THU");
        titleCell.setCellStyle(headerStyle);

        // Date range
        Row dateRow = sheet.createRow(1);
        dateRow.createCell(0).setCellValue("Từ ngày: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " - Đến ngày: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Create headers
        Row headerRow = sheet.createRow(3);
        String[] headers = {"Ngày", "Doanh thu", "Số đơn hàng", "Trung bình/đơn", "Tăng trưởng"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Fill data - placeholder for SalesData that doesn't exist yet
        int rowNum = 4;
        // TODO: Replace with actual SalesData implementation when available
        /*
        for (SalesData data : salesList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getDate());
            row.createCell(1).setCellValue(data.getRevenue());
            row.createCell(2).setCellValue(data.getOrderCount());
            row.createCell(3).setCellValue(data.getAvgOrder());
            row.createCell(4).setCellValue(data.getGrowth());
        }
        */
        
        // Placeholder row
        if (rowNum == 4) { // Only add placeholder if no data
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Đang cập nhật...");
            row.createCell(1).setCellValue(0);
            row.createCell(2).setCellValue(0);
            row.createCell(3).setCellValue(0);
            row.createCell(4).setCellValue(0);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save file
        String fileName = "BaoCaoDanhThu_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    public void exportProductReport(List<ProductData> productList, LocalDate startDate, LocalDate endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo sản phẩm");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Create title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO SẢN PHẨM");
        titleCell.setCellStyle(headerStyle);

        // Date range
        Row dateRow = sheet.createRow(1);
        dateRow.createCell(0).setCellValue("Từ ngày: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " - Đến ngày: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Create headers
        Row headerRow = sheet.createRow(3);
        String[] headers = {"Sản phẩm", "Số lượng bán", "Doanh thu", "% Tổng doanh thu"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Fill data
        int rowNum = 4;
        for (ProductData data : productList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getProductName());
            row.createCell(1).setCellValue(data.getQuantitySold());
            row.createCell(2).setCellValue(data.getRevenue());
            row.createCell(3).setCellValue((data.getRevenue() / getTotalRevenue(productList)) * 100);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save file
        String fileName = "BaoCaoSanPham_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    public void exportCustomerReport(List<CustomerData> customerList, LocalDate startDate, LocalDate endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo khách hàng");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Create title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO KHÁCH HÀNG");
        titleCell.setCellStyle(headerStyle);

        // Date range
        Row dateRow = sheet.createRow(1);
        dateRow.createCell(0).setCellValue("Từ ngày: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " - Đến ngày: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Create headers
        Row headerRow = sheet.createRow(3);
        String[] headers = {"Khách hàng", "Số đơn hàng", "Tổng chi tiêu", "Đơn hàng cuối"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Fill data
        int rowNum = 4;
        for (CustomerData data : customerList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getFullName());
            row.createCell(1).setCellValue(data.getTotalOrders());
            row.createCell(2).setCellValue(data.getTotalSpent());
            row.createCell(3).setCellValue(data.getLastOrderDate());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save file
        String fileName = "BaoCaoKhachHang_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    public void exportAllReports(List<Object> salesList, List<ProductData> productList,
                                 List<CustomerData> customerList, LocalDate startDate, LocalDate endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // Create sales sheet
        createSalesSheet(workbook, salesList, startDate, endDate);

        // Create product sheet
        createProductSheet(workbook, productList, startDate, endDate);

        // Create customer sheet
        createCustomerSheet(workbook, customerList, startDate, endDate);

        // Save file
        String fileName = "BaoCaoTongHop_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
    
    /**
     * Helper method to calculate total revenue from product list
     */
    private double getTotalRevenue(List<ProductData> productList) {
        return productList.stream()
                .mapToDouble(ProductData::getRevenue)
                .sum();
    }

    private void createSalesSheet(Workbook workbook, List<Object> salesList, LocalDate startDate, LocalDate endDate) {
        // Implementation similar to exportSalesReport but creating sheet in existing workbook
        Sheet sheet = workbook.createSheet("Doanh thu");
        // ... (similar implementation)
    }

    private void createProductSheet(Workbook workbook, List<ProductData> productList, LocalDate startDate, LocalDate endDate) {
        // Implementation similar to exportProductReport but creating sheet in existing workbook
        Sheet sheet = workbook.createSheet("Sản phẩm");
        // ... (similar implementation)
    }

    private void createCustomerSheet(Workbook workbook, List<CustomerData> customerList, LocalDate startDate, LocalDate endDate) {
        // Implementation similar to exportCustomerReport but creating sheet in existing workbook
        Sheet sheet = workbook.createSheet("Khách hàng");
        // ... (similar implementation)
    }
}
