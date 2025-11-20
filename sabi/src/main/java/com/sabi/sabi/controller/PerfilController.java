package com.sabi.sabi.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.security.CustomUserDetails;
import com.sabi.sabi.service.UsuarioService;

import lombok.RequiredArgsConstructor;

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
            model.addAttribute("aniosExperiencia", entrenador.getAniosExperiencia());

            // Pasar especialidades como lista
            String especialidadesStr = entrenador.getEspecialidades();
            if (especialidadesStr != null && !especialidadesStr.isEmpty()) {
                java.util.List<String> listaEspecialidades = java.util.Arrays.asList(especialidadesStr.split(","));
                // Trim cada especialidad
                listaEspecialidades = listaEspecialidades.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList());
                model.addAttribute("especialidades", listaEspecialidades);
            } else {
                model.addAttribute("especialidades", java.util.Collections.emptyList());
            }

            // Pasar certificaciones si existen
            String certificaciones = entrenador.getCertificaciones();
            if (certificaciones != null && !certificaciones.isEmpty()) {
                // Dividir las certificaciones por coma si hay varias
                java.util.List<String> listaCertificaciones = java.util.Arrays.asList(certificaciones.split(","));
                model.addAttribute("certificaciones", listaCertificaciones);
            }
        }

        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String objetivo,
            @RequestParam(required = false) String especialidades,
            @RequestParam(required = false) MultipartFile fotoPerfil,
            Model model) {

        try {
            Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());

            // Actualizar descripción
            if (descripcion != null && !descripcion.isEmpty()) {
                usuario.setDescripcion(descripcion);
            }

            // Variable para saber si se actualizó la foto
            boolean fotoActualizada = false;

            // Subir foto de perfil si se proporcionó
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                String fotoUrl = guardarFotoPerfil(fotoPerfil);
                usuario.setFotoPerfilUrl(fotoUrl);
                fotoActualizada = true;
            }

            // Actualizar campos específicos según el rol
            if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;
                if (objetivo != null && !objetivo.isEmpty()) {
                    cliente.setObjetivo(objetivo);
                }
            } else if (usuario instanceof Entrenador) {
                Entrenador entrenador = (Entrenador) usuario;
                // Actualizar especialidades
                if (especialidades != null && !especialidades.isEmpty()) {
                    entrenador.setEspecialidades(especialidades);
                    // Mantener compatibilidad con especialidad singular (usar la primera)
                    String primeraEspecialidad = especialidades.split(",")[0].trim();
                    entrenador.setEspecialidad(primeraEspecialidad);
                }
            }
            // Nota: Los años de experiencia y certificaciones del entrenador
            // NO son editables desde el perfil. Solo se establecen durante el registro inicial.

            // Guardar cambios en la base de datos
            usuarioService.actualizarUsuario(usuario);

            // Si se actualizó la foto, refrescar el contexto de seguridad
            if (fotoActualizada) {
                actualizarContextoSeguridad(usuario);
            }

            model.addAttribute("success", "Perfil actualizado exitosamente");
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar perfil: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    /**
     * Actualiza el contexto de seguridad de Spring con los datos más recientes del usuario
     * Esto es necesario para que los cambios (como la foto de perfil) se reflejen inmediatamente
     * en el navbar sin necesidad de cerrar sesión para que sirva ESTA LOCURAAAAAAA
     */
    private void actualizarContextoSeguridad(Usuario usuarioActualizado) {
        // Obtener la autenticación actual
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        // Crear un nuevo CustomUserDetails con los datos actualizados
        CustomUserDetails updatedUserDetails = new CustomUserDetails(usuarioActualizado);

        // Crear nueva autenticación con los datos actualizados
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
            updatedUserDetails,
            currentAuth.getCredentials(),
            updatedUserDetails.getAuthorities()
        );

        // Actualizar el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    private String guardarFotoPerfil(MultipartFile file) throws IOException {
        // Obtener nombre y extensión del archivo original
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Nombre de archivo inválido");
        }

        // Extraer la extensión
        String extension = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalFilename.substring(lastDot).toLowerCase();
        } else {
            // Si no tiene extensión, intentar determinarla del content type
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

        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new IOException("El archivo está vacío");
        }

        // Validar tamaño máximo (10MB para ser más permisivo)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IOException("El archivo es demasiado grande (máximo 10MB)");
        }

        // Crear directorio si no existe
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generar nombre único manteniendo la extensión original
        String fileName = UUID.randomUUID() + extension;
        Path filePath = uploadDir.resolve(fileName);

        // Guardar el archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retornar la URL relativa
        return "/uploads/perfiles/" + fileName;
    }
}

