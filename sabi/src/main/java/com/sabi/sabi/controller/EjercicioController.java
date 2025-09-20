package com.sabi.sabi.controller;

import com.sabi.sabi.dto.EjercicioDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.service.EjercicioService;
import com.sabi.sabi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EjercicioController {
    @Autowired
    private EjercicioService ejercicioService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/ejercicios")
    public String listarEjercicios(@AuthenticationPrincipal UserDetails userDetails,
                                   Model model,
                                   @RequestParam(value = "fromEjes", required = false) Boolean fromEjes,
                                   @RequestParam(value = "idDia", required = false) Long idDia) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario;
        try {
            usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        } catch (RuntimeException ex) {
            return "redirect:/auth/login";
        }
        model.addAttribute("ejercicios", ejercicioService.getEjerciciosPorUsuario(usuario.getId()));
        model.addAttribute("idUsuarioActual", usuario.getId());
        model.addAttribute("fromEjes", Boolean.TRUE.equals(fromEjes));
        model.addAttribute("idDia", idDia);
        return "ejercicios/lista";
    }


    @GetMapping("/ejercicios/nuevo")
    public String crearEjercicioView(Model model,
                                     @RequestParam(value = "fromEjes", required = false) Boolean fromEjes,
                                     @RequestParam(value = "idDia", required = false) Long idDia) {
        EjercicioDTO ejercicioDTO = new EjercicioDTO();
        model.addAttribute("ejercicio", ejercicioDTO);
        model.addAttribute("fromEjes", Boolean.TRUE.equals(fromEjes));
        model.addAttribute("idDia", idDia);
        return "ejercicios/formulario";
    }

    @GetMapping("/ejercicios/editar/{id}")
    public String editarEjercicioView(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model,
                                      @RequestParam(value = "fromEjes", required = false) Boolean fromEjes,
                                      @RequestParam(value = "idDia", required = false) Long idDia) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        EjercicioDTO ejercicioDTO = ejercicioService.getEjercicioById(id);
        if (ejercicioDTO == null) {
            return "redirect:/ejercicios?error=notfound";
        }
        // Solo permitir editar si es propietario
        if (ejercicioDTO.getIdUsuario() == null || !ejercicioDTO.getIdUsuario().equals(usuario.getId())) {
            return "redirect:/ejercicios?error=forbidden";
        }
        model.addAttribute("ejercicio", ejercicioDTO);
        model.addAttribute("fromEjes", Boolean.TRUE.equals(fromEjes));
        model.addAttribute("idDia", idDia);
        return "ejercicios/formulario";
    }

    @PostMapping("/ejercicios/guardar")
    public String crearEjercicio(@ModelAttribute EjercicioDTO ejercicioDTO,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(value = "fromEjes", required = false) Boolean fromEjes,
                                 @RequestParam(value = "idDia", required = false) Long idDia) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        // Si viene ID (update), validar propiedad
        if (ejercicioDTO.getIdEjercicio() != null) {
            EjercicioDTO existente = ejercicioService.getEjercicioById(ejercicioDTO.getIdEjercicio());
            if (existente.getIdUsuario() == null || !existente.getIdUsuario().equals(usuario.getId())) {
                return "redirect:/ejercicios?error=forbidden";
            }
        }
        ejercicioService.createEjercicio(ejercicioDTO, usuario.getId());
        if (Boolean.TRUE.equals(fromEjes) && idDia != null) {
            return "redirect:/ejercicios?fromEjes=true&idDia=" + idDia;
        }
        return "redirect:/ejercicios";
    }

    @PostMapping("/ejercicios/eliminar/{id}")
    public String eliminarEjercicio(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam(value = "fromEjes", required = false) Boolean fromEjes,
                                    @RequestParam(value = "idDia", required = false) Long idDia) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        EjercicioDTO ejercicioDTO = ejercicioService.getEjercicioById(id);
        if (ejercicioDTO.getIdUsuario() == null || !ejercicioDTO.getIdUsuario().equals(usuario.getId())) {
            return "redirect:/ejercicios?error=forbidden";
        }
        ejercicioService.desactivateEjercicio(id);
        if (Boolean.TRUE.equals(fromEjes) && idDia != null) {
            return "redirect:/ejercicios?fromEjes=true&idDia=" + idDia;
        }
        return "redirect:/ejercicios";
    }
}