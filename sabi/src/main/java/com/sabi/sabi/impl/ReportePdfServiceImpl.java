package com.sabi.sabi.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.service.EntrenadorService;
import com.sabi.sabi.service.ReportePdfService;
import com.sabi.sabi.service.SuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportePdfServiceImpl implements ReportePdfService {

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private EntrenadorService entrenadorService;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ByteArrayOutputStream generarReporteSuscripciones() {
        List<SuscripcionDTO> todas = suscripcionService.getAllActiveSuscripciones();

        long total = todas.size();
        long pendientes = todas.stream().filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.PENDIENTE).count();
        long cotizadas = todas.stream().filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.COTIZADA).count();
        long aceptadas = todas.stream().filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.ACEPTADA).count();
        long rechazadas = todas.stream().filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA).count();

        Map<Long, Long> porEntrenador = todas.stream()
                .filter(s -> s.getIdEntrenador() != null)
                .collect(Collectors.groupingBy(SuscripcionDTO::getIdEntrenador, Collectors.counting()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // Título
            com.lowagie.text.Font tituloFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, Color.BLACK);
            Paragraph titulo = new Paragraph("Reporte Estadístico de Suscripciones", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10f);
            doc.add(titulo);

            // Resumen
            com.lowagie.text.Font seccionFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD, Color.BLACK);
            doc.add(new Paragraph("Resumen", seccionFont));
            doc.add(new Paragraph("Total: " + total));
            doc.add(new Paragraph("Pendientes: " + pendientes));
            doc.add(new Paragraph("Cotizadas: " + cotizadas));
            doc.add(new Paragraph("Aceptadas: " + aceptadas));
            doc.add(new Paragraph("Rechazadas: " + rechazadas));
            doc.add(Chunk.NEWLINE);

            // Tabla por entrenador
            doc.add(new Paragraph("Suscripciones por Entrenador", seccionFont));
            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(5f);
            PdfPCell c1 = new PdfPCell(new Phrase("Entrenador"));
            PdfPCell c2 = new PdfPCell(new Phrase("Cantidad"));
            c1.setBackgroundColor(new Color(230,230,230));
            c2.setBackgroundColor(new Color(230,230,230));
            tabla.addCell(c1);
            tabla.addCell(c2);

            for (Map.Entry<Long, Long> e : porEntrenador.entrySet()) {
                String nombre = "-";
                try {
                    var ent = entrenadorService.getEntrenadorById(e.getKey());
                    if (ent != null && ent.getNombre() != null) nombre = ent.getNombre();
                } catch (Exception ex) {
                    nombre = String.valueOf(e.getKey());
                }
                tabla.addCell(nombre);
                tabla.addCell(String.valueOf(e.getValue()));
            }
            doc.add(tabla);
            doc.add(Chunk.NEWLINE);

            // Tabla detalle (opcional breve)
            doc.add(new Paragraph("Detalle (reciente)", seccionFont));
            PdfPTable detalle = new PdfPTable(5);
            detalle.setWidthPercentage(100);
            detalle.setSpacingBefore(5f);
            detalle.addCell("ID");
            detalle.addCell("ClienteID");
            detalle.addCell("Entrenador");
            detalle.addCell("Estado");
            detalle.addCell("Rango");
            int count = 0;
            for (SuscripcionDTO s : todas) {
                if (count++ > 25) break; // limitar tamaño
                detalle.addCell(String.valueOf(s.getIdSuscripcion()));
                detalle.addCell(s.getIdCliente() != null ? String.valueOf(s.getIdCliente()) : "-");
                String ent = "-";
                if (s.getIdEntrenador() != null) {
                    try {
                        var en = entrenadorService.getEntrenadorById(s.getIdEntrenador());
                        ent = en != null && en.getNombre() != null ? en.getNombre() : String.valueOf(s.getIdEntrenador());
                    } catch (Exception ignored) {}
                }
                detalle.addCell(ent);
                detalle.addCell(s.getEstadoSuscripcion() != null ? s.getEstadoSuscripcion().name() : "-");
                String rango = "";
                if (s.getFechaInicio() != null) rango += DF.format(s.getFechaInicio());
                rango += " - ";
                if (s.getFechaFin() != null) rango += DF.format(s.getFechaFin());
                detalle.addCell(rango);
            }
            doc.add(detalle);

        } catch (Exception ex) {
            // en caso de error, aún devolver lo que haya
        } finally {
            doc.close();
        }
        return baos;
    }
}
