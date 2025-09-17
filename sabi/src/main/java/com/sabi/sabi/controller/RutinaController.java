package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RutinaController {
    @Autowired
    private RutinaService rutinaService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/rutinas")
    public String listarRutinas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario;
        try {
            usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        } catch (RuntimeException ex) {
            return "redirect:/auth/login";
        }
        // Validar que el usuario sea entrenador
        if (usuario.getRol() == null || !usuario.getRol().name().equalsIgnoreCase("ENTRENADOR")) {
            return "redirect:/cliente/dashboard"; // O redirigir al dashboard del cliente si lo prefieres
        }
        model.addAttribute("rutinas", rutinaService.getRutinasPorUsuario(usuario.getId()));
        model.addAttribute("idUsuarioActual", usuario.getId());
        return "rutinas/lista";
    }

    @GetMapping("/rutinas/nueva")
    public String crearRutinaView(Model model) {
        RutinaDTO rutinaDTO = new RutinaDTO();
        model.addAttribute("rutina", rutinaDTO);
        return "rutinas/formulario";
    }

    @GetMapping("/rutinas/editar/{id}")
    public String editarRutinaView(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(id);
        if (rutinaDTO == null) {
            return "redirect:/rutinas?error=notfound";
        }
        model.addAttribute("rutina", rutinaDTO);
        return "rutinas/formulario";
    }

    @PostMapping("/rutinas/guardar")
    public String guardarRutina(@ModelAttribute("rutina") RutinaDTO rutinaDTO,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        // Asignar el entrenador actual si es creación
        if (rutinaDTO.getIdRutina() == null) {
            rutinaDTO.setIdEntrenador(usuario.getId());
        }
        RutinaDTO rutinaGuardada;
        if (rutinaDTO.getIdRutina() == null) {
            rutinaGuardada = rutinaService.createRutina(rutinaDTO);
            // Redirigir a detalles si es creación
            return "redirect:/semanas/listar/" + rutinaGuardada.getIdRutina();
        } else {
            rutinaGuardada = rutinaService.updateRutina(rutinaDTO.getIdRutina(), rutinaDTO);
            // Redirigir a la lista si es edición
            redirectAttributes.addFlashAttribute("success", "Rutina actualizada correctamente");
            return "redirect:/rutinas";
        }
    }
}
