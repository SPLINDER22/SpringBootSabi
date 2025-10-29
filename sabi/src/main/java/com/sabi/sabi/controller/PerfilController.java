package com.sabi.sabi.controller;

import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;

    @Value("${upload.path:uploads/perfiles}")
    private String uploadPath;

    @GetMapping
    public String verPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        model.addAttribute("usuario", usuario);

        if (usuario instanceof Cliente) {
            Cliente cliente = (Cliente) usuario;
            model.addAttribute("objetivo", cliente.getObjetivo());
        } else if (usuario instanceof Entrenador) {
            Entrenador entrenador = (Entrenador) usuario;
            model.addAttribute("especialidad", entrenador.getEspecialidad());
        }

        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String objetivo,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) MultipartFile fotoPerfil,
            Model model) {

        try {
            Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());

            // Actualizar descripción
            if (descripcion != null && !descripcion.isEmpty()) {
                usuario.setDescripcion(descripcion);
            }

            // Subir foto de perfil si se proporcionó
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                String fotoUrl = guardarFotoPerfil(fotoPerfil);
                usuario.setFotoPerfilUrl(fotoUrl);
            }

            // Actualizar campos específicos según el rol
            if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;
                if (objetivo != null && !objetivo.isEmpty()) {
                    cliente.setObjetivo(objetivo);
                }
            } else if (usuario instanceof Entrenador) {
                Entrenador entrenador = (Entrenador) usuario;
                if (especialidad != null && !especialidad.isEmpty()) {
                    entrenador.setEspecialidad(especialidad);
                }
            }

            // Guardar cambios
            usuarioService.actualizarUsuario(usuario);

            model.addAttribute("success", "Perfil actualizado exitosamente");
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar perfil: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    private String guardarFotoPerfil(MultipartFile file) throws IOException {
        // Validar que sea una imagen
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("El archivo debe ser una imagen (JPEG, PNG, GIF, etc.)");
        }

        // Lista de extensiones válidas
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Nombre de archivo inválido");
        }

        String extension = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalFilename.substring(lastDot).toLowerCase();
        }

        // Validar extensión (aceptar todos los formatos de imagen comunes)
        String[] extensionesValidas = {".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".svg", ".jfif"};
        boolean extensionValida = false;
        for (String ext : extensionesValidas) {
            if (extension.equals(ext)) {
                extensionValida = true;
                break;
            }
        }

        if (!extensionValida && !extension.isEmpty()) {
            throw new IOException("Formato de imagen no soportado. Usa: JPG, JPEG, PNG, GIF, WEBP, BMP, SVG o JFIF");
        }

        // Crear directorio si no existe
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generar nombre único para el archivo manteniendo la extensión original
        String fileName = UUID.randomUUID() + extension;
        Path filePath = uploadDir.resolve(fileName);

        // Guardar el archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retornar la URL relativa
        return "/uploads/perfiles/" + fileName;
    }
}

