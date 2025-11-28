package com.sabi.sabi.controller;

import com.sabi.sabi.dto.MensajePregrabadoDTO;
import com.sabi.sabi.dto.EntrenadorDTO;
import com.sabi.sabi.service.EntrenadorService;
import com.sabi.sabi.service.MensajePregrabadoService;
import com.sabi.sabi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mensajes-pregrabados")
public class MensajePregrabadoController {

    @Autowired
    private MensajePregrabadoService mensajePregrabadoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EntrenadorService entrenadorService;

    @GetMapping
    public String listarMensajes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        EntrenadorDTO entrenador = entrenadorService.getEntrenadorById(usuario.getId());
        
        if (entrenador == null) {
            return "redirect:/dashboard-entrenador?error=notentrenador";
        }

        var mensajes = mensajePregrabadoService.obtenerMensajesPorEntrenador(entrenador.getIdUsuario());
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("entrenador", entrenador);
        
        return "mensajes-pregrabados/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoMensajeForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        EntrenadorDTO entrenador = entrenadorService.getEntrenadorById(usuario.getId());
        
        if (entrenador == null) {
            return "redirect:/dashboard-entrenador?error=notentrenador";
        }

        model.addAttribute("mensaje", new MensajePregrabadoDTO());
        model.addAttribute("entrenador", entrenador);
        
        return "mensajes-pregrabados/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarMensajeForm(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        var mensaje = mensajePregrabadoService.obtenerMensajePorId(id);
        if (mensaje == null) {
            redirectAttributes.addFlashAttribute("error", "Mensaje no encontrado.");
            return "redirect:/mensajes-pregrabados";
        }

        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        EntrenadorDTO entrenador = entrenadorService.getEntrenadorById(usuario.getId());
        
        // Verificar que el mensaje pertenece al entrenador
        if (!mensaje.getEntrenadorId().equals(entrenador.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar este mensaje.");
            return "redirect:/mensajes-pregrabados";
        }

        model.addAttribute("mensaje", mensaje);
        model.addAttribute("entrenador", entrenador);
        
        return "mensajes-pregrabados/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMensaje(@ModelAttribute MensajePregrabadoDTO mensajeDTO,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        try {
            var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
            
            // Establecer el ID del entrenador (que es el ID del usuario)
            mensajeDTO.setEntrenadorId(usuario.getId());

            if (mensajeDTO.getId() != null) {
                mensajePregrabadoService.actualizarMensaje(mensajeDTO.getId(), mensajeDTO);
                redirectAttributes.addFlashAttribute("success", "Mensaje actualizado correctamente.");
            } else {
                mensajePregrabadoService.crearMensaje(mensajeDTO);
                redirectAttributes.addFlashAttribute("success", "Mensaje creado correctamente.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el mensaje: " + e.getMessage());
        }

        return "redirect:/mensajes-pregrabados";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMensaje(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        try {
            var mensaje = mensajePregrabadoService.obtenerMensajePorId(id);
            var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
            
            // Verificar que el mensaje pertenece al entrenador
            if (!mensaje.getEntrenadorId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar este mensaje.");
                return "redirect:/mensajes-pregrabados";
            }

            mensajePregrabadoService.eliminarMensaje(id);
            redirectAttributes.addFlashAttribute("success", "Mensaje eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el mensaje: " + e.getMessage());
        }

        return "redirect:/mensajes-pregrabados";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivarMensaje(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        try {
            var mensaje = mensajePregrabadoService.obtenerMensajePorId(id);
            var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
            
            // Verificar que el mensaje pertenece al entrenador
            if (!mensaje.getEntrenadorId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para desactivar este mensaje.");
                return "redirect:/mensajes-pregrabados";
            }

            mensajePregrabadoService.desactivarMensaje(id);
            redirectAttributes.addFlashAttribute("success", "Mensaje desactivado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar el mensaje: " + e.getMessage());
        }

        return "redirect:/mensajes-pregrabados";
    }

    /**
     * API para obtener mensajes del entrenador (usado en AJAX)
     */
    @GetMapping("/api/mis-mensajes")
    @ResponseBody
    public java.util.List<MensajePregrabadoDTO> obtenerMisMensajes(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return java.util.Collections.emptyList();
        }

        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        return mensajePregrabadoService.obtenerMensajesActivosPorEntrenador(usuario.getId());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public MensajePregrabadoDTO obtenerMensaje(@PathVariable Long id) {
        return mensajePregrabadoService.obtenerMensajePorId(id);
    }
}
