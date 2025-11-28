package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RegistroSerieDTO;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.repository.EjercicioAsignadoRepository;
import com.sabi.sabi.repository.RegistroSerieRepository;
import com.sabi.sabi.repository.SerieRepository;
import com.sabi.sabi.service.RegistroSerieService;
import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.EjercicioAsignadoDTO;
import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.service.EjercicioAsignadoService;
import com.sabi.sabi.service.EjercicioService;
import com.sabi.sabi.service.SemanaService;
import com.sabi.sabi.service.DiaService;
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
    @Autowired private EjercicioAsignadoService ejercicioAsignadoService;
    @Autowired private SemanaService semanaService;
    @Autowired private DiaService diaService;
    @Autowired private EjercicioService ejercicioService;

    @GetMapping("/registros-series/{idEjercicioAsignado}")
    public String verRegistroSeries(@PathVariable Long idEjercicioAsignado,
                                    @RequestParam(value = "readonly", required = false) Boolean readonly,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        boolean esEntrenador = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority() != null && a.getAuthority().contains("ENTRENADOR"));
        // Entrenador en modo creación => redirigir a gestión de series (no registros)
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
                        dto.setIntensidadReal(r.getIntensidadReal());
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
            // Añadir datos para las pestañas (semanas/dias/ejes) — similar a SerieController.detallarSeries
            try {
                EjercicioAsignadoDTO ejeDTO = null;
                try { ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(idEjercicioAsignado); } catch (Exception ignore) {}
                if (ejeDTO != null) {
                    DiaDTO diaDTO = null;
                    try { diaDTO = diaService.getDiaById(ejeDTO.getIdDia()); } catch (Exception ignore) {}
                    SemanaDTO semanaDTO = null;
                    if (diaDTO != null) {
                        try { semanaDTO = semanaService.getSemanaById(diaDTO.getIdSemana()); } catch (Exception ignore) {}
                    }

                    if (diaDTO != null) model.addAttribute("dia", diaDTO);
                    if (diaDTO != null) model.addAttribute("idSemana", diaDTO.getIdSemana());
                    if (semanaDTO != null) model.addAttribute("idRutina", semanaDTO.getIdRutina());

                    try {
                        if (semanaDTO != null) model.addAttribute("semanas", semanaService.getSemanasRutina(semanaDTO.getIdRutina()));
                    } catch (Exception ignore) {}
                    try { if (semanaDTO != null) model.addAttribute("dias", diaService.getDiasSemana(semanaDTO.getIdSemana())); } catch (Exception ignore) {}
                    try { if (diaDTO != null) model.addAttribute("ejes", ejercicioAsignadoService.getEjesDia(diaDTO.getIdDia())); } catch (Exception ignore) {}

                    model.addAttribute("totalSeries", series.size());
                    // nombreEjercicio: preferir valor del DTO, fallback a servicio
                    if (ejeDTO.getNombreEjercicio() != null && !ejeDTO.getNombreEjercicio().isBlank()) {
                        model.addAttribute("nombreEjercicio", ejeDTO.getNombreEjercicio());
                    } else if (ejeDTO.getIdEjercicio() != null) {
                        try {
                            var ej = ejercicioService.getEjercicioById(ejeDTO.getIdEjercicio());
                            if (ej != null) model.addAttribute("nombreEjercicio", ej.getNombre());
                        } catch (Exception ignore) {}
                    }
                }
            } catch (Exception ignore) {}
            model.addAttribute("readonly", esEntrenador || Boolean.TRUE.equals(readonly));
        } catch (Exception ex) {
            log.error("Error cargando registros de series", ex);
            model.addAttribute("error", "No se pudo cargar la vista de registros");
        }
        return "registros-series/formulario";
    }

    @PostMapping("/registros-series/guardar")
    public String guardarRegistro(@ModelAttribute RegistroSerieDTO registroSerieDTO,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes ra) {
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
            boolean esEntrenador = userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority() != null && a.getAuthority().contains("ENTRENADOR"));
            String sufijo = esEntrenador ? "?readonly=true" : "";
            return "redirect:/registros-series/" + serie.getEjercicioAsignado().getIdEjercicioAsignado() + sufijo;
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "No se pudo guardar");
            if (registroSerieDTO.getIdSerie() != null) {
                try {
                    var serie = serieRepository.findById(registroSerieDTO.getIdSerie()).orElse(null);
                    if (serie != null) {
                        boolean esEntrenador = userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority() != null && a.getAuthority().contains("ENTRENADOR"));
                        String sufijo = esEntrenador ? "?readonly=true" : "";
                        return "redirect:/registros-series/" + serie.getEjercicioAsignado().getIdEjercicioAsignado() + sufijo;
                    }
                } catch (Exception ignore) {}
            }
            return "redirect:/ejercicios";
        }
    }

    @PostMapping("/registros-series/limpiar")
    public String limpiarRegistro(@RequestParam Long idSerie,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes ra) {
        var serie = serieRepository.findById(idSerie).orElseThrow(() -> new RuntimeException("Serie no encontrada"));
        registroSerieRepository.findFirstBySerie_Id(idSerie).ifPresent(r -> registroSerieRepository.deleteById(r.getId()));
        ra.addFlashAttribute("success", "Registro limpiado");
        boolean esEntrenador = userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority() != null && a.getAuthority().contains("ENTRENADOR"));
        String sufijo = esEntrenador ? "?readonly=true" : "";
        return "redirect:/registros-series/" + serie.getEjercicioAsignado().getIdEjercicioAsignado() + sufijo;
    }

    /**
     * Endpoint REST para guardar registro desde la vista unificada del cliente
     * Devuelve JSON en lugar de redireccionar
     */
    @PostMapping("/api/registros-series/guardar")
    @ResponseBody
    public Map<String, Object> guardarRegistroAjax(@RequestBody RegistroSerieDTO registroSerieDTO,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (registroSerieDTO.getIdSerie() == null) {
                response.put("success", false);
                response.put("message", "Serie no especificada");
                return response;
            }

            // Verificar que la serie existe
            var serie = serieRepository.findById(registroSerieDTO.getIdSerie())
                    .orElseThrow(() -> new RuntimeException("Serie no encontrada"));

            // Establecer fecha de ejecución si no viene
            if (registroSerieDTO.getFechaEjecucion() == null) {
                registroSerieDTO.setFechaEjecucion(LocalDateTime.now());
            }

            // Guardar el registro
            RegistroSerieDTO registroGuardado = registroSerieService.saveOrUpdateRegistroSerie(registroSerieDTO);

            // Verificar si se completaron todas las series del día
            verificarYCompletarDia(serie.getEjercicioAsignado());

            response.put("success", true);
            response.put("message", "Registro guardado correctamente");
            response.put("registro", registroGuardado);

            log.info("Registro de serie guardado: ID={}, Serie={}, Usuario={}",
                    registroGuardado.getIdRegistroSerie(),
                    registroSerieDTO.getIdSerie(),
                    userDetails.getUsername());

        } catch (Exception ex) {
            log.error("Error al guardar registro de serie", ex);
            response.put("success", false);
            response.put("message", "Error al guardar el registro: " + ex.getMessage());
        }
        return response;
    }

    /**
     * Verifica si todas las series de todos los ejercicios del día han sido completadas.
     * Si es así, marca el día como completado automáticamente.
     */
    private void verificarYCompletarDia(EjercicioAsignado ejercicioAsignado) {
        try {
            if (ejercicioAsignado == null || ejercicioAsignado.getDia() == null) {
                return;
            }

            var dia = ejercicioAsignado.getDia();

            // Obtener todos los ejercicios asignados del día
            var ejerciciosDelDia = ejercicioAsignadoRepository.findByDia(dia);
            if (ejerciciosDelDia == null || ejerciciosDelDia.isEmpty()) {
                return;
            }

            boolean todasLasSeriesCompletadas = true;

            // Verificar cada ejercicio del día
            for (EjercicioAsignado eja : ejerciciosDelDia) {
                // Obtener todas las series del ejercicio
                var seriesDelEjercicio = serieRepository.findByEjercicioAsignado(eja);
                if (seriesDelEjercicio == null || seriesDelEjercicio.isEmpty()) {
                    continue;
                }

                // Verificar si todas las series tienen registro
                for (Serie s : seriesDelEjercicio) {
                    var registros = registroSerieRepository.findBySerie_Id(s.getId());
                    if (registros == null || registros.isEmpty()) {
                        todasLasSeriesCompletadas = false;
                        break;
                    }
                }

                if (!todasLasSeriesCompletadas) {
                    break;
                }
            }

            // Si todas las series están completadas, marcar el día como completado
            if (todasLasSeriesCompletadas && (dia.getEstado() == null || !dia.getEstado())) {
                diaService.toggleChecked(dia.getId());
                log.info("Día {} marcado automáticamente como completado", dia.getId());
            }

        } catch (Exception ex) {
            log.error("Error al verificar completitud del día", ex);
            // No lanzar excepción para no afectar el guardado del registro
        }
    }
}
