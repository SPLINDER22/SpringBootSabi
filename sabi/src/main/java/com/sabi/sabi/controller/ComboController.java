package com.sabi.sabi.controller;

import com.sabi.sabi.dto.ComboDTO;
import com.sabi.sabi.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ComboController {
    @Autowired
    private ComboService comboService;

    @GetMapping("/combos/listar/{idEjercicioAsignado}")
    public String listarCombos(@PathVariable Long idEjercicioAsignado, Model model) {
        List<ComboDTO> combos = comboService.getCombosByEjercicioAsignadoId(idEjercicioAsignado);
        model.addAttribute("combos", combos);
        model.addAttribute("idEjercicioAsignado", idEjercicioAsignado);
        return "combos/lista";
    }

    @GetMapping("/combos/crear/{idEjercicioAsignado}/{nombreCombo}")
    public String crearCombo(@PathVariable Long idEjercicioAsignado,
                             @PathVariable String nombreCombo,
                             RedirectAttributes redirectAttributes) {
        try {
            comboService.createCombo(idEjercicioAsignado, nombreCombo);
            redirectAttributes.addFlashAttribute("success", "Combo creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el combo: " + e.getMessage());
        }
        return "redirect:/series/detallar/" + idEjercicioAsignado;
    }

    @GetMapping("/combos/clonar/{comboId}/{idEjercicioAsignado}")
    public String clonarCombo(@PathVariable Long comboId,
                              @PathVariable Long idEjercicioAsignado,
                              RedirectAttributes redirectAttributes) {
        try {
            comboService.clonarCombo(comboId, idEjercicioAsignado);
            redirectAttributes.addFlashAttribute("success", "Series del combo clonadas exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al clonar el combo: " + e.getMessage());
        }
        return "redirect:/series/detallar/" + idEjercicioAsignado;
    }

}
