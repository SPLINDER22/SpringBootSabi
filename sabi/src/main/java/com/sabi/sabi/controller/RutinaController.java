package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.dto.ComentarioDTO; // nuevo
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.SemanaService;
import com.sabi.sabi.service.UsuarioService;
import com.sabi.sabi.service.ComentarioService; // nuevo
import com.sabi.sabi.service.SuscripcionService; // nuevo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class RutinaController {
    @Autowired
    private RutinaService rutinaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private SemanaService semanaService;
    @Autowired
    private ComentarioService comentarioService; // nuevo
    @Autowired
    private SuscripcionService suscripcionService; // nuevo
    @Autowired
    private com.sabi.sabi.service.ClienteService clienteService; // nuevo
    @Autowired
    private com.sabi.sabi.service.DiaService diaService;
    @Autowired
    private com.sabi.sabi.service.EjercicioAsignadoService ejercicioAsignadoService;
    @Autowired
    private com.sabi.sabi.service.SerieService serieService;
    @Autowired
    private com.sabi.sabi.repository.RegistroSerieRepository registroSerieRepository;

    @GetMapping("/rutina/cliente")
    public String verRutinaCliente(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario;
        try {
            usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        } catch (RuntimeException ex) {
            return "redirect:/auth/login";
        }
        if (usuario.getRol() == null || !usuario.getRol().name().equalsIgnoreCase("CLIENTE")) {
            return "redirect:/rutinas";
        }
        // Obtener única rutina activa
        RutinaDTO activa = rutinaService.getRutinaActivaCliente(usuario.getId());
        if (activa != null && activa.getIdRutina() != null) {
            // Redirigir a la nueva vista unificada para clientes
            return "redirect:/rutina/cliente/vista-unificada/" + activa.getIdRutina();
        }
        // No tiene rutina activa: mostrar globales
        model.addAttribute("rutinas", rutinaService.getRutinasGlobales());
        model.addAttribute("mostrandoGlobales", true);
        model.addAttribute("isCliente", true);
        model.addAttribute("idUsuarioActual", -1); // no coincide con idEntrenador de ninguna global
        return "rutinas/lista";
    }

    @PostMapping("/rutinas/adoptar/{idRutina}")
    public String adoptarRutina(@PathVariable Long idRutina,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        // Verificar si ya tiene una activa (defensivo; el servicio ya finaliza la previa si existiera, pero preservamos la regla si quieres bloquear)
        RutinaDTO activaAntes = rutinaService.getRutinaActivaCliente(usuario.getId());
        if (activaAntes != null && activaAntes.getIdRutina() != null) {
            redirectAttributes.addFlashAttribute("error", "Ya tienes una rutina activa. Finalízala para adoptar otra.");
            return "redirect:/rutina/cliente";
        }
        // Adoptar (clona y activa la nueva)
        rutinaService.adoptarRutina(idRutina, usuario.getId());
        // Obtener ahora la nueva activa (la clonada)
        RutinaDTO activaNueva = rutinaService.getRutinaActivaCliente(usuario.getId());
        if (activaNueva == null || activaNueva.getIdRutina() == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo determinar la rutina adoptada. Intenta nuevamente.");
            return "redirect:/rutina/cliente";
        }
        redirectAttributes.addFlashAttribute("success", "Rutina adoptada correctamente. Ahora puedes gestionar sus semanas.");
        return "redirect:/semanas/detallar/" + activaNueva.getIdRutina();
    }

    @PostMapping("/rutinas/finalizar/{idRutina}")
    public String finalizarRutina(@PathVariable Long idRutina,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        rutinaService.finalizarRutinaCliente(idRutina, usuario.getId());
        redirectAttributes.addFlashAttribute("success", "Rutina finalizada. Ya puedes elegir otra.");
        return "redirect:/rutina/cliente";
    }

    @PostMapping("/rutinas/finalizar-comentario/{idRutina}")
    public String finalizarRutinaConComentario(@PathVariable Long idRutina,
                                               @AuthenticationPrincipal UserDetails userDetails,
                                               @RequestParam(name = "texto", required = false) String texto,
                                               @RequestParam(name = "calificacion", required = false) Double calificacion,
                                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        RutinaDTO rutina = rutinaService.getRutinaById(idRutina);
        if (rutina == null || rutina.getIdCliente() == null || !rutina.getIdCliente().equals(usuario.getId())) {
            redirectAttributes.addFlashAttribute("error", "Rutina no válida para finalizar.");
            return "redirect:/rutina/cliente";
        }
        try {
            rutinaService.finalizarRutinaCliente(idRutina, usuario.getId());
            boolean hayTexto = texto != null && !texto.isBlank();
            boolean hayCal = calificacion != null;
            if (hayCal && (calificacion < 0 || calificacion > 5)) {
                redirectAttributes.addFlashAttribute("error", "La calificación debe estar entre 0 y 5.");
                return "redirect:/rutina/cliente";
            }
            if (hayTexto || hayCal) {
                if (rutina.getIdEntrenador() != null) {
                    ComentarioDTO dto = new ComentarioDTO();
                    dto.setTexto(hayTexto ? texto.trim() : null);
                    dto.setCalificacion(hayCal ? calificacion : null);
                    dto.setIdCliente(usuario.getId());
                    dto.setIdEntrenador(rutina.getIdEntrenador());
                    dto.setIdRutina(idRutina);
                    try {
                        comentarioService.crearComentario(dto);
                        if (hayTexto && hayCal) {
                            redirectAttributes.addFlashAttribute("success", "Rutina finalizada, comentario y calificación registrados.");
                        } else if (hayTexto) {
                            redirectAttributes.addFlashAttribute("success", "Rutina finalizada y comentario registrado.");
                        } else {
                            redirectAttributes.addFlashAttribute("success", "Rutina finalizada y calificación registrada.");
                        }
                    } catch (Exception ex) {
                        redirectAttributes.addFlashAttribute("warning", "Rutina finalizada, pero el comentario/calificación no se guardó: " + ex.getMessage());
                    }
                } else {
                    redirectAttributes.addFlashAttribute("warning", "Rutina finalizada. No se encontró entrenador para asociar el comentario/calificación.");
                }
            } else {
                redirectAttributes.addFlashAttribute("success", "Rutina finalizada correctamente.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "No se pudo finalizar la rutina: " + ex.getMessage());
        }
        return "redirect:/rutina/cliente";
    }

    @GetMapping("/rutinas")
    public String listarRutinas(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(value = "asignar", required = false) Boolean asignar,
                                @RequestParam(value = "idCliente", required = false) Long idCliente,
                                Model model) {
        Usuario usuario;
        try {
            usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        } catch (RuntimeException ex) {
            return "redirect:/auth/login";
        }
        // Si no es entrenador, redirigir flujo cliente existente
        if (usuario.getRol() == null || !usuario.getRol().name().equalsIgnoreCase("ENTRENADOR")) {
            return "redirect:/rutina/cliente";
        }

        // Modo asignar rutina a un cliente (desde suscripción aceptada)
        boolean modoAsignar = Boolean.TRUE.equals(asignar) && idCliente != null;

        if (modoAsignar) {
            // Obtener la suscripción activa del cliente para filtrar por duración en semanas
            var suscripcionActiva = suscripcionService.getSuscripcionActualByClienteId(idCliente);
            if (suscripcionActiva != null && suscripcionActiva.getDuracionSemanas() != null) {
                // Filtrar rutinas que coincidan con la duración de la suscripción
                Long duracionSemanas = suscripcionActiva.getDuracionSemanas().longValue();
                model.addAttribute("rutinas", rutinaService.getRutinasPorNumeroSemanasYEntrenador(duracionSemanas, usuario.getId()));
                model.addAttribute("duracionSemanasFiltro", duracionSemanas);
            } else {
                // Si no hay suscripción activa o no tiene duración definida, mostrar todas las rutinas del entrenador
                model.addAttribute("rutinas", rutinaService.getRutinasPorUsuario(usuario.getId()));
                model.addAttribute("duracionSemanasFiltro", null);
            }
        } else {
            // Modo normal: mostrar todas las rutinas del entrenador
            model.addAttribute("rutinas", rutinaService.getRutinasPorUsuario(usuario.getId()));
        }

        model.addAttribute("idUsuarioActual", usuario.getId());
        model.addAttribute("isCliente", false);
        model.addAttribute("modoAsignar", modoAsignar);
        model.addAttribute("idClienteAsignacion", idCliente);
        return "rutinas/lista";
    }

    @GetMapping("/rutinas/rutinas-asignables/{idSuscripcion}")
    public String listarRutinasAsignables(@PathVariable Long idSuscripcion,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           Model model) {
        //model.addAttribute("rutinas", rutinaService.);
        return "rutinas/rutinas-asignables";
    }

    @GetMapping("/rutinas/seleccionar-cliente/{idRutina}")
    public String seleccionarClienteParaRutina(@PathVariable Long idRutina,
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                Model model,
                                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario entrenador = usuarioService.obtenerPorEmail(userDetails.getUsername());
        if (entrenador.getRol() == null || !entrenador.getRol().name().equalsIgnoreCase("ENTRENADOR")) {
            redirectAttributes.addFlashAttribute("error", "No autorizado.");
            return "redirect:/rutinas";
        }

        // Obtener la rutina para conocer su número de semanas
        RutinaDTO rutina = rutinaService.getRutinaById(idRutina);
        if (rutina == null) {
            redirectAttributes.addFlashAttribute("error", "Rutina no encontrada.");
            return "redirect:/rutinas";
        }

        // Obtener suscripciones con la misma duración en semanas (solo ACEPTADAS)
        List<com.sabi.sabi.dto.SuscripcionDTO> suscripcionesFiltradas = List.of();
        if (rutina.getNumeroSemanas() != null) {
            suscripcionesFiltradas = suscripcionService.getSuscripcionesByDuracionSemanas(rutina.getNumeroSemanas().intValue());
        }

        // Crear un mapa de ID de cliente -> nombre de cliente
        java.util.Map<Long, String> nombresClientes = suscripcionesFiltradas.stream()
                .filter(s -> s.getIdCliente() != null)
                .collect(java.util.stream.Collectors.toMap(
                        com.sabi.sabi.dto.SuscripcionDTO::getIdCliente,
                        s -> clienteService.getClienteById(s.getIdCliente()).getNombre(),
                        (existing, replacement) -> existing // En caso de duplicados, mantener el primero
                ));

        model.addAttribute("rutina", rutina);
        model.addAttribute("suscripciones", suscripcionesFiltradas);
        model.addAttribute("nombresClientes", nombresClientes);
        model.addAttribute("numeroSemanasFiltro", rutina.getNumeroSemanas());
        return "rutinas/seleccionar-cliente";
    }

    @PostMapping("/rutinas/asignar/{idRutina}")
    public String asignarRutinaACliente(@PathVariable Long idRutina,
                                        @RequestParam(name = "idCliente") Long idCliente,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario entrenador = usuarioService.obtenerPorEmail(userDetails.getUsername());
        if (entrenador.getRol() == null || !entrenador.getRol().name().equalsIgnoreCase("ENTRENADOR")) {
            redirectAttributes.addFlashAttribute("error", "No autorizado.");
            return "redirect:/rutinas";
        }
        if (idCliente == null) {
            redirectAttributes.addFlashAttribute("error", "Cliente no especificado.");
            return "redirect:/rutinas";
        }
        try {
            rutinaService.adoptarRutina(idRutina, idCliente);
            redirectAttributes.addFlashAttribute("success", "Rutina asignada y clonada correctamente para el cliente.");
            return "redirect:/entrenador/suscripciones";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "No se pudo asignar la rutina: " + ex.getMessage());
            return "redirect:/rutinas?asignar=true&idCliente=" + idCliente;
        }
    }

    @GetMapping("/rutinas/nueva")
    public String crearRutinaView(Model model) {
        RutinaDTO rutinaDTO = new RutinaDTO();
        model.addAttribute("rutina", rutinaDTO);
        return "rutinas/formulario";
    }

    @GetMapping("/rutinas/editar/{id}")
    public String editarRutinaView(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(id);
        if (rutinaDTO == null) {
            return "redirect:/rutinas?error=notfound";
        }
        model.addAttribute("rutina", rutinaDTO);
        return "rutinas/formulario";
    }

    @PostMapping("/rutinas/guardar")
    public String guardarRutina(@ModelAttribute("rutina") RutinaDTO rutinaDTO,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        if (rutinaDTO.getIdRutina() == null) {
            rutinaDTO.setIdEntrenador(usuario.getId());
        }

        RutinaDTO rutinaGuardada;
        if (rutinaDTO.getIdRutina() == null) {
            rutinaGuardada = rutinaService.createRutina(rutinaDTO);
            redirectAttributes.addFlashAttribute("success", "Rutina creada correctamente. Ahora define sus semanas.");
            return "redirect:/semanas/detallar/" + rutinaGuardada.getIdRutina();
        } else {
            rutinaGuardada = rutinaService.updateRutina(rutinaDTO.getIdRutina(), rutinaDTO);
            redirectAttributes.addFlashAttribute("success", "Rutina actualizada correctamente.");
            return "redirect:/rutinas";
        }
    }

    @PostMapping("/rutinas/desactivate/{id}")
    public String desactivarRutina(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(id);
        if (rutinaDTO == null) {
            redirectAttributes.addFlashAttribute("error", "La rutina no existe.");
            return "redirect:/rutinas";
        }
        rutinaService.desactivateRutina(id);
        redirectAttributes.addFlashAttribute("success", "Estado de la rutina actualizado.");
        return "redirect:/rutinas";
    }

    @GetMapping("/rutina/cliente/vista-unificada/{idRutina}")
    public String vistaUnificadaCliente(@PathVariable Long idRutina,
                                         @RequestParam(required = false) Long semanaId,
                                         @RequestParam(required = false) Long diaId,
                                         @RequestParam(required = false) Long ejercicioId,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        Usuario usuario;
        try {
            usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        } catch (RuntimeException ex) {
            return "redirect:/auth/login";
        }

        // Solo clientes pueden acceder a esta vista
        if (usuario.getRol() == null || !usuario.getRol().name().equalsIgnoreCase("CLIENTE")) {
            redirectAttributes.addFlashAttribute("error", "Esta vista es solo para clientes.");
            return "redirect:/rutinas";
        }

        // Obtener la rutina
        RutinaDTO rutina = rutinaService.getRutinaById(idRutina);
        System.out.println("DEBUG - Rutina encontrada: " + (rutina != null ? rutina.getNombre() : "NULL"));
        if (rutina == null) {
            redirectAttributes.addFlashAttribute("error", "Rutina no encontrada.");
            return "redirect:/rutina/cliente";
        }

        // Verificar que la rutina pertenece al cliente
        if (rutina.getIdCliente() == null || !rutina.getIdCliente().equals(usuario.getId())) {
            redirectAttributes.addFlashAttribute("error", "No tienes acceso a esta rutina.");
            return "redirect:/rutina/cliente";
        }

        // Obtener toda la estructura: semanas -> días -> ejercicios -> series
        var semanas = semanaService.getSemanasRutina(idRutina);
        System.out.println("DEBUG - Número de semanas encontradas: " + (semanas != null ? semanas.size() : "NULL"));

        // Para cada semana, obtener sus días
        var semanasConDias = semanas.stream().map(semana -> {
            Long idSemana = semana.getId();
            System.out.println("DEBUG - Procesando semana ID: " + idSemana + ", Número: " + semana.getNumeroSemana());
            var dias = diaService.getDiasSemana(idSemana);
            System.out.println("DEBUG - Días encontrados para semana " + idSemana + ": " + (dias != null ? dias.size() : "NULL"));

            // Para cada día, obtener sus ejercicios
            var diasConEjercicios = dias.stream().map(dia -> {
                Long idDia = dia.getId();
                System.out.println("DEBUG - Procesando día ID: " + idDia + ", Número: " + dia.getNumeroDia());
                var ejercicios = ejercicioAsignadoService.getEjesDia(idDia);
                System.out.println("DEBUG - Ejercicios encontrados para día " + idDia + ": " + (ejercicios != null ? ejercicios.size() : "NULL"));

                // Para cada ejercicio, obtener sus series
                var ejerciciosConSeries = ejercicios.stream().map(eje -> {
                    Long idEje = eje.getIdEjercicioAsignado();
                    System.out.println("DEBUG - Procesando ejercicio ID: " + idEje);
                    var series = serieService.getSerieEje(idEje);
                    System.out.println("DEBUG - Series encontradas para ejercicio " + idEje + ": " + (series != null ? series.size() : "NULL"));

                    // Cargar los registros existentes para cada serie
                    var seriesConRegistros = series.stream().map(serie -> {
                        java.util.Map<String, Object> serieMap = new java.util.HashMap<>();
                        serieMap.put("serie", serie);

                        // Buscar registro existente para esta serie
                        var registroOpt = registroSerieRepository.findFirstBySerie_Id(serie.getId());
                        if (registroOpt.isPresent()) {
                            var registro = registroOpt.get();
                            // Crear un mapa con los datos del registro
                            java.util.Map<String, Object> registroMap = new java.util.HashMap<>();
                            registroMap.put("repeticionesReales", registro.getRepeticionesReales());
                            registroMap.put("pesoReal", registro.getPesoReal());
                            registroMap.put("descansoReal", registro.getDescansoReal());
                            registroMap.put("intensidadReal", registro.getIntensidadReal() != null ? registro.getIntensidadReal().name() : null);
                            registroMap.put("comentariosCliente", registro.getComentariosCliente());
                            serieMap.put("registro", registroMap);
                            System.out.println("DEBUG - Registro encontrado para serie " + serie.getId() + ": " +
                                             registro.getRepeticionesReales() + " reps, " + registro.getPesoReal() + " kg");
                        } else {
                            serieMap.put("registro", null);
                            System.out.println("DEBUG - No hay registro para serie " + serie.getId());
                        }

                        return serieMap;
                    }).toList();

                    // Verificar si todas las series del ejercicio tienen registro
                    boolean todasSeriesCompletadas = seriesConRegistros.stream()
                        .allMatch(sm -> sm.get("registro") != null);

                    java.util.Map<String, Object> ejeMap = new java.util.HashMap<>();
                    ejeMap.put("ejercicio", eje);
                    ejeMap.put("series", seriesConRegistros);
                    ejeMap.put("todasSeriesCompletadas", todasSeriesCompletadas);
                    return ejeMap;
                }).toList();

                // Verificar si todos los ejercicios del día están completados
                boolean todasSeriesCompletadasDia = ejerciciosConSeries.stream()
                    .allMatch(em -> {
                        Boolean completado = (Boolean) em.get("todasSeriesCompletadas");
                        return completado != null && completado;
                    });

                java.util.Map<String, Object> diaMap = new java.util.HashMap<>();
                diaMap.put("dia", dia);
                diaMap.put("ejercicios", ejerciciosConSeries);
                diaMap.put("todasSeriesCompletadas", todasSeriesCompletadasDia);
                return diaMap;
            }).toList();

            java.util.Map<String, Object> semanaMap = new java.util.HashMap<>();
            semanaMap.put("semana", semana);
            semanaMap.put("dias", diasConEjercicios);
            return semanaMap;
        }).toList();

        System.out.println("DEBUG - Estructura completa construida. Total semanas con datos: " + semanasConDias.size());
        model.addAttribute("rutina", rutina);
        model.addAttribute("semanasCompletas", semanasConDias);

        // Pasar parámetros de navegación automática al frontend
        model.addAttribute("autoSemanaId", semanaId);
        model.addAttribute("autoDiaId", diaId);
        model.addAttribute("autoEjercicioId", ejercicioId);

        return "rutinas/vista-unificada-cliente";
    }
}
