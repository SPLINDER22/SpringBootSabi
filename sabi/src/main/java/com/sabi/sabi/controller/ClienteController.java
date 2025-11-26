package com.sabi.sabi.controller;

import java.util.List;

import com.sabi.sabi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.DiagnosticoDTO;
import com.sabi.sabi.dto.EntrenadorDTO;
import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.security.CustomUserDetails;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private DiagnosticoService diagnosticoService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private EntrenadorService entrenadorService;
    @Autowired
    private RutinaService rutinaService;
    @Autowired
    private DiaService diaService;
    @Autowired
    private ComentarioService comentarioService; // nuevo

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard(Model model,
                                   HttpServletResponse response) {
        // ⚠️ IMPORTANTE: Evitar caché del navegador para que siempre cargue datos frescos
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();

        // Obtener diagnostico actual
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        boolean tieneDiagnostico = diagnosticoActual != null;

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  🔄 CARGANDO DASHBOARD - CLIENTE ID: " + clienteId);
        System.out.println("╠════════════════════════════════════════════════════════╣");
        if (diagnosticoActual != null) {
            System.out.println("║  📋 Diagnóstico ID: " + diagnosticoActual.getIdDiagnostico());
            System.out.println("║  📅 Fecha: " + diagnosticoActual.getFecha());
            System.out.println("║  🎯 OBJETIVO: " + (diagnosticoActual.getObjetivo() != null ? "\"" + diagnosticoActual.getObjetivo() + "\"" : "SIN OBJETIVO"));
        } else {
            System.out.println("║  ⚠️ NO HAY DIAGNÓSTICO");
        }
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Obtener historial completo para comparativa
        List<DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);

        // DEBUG: Ver qué diagnósticos tenemos
        System.out.println("=== DEBUG DASHBOARD ===");
        System.out.println("Cliente ID: " + clienteId);
        System.out.println("Total diagnósticos: " + (historial != null ? historial.size() : 0));
        if (historial != null && !historial.isEmpty()) {
            System.out.println("Diagnósticos encontrados:");
            for (int i = 0; i < historial.size(); i++) {
                DiagnosticoDTO d = historial.get(i);
                System.out.println("  [" + i + "] Fecha: " + d.getFecha() + ", Peso: " + d.getPeso() + " kg");
            }
        }

        // Obtener diagnostico anterior (el segundo mas reciente)
        DiagnosticoDTO diagnosticoAnterior = null;
        if (historial != null && historial.size() >= 2) {
            // El historial viene ordenado por fecha DESC, entonces [0]=actual, [1]=anterior
            diagnosticoAnterior = historial.get(1);
            System.out.println("✅ COMPARATIVA ACTIVADA");
            System.out.println("  Diagnóstico actual: " + diagnosticoActual.getFecha() + " - " + diagnosticoActual.getPeso() + " kg");
            System.out.println("  Diagnóstico anterior: " + diagnosticoAnterior.getFecha() + " - " + diagnosticoAnterior.getPeso() + " kg");
        } else {
            System.out.println("❌ COMPARATIVA DESACTIVADA - Solo hay " + (historial != null ? historial.size() : 0) + " diagnóstico(s)");
        }
        System.out.println("======================");

        model.addAttribute("tieneDiagnostico", tieneDiagnostico);
        model.addAttribute("diagnosticoActual", diagnosticoActual);
        model.addAttribute("diagnosticoAnterior", diagnosticoAnterior);
        model.addAttribute("tieneComparativa", diagnosticoAnterior != null);
        model.addAttribute("usuario", userDetails.getUsuario());

         // Rutina activa
        RutinaDTO rutinaDTO = rutinaService.getRutinaActivaCliente(clienteId);
        if (rutinaDTO != null) {
            DiaDTO diaDTO = diaService.getDiaActual(clienteId);
            long porcentajeCompletado = diaService.calcularProgresoRutina(clienteId);

            model.addAttribute("tieneRutinaActiva", true);
            model.addAttribute("rutina", rutinaDTO);
            model.addAttribute("diaActual", diaDTO);
            model.addAttribute("porcentajeCompletado", porcentajeCompletado);
        } else {
            model.addAttribute("tieneRutinaActiva", false);
        }

        // Suscripción actual
        SuscripcionDTO suscripcionActual = suscripcionService.getSuscripcionActualByClienteId(clienteId);
        model.addAttribute("suscripcion", suscripcionActual);
        if (suscripcionActual != null && suscripcionActual.getIdEntrenador() != null) {
            var entrenador = entrenadorService.getEntrenadorById(suscripcionActual.getIdEntrenador());
            if (entrenador != null) {
                String nombreCompleto = entrenador.getNombre();
                if (entrenador.getApellido() != null && !entrenador.getApellido().isEmpty()) {
                    nombreCompleto += " " + entrenador.getApellido();
                }
                model.addAttribute("nombreEntrenador", nombreCompleto);
                model.addAttribute("fotoEntrenador", entrenador.getFotoPerfilUrl() != null ? entrenador.getFotoPerfilUrl() : "/img/fotoPerfil.png");
            }
        }
        // Calcular métricas de suscripción en el backend para evitar errores en la vista
        if (suscripcionActual != null && suscripcionActual.getFechaInicio() != null && suscripcionActual.getFechaFin() != null) {
            try {
                long diasTotales = java.time.temporal.ChronoUnit.DAYS.between(suscripcionActual.getFechaInicio(), suscripcionActual.getFechaFin());
                long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), suscripcionActual.getFechaFin());
                // Asegurar valores no negativos en avance y evitar division por cero
                Long avance = null;
                if (diasTotales > 0) {
                    long transcurridos = diasTotales - diasRestantes;
                    double porcentaje = (transcurridos * 100.0) / diasTotales;
                    if (porcentaje < 0) porcentaje = 0; // si fechas invertidas o futuro
                    if (porcentaje > 100) porcentaje = 100;
                    avance = Math.round(porcentaje);
                }
                model.addAttribute("susDiasTotales", diasTotales >= 0 ? diasTotales : null);
                model.addAttribute("susDiasRestantes", diasRestantes);
                model.addAttribute("susAvance", avance);
                model.addAttribute("susVencida", diasRestantes < 0);
            } catch (Exception e) {
                // En caso de error, dejar métricas nulas para no romper la vista
                model.addAttribute("susDiasTotales", null);
                model.addAttribute("susDiasRestantes", null);
                model.addAttribute("susAvance", null);
                model.addAttribute("susVencida", false);
            }
        } else {
            model.addAttribute("susDiasTotales", null);
            model.addAttribute("susDiasRestantes", null);
            model.addAttribute("susAvance", null);
            model.addAttribute("susVencida", false);
        }

        // 🎯 Mostrar objetivo del cliente: usar el objetivo del PERFIL (más simple y directo)
        String objetivoCliente = null;
        try {
            com.sabi.sabi.dto.ClienteDTO clienteDTO = clienteService.getClienteById(clienteId);
            if (clienteDTO != null && clienteDTO.getObjetivo() != null && !clienteDTO.getObjetivo().isEmpty()) {
                objetivoCliente = clienteDTO.getObjetivo();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al obtener objetivo del perfil: " + e.getMessage());
        }

        System.out.println("🎯 OBJETIVO PARA DASHBOARD (desde PERFIL):");
        System.out.println("   - Cliente ID: " + clienteId);
        System.out.println("   - Objetivo del perfil: " + (objetivoCliente != null ? "\"" + objetivoCliente + "\"" : "SIN OBJETIVO"));
        System.out.println("   - Se mostrará: " + (objetivoCliente != null ? "SÍ ✅" : "NO ❌"));

        model.addAttribute("objetivoCliente", objetivoCliente);

        return "cliente/dashboard";
    }

    @GetMapping("/cliente/rutinas")
    public String clienteRutinas(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        if (diagnosticoActual == null) {
            return "redirect:/cliente/diagnostico/crear?motivo=obligatorio";
        }
        boolean tieneDiagnostico = diagnosticoActual != null;
        List<RutinaDTO> rutinas = clienteService.getRutinasByClienteId(clienteId);
        model.addAttribute("tieneRutinas", !rutinas.isEmpty());
        model.addAttribute("rutinas", rutinas);
        model.addAttribute("tieneDiagnostico", tieneDiagnostico);
        return "cliente/rutinas";
    }

    @GetMapping("/cliente/listaEntrenadores")
    public String clienteListaEntrenadores(
        @RequestParam(value = "nombre", required = false) String nombre,
        @RequestParam(value = "especialidad", required = false) String especialidad,
        @RequestParam(value = "minPuntuacion", required = false) Double minPuntuacion,
        @RequestParam(value = "maxPuntuacion", required = false) Double maxPuntuacion,
        @RequestParam(value = "minExperiencia", required = false) Integer minExperiencia,
        @RequestParam(value = "minPrecio", required = false) Double minPrecio,
        @RequestParam(value = "maxPrecio", required = false) Double maxPrecio,
        @RequestParam(value = "certificaciones", required = false) String certificaciones,
        Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();

        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        if (diagnosticoActual == null) {
            // Redirect correcto a formulario de diagnóstico
            return "redirect:/cliente/diagnostico/crear?motivo=obligatorio";
        }
        boolean tieneDiagnostico = diagnosticoActual != null;

    String criterio = (nombre != null) ? nombre.trim() : null;
    String esp = (especialidad != null) ? especialidad.trim() : null;
    String cert = (certificaciones != null) ? certificaciones.trim() : null;
    boolean hasRange = (minPuntuacion != null && minPuntuacion >= 0) || (maxPuntuacion != null && maxPuntuacion >= 0);
    boolean hasExperience = (minExperiencia != null && minExperiencia >= 0);
    boolean hasPrecio = (minPrecio != null && minPrecio >= 0) || (maxPrecio != null && maxPrecio >= 0);
    boolean hasCertificaciones = (cert != null && !cert.isEmpty());
    boolean noFilters = (criterio == null || criterio.isEmpty()) && (esp == null || esp.isEmpty()) && !hasRange && !hasExperience && !hasPrecio && !hasCertificaciones;

    List<EntrenadorDTO> entrenadores = noFilters
        ? entrenadorService.getAllActiveEntrenadores()
        : entrenadorService.buscarEntrenadores(criterio, esp, minPuntuacion, maxPuntuacion, minExperiencia, cert);

    // Filtrar por precio si se especificó
    if (hasPrecio && entrenadores != null) {
        entrenadores = entrenadores.stream()
            .filter(e -> {
                if (minPrecio != null && e.getPrecioMinimo() != null && e.getPrecioMinimo() < minPrecio) {
                    return false;
                }
                if (maxPrecio != null && e.getPrecioMaximo() != null && e.getPrecioMaximo() > maxPrecio) {
                    return false;
                }
                return true;
            })
            .collect(java.util.stream.Collectors.toList());
    }

        model.addAttribute("entrenadores", entrenadores);
        model.addAttribute("tieneDiagnostico", tieneDiagnostico);
        model.addAttribute("nombre", nombre);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("minPuntuacion", minPuntuacion);
        model.addAttribute("maxPuntuacion", maxPuntuacion);
        model.addAttribute("minExperiencia", minExperiencia);
        model.addAttribute("minPrecio", minPrecio);
        model.addAttribute("maxPrecio", maxPrecio);
        model.addAttribute("certificaciones", certificaciones);
        // Limpiar posible atributo 'error' heredado de un flash anterior que solo contenga 'Error'
        Object err = model.getAttribute("error");
        if (err instanceof String str && str.trim().equalsIgnoreCase("Error")) {
            model.addAttribute("error", null);
        }
        return "cliente/listaEntrenadores";
    }

    @GetMapping("/cliente/suscripcion")
    public String clienteSuscripcionActual(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        SuscripcionDTO actual = suscripcionService.getSuscripcionActualByClienteId(clienteId);
        model.addAttribute("suscripcion", actual);
        if (actual != null && actual.getIdEntrenador() != null) {
            var entrenador = entrenadorService.getEntrenadorById(actual.getIdEntrenador());
            model.addAttribute("nombreEntrenador", entrenador != null ? entrenador.getNombre() : null);
        }
        return "cliente/suscripcion";
    }

    @GetMapping("/cliente/suscripcion/historial")
    public String clienteSuscripcionHistorial(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        List<SuscripcionDTO> historial = suscripcionService.getHistorialByClienteId(clienteId);
        model.addAttribute("suscripciones", historial);
        // Mapear nombres de entrenadores para la vista
        var nombresEntrenadores = historial.stream()
                .map(SuscripcionDTO::getIdEntrenador)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.toMap(
                        id -> id,
                        id -> {
                            var e = entrenadorService.getEntrenadorById(id);
                            return e != null ? e.getNombre() : "Entrenador no disponible";
                        }));
        model.addAttribute("nombresEntrenadores", nombresEntrenadores);
        return "cliente/suscripcion-historial";
    }

    @PostMapping("/cliente/suscripcion/cancelar/{id}")
    public String cancelarSuscripcionCliente(@PathVariable("id") Long id) {
        suscripcionService.cancelarSuscripcion(id);
        return "redirect:/cliente/suscripcion";
    }

    @GetMapping("/cliente/diagnostico/historial")
    public String clienteDiagnosticoHistorial(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        if (diagnosticoActual == null) {
            return "redirect:/cliente/diagnostico/crear?motivo=obligatorio";
        }
        List<DiagnosticoDTO> diagnosticos = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
        model.addAttribute("diagnosticos", diagnosticos);
        return "cliente/diagnostico-historial";
    }

    @GetMapping("/cliente/diagnostico/crear")
    public String mostrarFormularioDiagnostico(Model model,
            @RequestParam(value = "motivo", required = false) String motivo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        
        // Obtener el diagnóstico actual para prellenar campos
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        
        // Crear un nuevo diagnóstico vacío para el formulario
        DiagnosticoDTO diagnosticoFormulario = new DiagnosticoDTO();
        
        // Si existe diagnóstico actual, prellenar los campos para facilitar la actualización
        if (diagnosticoActual != null) {
            diagnosticoFormulario.setPeso(diagnosticoActual.getPeso());
            diagnosticoFormulario.setEstatura(diagnosticoActual.getEstatura());
            diagnosticoFormulario.setNivelExperiencia(diagnosticoActual.getNivelExperiencia());
            diagnosticoFormulario.setObjetivo(diagnosticoActual.getObjetivo());
            diagnosticoFormulario.setDisponibilidadTiempo(diagnosticoActual.getDisponibilidadTiempo());
            diagnosticoFormulario.setAccesoRecursos(diagnosticoActual.getAccesoRecursos());
            diagnosticoFormulario.setLesiones(diagnosticoActual.getLesiones());
            diagnosticoFormulario.setCondicionesMedicas(diagnosticoActual.getCondicionesMedicas());
            diagnosticoFormulario.setHorasSueno(diagnosticoActual.getHorasSueno());
            diagnosticoFormulario.setHabitosAlimenticios(diagnosticoActual.getHabitosAlimenticios());
            
            // Campos opcionales
            diagnosticoFormulario.setPorcentajeGrasaCorporal(diagnosticoActual.getPorcentajeGrasaCorporal());
            diagnosticoFormulario.setCircunferenciaCintura(diagnosticoActual.getCircunferenciaCintura());
            diagnosticoFormulario.setCircunferenciaCadera(diagnosticoActual.getCircunferenciaCadera());
            diagnosticoFormulario.setCircunferenciaBrazo(diagnosticoActual.getCircunferenciaBrazo());
            diagnosticoFormulario.setCircunferenciaPierna(diagnosticoActual.getCircunferenciaPierna());
            diagnosticoFormulario.setFrecuenciaCardiacaReposo(diagnosticoActual.getFrecuenciaCardiacaReposo());
            diagnosticoFormulario.setPresionArterial(diagnosticoActual.getPresionArterial());
            
            // Información para el entrenador
            diagnosticoFormulario.setPreferenciasEntrenamiento(diagnosticoActual.getPreferenciasEntrenamiento());
            diagnosticoFormulario.setExperienciaPreviaDeportes(diagnosticoActual.getExperienciaPreviaDeportes());
            diagnosticoFormulario.setDiasDisponiblesSemana(diagnosticoActual.getDiasDisponiblesSemana());
            diagnosticoFormulario.setHorarioPreferido(diagnosticoActual.getHorarioPreferido());
            diagnosticoFormulario.setNivelEstres(diagnosticoActual.getNivelEstres());
            diagnosticoFormulario.setLimitacionesFisicas(diagnosticoActual.getLimitacionesFisicas());
            diagnosticoFormulario.setMotivacionPrincipal(diagnosticoActual.getMotivacionPrincipal());
            diagnosticoFormulario.setSuplementosActuales(diagnosticoActual.getSuplementosActuales());
            diagnosticoFormulario.setFumador(diagnosticoActual.getFumador());
            diagnosticoFormulario.setConsumeAlcohol(diagnosticoActual.getConsumeAlcohol());
            diagnosticoFormulario.setFrecuenciaAlcohol(diagnosticoActual.getFrecuenciaAlcohol());
        }
        
        model.addAttribute("diagnostico", diagnosticoFormulario);
        model.addAttribute("diagnosticoActual", diagnosticoActual); // Para comparación visual
        
        if (motivo != null && motivo.equals("obligatorio")) {
            model.addAttribute("mensajeObligatorio",
                    "Debes completar tu diagnóstico para acceder al resto de la plataforma.");
        }
        return "cliente/diagnostico-form";
    }

    // Método eliminado: se usa /diagnostico/crear que maneja fotos correctamente

    @GetMapping("/cliente/diagnostico/descargar")
    public ResponseEntity<byte[]> descargarHistorialDiagnosticos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();

        // Obtener el historial de diagnósticos
        List<DiagnosticoDTO> diagnosticos = clienteService.getHistorialDiagnosticosByClienteId(clienteId);

        // Generar el archivo Excel
        byte[] archivoExcel = excelService.generarExcelDiagnosticos(diagnosticos);

        // Configurar cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("historial_diagnosticos.xlsx").build());

        return ResponseEntity.ok().headers(headers).body(archivoExcel);
    }

    @GetMapping("/cliente/diagnostico/descargar/{id}")
    public ResponseEntity<byte[]> descargarDiagnosticoEspecifico(@PathVariable("id") Long idDiagnostico) {
        // Obtener el diagnóstico específico
        DiagnosticoDTO diagnostico = diagnosticoService.getDiagnosticoById(idDiagnostico);

        // Obtener el nombre del cliente
        String nombreCliente = "";
        if (diagnostico != null && diagnostico.getIdCliente() != null) {
            try {
                var cliente = clienteService.getClienteById(diagnostico.getIdCliente());
                nombreCliente = cliente != null && cliente.getNombre() != null ? cliente.getNombre() : "";
            } catch (Exception e) {
                nombreCliente = "";
            }
        }

        // Leer el logo como bytes
        byte[] logoBytes = null;
        try (java.io.InputStream is = getClass().getResourceAsStream("/static/img/logoF.png")) {
            if (is != null) {
                logoBytes = is.readAllBytes();
            }
        } catch (Exception e) {
            logoBytes = null;
        }

        // Generar el archivo Excel
        byte[] archivoExcel = excelService.generarExcelDiagnostico(diagnostico, nombreCliente, logoBytes);

        // Configurar cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("diagnostico_" + idDiagnostico + ".xlsx").build());

        return new ResponseEntity<>(archivoExcel, headers, HttpStatus.OK);
    }

    @GetMapping("/api/cliente/entrenador/{entrenadorId}/info")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> obtenerInfoEntrenador(@PathVariable Long entrenadorId) {
        try {
            com.sabi.sabi.dto.EntrenadorDTO entrenador = entrenadorService.getEntrenadorById(entrenadorId);
            if (entrenador == null) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            // Crear un objeto con solo la info necesaria para el modal
            java.util.Map<String, Object> info = new java.util.HashMap<>();
            String nombreCompleto = entrenador.getNombre();
            if (entrenador.getApellido() != null && !entrenador.getApellido().isEmpty()) {
                nombreCompleto += " " + entrenador.getApellido();
            }
            info.put("nombre", nombreCompleto);
            info.put("email", entrenador.getEmail());
            info.put("fotoPerfilUrl",
                    entrenador.getFotoPerfilUrl() != null ? entrenador.getFotoPerfilUrl() : "/img/fotoPerfil.png");
            // Priorizar especialidades múltiples sobre especialidad singular
            String especialidadesTexto = entrenador.getEspecialidades();
            if (especialidadesTexto == null || especialidadesTexto.isEmpty()) {
                especialidadesTexto = entrenador.getEspecialidad();
            }
            info.put("especialidades", especialidadesTexto);
            info.put("experiencia", entrenador.getAniosExperiencia() != null ? entrenador.getAniosExperiencia() : 0);
            info.put("certificaciones", entrenador.getCertificaciones());
            info.put("calificacionPromedio",
                    entrenador.getCalificacionPromedio() != null ? entrenador.getCalificacionPromedio() : 0.0);
            info.put("telefono", entrenador.getTelefono());
            info.put("descripcion", entrenador.getDescripcion());
            info.put("precioMinimo", entrenador.getPrecioMinimo());
            info.put("precioMaximo", entrenador.getPrecioMaximo());
            info.put("id", entrenador.getId());

            return org.springframework.http.ResponseEntity.ok(info);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/api/cliente/entrenador/{entrenadorId}/comentarios")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> obtenerComentariosEntrenador(@PathVariable Long entrenadorId) {
        try {
            var entrenador = entrenadorService.getEntrenadorById(entrenadorId);
            if (entrenador == null) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
            var lista = comentarioService.getComentariosPorEntrenador(entrenadorId);
            // Ordenar por fechaCreacion DESC (nulls al final) y deduplicar por idComentario
            lista = lista.stream()
                    .filter(c -> c != null)
                    .sorted((a,b) -> {
                        if (a.getFechaCreacion()==null && b.getFechaCreacion()==null) return 0;
                        if (a.getFechaCreacion()==null) return 1;
                        if (b.getFechaCreacion()==null) return -1;
                        return b.getFechaCreacion().compareTo(a.getFechaCreacion());
                    })
                    .collect(java.util.stream.Collectors.toMap(
                            c -> c.getIdComentario(),
                            c -> c,
                            (c1,c2) -> c1, // si hay duplicado id tomar primero
                            java.util.LinkedHashMap::new))
                    .values().stream().toList();
            java.util.List<java.util.Map<String, Object>> respuesta = lista.stream().map(c -> {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("idComentario", c.getIdComentario());
                m.put("texto", c.getTexto());
                m.put("calificacion", c.getCalificacion());
                m.put("fechaCreacion", c.getFechaCreacion());
                String nombreCliente = null;
                if (c.getIdCliente() != null) {
                    try {
                        var cli = clienteService.getClienteById(c.getIdCliente());
                        if (cli != null) {
                            nombreCliente = cli.getNombre();
                            if (cli.getApellido() != null && !cli.getApellido().isBlank()) {
                                nombreCliente += " " + cli.getApellido();
                            }
                        }
                    } catch (Exception ignored) {}
                }
                m.put("clienteNombre", nombreCliente);
                return m;
            }).toList();
            return org.springframework.http.ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
