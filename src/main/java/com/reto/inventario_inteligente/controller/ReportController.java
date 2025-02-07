package com.reto.inventario_inteligente.controller;

import com.reto.inventario_inteligente.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/generate-inventory-report")
    public ResponseEntity<FileSystemResource> downloadReport() throws IOException, JRException, SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sqlTotalInventario = "SELECT sum(price * stock_quantity) FROM products WHERE status = true";
        Double totalInventario = jdbcTemplate.queryForObject(sqlTotalInventario, Double.class);
        String sqlAVGInventario = "SELECT AVG(price) FROM products WHERE status = true";
        Double avgInventario = jdbcTemplate.queryForObject(sqlAVGInventario, Double.class);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("documentInputName", "inventory_report");
        parameters.put("documentOutputName", "inventory_report");
        parameters.put("documentPdfName", "inventory_report");
        parameters.put("totalInventario", totalInventario);
        parameters.put("avgInventario", avgInventario);

        try{
            reportService.compileReport(parameters);
            reportService.generateReport(parameters, connection);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al generar el reporte: " + e.getMessage());
        }

        // Ruta donde se crea el reporte
        File reportFile = new File("target/reports/"+parameters.get("documentPdfName")+".pdf");

        if (!reportFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        // Retornar el archivo con el tipo de contenido "application/pdf"
        FileSystemResource fileSystemResource = new FileSystemResource(reportFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(fileSystemResource);
    }

    @GetMapping("/generate-invoice/{id}")
    public ResponseEntity<FileSystemResource> downloadInvoice(@PathVariable Long id) throws IOException, JRException, SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();

        String documentPdfName = "invoice_{0}";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("documentInputName", "invoiceTemplate");
        parameters.put("documentOutputName", "invoiceTemplate");
        parameters.put("documentPdfName", MessageFormat.format(documentPdfName, id));
        parameters.put("orderId", id);
        System.out.println("docummentnene" + MessageFormat.format(documentPdfName, id));

        try{
            reportService.compileReport(parameters);
            reportService.generateReport(parameters, connection);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al generar el reporte: " + e.getMessage());
        }

        // Ruta donde se generó el reporte
        File reportFile = new File("target/reports/"+parameters.get("documentPdfName")+".pdf");

        // Verificar si el archivo existe
        if (!reportFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Retornar el archivo con el tipo de contenido "application/pdf"
        FileSystemResource fileSystemResource = new FileSystemResource(reportFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(fileSystemResource);
    }

}
