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
                row.createCell(4).setCellValue(diagnostico.getHorasSueno());
                row.createCell(5).setCellValue(diagnostico.getAlimentacion());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }

    public byte[] generarExcelDiagnostico(DiagnosticoDTO diagnostico, String nombreCliente, byte[] logoBytes) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Diagnóstico");

            int rowNum = 0;

            // Insertar logo si está disponible
            if (logoBytes != null) {
                int pictureIdx = workbook.addPicture(logoBytes, Workbook.PICTURE_TYPE_PNG);
                CreationHelper helper = workbook.getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(rowNum);
                Picture pict = drawing.createPicture(anchor, pictureIdx);
                pict.resize(2, 2);
                rowNum += 3;
            }

            // Nombre y fecha
            Row nombreRow = sheet.createRow(rowNum++);
            Cell nombreCell = nombreRow.createCell(0);
            nombreCell.setCellValue("Nombre del Cliente:");
            nombreCell.setCellStyle(crearEstiloEncabezado(workbook));
            nombreRow.createCell(1).setCellValue(nombreCliente);

            Row fechaRow = sheet.createRow(rowNum++);
            Cell fechaCell = fechaRow.createCell(0);
            fechaCell.setCellValue("Fecha del Diagnóstico:");
            fechaCell.setCellStyle(crearEstiloEncabezado(workbook));
            fechaRow.createCell(1).setCellValue(diagnostico.getFecha() != null ? diagnostico.getFecha().toString() : "");

            rowNum++; // Espacio

            // Encabezados de tabla
            Row headerRow = sheet.createRow(rowNum++);
            String[] columnas = {"Campo", "Valor"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(crearEstiloEncabezado(workbook));
            }

            // Llenar datos principales
            String[][] datos = {
                {"Peso", diagnostico.getPeso() != null ? String.valueOf(diagnostico.getPeso()) : ""},
                {"Estatura", diagnostico.getEstatura() != null ? String.valueOf(diagnostico.getEstatura()) : ""},
                {"IMC", String.valueOf(calcularIMC(diagnostico))},
                {"Horas de Sueño", diagnostico.getHorasSueno() != null ? String.valueOf(diagnostico.getHorasSueno()) : ""},
                {"Alimentación", diagnostico.getAlimentacion() != null ? diagnostico.getAlimentacion() : ""}
            };

            CellStyle styleCampo = workbook.createCellStyle();
            Font fontCampo = workbook.createFont();
            fontCampo.setColor(IndexedColors.DARK_BLUE.getIndex());
            fontCampo.setBold(true);
            styleCampo.setFont(fontCampo);
            styleCampo.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            styleCampo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleCampo.setBorderBottom(BorderStyle.THIN);
            styleCampo.setBorderTop(BorderStyle.THIN);
            styleCampo.setBorderLeft(BorderStyle.THIN);
            styleCampo.setBorderRight(BorderStyle.THIN);

            CellStyle styleValor = workbook.createCellStyle();
            Font fontValor = workbook.createFont();
            fontValor.setColor(IndexedColors.DARK_GREEN.getIndex());
            styleValor.setFont(fontValor);
            styleValor.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            styleValor.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleValor.setBorderBottom(BorderStyle.THIN);
            styleValor.setBorderTop(BorderStyle.THIN);
            styleValor.setBorderLeft(BorderStyle.THIN);
            styleValor.setBorderRight(BorderStyle.THIN);

            for (String[] fila : datos) {
                Row row = sheet.createRow(rowNum++);
                Cell cellCampo = row.createCell(0);
                cellCampo.setCellValue(fila[0]);
                cellCampo.setCellStyle(styleCampo);
                Cell cellValor = row.createCell(1);
                cellValor.setCellValue(fila[1]);
                cellValor.setCellStyle(styleValor);
            }

            // Ajustar ancho de columnas
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

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
