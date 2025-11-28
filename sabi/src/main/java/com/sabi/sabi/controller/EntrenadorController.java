package com.sabi.sabi.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller; // añadido
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.entity.enums.EstadoSuscripcion;
import com.sabi.sabi.service.ClienteService;
import com.sabi.sabi.service.EmailService;
import com.sabi.sabi.service.EntrenadorService;
import com.sabi.sabi.service.MensajePregrabadoService;
import com.sabi.sabi.service.ReportePdfService;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.UsuarioService;

@Controller
public class EntrenadorController {
    @Autowired
    private EntrenadorService entrenadorService;

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ReportePdfService reportePdfService;

    @Autowired
    private RutinaService rutinaService; // nuevo

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private MensajePregrabadoService mensajePregrabadoService;

    @GetMapping("/entrenador/dashboard")
    public String entrenadorDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        var entrenadorId = usuario.getId();
        
        // Obtener todas las suscripciones del entrenador
        var todasSuscripciones = suscripcionService.getAllSuscripciones().stream()
            .filter(s -> s.getIdEntrenador() != null && s.getIdEntrenador().equals(entrenadorId))
            .toList();
        
        // Suscripciones activas (ACEPTADA)
        var suscripcionesActivas = todasSuscripciones.stream()
            .filter(s -> s.getEstadoSuscripcion() == EstadoSuscripcion.ACEPTADA)
            .toList();
        
        // Solicitudes pendientes (PENDIENTE)
        var solicitudesPendientes = todasSuscripciones.stream()
            .filter(s -> s.getEstadoSuscripcion() == EstadoSuscripcion.PENDIENTE)
            .toList();
        
        // Clientes activos únicos
        var clientesActivos = suscripcionesActivas.stream()
            .map(SuscripcionDTO::getIdCliente)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        
        // Obtener todas las rutinas creadas por el entrenador
        var rutinasCreadas = rutinaService.getRutinasPorUsuario(entrenadorId);
        
        // Rutinas activas (asignadas a clientes)
        var rutinasActivas = clientesActivos.stream()
            .map(rutinaService::getRutinaActivaCliente)
            .filter(Objects::nonNull)
            .toList();
        
        // Calificación promedio del entrenador
        var entrenadorDTO = entrenadorService.getEntrenadorById(entrenadorId);
        Double calificacionPromedio = entrenadorDTO != null ? entrenadorDTO.getCalificacionPromedio() : null;
        Integer aniosExperiencia = entrenadorDTO != null ? entrenadorDTO.getAniosExperiencia() : null;
        
        // Información de solicitudes pendientes con datos del cliente
        var solicitudesInfo = solicitudesPendientes.stream()
            .map(s -> {
                var cliente = clienteService.getClienteById(s.getIdCliente());
                Map<String, Object> info = new java.util.HashMap<>();
                info.put("suscripcion", s);
                info.put("clienteNombre", cliente != null ? cliente.getNombre() : "Cliente");
                info.put("clienteFoto", cliente != null && cliente.getFotoPerfilUrl() != null ? 
                    cliente.getFotoPerfilUrl() : "/img/fotoPerfil.png");
                return info;
            })
            .toList();
        
        // Agregar atributos al modelo
        model.addAttribute("totalClientes", clientesActivos.size());
        model.addAttribute("totalRutinas", rutinasCreadas.size());
        model.addAttribute("rutinasActivas", rutinasActivas.size());
        model.addAttribute("solicitudesPendientes", solicitudesPendientes.size());
        model.addAttribute("calificacionPromedio", calificacionPromedio != null ? calificacionPromedio : 0.0);
        model.addAttribute("aniosExperiencia", aniosExperiencia != null ? aniosExperiencia : 0);
        model.addAttribute("solicitudesInfo", solicitudesInfo);
        model.addAttribute("usuario", usuario);
        
