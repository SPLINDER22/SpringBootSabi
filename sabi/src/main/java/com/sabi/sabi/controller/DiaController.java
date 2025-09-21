package com.sabi.sabi.controller;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.service.DiaService;
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
public class DiaController {
    @Autowired
    private DiaService diaService;
    @Autowired
    private SemanaService semanaService;

    private boolean hasRole(UserDetails userDetails, String role) {
        if (userDetails == null) return false;
        return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role) || a.getAuthority().equals(role));
    }

    @GetMapping("/dias/detallar/{idSemana}")
    public String detallarDias(@PathVariable Long idSemana,
                                @RequestParam(value = "readonly", required = false) Boolean readonly,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model, RedirectAttributes redirectAttributes) {
        SemanaDTO semanaDTO;
        try {
            semanaDTO = semanaService.getSemanaById(idSemana);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La semana especificada no existe.");
            return "redirect:/rutinas";
        }
        List<?> dias = diaService.getDiasSemana(idSemana);
        boolean esCliente = hasRole(userDetails, "CLIENTE");
        boolean readonlyEffective = esCliente || Boolean.TRUE.equals(readonly);
        model.addAttribute("dias", dias);
        model.addAttribute("totalDias", dias.size());
        model.addAttribute("semana", semanaDTO);
        if (semanaDTO != null) {
            model.addAttribute("idRutina", semanaDTO.getIdRutina());
        }
        model.addAttribute("readonly", readonlyEffective);
        return "dias/lista";
    }

    @GetMapping("/dias/crear/{idSemana}")
    public String crearDiaView(@PathVariable Long idSemana,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model, RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            return "redirect:/dias/detallar/" + idSemana + "?readonly=true";
        }
        SemanaDTO semanaDTO;
        try {
            semanaDTO = semanaService.getSemanaById(idSemana);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La semana no existe.");
            return "redirect:/rutinas";
        }
        var dias = diaService.getDiasSemana(idSemana);
        int total = dias != null ? dias.size() : 0;
        DiaDTO diaDTO = new DiaDTO();
        diaDTO.setIdSemana(idSemana);
        diaDTO.setNumeroDia((long) (total + 1));
        model.addAttribute("semana", semanaDTO);
        model.addAttribute("dias", dias);
        model.addAttribute("totalDias", total);
        model.addAttribute("dia", diaDTO);
        if (semanaDTO != null) {
            model.addAttribute("idRutina", semanaDTO.getIdRutina());
        }
        return "dias/formulario";
    }

    @GetMapping("/dias/editar/{idDia}")
    public String editarDiaView(@PathVariable Long idDia,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model, RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            DiaDTO diaDTO;
            try { diaDTO = diaService.getDiaById(idDia); } catch (RuntimeException ex) { return "redirect:/rutinas"; }
            return "redirect:/dias/detallar/" + diaDTO.getIdSemana() + "?readonly=true";
        }
        DiaDTO diaDTO;
        try {
            diaDTO = diaService.getDiaById(idDia);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El día no existe.");
            return "redirect:/rutinas";
        }
        SemanaDTO semanaDTO = semanaService.getSemanaById(diaDTO.getIdSemana());
        var dias = diaService.getDiasSemana(diaDTO.getIdSemana());
        int total = dias != null ? dias.size() : 0;
        model.addAttribute("semana", semanaDTO);
        model.addAttribute("dias", dias);
        model.addAttribute("totalDias", total);
        model.addAttribute("dia", diaDTO);
        if (semanaDTO != null) {
            model.addAttribute("idRutina", semanaDTO.getIdRutina());
        }
        return "dias/formulario";
    }

    @PostMapping("/dias/guardar")
    public String guardarDia(@ModelAttribute DiaDTO diaDTO,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            return "redirect:/dias/detallar/" + diaDTO.getIdSemana() + "?readonly=true";
        }
        boolean esNuevo = diaDTO.getIdDia() == null;
        DiaDTO saved = diaService.createDia(diaDTO);
        if (esNuevo) {
            if (saved == null || saved.getIdDia() == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo crear el día. Intenta nuevamente.");
                return "redirect:/dias/detallar/" + (diaDTO.getIdSemana() != null ? diaDTO.getIdSemana() : "");
            }
            redirectAttributes.addFlashAttribute("success", "Día creado correctamente. Ahora asigna los ejercicios.");
            return "redirect:/ejes/detallar/" + saved.getIdDia();
        } else {
            redirectAttributes.addFlashAttribute("success", "Día actualizado correctamente.");
        }
        Long idSemana = saved != null && saved.getIdSemana() != null ? saved.getIdSemana() : diaDTO.getIdSemana();
        return "redirect:/dias/detallar/" + idSemana;
    }

    @PostMapping("/dias/eliminar/{idDia}")
    public String eliminarDia(@PathVariable Long idDia,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            DiaDTO diaDTO;
            try { diaDTO = diaService.getDiaById(idDia); } catch (RuntimeException ex) { return "redirect:/rutinas"; }
            return "redirect:/dias/detallar/" + diaDTO.getIdSemana() + "?readonly=true";
        }
        DiaDTO diaDTO;
        try {
            diaDTO = diaService.getDiaById(idDia);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El día no existe.");
            return "redirect:/rutinas";
        }
        diaService.deleteDia(idDia);
        redirectAttributes.addFlashAttribute("success", "Día eliminado correctamente.");
        return "redirect:/dias/detallar/" + diaDTO.getIdSemana();
    }

    @GetMapping("/dias/check/{idDia}")
    public String toggleChecked(@PathVariable Long idDia,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            DiaDTO diaDTO;
            try { diaDTO = diaService.getDiaById(idDia); } catch (RuntimeException ex) { return "redirect:/rutinas"; }
            return "redirect:/dias/detallar/" + diaDTO.getIdSemana() + "?readonly=true";
        }
        try {
            var dia = diaService.toggleChecked(idDia);
            redirectAttributes.addFlashAttribute("success", dia.getChecked() ? "Día marcado como completado." : "Día marcado como pendiente.");
            return "redirect:/dias/detallar/" + dia.getIdSemana();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el estado del día.");
            return "redirect:/rutinas";
        }
    }
}
