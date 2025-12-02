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
            model.addAttribute("entrenador", entrenador);
            model.addAttribute("esVerificado", entrenador.isVerified());

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

    @PostMapping("/certificaciones/subir")
    public String subirCertificaciones(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("certificaciones") MultipartFile[] certificaciones,
            Model model) {

        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  📄 Subiendo Certificaciones desde Perfil");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        try {
            Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());

            // Verificar que sea entrenador
            if (!(usuario instanceof Entrenador)) {
                model.addAttribute("error", "Solo los entrenadores pueden subir certificaciones");
                return "redirect:/perfil";
            }

            Entrenador entrenador = (Entrenador) usuario;
            System.out.println("  👤 Entrenador: " + entrenador.getEmail());
            System.out.println("  📁 Archivos recibidos: " + certificaciones.length);

            // Validaciones
            if (certificaciones == null || certificaciones.length == 0) {
                model.addAttribute("error", "Debes seleccionar al menos un archivo PDF");
                return "redirect:/perfil";
            }

            // Crear directorio para certificaciones si no existe
            Path certDir = Paths.get("uploads/certificaciones");
            if (!Files.exists(certDir)) {
                Files.createDirectories(certDir);
                System.out.println("  📁 Directorio de certificaciones creado");
            }

            // Lista para almacenar las rutas de las certificaciones nuevas
            java.util.List<String> nuevasCertificaciones = new java.util.ArrayList<>();

            // Procesar cada archivo
            for (MultipartFile cert : certificaciones) {
                if (!cert.isEmpty()) {
                    // Validar que sea PDF
                    String contentType = cert.getContentType();
                    if (contentType == null || !contentType.equals("application/pdf")) {
                        System.out.println("  ⚠️ Archivo " + cert.getOriginalFilename() + " no es PDF. Saltando...");
                        continue;
                    }

                    // Validar tamaño (5MB máximo)
                    long maxSize = 5 * 1024 * 1024;
                    if (cert.getSize() > maxSize) {
                        System.out.println("  ⚠️ Archivo " + cert.getOriginalFilename() + " demasiado grande. Saltando...");
                        continue;
                    }

                    try {
                        // Generar nombre único
                        String originalFilename = cert.getOriginalFilename();
                        String safeFilename = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_") : "certificacion.pdf";
                        String fileName = "cert_" + entrenador.getId() + "_" + System.currentTimeMillis() + "_" + safeFilename;
                        Path filePath = certDir.resolve(fileName);

                        // Guardar archivo
                        Files.copy(cert.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        // Añadir ruta relativa a la lista
                        String rutaRelativa = "uploads/certificaciones/" + fileName;
                        nuevasCertificaciones.add(rutaRelativa);

                        System.out.println("  ✅ Certificación guardada: " + fileName);
                    } catch (IOException e) {
                        System.err.println("  ❌ Error al guardar " + cert.getOriginalFilename() + ": " + e.getMessage());
                    }
                }
            }

            if (nuevasCertificaciones.isEmpty()) {
                model.addAttribute("error", "No se pudo guardar ninguna certificación. Verifica que sean archivos PDF válidos menores a 5MB");
                return "redirect:/perfil";
            }

            // Obtener certificaciones existentes
            String certificacionesExistentes = entrenador.getCertificaciones();
            java.util.List<String> todasLasCertificaciones = new java.util.ArrayList<>();

            // Añadir las existentes
            if (certificacionesExistentes != null && !certificacionesExistentes.trim().isEmpty()) {
                String[] existentes = certificacionesExistentes.split(",");
                for (String cert : existentes) {
                    String certTrimmed = cert.trim();
                    if (!certTrimmed.isEmpty()) {
                        todasLasCertificaciones.add(certTrimmed);
                    }
                }
                System.out.println("  📋 Certificaciones existentes: " + todasLasCertificaciones.size());
            }

            // Añadir las nuevas
            todasLasCertificaciones.addAll(nuevasCertificaciones);
            System.out.println("  📋 Total de certificaciones ahora: " + todasLasCertificaciones.size());

            // Unir todas las certificaciones con comas
            String todasCertificacionesStr = String.join(",", todasLasCertificaciones);

            // Actualizar entrenador
            entrenador.setCertificaciones(todasCertificacionesStr);
            usuarioService.actualizarUsuario(entrenador);

            System.out.println("  ✅ Certificaciones actualizadas en BD");
            System.out.println("  🎯 El entrenador ahora es CANDIDATO a verificación");

            model.addAttribute("success",
                nuevasCertificaciones.size() == 1
                    ? "Se subió 1 certificación exitosamente. Ahora eres candidato a verificación por SABI."
                    : "Se subieron " + nuevasCertificaciones.size() + " certificaciones exitosamente. Ahora eres candidato a verificación por SABI.");

        } catch (Exception e) {
            System.err.println("  ❌ Error general: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al subir certificaciones: " + e.getMessage());
        }

        return "redirect:/perfil";
    }
}
