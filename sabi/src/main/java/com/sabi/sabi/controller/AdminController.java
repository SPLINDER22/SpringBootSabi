package com.sabi.sabi.controller;

import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador del panel de administración
 * Gestiona usuarios, entrenadores y verificaciones del sistema SABI
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final ClienteRepository clienteRepository;
    private final EmailService emailService;


    /**
     * Dashboard principal del administrador
     * Muestra estadísticas generales del sistema
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🔐 ADMIN DASHBOARD - Cargando Estadísticas                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Obtener todos los usuarios
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        List<Entrenador> todosEntrenadores = entrenadorRepository.findAll();

        // Calcular estadísticas
        long totalUsuarios = todosUsuarios.size();
        long totalClientes = todosUsuarios.stream()
                .filter(u -> u.getRol() == Rol.CLIENTE)
                .count();
        long totalEntrenadores = todosEntrenadores.size();
        long entrenadoresVerificados = todosEntrenadores.stream()
                .filter(Entrenador::isVerified)
                .count();
        long entrenadoresPendientes = totalEntrenadores - entrenadoresVerificados;
        long usuariosActivos = todosUsuarios.stream()
                .filter(Usuario::getEstado)
                .count();
        long usuariosBloqueados = totalUsuarios - usuariosActivos;

        // Log de estadísticas
        System.out.println("\n📊 ESTADÍSTICAS DEL SISTEMA:");
        System.out.println("  ├─ Total Usuarios: " + totalUsuarios);
        System.out.println("  ├─ Total Clientes: " + totalClientes);
        System.out.println("  ├─ Total Entrenadores: " + totalEntrenadores);
        System.out.println("  │   ├─ ✅ Verificados: " + entrenadoresVerificados);
        System.out.println("  │   └─ ⏳ Pendientes: " + entrenadoresPendientes);
        System.out.println("  ├─ 🟢 Usuarios Activos: " + usuariosActivos);
        System.out.println("  └─ 🔴 Usuarios Bloqueados: " + usuariosBloqueados);
        System.out.println();

        // Agregar al modelo
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalEntrenadores", totalEntrenadores);
        model.addAttribute("entrenadoresVerificados", entrenadoresVerificados);
        model.addAttribute("entrenadorespendientes", entrenadoresPendientes);
        model.addAttribute("usuariosActivos", usuariosActivos);
        model.addAttribute("usuariosBloqueados", usuariosBloqueados);

        return "admin/dashboard";
    }

    /**
     * Lista de todos los usuarios del sistema
     * Incluye clientes, entrenadores y administradores
     */
    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  👥 ADMIN - Listado de Usuarios                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        List<Usuario> usuarios = usuarioRepository.findAll();
        System.out.println("📋 Total de usuarios en el sistema: " + usuarios.size());

        // Obtener IDs de entrenadores verificados para mostrar en vista
        List<Long> entrenadoresVerificadosIds = entrenadorRepository.findAll().stream()
                .filter(Entrenador::isVerified)
                .map(Entrenador::getId)
                .toList();

        System.out.println("✅ Entrenadores verificados: " + entrenadoresVerificadosIds.size());

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("entrenadoresVerificadosIds", entrenadoresVerificadosIds);

        return "admin/usuarios";
    }

    /**
     * Panel de verificación de entrenadores
     * Lista todos los entrenadores y permite verificarlos o revocar verificación
     */
    @GetMapping("/entrenadores")
    public String entrenadores(Model model) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🏋️ ADMIN - Panel de Verificación de Entrenadores          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Obtener todos los entrenadores
        List<Entrenador> entrenadores = entrenadorRepository.findAll();

        // Clasificar entrenadores
        long totalEntrenadores = entrenadores.size();
        long verificados = entrenadores.stream()
                .filter(Entrenador::isVerified)
                .count();
        long pendientes = totalEntrenadores - verificados;

        // Entrenadores con certificaciones
        List<Entrenador> entrenadoresConCert = entrenadorRepository.findConCertificaciones();

        // Candidatos a verificación (no verificados pero con certificaciones)
        List<Entrenador> candidatosVerificacion = entrenadorRepository.findPendientesConCertificaciones();

        // Log de estadísticas
        System.out.println("\n📊 ESTADÍSTICAS DE ENTRENADORES:");
        System.out.println("  ├─ Total: " + totalEntrenadores);
        System.out.println("  ├─ ✅ Verificados: " + verificados);
        System.out.println("  ├─ ⏳ Pendientes: " + pendientes);
        System.out.println("  ├─ 📄 Con certificaciones: " + entrenadoresConCert.size());
        System.out.println("  └─ 🎯 Candidatos a verificar: " + candidatosVerificacion.size());
        System.out.println();

        // Agregar al modelo
        model.addAttribute("entrenadores", entrenadores);
        model.addAttribute("entrenadoresConCert", entrenadoresConCert);
        model.addAttribute("candidatosVerificacion", candidatosVerificacion);

        return "admin/entrenadores";
    }

    @GetMapping("/usuarios/{id}/perfil")
    public String verPerfilUsuario(@PathVariable Long id, Model model) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  🔐 ADMIN - Ver Perfil de Usuario ID: " + id);
        System.out.println("╚════════════════════════════════════════════════════════╝");

        // Primero obtenemos el usuario base para ver su rol
        Usuario usuarioBase = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        System.out.println("  👤 Usuario: " + usuarioBase.getNombre());
        System.out.println("  📧 Email: " + usuarioBase.getEmail());
        System.out.println("  🏷️ Rol: " + usuarioBase.getRol().name());

        // Buscar en el repositorio específico según el rol para obtener todos los datos
        if (usuarioBase.getRol() == Rol.ENTRENADOR) {
            System.out.println("  🏋️ Buscando en EntrenadorRepository...");
            Entrenador entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            System.out.println("  ✅ Verificado: " + (entrenador.isVerified() ? "Sí" : "No"));
            System.out.println("  📱 Teléfono: " + entrenador.getTelefono());
            System.out.println("  🏙️ Ciudad: " + entrenador.getCiudad());
            System.out.println("  🎂 Edad: " + entrenador.getEdad());

            model.addAttribute("usuario", entrenador);
            model.addAttribute("esEntrenador", true);
            model.addAttribute("entrenador", entrenador);

        } else if (usuarioBase.getRol() == Rol.CLIENTE) {
            System.out.println("  👥 Buscando en ClienteRepository...");
            Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            System.out.println("  📱 Teléfono: " + cliente.getTelefono());
            System.out.println("  🏙️ Ciudad: " + cliente.getCiudad());
            System.out.println("  🎂 Edad: " + cliente.getEdad());

            model.addAttribute("usuario", cliente);
            model.addAttribute("esEntrenador", false);

        } else {
            // Es ADMIN - usar el usuario base
            System.out.println("  🔐 Es un ADMIN");
            model.addAttribute("usuario", usuarioBase);
            model.addAttribute("esEntrenador", false);
        }

        System.out.println("  ✅ Datos cargados correctamente");

        return "admin/perfil-usuario";
    }

    @PostMapping("/usuarios/{id}/bloquear")
    public String bloquear(@PathVariable Long id, @RequestParam(value = "motivo", required = false) String motivo, @RequestParam(value = "redirect", defaultValue = "usuarios") String redirect) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  🚫 ADMIN - Bloqueando Usuario ID: " + id);
        System.out.println("║  📝 Motivo: " + (motivo != null ? motivo : "No especificado"));
        System.out.println("╚════════════════════════════════════════════════════════╝");

        usuarioRepository.findById(id).ifPresent(usuario -> {
            System.out.println("  👤 Bloqueando a: " + usuario.getEmail());
            // Enviar correo previo al bloqueo
            try {
                emailService.enviarAvisoBloqueo(usuario.getEmail(), motivo);
                System.out.println("  ✉️ Correo de aviso enviado");
            } catch (Exception e) {
                System.out.println("  ⚠️ No se pudo enviar el correo: " + e.getMessage());
            }
            usuario.setEstado(false);
            usuarioRepository.save(usuario);
            System.out.println("  ✅ Usuario bloqueado exitosamente");
        });
        return "redirect:/admin/" + redirect;
    }

    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquear(@PathVariable Long id, @RequestParam(value = "redirect", defaultValue = "usuarios") String redirect) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ ADMIN - Desbloqueando Usuario ID: " + id);
        System.out.println("╚════════════════════════════════════════════════════════╝");

        usuarioRepository.findById(id).ifPresent(usuario -> {
            System.out.println("  👤 Desbloqueando a: " + usuario.getEmail());
            usuario.setEstado(true);
            usuarioRepository.save(usuario);
            System.out.println("  ✅ Usuario desbloqueado exitosamente");
        });
        return "redirect:/admin/" + redirect;
    }

    /**
     * Verificar un entrenador (otorgar verificación oficial de SABI)
     */
    @PostMapping("/entrenadores/{id}/verificar")
    public String verificar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ VERIFICANDO ENTRENADOR                                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        try {
            Entrenador entrenador = entrenadorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado con ID: " + id));

            System.out.println("📋 Datos del entrenador:");
            System.out.println("  ├─ ID: " + entrenador.getId());
            System.out.println("  ├─ Nombre: " + entrenador.getNombre() + " " +
                    (entrenador.getApellido() != null ? entrenador.getApellido() : ""));
            System.out.println("  ├─ Email: " + entrenador.getEmail());
            System.out.println("  └─ Estado anterior: " + (entrenador.isVerified() ? "✅ YA VERIFICADO" : "⏳ PENDIENTE"));

            if (entrenador.isVerified()) {
                System.out.println("\n⚠️ ADVERTENCIA: El entrenador ya estaba verificado");
                redirectAttributes.addFlashAttribute("warning",
                        "El entrenador ya estaba verificado previamente.");
                return "redirect:/admin/entrenadores";
            }

            // Otorgar verificación
            entrenador.setVerified(true);
            entrenadorRepository.save(entrenador);
            System.out.println("\n✅ VERIFICACIÓN OTORGADA exitosamente");

            // Enviar correo de notificación
            try {
                emailService.enviarAvisoVerificacion(entrenador.getEmail());
                System.out.println("📧 Correo de verificación enviado a: " + entrenador.getEmail());
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo enviar el correo: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("success",
                    "✅ Entrenador verificado correctamente. Se ha enviado un correo de notificación.");

        } catch (Exception e) {
            System.out.println("❌ ERROR al verificar entrenador: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al verificar el entrenador: " + e.getMessage());
        }

        return "redirect:/admin/entrenadores";
    }

    /**
     * Revocar la verificación de un entrenador
     */
    @PostMapping("/entrenadores/{id}/revocar")
    public String revocar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  ⚠️ REVOCANDO VERIFICACIÓN DE ENTRENADOR                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        try {
            Entrenador entrenador = entrenadorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado con ID: " + id));

            System.out.println("📋 Datos del entrenador:");
            System.out.println("  ├─ ID: " + entrenador.getId());
            System.out.println("  ├─ Nombre: " + entrenador.getNombre() + " " +
                    (entrenador.getApellido() != null ? entrenador.getApellido() : ""));
            System.out.println("  ├─ Email: " + entrenador.getEmail());
            System.out.println("  └─ Estado anterior: " + (entrenador.isVerified() ? "✅ VERIFICADO" : "⏳ PENDIENTE"));

            if (!entrenador.isVerified()) {
                System.out.println("\n⚠️ ADVERTENCIA: El entrenador ya estaba sin verificar");
                redirectAttributes.addFlashAttribute("warning",
                        "El entrenador no estaba verificado.");
                return "redirect:/admin/entrenadores";
            }

            // Revocar verificación
            entrenador.setVerified(false);
            entrenadorRepository.save(entrenador);
            System.out.println("\n⚠️ VERIFICACIÓN REVOCADA exitosamente");

            redirectAttributes.addFlashAttribute("success",
                    "⚠️ Verificación revocada correctamente.");

        } catch (Exception e) {
            System.out.println("❌ ERROR al revocar verificación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al revocar la verificación: " + e.getMessage());
        }

        return "redirect:/admin/entrenadores";
    }

    /**
     * Descargar certificación de un entrenador
     */
    @GetMapping("/entrenadores/certificacion/descargar")
    @ResponseBody
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> descargarCertificacion(
            @RequestParam String ruta) {
        try {
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║  📥 DESCARGANDO CERTIFICACIÓN                                ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println("  📁 Ruta solicitada: " + ruta);

            // Construir la ruta completa del archivo
            String baseDir = System.getProperty("user.dir");
            java.nio.file.Path filePath = java.nio.file.Paths.get(baseDir, ruta);

            System.out.println("  📂 Ruta completa: " + filePath.toAbsolutePath());

            // Verificar que el archivo existe
            if (!java.nio.file.Files.exists(filePath)) {
                System.out.println("  ❌ Archivo no encontrado");
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            // Cargar el archivo como recurso
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.out.println("  ❌ Archivo no accesible");
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            // Obtener el nombre del archivo
            String nombreArchivo = filePath.getFileName().toString();
            System.out.println("  ✅ Archivo encontrado: " + nombreArchivo);

            // Configurar headers para descarga
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.add(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + nombreArchivo + "\"");
            headers.add(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf");

            return org.springframework.http.ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            System.out.println("  ❌ Error al descargar: " + e.getMessage());
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }
}
