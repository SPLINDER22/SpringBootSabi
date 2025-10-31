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

import java.util.List;

@Controller
public class DiaController {
    @Autowired
    private DiaService diaService;
    @Autowired
    private SemanaService semanaService;

    @GetMapping("/dias/detallar/{idSemana}")
    public String detallarSemanas(@PathVariable Long idSemana, Model model, RedirectAttributes redirectAttributes) {
        SemanaDTO semanaDTO;
        try {
            semanaDTO = semanaService.getSemanaById(idSemana);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La semana especificada no existe.");
            return "redirect:/rutinas";
        }
        List<?> dias = diaService.getDiasSemana(idSemana);
        model.addAttribute("dias", dias);
        model.addAttribute("totalDias", dias.size());
        model.addAttribute("semana", semanaDTO);
        // Calcular un id de semana seguro para evitar NullPointerException en las plantillas
        Long safeSemanaId = null;
        if (semanaDTO != null) {
            model.addAttribute("idRutina", semanaDTO.getIdRutina());
<<<<<<< Updated upstream
        }
=======
            // Añadimos la lista de semanas de la rutina para renderizar las pestañas (si falla, la vista lo tolera)
            try {
                var semanasList = semanaService.getSemanasRutina(semanaDTO.getIdRutina());
                model.addAttribute("semanas", semanasList);
            } catch (RuntimeException ex) {
                // No hacemos nada; la vista puede funcionar sin 'semanas'
            }
            // Usar getIdSemana() (es el nombre definido en SemanaDTO)
            if (semanaDTO.getIdSemana() != null) safeSemanaId = semanaDTO.getIdSemana();
         }
        // Añadimos al modelo un id seguro y un selectedSemanaId para que la plantilla no evalúe propiedades sobre null
        model.addAttribute("safeSemanaId", safeSemanaId);
        model.addAttribute("selectedSemanaId", safeSemanaId);
        model.addAttribute("readonly", readonlyEffective);
>>>>>>> Stashed changes
        return "dias/lista";
    }

    @GetMapping("/dias/crear/{idSemana}")
    public String crearDiaView(@PathVariable Long idSemana, Model model, RedirectAttributes redirectAttributes) {
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
    public String editarDiaView(@PathVariable Long idDia, Model model, RedirectAttributes redirectAttributes) {
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
    public String guardarDia(@ModelAttribute DiaDTO diaDTO, RedirectAttributes redirectAttributes) {
        boolean esNuevo = diaDTO.getIdDia() == null;
        diaService.createDia(diaDTO); // createDia internamente detecta update si trae idDia
        if (esNuevo) {
            redirectAttributes.addFlashAttribute("success", "Día creado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Día actualizado correctamente.");
        }
        return "redirect:/dias/detallar/" + diaDTO.getIdSemana();
    }

    @PostMapping("/dias/eliminar/{idDia}")
    public String eliminarDia(@PathVariable Long idDia, RedirectAttributes redirectAttributes) {
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
}
