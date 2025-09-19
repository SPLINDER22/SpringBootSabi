package com.sabi.sabi.controller;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.SemanaService;
import com.sabi.sabi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class SemanasController {
    @Autowired
    private SemanaService semanaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RutinaService rutinaService;

    @GetMapping("/semanas/detallar/{idRutina}")
    public String detallarSemanas(@PathVariable Long idRutina, Model model) {
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
        List<?> semanas = semanaService.getSemanasRutina(idRutina);
        model.addAttribute("semanas", semanas);
        model.addAttribute("totalSemanas", semanas.size());
        model.addAttribute("rutina", rutinaDTO);
        return "semanas/lista";
    }

    @GetMapping("/semanas/crear/{idRutina}")
    public String crearSemanaView(@PathVariable Long idRutina, Model model) {
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
    public String editarSemanaView(@PathVariable Long idSemana, Model model) {
        SemanaDTO semanaDTO = semanaService.getSemanaById(idSemana);
        if (semanaDTO == null) {
            return "redirect:/semanas?error=notfound";
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
    public String guardarSemana(@ModelAttribute SemanaDTO semanaDTO) {
        semanaService.createSemana(semanaDTO);
        return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina();
    }

    @PostMapping("/semanas/eliminar/{idSemana}")
    public String eliminarSemana(@PathVariable Long idSemana) {
        SemanaDTO semanaDTO = semanaService.getSemanaById(idSemana);
        if (semanaDTO != null) {
            semanaService.deleteSemana(idSemana);
            return "redirect:/semanas/detallar/" + semanaDTO.getIdRutina();
        }
        return "redirect:/semanas?error=notfound";
    }
}
