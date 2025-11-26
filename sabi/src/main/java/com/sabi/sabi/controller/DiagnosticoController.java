
package com.sabi.sabi.controller;

import com.sabi.sabi.dto.DiagnosticoDTO;
import com.sabi.sabi.service.DiagnosticoService;
import com.sabi.sabi.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/diagnostico")
public class DiagnosticoController {

    @Autowired
    private DiagnosticoService diagnosticoService;
    @Autowired
    private ClienteService clienteService;

    @Value("${upload.diagnosticos.path:uploads/diagnosticos}")
    private String uploadPath;

    @GetMapping("/cliente")
    public String diagnosticoCliente(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam(required = false) String action,
                                     Model model) {
        Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        List<DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
        
        model.addAttribute("diagnosticoActual", diagnosticoActual);
        model.addAttribute("historial", historial);
        
        // ✅ Prellenar formulario con datos del diagnóstico anterior para facilitar
        DiagnosticoDTO diagnosticoFormulario = new DiagnosticoDTO();

        if (diagnosticoActual != null && !"actualizar".equals(action)) {
            // Para nuevo diagnóstico: prellenar con datos anteriores
            // Usuario solo actualiza lo que cambió (peso, medidas, etc.)
            diagnosticoFormulario.setPeso(diagnosticoActual.getPeso());
            diagnosticoFormulario.setEstatura(diagnosticoActual.getEstatura());
            diagnosticoFormulario.setNivelExperiencia(diagnosticoActual.getNivelExperiencia());
            diagnosticoFormulario.setDisponibilidadTiempo(diagnosticoActual.getDisponibilidadTiempo());
            diagnosticoFormulario.setAccesoRecursos(diagnosticoActual.getAccesoRecursos());
            diagnosticoFormulario.setLesiones(diagnosticoActual.getLesiones());
            diagnosticoFormulario.setCondicionesMedicas(diagnosticoActual.getCondicionesMedicas());
            diagnosticoFormulario.setHorasSueno(diagnosticoActual.getHorasSueno());
            diagnosticoFormulario.setHabitosAlimenticios(diagnosticoActual.getHabitosAlimenticios());
        } else if ("actualizar".equals(action) && diagnosticoActual != null) {
            // Solo si explícitamente se pide actualizar, cargar el actual completo
            diagnosticoFormulario = diagnosticoActual;
        }

        model.addAttribute("diagnostico", diagnosticoFormulario);
        model.addAttribute("esNuevo", !"actualizar".equals(action));

        return "cliente/diagnostico-form";
    }

