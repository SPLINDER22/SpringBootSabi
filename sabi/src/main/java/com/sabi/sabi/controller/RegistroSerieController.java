package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RegistroSerieDTO;
import com.sabi.sabi.dto.SerieViewDTO;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.repository.EjercicioAsignadoRepository;
import com.sabi.sabi.repository.RegistroSerieRepository;
import com.sabi.sabi.repository.SerieRepository;
import com.sabi.sabi.service.RegistroSerieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class RegistroSerieController {

    private static final Logger log = LoggerFactory.getLogger(RegistroSerieController.class);

    @Autowired
    private EjercicioAsignadoRepository ejercicioAsignadoRepository;
    @Autowired
    private SerieRepository serieRepository;
    @Autowired
    private RegistroSerieRepository registroSerieRepository;
    @Autowired
    private RegistroSerieService registroSerieService;

    @GetMapping("/registros-series/raw/{idEjercicioAsignado}")
    @ResponseBody
    public ResponseEntity<?> rawSeries(@PathVariable Long idEjercicioAsignado) {
        Map<String,Object> resp = new HashMap<>();
        try {
            EjercicioAsignado asignado = ejercicioAsignadoRepository.findById(idEjercicioAsignado).orElse(null);
            if (asignado == null) {
                resp.put("error","Ejercicio asignado no encontrado");
                return ResponseEntity.badRequest().body(resp);
            }
            List<Serie> series = serieRepository.getSerieEje(idEjercicioAsignado);
            if (series == null || series.isEmpty()) {
                series = serieRepository.findByEjercicioAsignado(asignado);
            }
            List<SerieViewDTO> dtos = series.stream().map(s -> new SerieViewDTO(
                    s.getId(), s.getOrden(), s.getRepeticiones(), s.getPeso(), s.getDescanso(),
                    s.getIntensidad()!=null? s.getIntensidad().name(): null
            )).toList();
            resp.put("series", dtos);
            resp.put("count", dtos.size());
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            resp.put("error", ex.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    @GetMapping("/registros-series/{idEjercicioAsignado}")
    public String verRegistroSeries(@PathVariable Long idEjercicioAsignado,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        long t0 = System.currentTimeMillis();
        log.debug("[RegistroSerie] Iniciando vista registros-series para ejercicioAsignado={}", idEjercicioAsignado);
        if (userDetails == null) {
            log.warn("[RegistroSerie] Usuario no autenticado, redirigiendo a login");
            return "redirect:/auth/login";
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
                    registroSerieRepository.findBySerie_IdIn(ids).forEach(registro -> {
                        RegistroSerieDTO dto = new RegistroSerieDTO();
                        dto.setIdRegistroSerie(registro.getId());
                        dto.setIdSerie(registro.getSerie().getId());
                        dto.setPesoReal(registro.getPesoReal());
                        dto.setRepeticionesReales(registro.getRepeticionesReales());
                        dto.setDescansoReal(registro.getDescansoReal());
                        dto.setComentariosCliente(registro.getComentariosCliente());
                        dto.setFechaEjecucion(registro.getFechaEjecucion());
                        registrosMap.put(registro.getSerie().getId(), dto);
                    });
                }
            }
            for (Serie s : series) {
                registrosMap.computeIfAbsent(s.getId(), k -> {
                    RegistroSerieDTO dto = new RegistroSerieDTO();
                    dto.setIdSerie(s.getId());
                    return dto;
                });
            }
            Long idDia = asignado.getDia() != null ? asignado.getDia().getId() : null;
            model.addAttribute("series", series);
            model.addAttribute("registrosMap", registrosMap);
            model.addAttribute("ejercicioAsignado", asignado);
            model.addAttribute("idDia", idDia);
        } catch (Exception ex) {
            log.error("[RegistroSerie] Error construyendo modelo", ex);
            model.addAttribute("error", "No se pudo cargar la vista de registros: " + ex.getMessage());
        } finally {
            log.debug("[RegistroSerie] Tiempo total ms: {}", (System.currentTimeMillis() - t0));
        }
        return "registros-series/formulario";
    }

    @PostMapping("/registros-series/guardar")
    public String guardarRegistro(@ModelAttribute RegistroSerieDTO registroSerieDTO,
                                   RedirectAttributes ra) {
        try {
            if (registroSerieDTO.getIdSerie() == null) {
                ra.addFlashAttribute("error", "Serie no especificada");
                return "redirect:/ejercicios";
            }
            Serie serie = serieRepository.findById(registroSerieDTO.getIdSerie())
                    .orElseThrow(() -> new RuntimeException("Serie no encontrada"));

            if (registroSerieDTO.getFechaEjecucion() == null) {
                registroSerieDTO.setFechaEjecucion(LocalDateTime.now());
            }
            registroSerieService.saveOrUpdateRegistroSerie(registroSerieDTO);
            ra.addFlashAttribute("success", "Registro guardado");
            Long idEje = serie.getEjercicioAsignado().getIdEjercicioAsignado();
            return "redirect:/registros-series/" + idEje;
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "No se pudo guardar: " + ex.getMessage());
            if (registroSerieDTO.getIdSerie() != null) {
                try {
                    Serie serie = serieRepository.findById(registroSerieDTO.getIdSerie()).orElse(null);
                    if (serie != null) {
                        return "redirect:/registros-series/" + serie.getEjercicioAsignado().getIdEjercicioAsignado();
                    }
                } catch (Exception ignore) {}
            }
            return "redirect:/ejercicios";
        }
    }

    @PostMapping("/registros-series/limpiar")
    public String limpiarRegistro(@RequestParam Long idSerie,
                                   RedirectAttributes ra) {
        Serie serie = serieRepository.findById(idSerie)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
        registroSerieRepository.findFirstBySerie_Id(idSerie).ifPresent(registro -> registroSerieRepository.deleteById(registro.getId()));
        ra.addFlashAttribute("success", "Registro limpiado");
        return "redirect:/registros-series/" + serie.getEjercicioAsignado().getIdEjercicioAsignado();
    }

    @GetMapping("/registros-series/simple/{idEjercicioAsignado}")
    public String verRegistroSeriesSimple(@PathVariable Long idEjercicioAsignado, Model model) {
        List<Serie> series = serieRepository.getSerieEje(idEjercicioAsignado);
        if (series == null || series.isEmpty()) {
            EjercicioAsignado asignado = ejercicioAsignadoRepository.findById(idEjercicioAsignado).orElse(null);
            if (asignado != null) {
                series = serieRepository.findByEjercicioAsignado(asignado);
            } else {
                series = Collections.emptyList();
            }
        }
        model.addAttribute("series", series);
        model.addAttribute("idEje", idEjercicioAsignado);
        model.addAttribute("count", series.size());
        return "registros-series/simple";
    }
}
