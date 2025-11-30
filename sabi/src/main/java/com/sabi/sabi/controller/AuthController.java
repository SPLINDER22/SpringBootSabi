package com.sabi.sabi.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sabi.sabi.dto.UsuarioDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.exception.EmailYaExisteException;
import com.sabi.sabi.service.EmailService;
import com.sabi.sabi.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Verificar que no sea usuario anónimo
            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                    return "redirect:/cliente/dashboard";
                } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ENTRENADOR"))) {
                    return "redirect:/entrenador/dashboard";
                }
            }
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model, Authentication authentication) {
        // Si el usuario ya está autenticado, redirigir al dashboard correspondiente
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                    return "redirect:/cliente/dashboard";
                } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ENTRENADOR"))) {
                    return "redirect:/entrenador/dashboard";
                }
            }
        }
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        return "auth/registro";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute UsuarioDTO usuarioDTO,
                                   @RequestParam(required = false) String genero,
                                   @RequestParam(required = false) String fechaNacimiento,
                                   @RequestParam(required = false) String departamento,
                                   @RequestParam(required = false) String ciudad,
                                   @RequestParam(required = false) String tipoDocumento,
                                   @RequestParam(required = false) String numeroDocumento,
                                   @RequestParam(required = false) String telefono,
                                   HttpServletRequest request,
                                   Model model) {

        try {
            // IMPORTANTE: Verificar PRIMERO si el email ya existe
            try {
                usuarioService.obtenerPorEmail(usuarioDTO.getEmail());
                // Si no lanza excepción, significa que el usuario existe
                model.addAttribute("error", "El correo electrónico ya está registrado. Por favor, usa otro correo o inicia sesión.");
                model.addAttribute("usuarioDTO", usuarioDTO);
                return "auth/registro";
            } catch (RuntimeException e) {
                // Usuario no existe, continuamos con el registro
            }

            // Convertir los parámetros String a los tipos enum correspondientes
            if (genero != null && !genero.isEmpty()) {
                usuarioDTO.setSexo(com.sabi.sabi.entity.enums.Sexo.valueOf(genero));
            }

            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                usuarioDTO.setFechaNacimiento(java.time.LocalDate.parse(fechaNacimiento));
            }

            if (departamento != null && !departamento.isEmpty()) {
                usuarioDTO.setDepartamento(com.sabi.sabi.entity.enums.Departamento.valueOf(departamento));
            }

            if (ciudad != null && !ciudad.isEmpty()) {
                usuarioDTO.setCiudad(ciudad);
            }

            if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
                usuarioDTO.setTipoDocumento(com.sabi.sabi.entity.enums.TipoDocumento.valueOf(tipoDocumento));
            }

            if (numeroDocumento != null && !numeroDocumento.isEmpty()) {
                usuarioDTO.setNumeroDocumento(numeroDocumento);
            }

            if (telefono != null && !telefono.isEmpty()) {
                usuarioDTO.setTelefono(telefono);
            }

            // Guardar raw contraseña para después
            String rawContraseña = usuarioDTO.getContraseña();

            // Encriptar la contraseña
            usuarioDTO.setContraseña(passwordEncoder.encode(rawContraseña));

            // NO crear el usuario todavía, solo guardar datos en sesión
            request.getSession().setAttribute("usuarioRegistro", usuarioDTO);
            request.getSession().setAttribute("rawContraseña", rawContraseña);

            // Redirigir a seleccionar rol SIN autenticar aún
            return "redirect:/auth/seleccionar-rol";

        } catch (EmailYaExisteException e) {
            model.addAttribute("error", "El correo electrónico ya está registrado. Por favor, usa otro correo o inicia sesión.");
            model.addAttribute("usuarioDTO", usuarioDTO);
            return "auth/registro";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            model.addAttribute("usuarioDTO", usuarioDTO);
            return "auth/registro";
        }
    }

    @GetMapping("/seleccionar-rol")
    public String seleccionarRolForm(HttpServletRequest request, Model model) {
        // Verificar que hay datos de registro en sesión
        UsuarioDTO usuarioDTO = (UsuarioDTO) request.getSession().getAttribute("usuarioRegistro");
        if (usuarioDTO == null) {
            // No hay datos de registro, redirigir a registro
            return "redirect:/auth/registro";
        }
        return "auth/seleccionar-rol";
    }

    @PostMapping("/seleccionar-rol")
    public String seleccionarRol(
            @RequestParam Rol rol,
            HttpServletRequest request
    ) {
        // Obtener datos de registro de la sesión
        UsuarioDTO usuarioDTO = (UsuarioDTO) request.getSession().getAttribute("usuarioRegistro");
        String rawContraseña = (String) request.getSession().getAttribute("rawContraseña");

        if (usuarioDTO == null || rawContraseña == null) {
            return "redirect:/auth/registro";
        }

        try {
            // Asignar el rol seleccionado
            usuarioDTO.setRol(rol);
            usuarioDTO.setEstado(true);

            // CREAR el usuario con el rol correcto desde el inicio
            usuarioService.createUsuario(usuarioDTO);

            // Limpiar datos de sesión
            request.getSession().removeAttribute("usuarioRegistro");
            request.getSession().removeAttribute("rawContraseña");

            // Autenticar al usuario
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                usuarioDTO.getEmail(),
                rawContraseña
            );
            Authentication auth = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Obtener usuario actualizado desde BD
            Usuario usuario = usuarioService.obtenerPorEmail(usuarioDTO.getEmail());

            // Enviar correo de bienvenida
            emailService.enviarCorreoBienvenida(usuario);

            if (rol == Rol.CLIENTE) {
                return "redirect:/cliente/dashboard";
            } else if (rol == Rol.ENTRENADOR) {
                return "redirect:/auth/completar-perfil-entrenador";
            } else {
                return "redirect:/";
            }

        } catch (EmailYaExisteException e) {
            // Email ya existe
            request.getSession().removeAttribute("usuarioRegistro");
            request.getSession().removeAttribute("rawContraseña");
            return "redirect:/auth/login?error=emailExiste";
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error al crear usuario: " + e.getMessage());
            return "redirect:/auth/registro";
        }
    }

    @GetMapping("/completar-perfil-entrenador")
    public String mostrarFormularioEntrenador(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        // Obtener el usuario desde el contexto de seguridad o desde userDetails
        String username;
        if (userDetails != null) {
            username = userDetails.getUsername();
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) auth.getPrincipal()).getUsername();
            } else if (auth != null && auth.getName() != null) {
                username = auth.getName();
            } else {
                return "redirect:/auth/login";
            }
        }

        Usuario usuario = usuarioService.obtenerPorEmail(username);

        // Verificar que el usuario sea entrenador
        if (usuario.getRol() != Rol.ENTRENADOR) {
            return "redirect:/";
        }

        // Si el entrenador ya completó su perfil (tiene especialidades o experiencia), redirigir al dashboard
        if (usuario instanceof com.sabi.sabi.entity.Entrenador entrenador) {
            if ((entrenador.getEspecialidades() != null && !entrenador.getEspecialidades().isEmpty()) ||
                (entrenador.getAniosExperiencia() != null && entrenador.getAniosExperiencia() > 0)) {
                return "redirect:/entrenador/dashboard";
            }
        }

        model.addAttribute("usuario", usuario);
        return "auth/completar-perfil-entrenador";
    }

    @PostMapping("/completar-perfil-entrenador")
    public String completarPerfilEntrenador(
            @RequestParam String especialidades,
            @RequestParam Double precioMinimo,
            @RequestParam Double precioMaximo,
            @RequestParam Integer anioInicioExperiencia,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile[] certificaciones,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  📝 COMPLETANDO PERFIL ENTRENADOR");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        try {
            // Obtener el usuario desde el contexto de seguridad o desde userDetails
            String username;
            if (userDetails != null) {
                username = userDetails.getUsername();
            } else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof UserDetails) {
                    username = ((UserDetails) auth.getPrincipal()).getUsername();
                } else if (auth != null && auth.getName() != null) {
                    username = auth.getName();
                } else {
                    System.out.println("❌ No se pudo obtener usuario autenticado");
                    return "redirect:/auth/login";
                }
            }

            System.out.println("  👤 Usuario: " + username);
            Usuario usuario = usuarioService.obtenerPorEmail(username);
            System.out.println("  🎭 Rol: " + usuario.getRol());

            // Verificar que el usuario sea entrenador
            if (usuario.getRol() != Rol.ENTRENADOR) {
                System.out.println("❌ Usuario NO es entrenador");
                return "redirect:/";
            }

            // Actualizar los datos del entrenador
            if (usuario instanceof com.sabi.sabi.entity.Entrenador entrenador) {
                System.out.println("  ✅ Usuario es instancia de Entrenador");
                System.out.println("  📋 Especialidades: " + especialidades);
                System.out.println("  💰 Precios: " + precioMinimo + " - " + precioMaximo);
                System.out.println("  📅 Año inicio: " + anioInicioExperiencia);

                entrenador.setEspecialidades(especialidades);
                // Mantener compatibilidad con especialidad singular (usar la primera)
                if (especialidades != null && !especialidades.isEmpty()) {
                    String primeraEspecialidad = especialidades.split(",")[0].trim();
                    entrenador.setEspecialidad(primeraEspecialidad);
                }
                entrenador.setPrecioMinimo(precioMinimo);
                entrenador.setPrecioMaximo(precioMaximo);
                
                // Calcular años de experiencia a partir del año de inicio
                int anioActual = java.time.Year.now().getValue();
                int aniosExperiencia = anioActual - anioInicioExperiencia;
                entrenador.setAniosExperiencia(aniosExperiencia);
                System.out.println("  🎯 Años experiencia: " + aniosExperiencia);

                // Procesar archivos PDF de certificaciones
                if (certificaciones != null && certificaciones.length > 0) {
                    System.out.println("  📄 Procesando " + certificaciones.length + " certificación(es)");
                    java.util.List<String> rutasArchivos = new java.util.ArrayList<>();

                    // Crear directorio si no existe
                    String uploadDir = "uploads/certificaciones/";
                    java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                    if (!java.nio.file.Files.exists(uploadPath)) {
                        java.nio.file.Files.createDirectories(uploadPath);
                    }

                    for (org.springframework.web.multipart.MultipartFile file : certificaciones) {
                        if (!file.isEmpty() && file.getOriginalFilename() != null) {
                            // Generar nombre único para el archivo
                            String nombreOriginal = file.getOriginalFilename();
                            String nombreArchivo = "cert_" + usuario.getId() + "_" +
                                                  System.currentTimeMillis() + "_" +
                                                  nombreOriginal.replaceAll("[^a-zA-Z0-9.]", "_");

                            // Guardar archivo
                            java.nio.file.Path rutaArchivo = uploadPath.resolve(nombreArchivo);
                            file.transferTo(rutaArchivo.toFile());

                            // Agregar ruta a la lista
                            rutasArchivos.add(uploadDir + nombreArchivo);
                            System.out.println("  ✅ Guardado: " + nombreArchivo);
                        }
                    }

                    // Guardar rutas separadas por coma
                    if (!rutasArchivos.isEmpty()) {
                        entrenador.setCertificaciones(String.join(",", rutasArchivos));
                        System.out.println("  ✅ Total certificaciones guardadas: " + rutasArchivos.size());
                    }
                } else {
                    System.out.println("  ⚠️ Sin certificaciones subidas");
                }

                usuarioService.actualizarUsuario(entrenador);
                System.out.println("  ✅ Perfil actualizado correctamente");
            }

            System.out.println("  🚀 Redirigiendo a: /entrenador/dashboard");
            return "redirect:/entrenador/dashboard";

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al completar perfil: " + e.getMessage());
            return "auth/completar-perfil-entrenador";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalidar la sesión
        request.getSession().invalidate();
        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();
        // Redirigir al login con mensaje
        return "redirect:/auth/login?logoutMessage=Has cerrado sesión exitosamente.";
    }
}
