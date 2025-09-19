package com.sabi.sabi.service;

import com.sabi.sabi.dto.DiagnosticoDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExcelService {

    public byte[] generarExcelDiagnosticos(List<DiagnosticoDTO> diagnosticos) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Historial Diagnósticos");

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] columnas = {"Fecha", "Peso", "Estatura", "IMC", "Horas de Sueño", "Alimentación"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(crearEstiloEncabezado(workbook));
            }

            // Llenar datos
            int rowNum = 1;
            for (DiagnosticoDTO diagnostico : diagnosticos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(diagnostico.getFecha().toString());
                row.createCell(1).setCellValue(diagnostico.getPeso());
                row.createCell(2).setCellValue(diagnostico.getEstatura());
                row.createCell(3).setCellValue(calcularIMC(diagnostico));
                row.createCell(4).setCellValue(diagnostico.getSueno());
                row.createCell(5).setCellValue(diagnostico.getAlimentacion());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }

    public byte[] generarExcelDiagnostico(DiagnosticoDTO diagnostico) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Diagnóstico");

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] columnas = {"Campo", "Valor"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(crearEstiloEncabezado(workbook));
            }

            // Llenar datos
            String[][] datos = {
                {"Fecha", diagnostico.getFecha().toString()},
                {"Peso", String.valueOf(diagnostico.getPeso())},
                {"Estatura", String.valueOf(diagnostico.getEstatura())},
                {"IMC", String.valueOf(calcularIMC(diagnostico))},
                {"Horas de Sueño", String.valueOf(diagnostico.getSueno())},
                {"Alimentación", diagnostico.getAlimentacion()}
            };

            int rowNum = 1;
            for (String[] fila : datos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(fila[0]);
                row.createCell(1).setCellValue(fila[1]);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }

    private double calcularIMC(DiagnosticoDTO diagnostico) {
        if (diagnostico.getPeso() != null && diagnostico.getEstatura() != null) {
            double estaturaMetros = diagnostico.getEstatura() / 100.0;
            return Math.round((diagnostico.getPeso() / (estaturaMetros * estaturaMetros)) * 10) / 10.0;
        }
        return 0;
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
