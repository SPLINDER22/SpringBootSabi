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
import org.springframework.web.bind.annotation.PathVariable;

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
        model.addAttribute("semanas", semanaService.getSemanasRutina(idRutina));
        model.addAttribute("rutina", rutinaDTO.getNombre());
        return "semanas/lista";
    }

    @GetMapping("/semanas/crear/{idRutina}")
    public String crearSemanaView(@PathVariable Long idRutina, Model model) {
        SemanaDTO semanaDTO = new SemanaDTO();
        RutinaDTO rutinaDTO = rutinaService.getRutinaById(idRutina);
        model.addAttribute("rutina", rutinaDTO);
        model.addAttribute("semana", semanaDTO);
        return "semanas/formulario";
    }
}
