package com.sabi.sabi.controller;

import com.sabi.sabi.dto.UsuarioDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                return "redirect:/cliente/dashboard";
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ENTRENADOR"))) {
                return "redirect:/entrenador/dashboard";
            }
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
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
                                   Model model) {

        try {
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

            // Asignar rol temporal CLIENTE
            usuarioDTO.setRol(Rol.CLIENTE);

            // Guardar raw contraseña para autenticación programática
            String rawContraseña = usuarioDTO.getContraseña();

            // Encriptar la contraseña antes de guardar
            usuarioDTO.setContraseña(passwordEncoder.encode(rawContraseña));

            usuarioService.createUsuario(usuarioDTO);

            // Autenticar al usuario automáticamente para que pueda seleccionar rol
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(), rawContraseña);
            Authentication auth = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            return "redirect:/auth/seleccionar-rol";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "auth/registro";
        }
    }

    @GetMapping("/seleccionar-rol")
    public String seleccionarRolForm() {
        return "auth/seleccionar-rol";
    }

    @PostMapping("/seleccionar-rol")
    public String seleccionarRol(
            @RequestParam Rol rol,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());

        if (rol == Rol.CLIENTE) {
            // Para clientes, simplemente cambiar el rol y redirigir
            usuario.setRol(rol);
            usuarioService.actualizarUsuario(usuario);

            // Actualizar el contexto de seguridad con el nuevo rol
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    usuario.getEmail(),
                    usuario.getContraseña(),
                    usuario.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);

            return "redirect:/cliente/dashboard";
        } else if (rol == Rol.ENTRENADOR) {
            // Para entrenadores, cambiar el rol temporalmente y redirigir al formulario adicional
            usuario.setRol(rol);
            usuarioService.actualizarUsuario(usuario);

            // Actualizar el contexto de seguridad
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    usuario.getEmail(),
                    usuario.getContraseña(),
                    usuario.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);

            return "redirect:/auth/completar-perfil-entrenador";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/completar-perfil-entrenador")
    public String mostrarFormularioEntrenador(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());

        // Verificar que el usuario sea entrenador
        if (usuario.getRol() != Rol.ENTRENADOR) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        return "auth/completar-perfil-entrenador";
    }

    @PostMapping("/completar-perfil-entrenador")
    public String completarPerfilEntrenador(
            @RequestParam String especialidad,
            @RequestParam Integer aniosExperiencia,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile[] certificaciones,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        try {
            Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());

            // Verificar que el usuario sea entrenador
            if (usuario.getRol() != Rol.ENTRENADOR) {
                return "redirect:/";
            }

            // Actualizar los datos del entrenador
            if (usuario instanceof com.sabi.sabi.entity.Entrenador entrenador) {
                entrenador.setEspecialidad(especialidad);
                entrenador.setAniosExperiencia(aniosExperiencia);

                // Procesar archivos PDF de certificaciones
                if (certificaciones != null && certificaciones.length > 0) {
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
                        }
                    }

                    // Guardar rutas separadas por coma
                    if (!rutasArchivos.isEmpty()) {
                        entrenador.setCertificaciones(String.join(",", rutasArchivos));
                    }
                }

                usuarioService.actualizarUsuario(entrenador);
            }

            return "redirect:/entrenador/dashboard";
        } catch (Exception e) {
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
