package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.SemanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Controller
public class SemanasController {
    @Autowired
    private SemanaService semanaService;
    @Autowired
    private RutinaService rutinaService;

    private boolean hasRole(UserDetails userDetails, String role) {
        if (userDetails == null) return false;
        return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role) || a.getAuthority().equals(role));
    }

    @GetMapping("/semanas/detallar/{idRutina}")
    public String detallarSemanas(@PathVariable Long idRutina,
                                  @RequestParam(value = "readonly", required = false) Boolean readonly,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
        List<?> semanas = semanaService.getSemanasRutina(idRutina);
        boolean esEntrenador = hasRole(userDetails, "ENTRENADOR");
        // Cliente ya no va en modo readonly por defecto: sólo modo readonly cuando el ENTRENADOR consulta con flag
        boolean readonlyEffective = esEntrenador && Boolean.TRUE.equals(readonly);
        model.addAttribute("semanas", semanas);
        model.addAttribute("totalSemanas", semanas.size());
        model.addAttribute("rutina", rutinaDTO);
        model.addAttribute("readonly", readonlyEffective);
        return "semanas/lista";
    }

    @GetMapping("/semanas/crear/{idRutina}")
    public String crearSemanaView(@PathVariable Long idRutina,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        if (hasRole(userDetails, "CLIENTE")) { // cliente no crea semanas
            return "redirect:/semanas/detallar/" + idRutina; // sin readonly
        }
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
        var semanas = semanaService.getSemanasRutina(idRutina);
        int total = semanas != null ? semanas.size() : 0;
        SemanaDTO semanaDTO = new SemanaDTO();
        semanaDTO.setIdRutina(idRutina);
        semanaDTO.setNumeroSemana((long) (total + 1));
        model.addAttribute("rutina", rutinaDTO);
        model.addAttribute("semanas", semanas);
        model.addAttribute("totalSemanas", total);
        model.addAttribute("semana", semanaDTO);
        return "semanas/formulario";
    }

    @GetMapping("/semanas/editar/{idSemana}")
    public String editarSemanaView(@PathVariable Long idSemana,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model, RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) { // cliente no edita semanas
            SemanaDTO semanaDTO = semanaService.getSemanaById(idSemana);
            if (semanaDTO != null) {
                return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina();
            }
            return "redirect:/rutinas";
        }
        SemanaDTO semanaDTO = semanaService.getSemanaById(idSemana);
        if (semanaDTO == null) {
            redirectAttributes.addFlashAttribute("error", "La semana no existe.");
            return "redirect:/rutinas";
        }
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(semanaDTO.getIdRutina());
        var semanas = semanaService.getSemanasRutina(semanaDTO.getIdRutina());
        int total = semanas != null ? semanas.size() : 0;
        model.addAttribute("rutina", rutinaDTO);
        model.addAttribute("semanas", semanas);
        model.addAttribute("totalSemanas", total);
        model.addAttribute("semana", semanaDTO);
        return "semanas/formulario";
    }

    @PostMapping("/semanas/guardar")
    public String guardarSemana(@ModelAttribute SemanaDTO semanaDTO,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina();
        }
        if (semanaDTO.getIdSemana() == null) {
            SemanaDTO creada = semanaService.createSemana(semanaDTO);
            redirectAttributes.addFlashAttribute("success", "Semana creada correctamente. Ahora agrega los días.");
            return "redirect:/dias/detallar/" + creada.getIdSemana();
        } else {
            semanaService.updateSemana(semanaDTO.getIdSemana(), semanaDTO);
            redirectAttributes.addFlashAttribute("success", "Semana actualizada correctamente.");
            return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina();
        }
    }

    @PostMapping("/semanas/eliminar/{idSemana}")
    public String eliminarSemana(@PathVariable Long idSemana,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) { // cliente no elimina semanas
            SemanaDTO semanaDTO = null;
            try { semanaDTO = semanaService.getSemanaById(idSemana); } catch (RuntimeException ignored) {}
            return "redirect:/semanas/detallar/" + (semanaDTO != null ? semanaDTO.getIdRutina() : "");
        }
        SemanaDTO semanaDTO = null;
        try {
            semanaDTO = semanaService.getSemanaById(idSemana);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La semana no existe.");
            return "redirect:/rutinas";
        }
        Long idRutina = semanaDTO.getIdRutina();
        semanaService.deleteSemana(idSemana);
        redirectAttributes.addFlashAttribute("success", "Semana eliminada correctamente.");
        return "redirect:/semanas/detallar/" + idRutina;
    }

    @GetMapping("/semanas/check/{idSemana}")
    public String checkSemana(@PathVariable Long idSemana,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        // Ahora sólo CLIENTE puede marcar completada / pendiente.
        if (!hasRole(userDetails, "CLIENTE")) {
            SemanaDTO semanaDTO;
            try { semanaDTO = semanaService.getSemanaById(idSemana); } catch (RuntimeException ex) { return "redirect:/rutinas"; }
            return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina() + "?readonly=true"; // entrenador visualizando
        }
        SemanaDTO semanaDTO;
        try {
            semanaDTO = semanaService.getSemanaById(idSemana);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La semana no existe.");
            return "redirect:/rutinas";
        }
        Long idRutina = semanaDTO.getIdRutina();
        semanaService.alterCheck(idSemana);
        redirectAttributes.addFlashAttribute("success", "Estado de la semana cambiado correctamente.");
        return "redirect:/semanas/detallar/" + idRutina;
    }
}
