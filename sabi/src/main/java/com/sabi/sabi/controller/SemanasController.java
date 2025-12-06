package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.SemanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SemanasController {
    @Autowired
    private SemanaService semanaService;
    @Autowired
    private RutinaService rutinaService;
    @Autowired
    private com.sabi.sabi.service.DiaService diaService;
    @Autowired
    private com.sabi.sabi.service.EjercicioAsignadoService ejercicioAsignadoService;
    @Autowired
    private com.sabi.sabi.repository.SerieRepository serieRepository;
    @Autowired
    private com.sabi.sabi.repository.RegistroSerieRepository registroSerieRepository;

    @GetMapping("/semanas/detallar/{idRutina}")
    public String detallarSemanas(@PathVariable Long idRutina, Model model) {
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
        List<?> semanas = semanaService.getSemanasRutina(idRutina);

        model.addAttribute("semanas", semanas);
        model.addAttribute("totalSemanas", semanas.size());
        model.addAttribute("rutina", rutinaDTO);
        return "semanas/lista";
    }

    @GetMapping("/semanas/crear/{idRutina}")
    public String crearSemanaView(@PathVariable Long idRutina, Model model) {
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
        var semanas = semanaService.getSemanasRutina(idRutina);
        int total = semanas != null ? semanas.size() : 0;
        SemanaDTO semanaDTO = new SemanaDTO();
        semanaDTO.setIdRutina(idRutina);
        semanaDTO.setNumeroSemana((long) (total + 1));

        model.addAttribute("rutina", rutinaDTO);
        model.addAttribute("semanas", semanas);
        model.addAttribute("totalSemanas", total);
        model.addAttribute("semana", semanaDTO);
        return "semanas/formulario";
    }

    @GetMapping("/semanas/editar/{idSemana}")
    public String editarSemanaView(@PathVariable Long idSemana, Model model, RedirectAttributes redirectAttributes) {
        SemanaDTO semanaDTO = semanaService.getSemanaById(idSemana);
        if (semanaDTO == null) {
            redirectAttributes.addFlashAttribute("error", "La semana no existe.");
            return "redirect:/rutinas";
        }

        RutinaDTO rutinaDTO = rutinaService.getRutinaById(semanaDTO.getIdRutina());
        var semanas = semanaService.getSemanasRutina(semanaDTO.getIdRutina());
        int total = semanas != null ? semanas.size() : 0;

        model.addAttribute("rutina", rutinaDTO);
        model.addAttribute("semanas", semanas);
        model.addAttribute("totalSemanas", total);
        model.addAttribute("semana", semanaDTO);
        return "semanas/formulario";
    }

    @PostMapping("/semanas/guardar")
    public String guardarSemana(@ModelAttribute SemanaDTO semanaDTO, RedirectAttributes redirectAttributes) {
        if (semanaDTO.getIdSemana() == null) {
            SemanaDTO creada = semanaService.createSemana(semanaDTO);
            redirectAttributes.addFlashAttribute("success", "Semana creada correctamente. Ahora agrega los días.");
            return "redirect:/dias/detallar/" + creada.getIdSemana();
        } else {
            semanaService.updateSemana(semanaDTO.getIdSemana(), semanaDTO);
            redirectAttributes.addFlashAttribute("success", "Semana actualizada correctamente.");
            return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina();
        }
    }

    @PostMapping("/semanas/eliminar/{idSemana}")
    public String eliminarSemana(@PathVariable Long idSemana, RedirectAttributes redirectAttributes) {
        SemanaDTO semanaDTO = null;
        try {
            semanaDTO = semanaService.getSemanaById(idSemana);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La semana no existe.");
            return "redirect:/rutinas";
        }
        Long idRutina = semanaDTO.getIdRutina();
        semanaService.deleteSemana(idSemana);
        redirectAttributes.addFlashAttribute("success", "Semana eliminada correctamente.");
        return "redirect:/semanas/detallar/" + idRutina;
    }

    @PostMapping("/semanas/duplicar/{idSemana}")
    public String duplicarSemana(@PathVariable Long idSemana, RedirectAttributes redirectAttributes) {
        try {
            SemanaDTO semanaOriginal = semanaService.getSemanaById(idSemana);
            semanaService.duplicarSemana(idSemana);
            redirectAttributes.addFlashAttribute("success", "Semana y todo su contenido duplicado correctamente.");
            return "redirect:/semanas/detallar/" + semanaOriginal.getIdRutina();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Error al duplicar la semana: " + ex.getMessage());
            return "redirect:/rutinas";
        }
    }

    @GetMapping("/semanas/progreso/{idRutina}")
    public String verProgresoCliente(@PathVariable Long idRutina,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model, RedirectAttributes redirectAttributes) {
        try {
            RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
            if (rutinaDTO == null) {
                redirectAttributes.addFlashAttribute("error", "Rutina no encontrada");
                return "redirect:/rutinas";
            }

            List<?> semanas = semanaService.getSemanasRutina(idRutina);

            if (rutinaDTO.getIdCliente() != null) {
                long porcentajeCompletado = diaService.calcularProgresoRutina(rutinaDTO.getIdCliente());
                model.addAttribute("porcentajeCompletado", porcentajeCompletado);

                List<com.sabi.sabi.dto.ProgresoSemanaDTO> progresosPorSemana = diaService.calcularProgresoPorSemana(rutinaDTO.getIdCliente());
                model.addAttribute("progresosPorSemana", progresosPorSemana);
            }

            java.util.Map<Long, java.util.List<?>> diasPorSemana = new java.util.HashMap<>();
            java.util.Map<Long, java.util.List<?>> ejerciciosPorDia = new java.util.HashMap<>();
            java.util.Map<Long, java.util.List<?>> seriesPorEjercicio = new java.util.HashMap<>();
            java.util.Map<Long, com.sabi.sabi.entity.RegistroSerie> registrosPorSerie = new java.util.HashMap<>();

            for (Object semanaObj : semanas) {
                if (semanaObj instanceof com.sabi.sabi.entity.Semana) {
                    com.sabi.sabi.entity.Semana semana = (com.sabi.sabi.entity.Semana) semanaObj;
                    Long idSemana = semana.getId();

                    if (idSemana != null) {
                        java.util.List<?> dias = diaService.getDiasSemana(idSemana);

                        if (!dias.isEmpty()) {
                            diasPorSemana.put(idSemana, dias);
                        }

                        for (Object diaObj : dias) {
                            if (diaObj instanceof com.sabi.sabi.entity.Dia) {
                                com.sabi.sabi.entity.Dia dia = (com.sabi.sabi.entity.Dia) diaObj;
                                Long idDia = dia.getId();

                                if (idDia != null) {
                                    java.util.List<?> ejercicios = ejercicioAsignadoService.getEjesDia(idDia);

                                    if (!ejercicios.isEmpty()) {
                                        ejerciciosPorDia.put(idDia, ejercicios);
                                    }

                                    for (Object ejeObj : ejercicios) {
                                        if (ejeObj instanceof com.sabi.sabi.dto.EjercicioAsignadoDTO) {
                                            com.sabi.sabi.dto.EjercicioAsignadoDTO eje = (com.sabi.sabi.dto.EjercicioAsignadoDTO) ejeObj;
                                            Long idEje = eje.getIdEjercicioAsignado();

                                            if (idEje != null) {
                                                java.util.List<com.sabi.sabi.entity.Serie> series = serieRepository.getSerieEje(idEje);
                                                if (series != null && !series.isEmpty()) {
                                                    seriesPorEjercicio.put(idEje, series);

                                                    java.util.List<Long> seriesIds = series.stream()
                                                        .map(com.sabi.sabi.entity.Serie::getId)
                                                        .filter(java.util.Objects::nonNull)
                                                        .collect(java.util.stream.Collectors.toList());

                                                    if (!seriesIds.isEmpty()) {
                                                        registroSerieRepository.findBySerie_IdIn(seriesIds).forEach(r -> {
                                                            if (r.getSerie() != null && r.getSerie().getId() != null) {
                                                                registrosPorSerie.put(r.getSerie().getId(), r);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            model.addAttribute("semanas", semanas);
            model.addAttribute("diasPorSemana", diasPorSemana);
            model.addAttribute("ejerciciosPorDia", ejerciciosPorDia);
            model.addAttribute("seriesPorEjercicio", seriesPorEjercicio);
            model.addAttribute("registrosPorSerie", registrosPorSerie);
            model.addAttribute("rutina", rutinaDTO);

            return "semanas/progreso";
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el progreso: " + ex.getMessage());
            return "redirect:/rutinas";
        }
    }
}
