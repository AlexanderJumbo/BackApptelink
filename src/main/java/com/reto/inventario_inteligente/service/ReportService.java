package com.reto.inventario_inteligente.service;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void compileReport(Map<String, Object> parameters) throws JRException {
        ///String jrxmlFilePath = "D:/Trabajos/Spring/inventario_inteligente/src/main/resources/reports/inventory_report.jrxml";
        String jrxmlFilePath = "D:/Trabajos/Spring/inventario_inteligente/src/main/resources/reports/"+parameters.get("documentInputName")+".jrxml";

        // Carga del archivo .jrxml desde la ruta absoluta
        InputStream jrxmlInputStream = null;
        try {
            jrxmlInputStream = new FileInputStream(jrxmlFilePath);
            System.out.println("Archivo .jrxml cargado correctamente desde: " + jrxmlFilePath);
        } catch (Exception e) {
            throw new JRException("No se pudo cargar el archivo .jrxml desde la ruta: " + jrxmlFilePath, e);
        }

        // Verificar si el .jrxml se cargó correctamente
        if (jrxmlInputStream == null) {
            throw new JRException("El archivo .jrxml no pudo ser cargado.");
        }

        // Compilación del archivo .jrxml a JasperReport
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(jrxmlInputStream);
            System.out.println("Reporte compilado con éxito.");
        } catch (JRException e) {
            throw new JRException("Error al compilar el archivo .jrxml a .jasper.", e);
        }
        // Verificar si la compilación fue exitosa
        if (jasperReport == null) {
            throw new JRException("El reporte no pudo ser compilado.");
        }

        // Ruta donde se guardará el archivo .jasper
        String jasperFilePath = "target/classes/reports/"+parameters.get("documentOutputName")+".jasper";

        File jasperFile = new File(jasperFilePath);
        File parentDir = jasperFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        // Escribir el archivo .jasper generado
        try {
            JasperCompileManager.compileReportToFile(jrxmlFilePath, jasperFilePath);
            System.out.println("Reporte compilado con éxito a: " + jasperFilePath);
        } catch (JRException e) {
            throw new JRException("Error al escribir el archivo .jasper.", e);
        }
    }

    public void generateReport(Map<String, Object> parameters, Connection connection) throws JRException, SQLException {
        String jasperFilePath = "/reports/"+parameters.get("documentInputName")+".jasper";  // Ruta desde el classpath
        System.out.println("jasperFilePath" + jasperFilePath);
        // Cargar el archivo .jasper desde el classpath
        JasperReport jasperReport = null;
        try {
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResource(jasperFilePath));
        } catch (JRException e) {
            throw new JRException("No se pudo cargar el archivo .jasper desde el classpath: " + jasperFilePath, e);
        }

        if (jasperReport == null) {
            throw new JRException("No se pudo cargar el archivo .jasper.");
        }

        System.out.println("Reporte cargado correctamente.");

        // Llenado de reporte con datos de la conexión de BD
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

        File outputDir = new File("target/reports");
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            if (created) {
                System.out.println("Directorio 'target/reports' creado.");
            } else {
                System.out.println("No se pudo crear el directorio 'target/reports'.");
            }
        }
        // Generación del reporte en PDF)
        String outputPdfPath = "target/reports/"+parameters.get("documentPdfName")+".pdf";

        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPdfPath);

        System.out.println("Reporte generado exitosamente en: " + outputPdfPath);

        connection.close();
    }

}