        return "entrenador/dashboard";
    }

    @GetMapping("/entrenadores")
    public String getAllActiveEntrenadores(Model model) {
        model.addAttribute("entrenadores", entrenadorService.getAllActiveEntrenadores());
        return "cliente/listaEntrenadores";
    }

    @GetMapping("/entrenador/suscripciones")
    public String listarSuscripcionesEntrenador(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        var todas = suscripcionService.getAllSuscripciones();
        if (todas == null) {
            todas = java.util.Collections.emptyList();
        }
        var suscripciones = todas.stream()
            .filter(s -> s != null)
            .filter(s -> s.getIdEntrenador() != null && s.getIdEntrenador().equals(usuario.getId()))
            .filter(s -> s.getEstadoSuscripcion() != com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA)
            .collect(java.util.stream.Collectors.toList());
        model.addAttribute("suscripciones", suscripciones);
        // Agrupar por estado para la vista en orden solicitado: PENDIENTE, COTIZADA, ACEPTADA
        var suscripcionesPendientes = suscripciones.stream()
            .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.PENDIENTE)
            .collect(java.util.stream.Collectors.toList());
        var suscripcionesCotizadas = suscripciones.stream()
            .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.COTIZADA)
            .collect(java.util.stream.Collectors.toList());
        var suscripcionesAceptadas = suscripciones.stream()
            .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.ACEPTADA)
            .collect(java.util.stream.Collectors.toList());
        model.addAttribute("suscripcionesPendientes", suscripcionesPendientes);
        model.addAttribute("suscripcionesCotizadas", suscripcionesCotizadas);
        model.addAttribute("suscripcionesAceptadas", suscripcionesAceptadas);

        // Mapa de nombres de entrenadores with null checks
        Map<Long, String> nombresEntrenadores = suscripciones.stream()
                .map(com.sabi.sabi.dto.SuscripcionDTO::getIdEntrenador)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(id -> {
                    var ent = entrenadorService.getEntrenadorById(id);
                    return ent != null ? java.util.Map.entry(id, ent.getNombre() != null ? ent.getNombre() : "Entrenador") : null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("nombresEntrenadores", nombresEntrenadores);

        // Mapa de nombres de clientes with null checks
        Map<Long, String> nombresClientes = suscripciones.stream()
                .map(com.sabi.sabi.dto.SuscripcionDTO::getIdCliente)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(id -> {
                    var cli = clienteService.getClienteById(id);
                    return cli != null ? java.util.Map.entry(id, cli.getNombre() != null ? cli.getNombre() : "Cliente") : null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("nombresClientes", nombresClientes);

        // Mapa de rutinas activas por cliente (para mostrar "Ver progreso")
        Map<Long, Long> rutinasActivasClientes = suscripciones.stream()
                .map(com.sabi.sabi.dto.SuscripcionDTO::getIdCliente)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(idCliente -> {
                    var r = rutinaService.getRutinaActivaCliente(idCliente);
                    return (r != null && r.getIdRutina() != null) ? java.util.Map.entry(idCliente, r.getIdRutina()) : null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("rutinasActivasClientes", rutinasActivasClientes);

        // Mapa de diagnóstico actual por cliente (evitar valores null en toMap)
        Map<Long, com.sabi.sabi.dto.DiagnosticoDTO> diagnosticosActuales = suscripciones.stream()
            .map(com.sabi.sabi.dto.SuscripcionDTO::getIdCliente)
            .filter(java.util.Objects::nonNull)
            .distinct()
            .map(id -> {
                var diag = clienteService.getDiagnosticoActualByClienteId(id);
                return diag != null ? java.util.Map.entry(id, diag) : null;
            })
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("diagnosticosActuales", diagnosticosActuales);
        // Log simple para depuración (se puede remover luego)
        System.out.println("[EntrenadorController] rutinasActivasClientes=" + rutinasActivasClientes);
        return "entrenador/suscripciones";
    }

    @org.springframework.web.bind.annotation.GetMapping("/api/entrenador/diagnostico/cliente/{clienteId}")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<?> obtenerDiagnosticoActual(@org.springframework.web.bind.annotation.PathVariable Long clienteId) {
        try {
            com.sabi.sabi.dto.DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
            if (diagnosticoActual == null) {
                return org.springframework.http.ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "No hay diagnostico"));
            }
            return org.springframework.http.ResponseEntity.ok(diagnosticoActual);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/entrenador/suscripciones/historial")
    public String historialSuscripcionesEntrenador(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        var todas = suscripcionService.getAllSuscripciones();
        if (todas == null) {
            todas = java.util.Collections.emptyList();
        }
        var suscripciones = todas.stream()
                .filter(s -> s != null)
                .filter(s -> s.getIdEntrenador() != null && s.getIdEntrenador().equals(usuario.getId()))
                .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("suscripciones", suscripciones);
        Map<Long, String> nombresEntrenadores = suscripciones.stream()
                .map(com.sabi.sabi.dto.SuscripcionDTO::getIdEntrenador)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(id -> {
                    var ent = entrenadorService.getEntrenadorById(id);
                    return ent != null ? java.util.Map.entry(id, ent.getNombre() != null ? ent.getNombre() : "Entrenador") : null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("nombresEntrenadores", nombresEntrenadores);
        Map<Long, String> nombresClientes = suscripciones.stream()
                .map(com.sabi.sabi.dto.SuscripcionDTO::getIdCliente)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(id -> {
                    var cli = clienteService.getClienteById(id);
                    return cli != null ? java.util.Map.entry(id, cli.getNombre() != null ? cli.getNombre() : "Cliente") : null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("nombresClientes", nombresClientes);
        return "entrenador/suscripciones-historial";
    }

    @GetMapping("/entrenador/enviar-correo-clientes")
    public String mostrarFormularioCorreo(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        var suscripciones = suscripcionService.getAllActiveSuscripciones().stream()
            .filter(s -> s.getIdEntrenador() != null && s.getIdEntrenador().equals(usuario.getId()))
            .filter(s -> s.getEstadoSuscripcion() != com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA)
            .collect(Collectors.toList());
        List<com.sabi.sabi.dto.ClienteDTO> clientes = suscripciones.stream()
            .map(s -> clienteService.getClienteById(s.getIdCliente()))
            .distinct()
            .toList();
        model.addAttribute("clientes", clientes);
        
        // Cargar mensajes pregrabados del entrenador
        try {
            var mensajesPregrabados = mensajePregrabadoService.obtenerMensajesActivosPorEntrenador(usuario.getId());
            model.addAttribute("mensajesPregrabados", mensajesPregrabados);
        } catch (Exception e) {
            model.addAttribute("mensajesPregrabados", java.util.Collections.emptyList());
        }
        
        return "entrenador/enviar-correo-clientes";
    }

    @PostMapping("/entrenador/enviar-correo-clientes")
    public String enviarCorreoClientes(@RequestParam String asunto,
                                        @RequestParam String mensaje,
                                        @RequestParam List<Long> idsClientes,
                                        RedirectAttributes redirectAttributes) {
        List<String> correos = idsClientes == null ? java.util.Collections.emptyList() : idsClientes.stream()
                .filter(java.util.Objects::nonNull)
                .map(id -> {
                    var c = clienteService.getClienteById(id);
                    return c != null ? c.getEmail() : null;
                })
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        try {
            emailService.sendEmail(asunto, mensaje, correos);
            redirectAttributes.addFlashAttribute("success", "Correos enviados exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudieron enviar los correos: " + e.getMessage());
        }
        return "redirect:/entrenador/enviar-correo-clientes";
    }

    @GetMapping("/entrenador/suscripciones/reporte.pdf")
    public org.springframework.http.ResponseEntity<byte[]> descargarReporteSuscripciones() {
        var baos = reportePdfService.generarReporteSuscripciones();
        byte[] pdf = baos.toByteArray();
        var headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.set("Content-Disposition", "attachment; filename=Reporte_Suscripciones.pdf");
        return org.springframework.http.ResponseEntity.ok().headers(headers).body(pdf);
    }

    @org.springframework.web.bind.annotation.PostMapping("/entrenadores/solicitar/{idEntrenador}")
    public String solicitarEntrenador(@org.springframework.web.bind.annotation.PathVariable Long idEntrenador,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        if (suscripcionService.existsSuscripcionActivaByClienteId(usuario.getId())) {
            redirectAttributes.addFlashAttribute("error", "No puedes solicitar un entrenador porque ya tienes una suscripción activa con otro entrenador.");
            return "redirect:/cliente/suscripcion";
        }
        SuscripcionDTO suscripcionDTO = new SuscripcionDTO();
        suscripcionDTO.setIdCliente(usuario.getId());
        suscripcionDTO.setIdEntrenador(idEntrenador);
        suscripcionDTO.setEstadoSuscripcion(EstadoSuscripcion.PENDIENTE);
        suscripcionService.createSuscripcion(suscripcionDTO);
        return "redirect:/cliente/suscripcion";
    }

    @GetMapping("/entrenador/diagnostico/vista/{clienteId}")
    public String verDiagnostico(@PathVariable Long clienteId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Obtener diagnostico actual del cliente
            com.sabi.sabi.dto.DiagnosticoDTO diagnostico = clienteService.getDiagnosticoActualByClienteId(clienteId);
            if (diagnostico == null) {
                model.addAttribute("error", "No se encontro un diagnostico para este cliente.");
                return "entrenador/ver-diagnostico";
            }

            // Obtener datos completos del cliente (para objetivo, descripcion, foto)
            com.sabi.sabi.dto.ClienteDTO cliente = clienteService.getClienteById(clienteId);

            // Marcar en la suscripción actual que el entrenador ya vio el diagnóstico
            try {
                if (userDetails != null) {
                    var entrenador = usuarioService.obtenerPorEmail(userDetails.getUsername());
                    // Obtener suscripción actual de este cliente
                    com.sabi.sabi.dto.SuscripcionDTO susActual = suscripcionService.getSuscripcionActualByClienteId(clienteId);
                    if (susActual != null && susActual.getIdEntrenador() != null && susActual.getIdEntrenador().equals(entrenador.getId())) {
                        if (susActual.getVistaDiagnostico() == null || !susActual.getVistaDiagnostico()) {
                            susActual.setVistaDiagnostico(true);
                            suscripcionService.updateSuscripcion(susActual.getIdSuscripcion(), susActual);
                        }
                    }
                }
            } catch (Exception e) {
                // No bloquear la vista por este error
                System.err.println("[verDiagnostico] No se pudo marcar vistaDiagnostico: " + e.getMessage());
            }

            model.addAttribute("diagnostico", diagnostico);
            model.addAttribute("cliente", cliente);
            return "entrenador/ver-diagnostico";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrio un error al cargar el diagnostico: " + e.getMessage());
            return "entrenador/ver-diagnostico";
        }
    }

    @GetMapping("/api/entrenador/cliente/{clienteId}/info")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> obtenerInfoCliente(@PathVariable Long clienteId) {
        try {
            com.sabi.sabi.dto.ClienteDTO cliente = clienteService.getClienteById(clienteId);
            if (cliente == null) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            // Crear un objeto con solo la info necesaria para el modal
            java.util.Map<String, Object> info = new java.util.HashMap<>();
            info.put("nombre", cliente.getNombre() + (cliente.getApellido() != null ? " " + cliente.getApellido() : ""));
            info.put("email", cliente.getEmail());
            info.put("fotoPerfilUrl", cliente.getFotoPerfilUrl() != null ? cliente.getFotoPerfilUrl() : "/img/fotoPerfil.png");
            info.put("objetivo", cliente.getObjetivo());
            info.put("descripcion", cliente.getDescripcion());
            info.put("id", cliente.getId());

            return org.springframework.http.ResponseEntity.ok(info);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/entrenador/diagnostico/historial/{clienteId}")
    public String verHistorialDiagnosticos(@PathVariable Long clienteId, Model model) {
        try {
            // Obtener historial completo de diagnósticos del cliente
            List<com.sabi.sabi.dto.DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
            if (historial == null || historial.isEmpty()) {
                model.addAttribute("error", "No se encontraron diagnósticos para este cliente.");
                return "entrenador/ver-historial-diagnosticos";
            }

            // Obtener datos del cliente
            com.sabi.sabi.dto.ClienteDTO cliente = clienteService.getClienteById(clienteId);
            if (cliente == null) {
                model.addAttribute("error", "Cliente no encontrado.");
                return "entrenador/ver-historial-diagnosticos";
            }

            // Obtener diagnóstico actual (el más reciente)
            com.sabi.sabi.dto.DiagnosticoDTO diagnosticoActual = historial.get(0);
            
            // Obtener diagnóstico anterior para comparativa (si existe)
            com.sabi.sabi.dto.DiagnosticoDTO diagnosticoAnterior = null;
            if (historial.size() >= 2) {
                diagnosticoAnterior = historial.get(1);
            }

            model.addAttribute("historial", historial);
            model.addAttribute("cliente", cliente);
            model.addAttribute("diagnosticoActual", diagnosticoActual);
            model.addAttribute("diagnosticoAnterior", diagnosticoAnterior);
            model.addAttribute("tieneComparativa", diagnosticoAnterior != null);
            
            return "entrenador/ver-historial-diagnosticos";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al cargar el historial de diagnósticos: " + e.getMessage());
            return "entrenador/ver-historial-diagnosticos";
        }
    }

    @GetMapping("/api/entrenador/diagnostico/historial/{clienteId}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> obtenerHistorialDiagnosticos(@PathVariable Long clienteId) {
        try {
            List<com.sabi.sabi.dto.DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
            if (historial == null || historial.isEmpty()) {
                return org.springframework.http.ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "No hay historial de diagnósticos"));
            }
            return org.springframework.http.ResponseEntity.ok(historial);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/uploads/certificaciones/{filename:.+}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> descargarCertificacion(@PathVariable String filename) {
        try {
            java.nio.file.Path file = java.nio.file.Paths.get("uploads/certificaciones").resolve(filename);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return org.springframework.http.ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                        .body(resource);
            } else {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/entrenador/{id}/rango-precio")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> obtenerRangoPrecioEntrenador(@PathVariable Long id) {
        try {
            var entrenador = entrenadorService.getEntrenadorById(id);
            if (entrenador == null) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
            java.util.Map<String, Object> rango = new java.util.HashMap<>();
            rango.put("min", entrenador.getPrecioMinimo());
            rango.put("max", entrenador.getPrecioMaximo());
            return org.springframework.http.ResponseEntity.ok(rango);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }
}
