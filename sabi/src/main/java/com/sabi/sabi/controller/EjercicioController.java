package com.sabi.sabi.controller;

import com.sabi.sabi.dto.EjercicioDTO;
import com.sabi.sabi.service.EjercicioService;
import com.sabi.sabi.service.EntrenadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EjercicioController {
    @Autowired
    private EjercicioService ejercicioService;
    @Autowired
    private EntrenadorService entrenadorService;

    @GetMapping("/ejercicios/{entrenadorId}")
    public String listarEjercicios(@PathVariable Long entrenadorId, Model model) {
        model.addAttribute("ejercicios", ejercicioService.getEjerciciosPorEntrenador(entrenadorId));
        return "ejercicios/lista";
    }

    @GetMapping("/ejercicios/nuevo")
    public String crearEjercicioView(Model model) {
        EjercicioDTO ejercicioDTO = new EjercicioDTO();
        model.addAttribute("ejercicio", ejercicioDTO);
        return "ejercicios/formulario";
    }

    @GetMapping("/ejercicios/editar/{id}")
    public String editarEjercicioView(@PathVariable Long id, Model model) {
        EjercicioDTO ejercicioDTO = ejercicioService.getEjercicioById(id);
        if (ejercicioDTO == null) {
            return "redirect:/ejercicios?error=notfound";
        }
        model.addAttribute("ejercicio", ejercicioDTO);
        return "ejercicios/formulario";
    }


    @PostMapping("/ejercicios/guardar")
    public String guardarEjercicio(@Valid @ModelAttribute("ejercicio") EjercicioDTO ejercicioDTO,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "ejercicios/formulario";
        }
        ejercicioService.createEjercicio(ejercicioDTO);
        return "redirect:/ejercicios";
    }

    @PostMapping("/ejercicios/eliminar/{id}")
    public String eliminarEjercicio(@PathVariable Long id) {
        ejercicioService.desactivateEjercicio(id);
        return "redirect:/ejercicios";
    }
}
