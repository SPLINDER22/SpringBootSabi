package com.sabi.sabi.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.service.ReportePdfService;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
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
        long suscripcionesPendientes = todas.stream()
                .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.PENDIENTE)
                .count();
        long suscripcionesCotizadas = todas.stream()
                .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.COTIZADA).count();
        long suscripcionesAceptadas = todas.stream()
                .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.ACEPTADA).count();
        long suscripcionesRechazadas = todas.stream()
                .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA)
                .count();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // Título
            com.lowagie.text.Font tituloFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18,
                    com.lowagie.text.Font.BOLD, Color.BLACK);
            Paragraph titulo = new Paragraph("Reporte Estadístico de Suscripciones", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10f);
            doc.add(titulo);

            // Resumen (KPIs visuales)
            com.lowagie.text.Font seccionFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14,
                    com.lowagie.text.Font.BOLD, Color.BLACK);
            doc.add(new Paragraph("Resumen", seccionFont));

            // Cálculos adicionales para KPIs
            double sumaPreciosAceptadas = 0d;
            for (SuscripcionDTO s : todas) {
                if (s.getPrecio() != null
                        && s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.ACEPTADA) {
                    sumaPreciosAceptadas += s.getPrecio();
                }
            }
            String ingresosTotalesStr = String.format(java.util.Locale.US, "%.2f", sumaPreciosAceptadas);

            // Fuentes para tarjetas KPI
            com.lowagie.text.Font kpiLabelFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9,
                    com.lowagie.text.Font.NORMAL, Color.WHITE);
            com.lowagie.text.Font kpiValueFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18,
                    com.lowagie.text.Font.BOLD, Color.WHITE);

            PdfPTable kpi = new PdfPTable(4);
            kpi.setWidthPercentage(100);
            kpi.setSpacingBefore(5f);
            kpi.setSpacingAfter(10f);

            // Helper inline para crear celdas KPI
            java.util.function.BiFunction<String, String, PdfPCell> makeBlue = (label, value) -> {
                PdfPCell c = new PdfPCell();
                c.setBackgroundColor(new Color(2, 136, 209)); // azul
                c.setPadding(10f);
                Paragraph l = new Paragraph(label, kpiLabelFont);
                l.setAlignment(Element.ALIGN_CENTER);
                Paragraph v = new Paragraph(value, kpiValueFont);
                v.setAlignment(Element.ALIGN_CENTER);
                c.addElement(l);
                c.addElement(v);
                c.setVerticalAlignment(Element.ALIGN_MIDDLE);
                return c;
            };
            java.util.function.BiFunction<String, String, PdfPCell> makeAmber = (label, value) -> {
                PdfPCell c = new PdfPCell();
                c.setBackgroundColor(new Color(255, 193, 7)); // ámbar
                c.setPadding(10f);
                Paragraph l = new Paragraph(label, kpiLabelFont);
                l.setAlignment(Element.ALIGN_CENTER);
                Paragraph v = new Paragraph(value, kpiValueFont);
                v.setAlignment(Element.ALIGN_CENTER);
                c.addElement(l);
                c.addElement(v);
                c.setVerticalAlignment(Element.ALIGN_MIDDLE);
                return c;
            };
            java.util.function.BiFunction<String, String, PdfPCell> makeGreen = (label, value) -> {
                PdfPCell c = new PdfPCell();
                c.setBackgroundColor(new Color(76, 175, 80)); // verde
                c.setPadding(10f);
                Paragraph l = new Paragraph(label, kpiLabelFont);
                l.setAlignment(Element.ALIGN_CENTER);
                Paragraph v = new Paragraph(value, kpiValueFont);
                v.setAlignment(Element.ALIGN_CENTER);
                c.addElement(l);
                c.addElement(v);
                c.setVerticalAlignment(Element.ALIGN_MIDDLE);
                return c;
            };
            java.util.function.BiFunction<String, String, PdfPCell> makeRed = (label, value) -> {
                PdfPCell c = new PdfPCell();
                c.setBackgroundColor(new Color(244, 67, 54)); // rojo
                c.setPadding(10f);
                Paragraph l = new Paragraph(label, kpiLabelFont);
                l.setAlignment(Element.ALIGN_CENTER);
                Paragraph v = new Paragraph(value, kpiValueFont);
                v.setAlignment(Element.ALIGN_CENTER);
                c.addElement(l);
                c.addElement(v);
                c.setVerticalAlignment(Element.ALIGN_MIDDLE);
                return c;
            };
            java.util.function.BiFunction<String, String, PdfPCell> makeSlate = (label, value) -> {
                PdfPCell c = new PdfPCell();
                c.setBackgroundColor(new Color(55, 71, 79)); // gris azulado
                c.setPadding(10f);
                Paragraph l = new Paragraph(label, kpiLabelFont);
                l.setAlignment(Element.ALIGN_CENTER);
                Paragraph v = new Paragraph(value, kpiValueFont);
                v.setAlignment(Element.ALIGN_CENTER);
                c.addElement(l);
                c.addElement(v);
                c.setVerticalAlignment(Element.ALIGN_MIDDLE);
                return c;
            };
            java.util.function.BiFunction<String, String, PdfPCell> makeTeal = (label, value) -> {
                PdfPCell c = new PdfPCell();
                c.setBackgroundColor(new Color(0, 150, 136)); // verde azulado
                c.setPadding(10f);
                Paragraph l = new Paragraph(label, kpiLabelFont);
                l.setAlignment(Element.ALIGN_CENTER);
                Paragraph v = new Paragraph(value, kpiValueFont);
                v.setAlignment(Element.ALIGN_CENTER);
                c.addElement(l);
                c.addElement(v);
                c.setVerticalAlignment(Element.ALIGN_MIDDLE);
                return c;
            };

            // Primera fila: KPIs por estado
            kpi.addCell(makeAmber.apply("Pendientes", String.valueOf(suscripcionesPendientes)));
            kpi.addCell(makeBlue.apply("Cotizadas", String.valueOf(suscripcionesCotizadas)));
            kpi.addCell(makeGreen.apply("Aceptadas", String.valueOf(suscripcionesAceptadas)));
            kpi.addCell(makeRed.apply("Rechazadas", String.valueOf(suscripcionesRechazadas)));

            // Segunda fila: Totales (ocupan 2 columnas cada uno)
            {
                PdfPCell totalCell = makeSlate.apply("Total Suscripciones", String.valueOf(totalSuscripciones));
                totalCell.setColspan(2);
                kpi.addCell(totalCell);

                PdfPCell ingresosCell = makeTeal.apply("Ingresos Totales (pagadas) $", ingresosTotalesStr);
                ingresosCell.setColspan(2);
                kpi.addCell(ingresosCell);
            }

            doc.add(kpi);

            // KPIs adicionales: Promedios
            double promedioPrecioTodos = todas.stream()
                    .filter(s -> s.getPrecio() != null)
                    .mapToDouble(SuscripcionDTO::getPrecio)
                    .average().orElse(0);
            double promedioPrecioAceptadas = todas.stream()
                    .filter(s -> s.getPrecio() != null
                            && s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.ACEPTADA)
                    .mapToDouble(SuscripcionDTO::getPrecio)
                    .average().orElse(0);
            double promedioPrecioRechazadas = todas.stream()
                    .filter(s -> s.getPrecio() != null
                            && s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA)
                    .mapToDouble(SuscripcionDTO::getPrecio)
                    .average().orElse(0);

            PdfPTable kpi2 = new PdfPTable(3);
            kpi2.setWidthPercentage(100);
            kpi2.setSpacingBefore(0f);
            kpi2.setSpacingAfter(10f);
            PdfPCell promTodos = new PdfPCell();
            promTodos.setBackgroundColor(new Color(63, 81, 181)); // indigo
            promTodos.setPadding(10f);
            promTodos.addElement(new Paragraph("Precio Promedio (todas)", kpiLabelFont));
            promTodos.addElement(
                    new Paragraph(String.format(java.util.Locale.US, "%.2f", promedioPrecioTodos), kpiValueFont));
            promTodos.setVerticalAlignment(Element.ALIGN_MIDDLE);
            promTodos.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell promAcept = new PdfPCell();
            promAcept.setBackgroundColor(new Color(121, 85, 72)); // brown
            promAcept.setPadding(10f);
            promAcept.addElement(new Paragraph("Precio Promedio (aceptadas)", kpiLabelFont));
            promAcept.addElement(
                    new Paragraph(String.format(java.util.Locale.US, "%.2f", promedioPrecioAceptadas), kpiValueFont));
            promAcept.setVerticalAlignment(Element.ALIGN_MIDDLE);
            promAcept.setHorizontalAlignment(Element.ALIGN_CENTER);

            kpi2.addCell(promTodos);
            kpi2.addCell(promAcept);
            PdfPCell promRech = new PdfPCell();
            promRech.setBackgroundColor(new Color(158, 158, 158)); // grey
            promRech.setPadding(10f);
            promRech.addElement(new Paragraph("Precio Promedio (rechazadas)", kpiLabelFont));
            promRech.addElement(
                    new Paragraph(String.format(java.util.Locale.US, "%.2f", promedioPrecioRechazadas), kpiValueFont));
            promRech.setVerticalAlignment(Element.ALIGN_MIDDLE);
            promRech.setHorizontalAlignment(Element.ALIGN_CENTER);
            kpi2.addCell(promRech);
            doc.add(kpi2);

            // Porcentajes Aceptadas vs Rechazadas
            long totalAceptRech = suscripcionesAceptadas + suscripcionesRechazadas;
            double pctAceptadas = totalAceptRech > 0 ? (suscripcionesAceptadas * 100.0 / totalAceptRech) : 0;
            double pctRechazadas = totalAceptRech > 0 ? (suscripcionesRechazadas * 100.0 / totalAceptRech) : 0;

            // Gráfico Pie: Aceptadas vs Rechazadas
            org.knowm.xchart.PieChart pie = new org.knowm.xchart.PieChartBuilder().width(480).height(320)
                    .title("Aceptadas vs Rechazadas").build();
            pie.getStyler().setLegendVisible(true);
            pie.getStyler().setCircular(true);
            pie.addSeries("Aceptadas", suscripcionesAceptadas);
            pie.addSeries("Rechazadas", suscripcionesRechazadas);

            byte[] piePng = org.knowm.xchart.BitmapEncoder.getBitmapBytes(pie,
                    org.knowm.xchart.BitmapEncoder.BitmapFormat.PNG);
            com.lowagie.text.Image pieImg = com.lowagie.text.Image.getInstance(piePng);
            pieImg.scaleToFit(260, 180);

            // Gráfico Barras: Promedios
            org.knowm.xchart.CategoryChart bar = new org.knowm.xchart.CategoryChartBuilder().width(480).height(320)
                    .title("Precio Promedio")
                    .xAxisTitle("Categoría").yAxisTitle("Precio ($)").build();
            bar.getStyler().setLegendVisible(false);
            bar.getStyler().setPlotGridVerticalLinesVisible(false);
            java.util.List<String> categorias = java.util.Arrays.asList("Todas", "Aceptadas", "Rechazadas");
            java.util.List<Double> valores = java.util.Arrays.asList(promedioPrecioTodos, promedioPrecioAceptadas,
                    promedioPrecioRechazadas);
            bar.addSeries("Promedio", categorias, valores);

            byte[] barPng = org.knowm.xchart.BitmapEncoder.getBitmapBytes(bar,
                    org.knowm.xchart.BitmapEncoder.BitmapFormat.PNG);
            com.lowagie.text.Image barImg = com.lowagie.text.Image.getInstance(barPng);
            barImg.scaleToFit(260, 180);

            // Colocar gráficos lado a lado
            PdfPTable charts = new PdfPTable(2);
            charts.setWidthPercentage(100);
            charts.setSpacingBefore(5f);
            charts.setSpacingAfter(5f);
            PdfPCell c1 = new PdfPCell(pieImg, true);
            c1.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell c2 = new PdfPCell(barImg, true);
            c2.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            charts.addCell(c1);
            charts.addCell(c2);
            doc.add(charts);

            // Nota de porcentajes textual
            com.lowagie.text.Font notaFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10,
                    com.lowagie.text.Font.NORMAL, Color.DARK_GRAY);
            doc.add(new Paragraph(String.format(java.util.Locale.US, "Aceptadas: %.1f%%    Rechazadas: %.1f%%",
                    pctAceptadas, pctRechazadas), notaFont));
            doc.add(Chunk.NEWLINE);

            // Información por entrenador omitida: el reporte es específico del entrenador
            // actual

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
                if (count++ > 25)
                    break; // limitar tamaño
                String clienteNombre = "-";
                if (s.getIdCliente() != null) {
                    try {
                        var cli = clienteService.getClienteById(s.getIdCliente());
                        clienteNombre = cli != null && cli.getNombre() != null ? cli.getNombre()
                                : String.valueOf(s.getIdCliente());
                    } catch (Exception ignored) {
                    }
                }
                detalle.addCell(clienteNombre);
                String precioStr = s.getPrecio() != null ? String.format(java.util.Locale.US, "%.2f", s.getPrecio())
                        : "-";
                detalle.addCell(precioStr);
                detalle.addCell(s.getEstadoSuscripcion() != null ? s.getEstadoSuscripcion().name() : "-");
                String rango = "";
                if (s.getFechaInicio() != null)
                    rango += DF.format(s.getFechaInicio());
                rango += " - ";
                if (s.getFechaFin() != null)
                    rango += DF.format(s.getFechaFin());
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
