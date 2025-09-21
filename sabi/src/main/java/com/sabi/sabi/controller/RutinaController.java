package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.SemanaService;
import com.sabi.sabi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RutinaController {
    @Autowired
    private RutinaService rutinaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private SemanaService semanaService;

    @GetMapping("/rutina/cliente")
    public String verRutinaCliente(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario;
        try {
            usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        } catch (RuntimeException ex) {
            return "redirect:/auth/login";
        }
        if (usuario.getRol() == null || !usuario.getRol().name().equalsIgnoreCase("CLIENTE")) {
            return "redirect:/rutinas";
        }
        // Obtener única rutina activa
        RutinaDTO activa = rutinaService.getRutinaActivaCliente(usuario.getId());
        if (activa != null && activa.getIdRutina() != null) {
            // Redirigir directamente a las semanas de su rutina activa
            return "redirect:/semanas/detallar/" + activa.getIdRutina();
        }
        // No tiene rutina activa: mostrar globales
        model.addAttribute("rutinas", rutinaService.getRutinasGlobales());
        model.addAttribute("mostrandoGlobales", true);
        model.addAttribute("isCliente", true);
        model.addAttribute("idUsuarioActual", -1); // no coincide con idEntrenador de ninguna global
        return "rutinas/lista";
    }

    @PostMapping("/rutinas/adoptar/{idRutina}")
    public String adoptarRutina(@PathVariable Long idRutina,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());

        RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
        RutinaDTO activa = rutinaService.getRutinaActivaCliente(usuario.getId());
        if (activa != null && activa.getIdRutina() != null) {
            redirectAttributes.addFlashAttribute("error", "Ya tienes una rutina activa. Solo puedes tener una a la vez.");
            return "redirect:/rutina/cliente";
        }
        // Adoptar la rutina
        rutinaService.adoptarRutina(idRutina, usuario.getId());
        redirectAttributes.addFlashAttribute("success", "Rutina adoptada correctamente. Ahora puedes ver sus semanas.");
        return "redirect:/semanas/detallar/" + idRutina;
    }

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
            return "redirect:/rutina/cliente";
        }
        model.addAttribute("rutinas", rutinaService.getRutinasPorUsuario(usuario.getId()));
        model.addAttribute("idUsuarioActual", usuario.getId());
        model.addAttribute("isCliente", false);
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
        if (rutinaDTO.getIdRutina() == null) {
            rutinaDTO.setIdEntrenador(usuario.getId());
        }

        RutinaDTO rutinaGuardada;
        if (rutinaDTO.getIdRutina() == null) {
            rutinaGuardada = rutinaService.createRutina(rutinaDTO);
            redirectAttributes.addFlashAttribute("success", "Rutina creada correctamente. Ahora define sus semanas.");
            return "redirect:/semanas/detallar/" + rutinaGuardada.getIdRutina();
        } else {
            rutinaGuardada = rutinaService.updateRutina(rutinaDTO.getIdRutina(), rutinaDTO);
            redirectAttributes.addFlashAttribute("success", "Rutina actualizada correctamente.");
            return "redirect:/rutinas";
        }
    }

    @PostMapping("/rutinas/desactivate/{id}")
    public String desactivarRutina(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(id);
        if (rutinaDTO == null) {
            redirectAttributes.addFlashAttribute("error", "La rutina no existe.");
            return "redirect:/rutinas";
        }
        rutinaService.desactivateRutina(id);
        redirectAttributes.addFlashAttribute("success", "Estado de la rutina actualizado.");
        return "redirect:/rutinas";
    }
}
