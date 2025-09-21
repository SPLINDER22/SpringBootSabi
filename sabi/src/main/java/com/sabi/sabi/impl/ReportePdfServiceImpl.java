package com.sabi.sabi.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.service.ReportePdfService;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
 

@Service
public class ReportePdfServiceImpl implements ReportePdfService {

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private ClienteService clienteService;

    

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ByteArrayOutputStream generarReporteSuscripciones() {
        List<SuscripcionDTO> todas = suscripcionService.getAllActiveSuscripciones();

    long totalSuscripciones = todas.size();
    long suscripcionesPendientes = todas.stream().filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.PENDIENTE).count();
    long suscripcionesCotizadas = todas.stream().filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.COTIZADA).count();
    long suscripcionesAceptadas = todas.stream().filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.ACEPTADA).count();
    long suscripcionesRechazadas = todas.stream().filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA).count();

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
            doc.add(new Paragraph("Total de Suscripciones: " + totalSuscripciones));
            doc.add(new Paragraph("Suscripciones Pendientes: " + suscripcionesPendientes));
            doc.add(new Paragraph("Suscripciones Cotizadas: " + suscripcionesCotizadas));
            doc.add(new Paragraph("Suscripciones Aceptadas: " + suscripcionesAceptadas));
            doc.add(new Paragraph("Suscripciones Rechazadas: " + suscripcionesRechazadas));
            doc.add(Chunk.NEWLINE);

            // Información por entrenador omitida: el reporte es específico del entrenador actual

            // Tabla detalle (opcional breve)
            doc.add(new Paragraph("Detalle (reciente)", seccionFont));
            PdfPTable detalle = new PdfPTable(4);
            detalle.setWidthPercentage(100);
            detalle.setSpacingBefore(5f);
            detalle.addCell("Cliente");
            detalle.addCell("Precio");
            detalle.addCell("Estado");
            detalle.addCell("Rango");
            int count = 0;
            for (SuscripcionDTO s : todas) {
                if (count++ > 25) break; // limitar tamaño
                String clienteNombre = "-";
                if (s.getIdCliente() != null) {
                    try {
                        var cli = clienteService.getClienteById(s.getIdCliente());
                        clienteNombre = cli != null && cli.getNombre() != null ? cli.getNombre() : String.valueOf(s.getIdCliente());
                    } catch (Exception ignored) {}
                }
                detalle.addCell(clienteNombre);
                String precioStr = s.getPrecio() != null ? String.format(java.util.Locale.US, "%.2f", s.getPrecio()) : "-";
                detalle.addCell(precioStr);
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
