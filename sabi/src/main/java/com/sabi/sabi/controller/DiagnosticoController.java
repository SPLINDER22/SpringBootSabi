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
    public String diagnosticoCliente(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        List<DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
        
        model.addAttribute("diagnosticoActual", diagnosticoActual);
        model.addAttribute("historial", historial);
        
        // Pasar el diagnóstico para el formulario - nuevo para creación, existente para actualización
        if (diagnosticoActual != null) {
            model.addAttribute("diagnostico", diagnosticoActual);
        } else {
            model.addAttribute("diagnostico", new DiagnosticoDTO());
        }
        
        return "cliente/diagnostico-form";
    }

    @PostMapping("/crear")
    public String crearOActualizarDiagnostico(@AuthenticationPrincipal UserDetails userDetails,
                                   @ModelAttribute DiagnosticoDTO diagnosticoDTO,
                                   @RequestParam(required = false) MultipartFile fotoFrontal,
                                   @RequestParam(required = false) MultipartFile fotoLateral,
                                   @RequestParam(required = false) MultipartFile fotoTrasera,
                                   Model model) {
        try {
            Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
            diagnosticoDTO.setIdCliente(clienteId);
            
            // Determinar si es creación o actualización
            boolean esCreacion = diagnosticoDTO.getIdDiagnostico() == null;
            
            if (esCreacion) {
                diagnosticoDTO.setFecha(java.time.LocalDate.now());
                
                // Para creación, las 3 fotos son obligatorias
                if (fotoFrontal == null || fotoFrontal.isEmpty() || 
                    fotoLateral == null || fotoLateral.isEmpty() || 
                    fotoTrasera == null || fotoTrasera.isEmpty()) {
                    model.addAttribute("error", "Las 3 fotos (frontal, lateral y trasera) son obligatorias para crear un diagnóstico");
                    model.addAttribute("diagnostico", diagnosticoDTO);
                    return "cliente/diagnostico-form";
                }
            }

            // Gestionar las fotos (para creación o actualización)
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
            diagnosticoService.createDiagnostico(diagnosticoDTO);

            // Si el objetivo fue proporcionado, actualizarlo también en el perfil del cliente
            if (diagnosticoDTO.getObjetivo() != null && !diagnosticoDTO.getObjetivo().isEmpty()) {
                com.sabi.sabi.dto.ClienteDTO clienteDTO = clienteService.getClienteById(clienteId);
                clienteDTO.setObjetivo(diagnosticoDTO.getObjetivo());
                clienteService.updateCliente(clienteId, clienteDTO);
            }

            model.addAttribute("success", esCreacion ? "Diagnóstico creado correctamente" : "Diagnóstico actualizado correctamente");
            return "redirect:/cliente/dashboard";
            
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

    @GetMapping("/historial")
    public String historialDiagnosticos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
        List<DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
        model.addAttribute("historial", historial);
        return "cliente/diagnostico-historial";
    }
}

