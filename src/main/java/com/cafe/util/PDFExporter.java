package com.cafe.util;

import com.cafe.controller.admin.ReportController.SalesData;
import com.cafe.controller.admin.ReportController.ProductData;
import com.cafe.controller.admin.ReportController.CustomerData;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting reports to PDF
 */
public class PDFExporter {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final Font RECEIPT_FONT = new Font(Font.FontFamily.COURIER, 9);

    public void exportSalesReport(List<SalesData> salesList, LocalDate startDate, LocalDate endDate) throws IOException, DocumentException {
        Document document = new Document();
        String fileName = "BaoCaoDanhThu_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Title
        Paragraph title = new Paragraph("BÁO CÁO DOANH THU", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Date range
        Paragraph dateRange = new Paragraph("Từ ngày: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " - Đến ngày: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), NORMAL_FONT);
        dateRange.setAlignment(Element.ALIGN_CENTER);
        document.add(dateRange);
        document.add(new Paragraph(" "));

        // Create table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Table headers
        String[] headers = {"Ngày", "Doanh thu", "Số đơn hàng", "Trung bình/đơn", "Tăng trưởng"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            table.addCell(cell);
        }

        // Table data
        for (SalesData data : salesList) {
            table.addCell(new PdfPCell(new Phrase(data.getDate(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getRevenue(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getOrderCount(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getAvgOrder(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getGrowth(), NORMAL_FONT)));
        }

        document.add(table);
        document.close();
    }

    public void exportProductReport(List<ProductData> productList, LocalDate startDate, LocalDate endDate) throws IOException, DocumentException {
        Document document = new Document();
        String fileName = "BaoCaoSanPham_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Title
        Paragraph title = new Paragraph("BÁO CÁO SẢN PHẨM", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Date range
        Paragraph dateRange = new Paragraph("Từ ngày: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " - Đến ngày: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), NORMAL_FONT);
        dateRange.setAlignment(Element.ALIGN_CENTER);
        document.add(dateRange);
        document.add(new Paragraph(" "));

        // Create table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Table headers
        String[] headers = {"Sản phẩm", "Số lượng bán", "Doanh thu", "% Tổng doanh thu"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            table.addCell(cell);
        }

        // Table data
        for (ProductData data : productList) {
            table.addCell(new PdfPCell(new Phrase(data.getProductName(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getQuantity(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getRevenue(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getPercentage(), NORMAL_FONT)));
        }

        document.add(table);
        document.close();
    }

    public void exportCustomerReport(List<CustomerData> customerList, LocalDate startDate, LocalDate endDate) throws IOException, DocumentException {
        Document document = new Document();
        String fileName = "BaoCaoKhachHang_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Title
        Paragraph title = new Paragraph("BÁO CÁO KHÁCH HÀNG", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Date range
        Paragraph dateRange = new Paragraph("Từ ngày: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " - Đến ngày: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), NORMAL_FONT);
        dateRange.setAlignment(Element.ALIGN_CENTER);
        document.add(dateRange);
        document.add(new Paragraph(" "));

        // Create table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Table headers
        String[] headers = {"Khách hàng", "Số đơn hàng", "Tổng chi tiêu", "Đơn hàng cuối"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            table.addCell(cell);
        }

        // Table data
        for (CustomerData data : customerList) {
            table.addCell(new PdfPCell(new Phrase(data.getCustomerName(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getOrderCount(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getTotalSpent(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(data.getLastOrder(), NORMAL_FONT)));
        }

        document.add(table);
        document.close();
    }

    public void exportAllReports(List<SalesData> salesList, List<ProductData> productList,
                                 List<CustomerData> customerList, LocalDate startDate, LocalDate endDate) throws IOException, DocumentException {
        Document document = new Document();
        String fileName = "BaoCaoTongHop_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Main title
        Paragraph mainTitle = new Paragraph("BÁO CÁO TỔNG HỢP", TITLE_FONT);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(mainTitle);
        document.add(new Paragraph(" "));

        // Date range
        Paragraph dateRange = new Paragraph("Từ ngày: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " - Đến ngày: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), NORMAL_FONT);
        dateRange.setAlignment(Element.ALIGN_CENTER);
        document.add(dateRange);
        document.add(new Paragraph(" "));

        // Sales Report Section
        document.add(new Paragraph("1. BÁO CÁO DOANH THU", HEADER_FONT));
        document.add(new Paragraph(" "));

        PdfPTable salesTable = new PdfPTable(5);
        salesTable.setWidthPercentage(100);
        salesTable.setSpacingBefore(10f);
        salesTable.setSpacingAfter(10f);

        // Sales headers
        String[] salesHeaders = {"Ngày", "Doanh thu", "Số đơn hàng", "Trung bình/đơn", "Tăng trưởng"};
        for (String header : salesHeaders) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            salesTable.addCell(cell);
        }

        // Sales data
        for (SalesData data : salesList) {
            salesTable.addCell(new PdfPCell(new Phrase(data.getDate(), NORMAL_FONT)));
            salesTable.addCell(new PdfPCell(new Phrase(data.getRevenue(), NORMAL_FONT)));
            salesTable.addCell(new PdfPCell(new Phrase(data.getOrderCount(), NORMAL_FONT)));
            salesTable.addCell(new PdfPCell(new Phrase(data.getAvgOrder(), NORMAL_FONT)));
            salesTable.addCell(new PdfPCell(new Phrase(data.getGrowth(), NORMAL_FONT)));
        }

        document.add(salesTable);
        document.newPage();

        // Product Report Section
        document.add(new Paragraph("2. BÁO CÁO SẢN PHẨM", HEADER_FONT));
        document.add(new Paragraph(" "));

        PdfPTable productTable = new PdfPTable(4);
        productTable.setWidthPercentage(100);
        productTable.setSpacingBefore(10f);
        productTable.setSpacingAfter(10f);

        // Product headers
        String[] productHeaders = {"Sản phẩm", "Số lượng bán", "Doanh thu", "% Tổng doanh thu"};
        for (String header : productHeaders) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            productTable.addCell(cell);
        }

        // Product data
        for (ProductData data : productList) {
            productTable.addCell(new PdfPCell(new Phrase(data.getProductName(), NORMAL_FONT)));
            productTable.addCell(new PdfPCell(new Phrase(data.getQuantity(), NORMAL_FONT)));
            productTable.addCell(new PdfPCell(new Phrase(data.getRevenue(), NORMAL_FONT)));
            productTable.addCell(new PdfPCell(new Phrase(data.getPercentage(), NORMAL_FONT)));
        }

        document.add(productTable);
        document.newPage();

        // Customer Report Section
        document.add(new Paragraph("3. BÁO CÁO KHÁCH HÀNG", HEADER_FONT));
        document.add(new Paragraph(" "));

        PdfPTable customerTable = new PdfPTable(4);
        customerTable.setWidthPercentage(100);
        customerTable.setSpacingBefore(10f);
        customerTable.setSpacingAfter(10f);

        // Customer headers
        String[] customerHeaders = {"Khách hàng", "Số đơn hàng", "Tổng chi tiêu", "Đơn hàng cuối"};
        for (String header : customerHeaders) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            customerTable.addCell(cell);
        }

        // Customer data
        for (CustomerData data : customerList) {
            customerTable.addCell(new PdfPCell(new Phrase(data.getCustomerName(), NORMAL_FONT)));
            customerTable.addCell(new PdfPCell(new Phrase(data.getOrderCount(), NORMAL_FONT)));
            customerTable.addCell(new PdfPCell(new Phrase(data.getTotalSpent(), NORMAL_FONT)));
            customerTable.addCell(new PdfPCell(new Phrase(data.getLastOrder(), NORMAL_FONT)));
        }

        document.add(customerTable);

        // Footer
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Báo cáo được tạo vào: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), NORMAL_FONT);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        document.close();
    }

    /**
     * Export receipt content to PDF
     * @param receiptContent Receipt content as string
     * @param filePath Output file path
     */
    public static void exportReceiptToPDF(String receiptContent, String filePath) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Add receipt content as preformatted text
        Paragraph receiptPara = new Paragraph(receiptContent, RECEIPT_FONT);
        receiptPara.setAlignment(Element.ALIGN_LEFT);
        document.add(receiptPara);

        document.close();
    }
}