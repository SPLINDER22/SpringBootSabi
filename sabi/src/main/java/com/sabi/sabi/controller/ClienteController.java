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
    @Autowired
    private EjercicioAsignadoService ejercicioAsignadoService;

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
            // Si hay una rutina activa, siempre mostrarla (sea asignada por entrenador o adoptada)
            model.addAttribute("tieneRutinaActiva", true);
            model.addAttribute("rutina", rutinaDTO);

            // Calcular progreso y día actual para todas las rutinas activas
            DiaDTO diaDTO = diaService.getDiaActual(clienteId);
            long porcentajeCompletado = diaService.calcularProgresoRutina(clienteId);
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
        if (suscripcionActual != null && suscripcionActual.getDuracionSemanas() != null) {
            try {
                long diasTotales = suscripcionActual.getDuracionSemanas() * 7L;
                // No contamos con fecha de inicio/fin en la nueva implementación, por lo que días restantes no pueden calcularse aquí
                model.addAttribute("susDiasTotales", diasTotales >= 0 ? diasTotales : null);
                model.addAttribute("susDiasRestantes", null);
                model.addAttribute("susAvance", null);
                model.addAttribute("susVencida", false);
            } catch (Exception e) {
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

        // ========================================
        // 🎯 RECOMENDACIÓN BASADA EN OBJETIVO
        // ========================================
        String objetivoCliente = diagnosticoActual.getObjetivo();
        String especialidadRecomendada = null;
        boolean mostrarRecomendacion = false;

        if (objetivoCliente != null && !objetivoCliente.isEmpty()) {
            String objetivoLower = objetivoCliente.toLowerCase();

            // 🔥 MAPEO INTELIGENTE DE OBJETIVOS → ESPECIALIDADES
            // Sistema de detección con múltiples palabras clave y sinónimos

            // ═══════════════════════════════════════════════════════════════
            // 🎯 PÉRDIDA DE PESO / ADELGAZAR
            // ═══════════════════════════════════════════════════════════════
            if (objetivoLower.contains("bajar") || objetivoLower.contains("perder") ||
                objetivoLower.contains("adelgazar") || objetivoLower.contains("reducir") ||
                objetivoLower.contains("quemar") || objetivoLower.contains("eliminar") ||
                objetivoLower.contains("peso") && (objetivoLower.contains("menos") || objetivoLower.contains("bajo")) ||
                objetivoLower.contains("grasa") && (objetivoLower.contains("perder") || objetivoLower.contains("quemar") || objetivoLower.contains("bajar")) ||
                objetivoLower.contains("kilos") || objetivoLower.contains("libras") ||
                objetivoLower.contains("definir") || objetivoLower.contains("definición") ||
                objetivoLower.contains("tonificar") || objetivoLower.contains("marcar") ||
                objetivoLower.contains("abdomen") && (objetivoLower.contains("plano") || objetivoLower.contains("marcado")) ||
                objetivoLower.contains("barriga") || objetivoLower.contains("panza") ||
                objetivoLower.contains("cintura") && objetivoLower.contains("reducir") ||
                objetivoLower.contains("talla") && (objetivoLower.contains("menos") || objetivoLower.contains("bajar")) ||
                objetivoLower.contains("obesidad") || objetivoLower.contains("sobrepeso") ||
                objetivoLower.contains("dieta") && objetivoLower.contains("ejercicio") ||
                objetivoLower.contains("calorías") && objetivoLower.contains("quemar") ||
                objetivoLower.contains("delgad") || objetivoLower.contains("flac") ||
                objetivoLower.contains("rebajar") || objetivoLower.contains("deshinchar") ||
                objetivoLower.contains("celulitis") || objetivoLower.contains("michelin")) {
                especialidadRecomendada = "Pérdida de Peso";

            // ═══════════════════════════════════════════════════════════════
            // 💪 GANANCIA MUSCULAR / HIPERTROFIA
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("masa") && (objetivoLower.contains("muscular") || objetivoLower.contains("ganar")) ||
                       objetivoLower.contains("muscular") || objetivoLower.contains("músculos") ||
                       objetivoLower.contains("ganar") && (objetivoLower.contains("peso") || objetivoLower.contains("volumen") || objetivoLower.contains("músculo")) ||
                       objetivoLower.contains("hipertrofia") || objetivoLower.contains("volumen") ||
                       objetivoLower.contains("aumentar") && (objetivoLower.contains("masa") || objetivoLower.contains("músculo") || objetivoLower.contains("tamaño")) ||
                       objetivoLower.contains("crecer") || objetivoLower.contains("grande") ||
                       objetivoLower.contains("bíceps") || objetivoLower.contains("pectorales") ||
                       objetivoLower.contains("espalda") && objetivoLower.contains("ancha") ||
                       objetivoLower.contains("piernas") && objetivoLower.contains("grandes") ||
                       objetivoLower.contains("brazos") && (objetivoLower.contains("grandes") || objetivoLower.contains("gruesos")) ||
                       objetivoLower.contains("gym") && (objetivoLower.contains("cuerpo") || objetivoLower.contains("físico")) ||
                       objetivoLower.contains("culturismo") || objetivoLower.contains("bodybuilding") ||
                       objetivoLower.contains("fitness") && objetivoLower.contains("ganar") ||
                       objetivoLower.contains("proteína") || objetivoLower.contains("suplementos") ||
                       objetivoLower.contains("bulk") || objetivoLower.contains("bulking") ||
                       objetivoLower.contains("corpulento") || objetivoLower.contains("fornido") ||
                       objetivoLower.contains("musculoso") || objetivoLower.contains("atlético") ||
                       objetivoLower.contains("fibra") && objetivoLower.contains("muscular")) {
                especialidadRecomendada = "Ganancia Muscular";

            // ═══════════════════════════════════════════════════════════════
            // 🏋️ FUERZA Y ACONDICIONAMIENTO / POWERLIFTING
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("fuerza") || objetivoLower.contains("potencia") ||
                       objetivoLower.contains("levantar") && (objetivoLower.contains("peso") || objetivoLower.contains("más")) ||
                       objetivoLower.contains("fuerte") || objetivoLower.contains("poder") ||
                       objetivoLower.contains("sentadilla") || objetivoLower.contains("squat") ||
                       objetivoLower.contains("press") && (objetivoLower.contains("banca") || objetivoLower.contains("bench")) ||
                       objetivoLower.contains("peso muerto") || objetivoLower.contains("deadlift") ||
                       objetivoLower.contains("powerlifting") || objetivoLower.contains("halterofilia") ||
                       objetivoLower.contains("levantamiento") && objetivoLower.contains("olímpico") ||
                       objetivoLower.contains("explosividad") || objetivoLower.contains("explosivo") ||
                       objetivoLower.contains("velocidad") && objetivoLower.contains("fuerza") ||
                       objetivoLower.contains("récord") || objetivoLower.contains("rm") ||
                       objetivoLower.contains("1rm") || objetivoLower.contains("máximo") ||
                       objetivoLower.contains("strongman") || objetivoLower.contains("crosslifting") ||
                       objetivoLower.contains("barra") || objetivoLower.contains("pesas") ||
                       objetivoLower.contains("olimpiadas") || objetivoLower.contains("competencia") && objetivoLower.contains("fuerza")) {
                especialidadRecomendada = "Fuerza y Acondicionamiento";

            // ═══════════════════════════════════════════════════════════════
            // 🏃 CARDIO Y RESISTENCIA
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("resistencia") || objetivoLower.contains("cardio") ||
                       objetivoLower.contains("aeróbico") || objetivoLower.contains("aerobico") ||
                       objetivoLower.contains("correr") || objetivoLower.contains("running") ||
                       objetivoLower.contains("maratón") || objetivoLower.contains("carrera") ||
                       objetivoLower.contains("5k") || objetivoLower.contains("10k") ||
                       objetivoLower.contains("media maratón") || objetivoLower.contains("trail") ||
                       objetivoLower.contains("trote") || objetivoLower.contains("trotar") ||
                       objetivoLower.contains("ciclismo") || objetivoLower.contains("bicicleta") ||
                       objetivoLower.contains("natación") || objetivoLower.contains("nadar") ||
                       objetivoLower.contains("spinning") || objetivoLower.contains("indoor cycling") ||
                       objetivoLower.contains("aguante") || objetivoLower.contains("stamina") ||
                       objetivoLower.contains("pulmón") || objetivoLower.contains("respiración") ||
                       objetivoLower.contains("vo2") || objetivoLower.contains("capacidad aeróbica") ||
                       objetivoLower.contains("triatlón") || objetivoLower.contains("duatlón") ||
                       objetivoLower.contains("endurance") || objetivoLower.contains("ultra") ||
                       objetivoLower.contains("caminata") && objetivoLower.contains("larga") ||
                       objetivoLower.contains("senderismo") || objetivoLower.contains("hiking") ||
                       objetivoLower.contains("corazón") && objetivoLower.contains("saludable")) {
                especialidadRecomendada = "Cardio y Resistencia";

            // ═══════════════════════════════════════════════════════════════
            // 🧘 YOGA / FLEXIBILIDAD / MOVILIDAD
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("yoga") || objetivoLower.contains("yogi") ||
                       objetivoLower.contains("flexibilidad") || objetivoLower.contains("flexible") ||
                       objetivoLower.contains("estiramiento") || objetivoLower.contains("estirar") ||
                       objetivoLower.contains("elongación") || objetivoLower.contains("elongar") ||
                       objetivoLower.contains("movilidad") || objetivoLower.contains("móvil") ||
                       objetivoLower.contains("pilates") || objetivoLower.contains("mat") ||
                       objetivoLower.contains("meditación") || objetivoLower.contains("meditar") ||
                       objetivoLower.contains("mindfulness") || objetivoLower.contains("zen") ||
                       objetivoLower.contains("relajación") || objetivoLower.contains("relajar") ||
                       objetivoLower.contains("estrés") && objetivoLower.contains("reducir") ||
                       objetivoLower.contains("ansiedad") || objetivoLower.contains("calma") ||
                       objetivoLower.contains("postura") || objetivoLower.contains("alineación") ||
                       objetivoLower.contains("equilibrio") || objetivoLower.contains("balance") ||
                       objetivoLower.contains("core") && objetivoLower.contains("estabilidad") ||
                       objetivoLower.contains("respiración") && objetivoLower.contains("consciente") ||
                       objetivoLower.contains("vinyasa") || objetivoLower.contains("hatha") ||
                       objetivoLower.contains("ashtanga") || objetivoLower.contains("yin") ||
                       objetivoLower.contains("kundalini") || objetivoLower.contains("bikram") ||
                       objetivoLower.contains("mente") && objetivoLower.contains("cuerpo") ||
                       objetivoLower.contains("holístico") || objetivoLower.contains("bienestar")) {
                especialidadRecomendada = "Yoga";

            // ═══════════════════════════════════════════════════════════════
            // 🤸 ENTRENAMIENTO FUNCIONAL
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("funcional") || objetivoLower.contains("functional") ||
                       objetivoLower.contains("movimiento") && (objetivoLower.contains("natural") || objetivoLower.contains("diario")) ||
                       objetivoLower.contains("agilidad") || objetivoLower.contains("ágil") ||
                       objetivoLower.contains("coordinación") || objetivoLower.contains("coordinar") ||
                       objetivoLower.contains("vida diaria") || objetivoLower.contains("cotidiano") ||
                       objetivoLower.contains("completo") || objetivoLower.contains("integral") ||
                       objetivoLower.contains("cuerpo completo") || objetivoLower.contains("full body") ||
                       objetivoLower.contains("trx") || objetivoLower.contains("suspension") ||
                       objetivoLower.contains("kettlebell") || objetivoLower.contains("pesas rusas") ||
                       objetivoLower.contains("battle rope") || objetivoLower.contains("cuerdas") ||
                       objetivoLower.contains("sandbag") || objetivoLower.contains("saco") ||
                       objetivoLower.contains("bosu") || objetivoLower.contains("fitball") ||
                       objetivoLower.contains("multiarticular") || objetivoLower.contains("compuesto") ||
                       objetivoLower.contains("animal flow") || objetivoLower.contains("primal") ||
                       objetivoLower.contains("calistenia") || objetivoLower.contains("peso corporal") ||
                       objetivoLower.contains("hiit") && objetivoLower.contains("funcional")) {
                especialidadRecomendada = "Entrenamiento Funcional";

            // ═══════════════════════════════════════════════════════════════
            // 🔥 CROSSFIT
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("crossfit") || objetivoLower.contains("cross fit") ||
                       objetivoLower.contains("wod") || objetivoLower.contains("workout of the day") ||
                       objetivoLower.contains("amrap") || objetivoLower.contains("emom") ||
                       objetivoLower.contains("tabata") || objetivoLower.contains("intervalo") ||
                       objetivoLower.contains("hiit") || objetivoLower.contains("alta intensidad") ||
                       objetivoLower.contains("box") && objetivoLower.contains("jump") ||
                       objetivoLower.contains("burpees") || objetivoLower.contains("muscle up") ||
                       objetivoLower.contains("wallball") || objetivoLower.contains("clean") ||
                       objetivoLower.contains("snatch") || objetivoLower.contains("thruster") ||
                       objetivoLower.contains("rx") || objetivoLower.contains("scaled") ||
                       objetivoLower.contains("metcon") || objetivoLower.contains("gymnastic") ||
                       objetivoLower.contains("kipping") || objetivoLower.contains("butterfly") ||
                       objetivoLower.contains("completo") && objetivoLower.contains("intenso") ||
                       objetivoLower.contains("comunidad") && objetivoLower.contains("fitness") ||
                       objetivoLower.contains("competitivo") || objetivoLower.contains("desafiante")) {
                especialidadRecomendada = "CrossFit";

            // ═══════════════════════════════════════════════════════════════
            // 🥊 BOXEO / ARTES MARCIALES
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("boxeo") || objetivoLower.contains("box") ||
                       objetivoLower.contains("artes marciales") || objetivoLower.contains("marcial") ||
                       objetivoLower.contains("mma") || objetivoLower.contains("mixtas") ||
                       objetivoLower.contains("kickboxing") || objetivoLower.contains("muay thai") ||
                       objetivoLower.contains("karate") || objetivoLower.contains("taekwondo") ||
                       objetivoLower.contains("jiu jitsu") || objetivoLower.contains("judo") ||
                       objetivoLower.contains("lucha") || objetivoLower.contains("combate") ||
                       objetivoLower.contains("defensa") && objetivoLower.contains("personal") ||
                       objetivoLower.contains("striking") || objetivoLower.contains("grappling") ||
                       objetivoLower.contains("saco") && objetivoLower.contains("golpear") ||
                       objetivoLower.contains("sparring") || objetivoLower.contains("ring") ||
                       objetivoLower.contains("pelea") || objetivoLower.contains("fighter") ||
                       objetivoLower.contains("reflexes") || objetivoLower.contains("reflejos")) {
                especialidadRecomendada = "Artes Marciales";

            // ═══════════════════════════════════════════════════════════════
            // 🏊 NATACIÓN
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("natación") || objetivoLower.contains("nadar") ||
                       objetivoLower.contains("swimming") || objetivoLower.contains("piscina") ||
                       objetivoLower.contains("crol") || objetivoLower.contains("mariposa") ||
                       objetivoLower.contains("espalda") && objetivoLower.contains("estilo") ||
                       objetivoLower.contains("pecho") && objetivoLower.contains("estilo") ||
                       objetivoLower.contains("acuático") || objetivoLower.contains("agua") ||
                       objetivoLower.contains("hidrogimnasia") || objetivoLower.contains("aquagym") ||
                       objetivoLower.contains("sincronizada") || objetivoLower.contains("waterpolo")) {
                especialidadRecomendada = "Natación";

            // ═══════════════════════════════════════════════════════════════
            // 🏃 RUNNING ESPECÍFICO
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("running") || objetivoLower.contains("runner") ||
                       objetivoLower.contains("correr") && (objetivoLower.contains("mejor") || objetivoLower.contains("tiempo")) ||
                       objetivoLower.contains("maratón") || objetivoLower.contains("medio maratón") ||
                       objetivoLower.contains("10k") || objetivoLower.contains("21k") ||
                       objetivoLower.contains("42k") || objetivoLower.contains("ultra") ||
                       objetivoLower.contains("trail running") || objetivoLower.contains("montaña") ||
                       objetivoLower.contains("velocidad") || objetivoLower.contains("sprint") ||
                       objetivoLower.contains("intervalos") && objetivoLower.contains("carrera") ||
                       objetivoLower.contains("pace") || objetivoLower.contains("ritmo") ||
                       objetivoLower.contains("fondo") || objetivoLower.contains("distancia") ||
                       objetivoLower.contains("trote") || objetivoLower.contains("jogging")) {
                especialidadRecomendada = "Running";

            // ═══════════════════════════════════════════════════════════════
            // 🚴 CICLISMO
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("ciclismo") || objetivoLower.contains("cycling") ||
                       objetivoLower.contains("bicicleta") || objetivoLower.contains("bici") ||
                       objetivoLower.contains("mtb") || objetivoLower.contains("mountain bike") ||
                       objetivoLower.contains("ruta") && objetivoLower.contains("ciclismo") ||
                       objetivoLower.contains("spinning") || objetivoLower.contains("indoor") ||
                       objetivoLower.contains("rodillo") || objetivoLower.contains("pedalear") ||
                       objetivoLower.contains("watts") || objetivoLower.contains("ftp") ||
                       objetivoLower.contains("cadencia") || objetivoLower.contains("rpm")) {
                especialidadRecomendada = "Ciclismo";

            // ═══════════════════════════════════════════════════════════════
            // 🏋️ POWERLIFTING ESPECÍFICO
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("powerlifting") || objetivoLower.contains("power lifting") ||
                       objetivoLower.contains("sentadilla") && objetivoLower.contains("máximo") ||
                       objetivoLower.contains("press banca") || objetivoLower.contains("bench press") ||
                       objetivoLower.contains("peso muerto") || objetivoLower.contains("deadlift") ||
                       objetivoLower.contains("squat") && objetivoLower.contains("heavy") ||
                       objetivoLower.contains("competencia") && objetivoLower.contains("fuerza") ||
                       objetivoLower.contains("ipf") || objetivoLower.contains("usapl") ||
                       objetivoLower.contains("wilks") || objetivoLower.contains("dots")) {
                especialidadRecomendada = "Powerlifting";

            // ═══════════════════════════════════════════════════════════════
            // 🏃‍♂️ CALISTENIA
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("calistenia") || objetivoLower.contains("calisthenics") ||
                       objetivoLower.contains("peso corporal") || objetivoLower.contains("bodyweight") ||
                       objetivoLower.contains("street workout") || objetivoLower.contains("barras") ||
                       objetivoLower.contains("paralelas") || objetivoLower.contains("dominadas") ||
                       objetivoLower.contains("pull up") || objetivoLower.contains("push up") ||
                       objetivoLower.contains("muscle up") || objetivoLower.contains("front lever") ||
                       objetivoLower.contains("back lever") || objetivoLower.contains("planche") ||
                       objetivoLower.contains("handstand") || objetivoLower.contains("pino") ||
                       objetivoLower.contains("freestyle") || objetivoLower.contains("dinámico")) {
                especialidadRecomendada = "Calistenia";

            // ═══════════════════════════════════════════════════════════════
            // 🏥 REHABILITACIÓN / TERAPÉUTICO
            // ═══════════════════════════════════════════════════════════════
            } else if (objetivoLower.contains("rehabilitación") || objetivoLower.contains("rehabilitar") ||
                       objetivoLower.contains("lesión") || objetivoLower.contains("lesionado") ||
                       objetivoLower.contains("recuperación") || objetivoLower.contains("recuperar") ||
                       objetivoLower.contains("terapia") || objetivoLower.contains("terapéutico") ||
                       objetivoLower.contains("fisioterapia") || objetivoLower.contains("kinesiología") ||
                       objetivoLower.contains("dolor") && (objetivoLower.contains("espalda") || objetivoLower.contains("rodilla")) ||
                       objetivoLower.contains("cirugía") || objetivoLower.contains("operación") ||
                       objetivoLower.contains("artritis") || objetivoLower.contains("artrosis") ||
                       objetivoLower.contains("hernía") || objetivoLower.contains("escoliosis") ||
                       objetivoLower.contains("cervical") || objetivoLower.contains("lumbar") ||
                       objetivoLower.contains("tendinitis") || objetivoLower.contains("bursitis") ||
                       objetivoLower.contains("adulto mayor") || objetivoLower.contains("tercera edad") ||
                       objetivoLower.contains("adaptado") || objetivoLower.contains("bajo impacto")) {
                especialidadRecomendada = "Rehabilitación";
            }

            // NUEVA LÓGICA: Solo recomendar si es la primera visita (sin query params en la URL)
            // Si el usuario hace búsqueda con "Todas", NO aplicar recomendación
            boolean primeraVisita = (nombre == null && especialidad == null && minPuntuacion == null &&
                                     maxPuntuacion == null && minExperiencia == null && minPrecio == null &&
                                     maxPrecio == null && certificaciones == null);

            if (primeraVisita && especialidadRecomendada != null) {
                // Primera visita → aplicar recomendación automática
                especialidad = especialidadRecomendada;
                mostrarRecomendacion = true;
                System.out.println("╔════════════════════════════════════════════════════════╗");
                System.out.println("║  🎯 RECOMENDACIÓN AUTOMÁTICA APLICADA");
                System.out.println("╠════════════════════════════════════════════════════════╣");
                System.out.println("  👤 Cliente ID: " + clienteId);
                System.out.println("  📋 Objetivo: " + objetivoCliente);
                System.out.println("  ✅ Especialidad recomendada: " + especialidadRecomendada);
                System.out.println("  💡 Cliente puede cambiar a 'Todas' si desea");
                System.out.println("╚════════════════════════════════════════════════════════╝");
            } else if (especialidadRecomendada != null) {
                // Visita con parámetros → solo mostrar info de recomendación, NO forzar
                mostrarRecomendacion = true;
                System.out.println("╔════════════════════════════════════════════════════════╗");
                System.out.println("║  💡 RECOMENDACIÓN DISPONIBLE (NO APLICADA)");
                System.out.println("╠════════════════════════════════════════════════════════╣");
                System.out.println("  👤 Cliente ID: " + clienteId);
                System.out.println("  📋 Objetivo: " + objetivoCliente);
                System.out.println("  💡 Recomendación: " + especialidadRecomendada);
                System.out.println("  ✅ Cliente tiene control total de filtros");
                System.out.println("╚════════════════════════════════════════════════════════╝");
            }
        }

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
        model.addAttribute("objetivoCliente", objetivoCliente);
        model.addAttribute("especialidadRecomendada", especialidadRecomendada);
        model.addAttribute("mostrarRecomendacion", mostrarRecomendacion);
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
            if (entrenador != null) {
                String nombreCompleto = entrenador.getNombre();
                if (entrenador.getApellido() != null && !entrenador.getApellido().isBlank()) {
                    nombreCompleto += " " + entrenador.getApellido();
                }
                model.addAttribute("nombreEntrenador", nombreCompleto);
                model.addAttribute("fotoEntrenador", entrenador.getFotoPerfilUrl() != null ? entrenador.getFotoPerfilUrl() : "/img/fotoPerfil.png");
                model.addAttribute("telefonoEntrenador", entrenador.getTelefono());
                model.addAttribute("emailEntrenador", entrenador.getEmail());
            } else {
                model.addAttribute("nombreEntrenador", null);
                model.addAttribute("fotoEntrenador", "/img/fotoPerfil.png");
                model.addAttribute("telefonoEntrenador", null);
                model.addAttribute("emailEntrenador", null);
            }
        }

        // Añadir información de rutina activa y progreso si existe
        RutinaDTO rutinaActiva = rutinaService.getRutinaActivaCliente(clienteId);
        if (rutinaActiva != null) {
            // Mostrar la rutina activa sin importar si tiene entrenador o fue adoptada
            model.addAttribute("tieneRutinaActiva", true);
            model.addAttribute("rutina", rutinaActiva);

            // Calcular progreso y día actual para todas las rutinas activas
            DiaDTO diaActual = diaService.getDiaActual(clienteId);
            long porcentajeCompletado = diaService.calcularProgresoRutina(clienteId);
            model.addAttribute("diaActual", diaActual);
            model.addAttribute("porcentajeCompletado", porcentajeCompletado);
         } else {
             model.addAttribute("tieneRutinaActiva", false);
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
    public String cancelarSuscripcionCliente(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            suscripcionService.cancelarSuscripcion(id);
            redirectAttributes.addFlashAttribute("success", "Suscripción cancelada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cancelar la suscripción: " + e.getMessage());
        }
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
        
        // Obtener el diagnóstico actual para prellenar campos y facilitar la actualización
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        
        // Crear un nuevo diagnóstico vacío para el formulario
        DiagnosticoDTO diagnosticoFormulario = new DiagnosticoDTO();
        
        // ✅ Prellenar TODOS los campos del diagnóstico anterior
        // Esto facilita al usuario: solo actualiza lo que cambió (peso, medidas, etc.)
        if (diagnosticoActual != null) {
            diagnosticoFormulario.setPeso(diagnosticoActual.getPeso());
            diagnosticoFormulario.setEstatura(diagnosticoActual.getEstatura());
            diagnosticoFormulario.setNivelExperiencia(diagnosticoActual.getNivelExperiencia());
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
            info.put("verified", entrenador.getVerified() != null ? entrenador.getVerified() : false);
            info.put("sexo", entrenador.getSexo()); // 👥 Agregar género del entrenador

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
