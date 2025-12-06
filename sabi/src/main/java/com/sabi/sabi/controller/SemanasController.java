package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.SemanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

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

    private boolean hasRole(UserDetails userDetails, String role) {
        if (userDetails == null) return false;
        return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role) || a.getAuthority().equals(role));
    }

    @GetMapping("/semanas/detallar/{idRutina}")
    public String detallarSemanas(@PathVariable Long idRutina,
                                  @RequestParam(value = "readonly", required = false) Boolean readonly,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
        List<?> semanas = semanaService.getSemanasRutina(idRutina);
        boolean esEntrenador = hasRole(userDetails, "ENTRENADOR");
        // Cliente ya no va en modo readonly por defecto: sólo modo readonly cuando el ENTRENADOR consulta con flag
        boolean readonlyEffective = esEntrenador && Boolean.TRUE.equals(readonly);

        // Calcular porcentaje de progreso si estamos en modo readonly (entrenador viendo progreso de cliente)
        if (readonlyEffective && rutinaDTO != null && rutinaDTO.getIdCliente() != null) {
            long porcentajeCompletado = diaService.calcularProgresoRutina(rutinaDTO.getIdCliente());
            model.addAttribute("porcentajeCompletado", porcentajeCompletado);

            // Agregar progreso detallado por semana
            List<com.sabi.sabi.dto.ProgresoSemanaDTO> progresosPorSemana = diaService.calcularProgresoPorSemana(rutinaDTO.getIdCliente());
            model.addAttribute("progresosPorSemana", progresosPorSemana);
        }

        model.addAttribute("semanas", semanas);
        model.addAttribute("totalSemanas", semanas.size());
        model.addAttribute("rutina", rutinaDTO);
        model.addAttribute("readonly", readonlyEffective);
        return "semanas/lista";
    }

    @GetMapping("/semanas/crear/{idRutina}")
    public String crearSemanaView(@PathVariable Long idRutina,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        if (hasRole(userDetails, "CLIENTE")) { // cliente no crea semanas
            return "redirect:/semanas/detallar/" + idRutina; // sin readonly
        }
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
    public String editarSemanaView(@PathVariable Long idSemana,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model, RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) { // cliente no edita semanas
            SemanaDTO semanaDTO = semanaService.getSemanaById(idSemana);
            if (semanaDTO != null) {
                return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina();
            }
            return "redirect:/rutinas";
        }
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
    public String guardarSemana(@ModelAttribute SemanaDTO semanaDTO,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina();
        }
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
    public String eliminarSemana(@PathVariable Long idSemana,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) { // cliente no elimina semanas
            SemanaDTO semanaDTO = null;
            try { semanaDTO = semanaService.getSemanaById(idSemana); } catch (RuntimeException ignored) {}
            return "redirect:/semanas/detallar/" + (semanaDTO != null ? semanaDTO.getIdRutina() : "");
        }
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

    @GetMapping("/semanas/check/{idSemana}")
    public String checkSemana(@PathVariable Long idSemana,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        // Ahora sólo CLIENTE puede marcar completada / pendiente.
        if (!hasRole(userDetails, "CLIENTE")) {
            SemanaDTO semanaDTO;
            try { semanaDTO = semanaService.getSemanaById(idSemana); } catch (RuntimeException ex) { return "redirect:/rutinas"; }
            return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina() + "?readonly=true"; // entrenador visualizando
        }
        SemanaDTO semanaDTO;
        try {
            semanaDTO = semanaService.getSemanaById(idSemana);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La semana no existe.");
            return "redirect:/rutinas";
        }
        Long idRutina = semanaDTO.getIdRutina();
        semanaService.alterCheck(idSemana);
        redirectAttributes.addFlashAttribute("success", "Estado de la semana cambiado correctamente.");
        return "redirect:/semanas/detallar/" + idRutina;
    }

    @GetMapping("/semanas/progreso/{idRutina}")
    public String verProgresoCliente(@PathVariable Long idRutina,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model, RedirectAttributes redirectAttributes) {
        boolean esEntrenador = hasRole(userDetails, "ENTRENADOR");
        if (!esEntrenador) {
            return "redirect:/semanas/detallar/" + idRutina;
        }

        try {
            RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
            if (rutinaDTO == null) {
                redirectAttributes.addFlashAttribute("error", "Rutina no encontrada");
                return "redirect:/rutinas";
            }

            List<?> semanas = semanaService.getSemanasRutina(idRutina);
            System.out.println("DEBUG: Total semanas encontradas: " + semanas.size());

            // Calcular progreso general
            if (rutinaDTO.getIdCliente() != null) {
                long porcentajeCompletado = diaService.calcularProgresoRutina(rutinaDTO.getIdCliente());
                model.addAttribute("porcentajeCompletado", porcentajeCompletado);

                List<com.sabi.sabi.dto.ProgresoSemanaDTO> progresosPorSemana = diaService.calcularProgresoPorSemana(rutinaDTO.getIdCliente());
                model.addAttribute("progresosPorSemana", progresosPorSemana);
            }

            // Cargar estructura completa: semanas -> días -> ejercicios -> series -> registros
            java.util.Map<Long, java.util.List<?>> diasPorSemana = new java.util.HashMap<>();
            java.util.Map<Long, java.util.List<?>> ejerciciosPorDia = new java.util.HashMap<>();
            java.util.Map<Long, java.util.List<?>> seriesPorEjercicio = new java.util.HashMap<>();
            java.util.Map<Long, com.sabi.sabi.entity.RegistroSerie> registrosPorSerie = new java.util.HashMap<>();

            for (Object semanaObj : semanas) {
                if (semanaObj instanceof com.sabi.sabi.entity.Semana) {
                    com.sabi.sabi.entity.Semana semana = (com.sabi.sabi.entity.Semana) semanaObj;
                    Long idSemana = semana.getId();
                    System.out.println("DEBUG: Procesando semana ID: " + idSemana + ", Numero: " + semana.getNumeroSemana());

                    if (idSemana != null) {
                        java.util.List<?> dias = diaService.getDiasSemana(idSemana);
                        System.out.println("DEBUG: Días encontrados para semana " + idSemana + ": " + dias.size());

                        if (!dias.isEmpty()) {
                            diasPorSemana.put(idSemana, dias);
                            System.out.println("DEBUG: Agregado al mapa diasPorSemana con key: " + idSemana);
                        }

                        for (Object diaObj : dias) {
                            if (diaObj instanceof com.sabi.sabi.entity.Dia) {
                                com.sabi.sabi.entity.Dia dia = (com.sabi.sabi.entity.Dia) diaObj;
                                Long idDia = dia.getId();
                                System.out.println("DEBUG: Procesando dia ID: " + idDia + ", Numero: " + dia.getNumeroDia());

                                if (idDia != null) {
                                    java.util.List<?> ejercicios = ejercicioAsignadoService.getEjesDia(idDia);
                                    System.out.println("DEBUG: Ejercicios encontrados para dia " + idDia + ": " + ejercicios.size());

                                    // DEBUG: Mostrar estado de cada ejercicio
                                    for (Object ejeObj : ejercicios) {
                                        if (ejeObj instanceof com.sabi.sabi.dto.EjercicioAsignadoDTO) {
                                            com.sabi.sabi.dto.EjercicioAsignadoDTO ejeDebug = (com.sabi.sabi.dto.EjercicioAsignadoDTO) ejeObj;
                                            System.out.println("  - Ejercicio ID: " + ejeDebug.getIdEjercicioAsignado() +
                                                             ", Nombre: " + ejeDebug.getNombreEjercicio() +
                                                             ", Estado: " + ejeDebug.getEstado());
                                        }
                                    }

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

                                                    // Cargar registros de series
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

            System.out.println("DEBUG: Total entradas en diasPorSemana: " + diasPorSemana.size());
            System.out.println("DEBUG: Keys en diasPorSemana: " + diasPorSemana.keySet());

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