    @PostMapping("/crear")
    public String crearOActualizarDiagnostico(@AuthenticationPrincipal UserDetails userDetails,
                                   @ModelAttribute DiagnosticoDTO diagnosticoDTO,
                                   @RequestParam(required = false) MultipartFile fotoFrontal,
                                   @RequestParam(required = false) MultipartFile fotoLateral,
                                   @RequestParam(required = false) MultipartFile fotoTrasera,
                                   Model model) {
        System.out.println("\n");
        System.out.println("████████████████████████████████████████");
        System.out.println("🚀 POST /diagnostico/crear EJECUTADO");
        System.out.println("████████████████████████████████████████");

        try {
            Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();

            System.out.println("   Cliente ID: " + clienteId);
            System.out.println("   DTO ID Diagnostico recibido: " + diagnosticoDTO.getIdDiagnostico());
            System.out.println("   Peso: " + diagnosticoDTO.getPeso());
            System.out.println("   Estatura: " + diagnosticoDTO.getEstatura());

            // ⚠️ CRÍTICO: FORZAR QUE SIEMPRE SEA NUEVO DIAGNÓSTICO
            // Eliminar el ID para forzar creación de nuevo registro
            diagnosticoDTO.setIdDiagnostico(null);
            diagnosticoDTO.setIdCliente(clienteId);
            diagnosticoDTO.setFecha(java.time.LocalDate.now());
            diagnosticoDTO.setEstado(true);

            System.out.println("   🆕 FORZANDO CREACIÓN DE NUEVO DIAGNÓSTICO");
            System.out.println("   ID establecido en: null");
            System.out.println("   Estado: true");
            System.out.println("   Fecha: " + diagnosticoDTO.getFecha());
            System.out.println("   🎯 Objetivo: " + (diagnosticoDTO.getObjetivo() != null ? diagnosticoDTO.getObjetivo() : "SIN OBJETIVO"));

            boolean esCreacion = true;

            if (esCreacion) {
                diagnosticoDTO.setFecha(java.time.LocalDate.now());
                
                // ✅ Las fotos ahora son OPCIONALES (no obligatorias)
                // El usuario puede crear diagnóstico con o sin fotos
            }

            // Gestionar las fotos (para creación o actualización)
            // Si es actualización, conservar las URLs existentes si no se cargan nuevas fotos
            if (!esCreacion) {
                DiagnosticoDTO diagnosticoExistente = diagnosticoService.getDiagnosticoById(diagnosticoDTO.getIdDiagnostico());
                if (diagnosticoExistente != null) {
                    // Conservar URLs existentes si no se suben nuevas fotos
                    if (fotoFrontal == null || fotoFrontal.isEmpty()) {
                        diagnosticoDTO.setFotoFrontalUrl(diagnosticoExistente.getFotoFrontalUrl());
                    }
                    if (fotoLateral == null || fotoLateral.isEmpty()) {
                        diagnosticoDTO.setFotoLateralUrl(diagnosticoExistente.getFotoLateralUrl());
                    }
                    if (fotoTrasera == null || fotoTrasera.isEmpty()) {
                        diagnosticoDTO.setFotoTraseraUrl(diagnosticoExistente.getFotoTraseraUrl());
                    }
                }
            }
            
            // Procesar nuevas fotos si se subieron
            if (fotoFrontal != null && !fotoFrontal.isEmpty()) {
                String urlFrontal = guardarFotoDiagnostico(fotoFrontal, "frontal");
                diagnosticoDTO.setFotoFrontalUrl(urlFrontal);
            }
            
            if (fotoLateral != null && !fotoLateral.isEmpty()) {
                String urlLateral = guardarFotoDiagnostico(fotoLateral, "lateral");
                diagnosticoDTO.setFotoLateralUrl(urlLateral);
            }
            
            if (fotoTrasera != null && !fotoTrasera.isEmpty()) {
                String urlTrasera = guardarFotoDiagnostico(fotoTrasera, "trasera");
                diagnosticoDTO.setFotoTraseraUrl(urlTrasera);
            }

            // Guardar el diagnóstico (create maneja tanto creación como actualización)
            DiagnosticoDTO resultado = diagnosticoService.createDiagnostico(diagnosticoDTO);

            System.out.println("   ✅✅✅ DIAGNÓSTICO GUARDADO EXITOSAMENTE");
            System.out.println("   ID del diagnóstico guardado: " + resultado.getIdDiagnostico());
            System.out.println("   Estado: " + resultado.getEstado());

            // 🎯 IMPORTANTE: El objetivo se guarda en DOS lugares
            // 1. En el PERFIL del cliente (para mostrar en dashboard)
            // 2. En el DIAGNÓSTICO (para historial - recordar meta de ese momento)
            String objetivoFormulario = diagnosticoDTO.getObjetivo();
            if (objetivoFormulario != null && !objetivoFormulario.isEmpty()) {
                com.sabi.sabi.dto.ClienteDTO clienteDTO = clienteService.getClienteById(clienteId);
                clienteDTO.setObjetivo(objetivoFormulario);
                clienteService.updateCliente(clienteId, clienteDTO);
                System.out.println("   🎯 Objetivo guardado en:");
                System.out.println("      ✅ PERFIL del cliente (se mostrará en dashboard)");
                System.out.println("      ✅ DIAGNÓSTICO (historial - meta de ese momento)");
                System.out.println("      📝 Texto: \"" + objetivoFormulario + "\"");
            }

            System.out.println("   ➡️ Redirigiendo a dashboard");
            System.out.println("████████████████████████████████████████\n");

            // Agregar parámetro de timestamp para evitar caché del navegador
            long timestamp = System.currentTimeMillis();
            model.addAttribute("success", esCreacion ? "Diagnóstico creado correctamente" : "Diagnóstico actualizado correctamente");
            return "redirect:/cliente/dashboard?refresh=" + timestamp;

        } catch (IOException e) {
            model.addAttribute("error", "Error al guardar las fotos: " + e.getMessage());
            model.addAttribute("diagnostico", diagnosticoDTO);
            return "cliente/diagnostico-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el diagnóstico: " + e.getMessage());
            model.addAttribute("diagnostico", diagnosticoDTO);
            return "cliente/diagnostico-form";
        }
    }

    private String guardarFotoDiagnostico(MultipartFile file, String tipo) throws IOException {
        // Obtener nombre y extension del archivo original
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Nombre de archivo invalido");
        }

        // Extraer la extension
        String extension = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalFilename.substring(lastDot).toLowerCase();
        } else {
            // Si no tiene extension, intentar determinarla del content type
            String contentType = file.getContentType();
            if (contentType != null) {
                if (contentType.contains("jpeg") || contentType.contains("jpg")) {
                    extension = ".jpg";
                } else if (contentType.contains("png")) {
                    extension = ".png";
                } else if (contentType.contains("gif")) {
                    extension = ".gif";
                } else if (contentType.contains("webp")) {
                    extension = ".webp";
                } else if (contentType.contains("bmp")) {
                    extension = ".bmp";
                }
            }
        }

        // Validar que el archivo no este vacio
        if (file.isEmpty()) {
            throw new IOException("El archivo esta vacio");
        }

        // Validar tamano maximo (10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IOException("El archivo es demasiado grande (maximo 10MB)");
        }

        // Crear directorio si no existe
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generar nombre unico: UUID_tipo_extension
        String fileName = UUID.randomUUID() + "_" + tipo + extension;
        Path filePath = uploadDir.resolve(fileName);

        // Guardar el archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retornar la URL relativa
        return "/uploads/diagnosticos/" + fileName;
    }

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public DiagnosticoDTO detalleDiagnostico(@PathVariable Long id) {
        return diagnosticoService.getDiagnosticoById(id);
    }

    // ⚠️ ENDPOINT TEMPORAL PARA CREAR DATOS DE PRUEBA
    @GetMapping("/crear-datos-prueba")
    public String crearDatosPrueba(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("\n🔧 CREANDO DIAGNÓSTICOS DE PRUEBA...");

        Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
        System.out.println("Cliente ID: " + clienteId);

        // Diagnóstico 1 (más antiguo - hace 1 mes)
        DiagnosticoDTO diagnostico1 = new DiagnosticoDTO();
        diagnostico1.setIdCliente(clienteId);
        diagnostico1.setFecha(java.time.LocalDate.now().minusMonths(1));
        diagnostico1.setPeso(68.5);
        diagnostico1.setEstatura(175.0);
        diagnostico1.setNivelExperiencia(com.sabi.sabi.entity.enums.NivelExperiencia.INTERMEDIO);
        diagnostico1.setDisponibilidadTiempo("3 veces por semana");
        diagnostico1.setAccesoRecursos("Gimnasio");
        diagnostico1.setObjetivo("Bajar de peso");
        diagnostico1.setPorcentajeGrasaCorporal(22.5);
        diagnostico1.setHorasSueno(7L);
        diagnostico1.setHabitosAlimenticios("Saludable");
        diagnostico1.setFotoFrontalUrl("/img/fotoPerfil.png");
        diagnostico1.setFotoLateralUrl("/img/fotoPerfil.png");
        diagnostico1.setFotoTraseraUrl("/img/fotoPerfil.png");
        diagnostico1.setEstado(true);

        DiagnosticoDTO resultado1 = diagnosticoService.createDiagnostico(diagnostico1);
        System.out.println("✅ Diagnóstico 1 creado con ID: " + resultado1.getIdDiagnostico());

        // Diagnóstico 2 (más reciente - hoy)
        DiagnosticoDTO diagnostico2 = new DiagnosticoDTO();
        diagnostico2.setIdCliente(clienteId);
        diagnostico2.setFecha(java.time.LocalDate.now());
        diagnostico2.setPeso(71.0);
        diagnostico2.setEstatura(175.0);
        diagnostico2.setNivelExperiencia(com.sabi.sabi.entity.enums.NivelExperiencia.INTERMEDIO);
        diagnostico2.setDisponibilidadTiempo("4 veces por semana");
        diagnostico2.setAccesoRecursos("Gimnasio");
        diagnostico2.setObjetivo("Ganar masa muscular");
        diagnostico2.setPorcentajeGrasaCorporal(20.5);
        diagnostico2.setHorasSueno(8L);
        diagnostico2.setHabitosAlimenticios("Muy saludable");
        diagnostico2.setFotoFrontalUrl("/img/fotoPerfil.png");
        diagnostico2.setFotoLateralUrl("/img/fotoPerfil.png");
        diagnostico2.setFotoTraseraUrl("/img/fotoPerfil.png");
        diagnostico2.setEstado(true);

        DiagnosticoDTO resultado2 = diagnosticoService.createDiagnostico(diagnostico2);
        System.out.println("✅ Diagnóstico 2 creado con ID: " + resultado2.getIdDiagnostico());

        System.out.println("🎉 ¡DATOS DE PRUEBA CREADOS! Ahora verás la comparativa en el dashboard.\n");

        return "redirect:/cliente/dashboard";
    }

    @GetMapping("/historial")
    public String historialDiagnosticos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
        List<DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);

        // Debug logging
        System.out.println("=== DEBUG HISTORIAL ===");
        System.out.println("Cliente ID: " + clienteId);
        System.out.println("Total diagnósticos: " + (historial != null ? historial.size() : 0));
        if (historial != null && !historial.isEmpty()) {
            System.out.println("Primer diagnóstico (más reciente):");
            System.out.println("  - Fecha: " + historial.get(0).getFecha());
            System.out.println("  - Peso: " + historial.get(0).getPeso());
            if (historial.size() >= 2) {
                System.out.println("Segundo diagnóstico:");
                System.out.println("  - Fecha: " + historial.get(1).getFecha());
                System.out.println("  - Peso: " + historial.get(1).getPeso());
            }
        }

        // Agregar variables para la comparativa
        if (historial != null && historial.size() >= 2) {
            model.addAttribute("diagnosticoActual", historial.get(0));
            model.addAttribute("diagnosticoAnterior", historial.get(1));
            model.addAttribute("tieneComparativa", true);
            System.out.println("Comparativa ACTIVADA");
        } else {
            model.addAttribute("tieneComparativa", false);
            System.out.println("Comparativa DESACTIVADA (menos de 2 diagnósticos)");
        }
        System.out.println("======================");

        model.addAttribute("historial", historial);
        return "cliente/diagnostico-historial";
    }
}

