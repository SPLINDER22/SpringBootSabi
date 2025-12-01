package com.sabi.sabi.controller;

import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.service.EmailService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final ClienteRepository clienteRepository;
    private final EmailService emailService;

    public AdminController(UsuarioRepository usuarioRepository,
                          EntrenadorRepository entrenadorRepository,
                          ClienteRepository clienteRepository,
                          EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.clienteRepository = clienteRepository;
        this.emailService = emailService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  🔐 ADMIN - Accediendo al Dashboard");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        List<Entrenador> todosEntrenadores = entrenadorRepository.findAll();

        long totalUsuarios = todosUsuarios.size();
        long totalEntrenadores = todosEntrenadores.size();
        long totalClientes = todosUsuarios.stream().filter(u -> u.getRol() == Rol.CLIENTE).count();
        long entrenadoresVerificados = todosEntrenadores.stream().filter(Entrenador::isVerified).count();
        long entrenadorespendientes = totalEntrenadores - entrenadoresVerificados;

        // Estadísticas adicionales
        long usuariosActivos = todosUsuarios.stream().filter(Usuario::getEstado).count();
        long usuariosBloqueados = totalUsuarios - usuariosActivos;

        System.out.println("  📊 Total Usuarios: " + totalUsuarios);
        System.out.println("  👥 Total Clientes: " + totalClientes);
        System.out.println("  🏋️ Total Entrenadores: " + totalEntrenadores);
        System.out.println("  ✅ Entrenadores Verificados: " + entrenadoresVerificados);
        System.out.println("  ⏳ Entrenadores Pendientes: " + entrenadorespendientes);
        System.out.println("  🟢 Usuarios Activos: " + usuariosActivos);
        System.out.println("  🔴 Usuarios Bloqueados: " + usuariosBloqueados);

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalEntrenadores", totalEntrenadores);
        model.addAttribute("entrenadoresVerificados", entrenadoresVerificados);
        model.addAttribute("entrenadorespendientes", entrenadorespendientes);
        model.addAttribute("usuariosActivos", usuariosActivos);
        model.addAttribute("usuariosBloqueados", usuariosBloqueados);

        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);

        // Crear mapa de IDs de entrenadores verificados para acceso fácil en la vista
        List<Long> entrenadoresVerificadosIds = entrenadorRepository.findAll().stream()
            .filter(Entrenador::isVerified)
            .map(Entrenador::getId)
            .toList();
        model.addAttribute("entrenadoresVerificadosIds", entrenadoresVerificadosIds);

        return "admin/usuarios";
    }

    @GetMapping("/entrenadores")
    public String entrenadores(Model model) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  🔐 ADMIN - Verificación de Entrenadores");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        // Todos los entrenadores
        java.util.List<Entrenador> entrenadores = entrenadorRepository.findAll();
        System.out.println("  📋 Total de entrenadores encontrados: " + entrenadores.size());

        long verificados = entrenadores.stream().filter(Entrenador::isVerified).count();
        long pendientes = entrenadores.size() - verificados;
        System.out.println("  ✅ Verificados: " + verificados);
        System.out.println("  ⏳ Pendientes: " + pendientes);

        // Solo entrenadores que tienen alguna certificación cargada
        java.util.List<Entrenador> entrenadoresConCert = entrenadorRepository.findConCertificaciones();
        System.out.println("  📄 Con certificaciones: " + entrenadoresConCert.size());

        // Pendientes de verificación PERO con certificaciones (candidatos a revisar/verificar)
        java.util.List<Entrenador> candidatosVerificacion = entrenadorRepository.findPendientesConCertificaciones();
        System.out.println("  🎯 Pendientes con certificaciones (candidatos): " + candidatosVerificacion.size());

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

    @PostMapping("/entrenadores/{id}/verificar")
    public String verificar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ ADMIN - Verificando Entrenador ID: " + id);
        System.out.println("╚════════════════════════════════════════════════════════╝");

        entrenadorRepository.findById(id).ifPresent(entrenador -> {
            System.out.println("  👤 Verificando a: " + entrenador.getEmail());
            entrenador.setVerified(true);
            entrenadorRepository.save(entrenador);
            try {
                emailService.enviarAvisoVerificacion(entrenador.getEmail());
                System.out.println("  ✉️ Correo de verificación enviado");
            } catch (Exception e) {
                System.out.println("  ⚠️ No se pudo enviar el correo: " + e.getMessage());
            }
            System.out.println("  ✅ Entrenador verificado exitosamente");
        });
        redirectAttributes.addFlashAttribute("success", "Entrenador verificado correctamente.");
        return "redirect:/admin/entrenadores";
    }

    @PostMapping("/entrenadores/{id}/revocar")
    public String revocar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  ⚠️ ADMIN - Revocando Verificación de Entrenador ID: " + id);
        System.out.println("╚════════════════════════════════════════════════════════╝");

        entrenadorRepository.findById(id).ifPresent(entrenador -> {
            System.out.println("  👤 Revocando verificación a: " + entrenador.getEmail());
            entrenador.setVerified(false);
            entrenadorRepository.save(entrenador);
            System.out.println("  ✅ Verificación revocada exitosamente");
        });
        redirectAttributes.addFlashAttribute("success", "Verificación revocada correctamente.");
        return "redirect:/admin/entrenadores";
    }
}
