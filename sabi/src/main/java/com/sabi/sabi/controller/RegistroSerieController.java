package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RegistroSerieDTO;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.repository.EjercicioAsignadoRepository;
import com.sabi.sabi.repository.RegistroSerieRepository;
import com.sabi.sabi.repository.SerieRepository;
import com.sabi.sabi.service.RegistroSerieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class RegistroSerieController {
    private static final Logger log = LoggerFactory.getLogger(RegistroSerieController.class);
    @Autowired private EjercicioAsignadoRepository ejercicioAsignadoRepository;
    @Autowired private SerieRepository serieRepository;
    @Autowired private RegistroSerieRepository registroSerieRepository;
    @Autowired private RegistroSerieService registroSerieService;

    @GetMapping("/registros-series/{idEjercicioAsignado}")
    public String verRegistroSeries(@PathVariable Long idEjercicioAsignado,
                                    @RequestParam(value = "readonly", required = false) Boolean readonly,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        // Si es ENTRENADOR y no viene en modo readonly, redirigir a gestión de series
        boolean esEntrenador = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority() != null && a.getAuthority().contains("ENTRENADOR"));
        if (esEntrenador && (readonly == null || !readonly)) {
            return "redirect:/series/detallar/" + idEjercicioAsignado;
        }
        try {
            EjercicioAsignado asignado = ejercicioAsignadoRepository.findById(idEjercicioAsignado)
                    .orElseThrow(() -> new RuntimeException("Ejercicio asignado no encontrado"));
            List<Serie> series = serieRepository.getSerieEje(idEjercicioAsignado);
            if (series == null || series.isEmpty()) {
                series = serieRepository.findByEjercicioAsignado(asignado);
            }
            if (series == null) series = new ArrayList<>();
            series = series.stream().sorted(Comparator.comparing(Serie::getOrden, Comparator.nullsLast(Long::compareTo))).toList();
            Map<Long, RegistroSerieDTO> registrosMap = new HashMap<>();
            if (!series.isEmpty()) {
                List<Long> ids = series.stream().map(Serie::getId).filter(Objects::nonNull).toList();
                if (!ids.isEmpty()) {
                    registroSerieRepository.findBySerie_IdIn(ids).forEach(r -> {
                        RegistroSerieDTO dto = new RegistroSerieDTO();
                        dto.setIdRegistroSerie(r.getId());
                        dto.setIdSerie(r.getSerie().getId());
                        dto.setPesoReal(r.getPesoReal());
                        dto.setRepeticionesReales(r.getRepeticionesReales());
                        dto.setDescansoReal(r.getDescansoReal());
                        dto.setComentariosCliente(r.getComentariosCliente());
                        dto.setFechaEjecucion(r.getFechaEjecucion());
                        registrosMap.put(r.getSerie().getId(), dto);
                    });
                }
            }
            for (Serie s : series) {
                registrosMap.computeIfAbsent(s.getId(), k -> { var d = new RegistroSerieDTO(); d.setIdSerie(s.getId()); return d; });
            }
            Long idDia = asignado.getDia() != null ? asignado.getDia().getId() : null;
            model.addAttribute("series", series);
            model.addAttribute("registrosMap", registrosMap);
            model.addAttribute("ejercicioAsignado", asignado);
            model.addAttribute("idDia", idDia);
            model.addAttribute("readonly", true); // Forzamos readonly para la vista de registros
        } catch (Exception ex) {
            log.error("Error cargando registros de series", ex);
            model.addAttribute("error", "No se pudo cargar la vista de registros");
        }
        return "registros-series/formulario";
    }

    @PostMapping("/registros-series/guardar")
    public String guardarRegistro(@ModelAttribute RegistroSerieDTO registroSerieDTO, RedirectAttributes ra) {
        try {
            if (registroSerieDTO.getIdSerie() == null) {
                ra.addFlashAttribute("error", "Serie no especificada");
                return "redirect:/ejercicios";
            }
            var serie = serieRepository.findById(registroSerieDTO.getIdSerie())
                    .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
            if (registroSerieDTO.getFechaEjecucion() == null) registroSerieDTO.setFechaEjecucion(LocalDateTime.now());
            registroSerieService.saveOrUpdateRegistroSerie(registroSerieDTO);
            ra.addFlashAttribute("success", "Registro guardado");
            return "redirect:/registros-series/" + serie.getEjercicioAsignado().getIdEjercicioAsignado() + "?readonly=true";
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "No se pudo guardar");
            if (registroSerieDTO.getIdSerie() != null) {
                try {
                    var serie = serieRepository.findById(registroSerieDTO.getIdSerie()).orElse(null);
                    if (serie != null) return "redirect:/registros-series/" + serie.getEjercicioAsignado().getIdEjercicioAsignado() + "?readonly=true";
                } catch (Exception ignore) {}
            }
            return "redirect:/ejercicios";
        }
    }

    @PostMapping("/registros-series/limpiar")
    public String limpiarRegistro(@RequestParam Long idSerie, RedirectAttributes ra) {
        var serie = serieRepository.findById(idSerie).orElseThrow(() -> new RuntimeException("Serie no encontrada"));
        registroSerieRepository.findFirstBySerie_Id(idSerie).ifPresent(r -> registroSerieRepository.deleteById(r.getId()));
        ra.addFlashAttribute("success", "Registro limpiado");
        return "redirect:/registros-series/" + serie.getEjercicioAsignado().getIdEjercicioAsignado() + "?readonly=true";
    }
}
