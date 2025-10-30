package com.sabi.sabi.controller;

import com.sabi.sabi.dto.DiagnosticoDTO;
import com.sabi.sabi.dto.EntrenadorDTO;
import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.EntrenadorService;
import com.sabi.sabi.service.ClienteService;
import com.sabi.sabi.service.DiagnosticoService;
import com.sabi.sabi.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.sabi.sabi.security.CustomUserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;

import java.util.List;

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

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        // Obtener diagnostico actual
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        boolean tieneDiagnostico = diagnosticoActual != null;

        // Obtener historial completo para comparativa
        List<DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);

        // Obtener diagnostico anterior (el segundo mas reciente)
        DiagnosticoDTO diagnosticoAnterior = null;
        if (historial != null && historial.size() >= 2) {
            // El historial viene ordenado por fecha DESC, entonces [0]=actual, [1]=anterior
            diagnosticoAnterior = historial.get(1);
        }

        model.addAttribute("tieneDiagnostico", tieneDiagnostico);
        model.addAttribute("diagnosticoActual", diagnosticoActual);
        model.addAttribute("diagnosticoAnterior", diagnosticoAnterior);
        model.addAttribute("tieneComparativa", diagnosticoAnterior != null);
        model.addAttribute("usuario", userDetails.getUsuario());

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
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();

        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        if (diagnosticoActual == null) {
            return "redirect:/cliente/diagnostico/crear?motivo=obligatorio";
        }
        boolean tieneDiagnostico = diagnosticoActual != null;

        String criterio = (nombre != null) ? nombre.trim() : null;
        List<EntrenadorDTO> entrenadores = (criterio == null || criterio.isEmpty())
                ? entrenadorService.getAllActiveEntrenadores()
                : entrenadorService.buscarEntrenadores(criterio);

        model.addAttribute("entrenadores", entrenadores);
        model.addAttribute("tieneDiagnostico", tieneDiagnostico);
        model.addAttribute("nombre", nombre);
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
                            return e != null ? e.getNombre() : null;
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
        model.addAttribute("diagnostico", new DiagnosticoDTO());
        if (motivo != null && motivo.equals("obligatorio")) {
            model.addAttribute("mensajeObligatorio",
                    "Debes completar tu diagnóstico para acceder al resto de la plataforma.");
        }
        return "cliente/diagnostico-form";
    }

    @PostMapping("/cliente/diagnostico/guardar")
    public String guardarDiagnostico(@ModelAttribute("diagnostico") @Valid DiagnosticoDTO diagnosticoDTO,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Por favor corrige los errores del formulario.");
            return "cliente/diagnostico-form";
        }
        // Obtener el cliente autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        diagnosticoDTO.setIdCliente(clienteId);

        // Guardar diagnóstico usando DiagnosticoService
        try {
            diagnosticoService.createDiagnostico(diagnosticoDTO);
            model.addAttribute("success", "Diagnóstico guardado correctamente.");
            return "redirect:/cliente/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el diagnóstico: " + e.getMessage());
            return "cliente/diagnostico-form";
        }
    }

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
                    entrenador.getFotoPerfilUrl() != null ? entrenador.getFotoPerfilUrl() : "/img/entrenador.jpg");
            info.put("especialidades", entrenador.getEspecialidad());
            info.put("experiencia", 0); // No hay campo de experiencia en DTO, poner 0 o null
            info.put("calificacionPromedio",
                    entrenador.getCalificacionPromedio() != null ? entrenador.getCalificacionPromedio() : 0.0);
            info.put("telefono", entrenador.getTelefono());
            info.put("descripcion", entrenador.getDescripcion());
            info.put("id", entrenador.getId());

            return org.springframework.http.ResponseEntity.ok(info);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
