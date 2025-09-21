package com.sabi.sabi.controller;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.EjercicioAsignadoDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.service.DiaService;
import com.sabi.sabi.service.EjercicioAsignadoService;
import com.sabi.sabi.service.EjercicioService;
import com.sabi.sabi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EjercicioAsignadoController {
    @Autowired
    private EjercicioAsignadoService ejercicioAsignadoService;
    @Autowired
    private DiaService diaService;
    @Autowired
    private EjercicioService ejercicioService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/ejes/detallar/{idDia}")
    public String detallarDia(@PathVariable Long idDia, Model model, RedirectAttributes redirectAttributes) {
        DiaDTO diaDTO;
        try {
            diaDTO = diaService.getDiaById(idDia);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El dia especificado no existe.");
            return "redirect:/dias/detallar/" + idDia;
        }
        List<?> ejes = ejercicioAsignadoService.getEjesDia(idDia);
        model.addAttribute("ejes", ejes);
        model.addAttribute("totalEjes", ejes.size());
        model.addAttribute("dia", diaDTO);
        if (diaDTO != null) {
            model.addAttribute("idSemana", diaDTO.getIdSemana());
        }
        return "ejercicios-asignados/lista";
    }

    @GetMapping("/ejes/crear/{idDia}")
    public String crearEjeView(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long idDia, Model model, RedirectAttributes redirectAttributes) {
        DiaDTO diaDTO;
        try {
            diaDTO = diaService.getDiaById(idDia);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El dia no existe.");
            return "redirect:/rutinas";
        }
        var ejes = ejercicioAsignadoService.getEjesDia(idDia);
        int total = ejes != null ? ejes.size() : 0;
        EjercicioAsignadoDTO ejeDTO = new EjercicioAsignadoDTO();
        ejeDTO.setIdDia(idDia);
        ejeDTO.setOrden((long) (total + 1));

        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        model.addAttribute("ejercicios", ejercicioService.getEjerciciosPorUsuario(usuario.getId()));
        model.addAttribute("dia", diaDTO);
        model.addAttribute("ejes", ejes);
        model.addAttribute("totalEjes", total);
        model.addAttribute("eje", ejeDTO);
        if (diaDTO != null) {
            model.addAttribute("idDia", diaDTO.getIdDia());
        }
        return "ejercicios-asignados/formulario";
    }

    @GetMapping("/ejes/editar/{idEje}")
    public String editarEjeView(@PathVariable Long idEje, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {
        EjercicioAsignadoDTO ejeDTO;
        try {
            ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(idEje);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El ejercicio no existe.");
            return "redirect:/rutinas";
        }
        DiaDTO diaDTO = diaService.getDiaById(ejeDTO.getIdDia());
        var ejes = ejercicioAsignadoService.getEjesDia(ejeDTO.getIdDia());
        int total = ejes != null ? ejes.size() : 0;
        model.addAttribute("dia", diaDTO);
        model.addAttribute("ejes", ejes);
        model.addAttribute("totalEjes", total);
        model.addAttribute("eje", ejeDTO);
        if (diaDTO != null) {
            model.addAttribute("idSemana", diaDTO.getIdSemana());
        }
        if (userDetails != null) {
            var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
            model.addAttribute("ejercicios", ejercicioService.getEjerciciosPorUsuario(usuario.getId()));
        }
        return "ejercicios-asignados/formulario";
    }

    @PostMapping("/ejes/guardar")
    public String guardarEje(@ModelAttribute EjercicioAsignadoDTO ejeDTO, RedirectAttributes redirectAttributes) {
        boolean esNuevo = ejeDTO.getIdEjercicioAsignado() == null;
        EjercicioAsignadoDTO guardado = ejercicioAsignadoService.createEjercicioAsignado(ejeDTO);
        if (esNuevo) {
            redirectAttributes.addFlashAttribute("success", "Ejercicio asignado creado correctamente. Ahora agrega sus series.");
            return "redirect:/series/detallar/" + guardado.getIdEjercicioAsignado();
        } else {
            redirectAttributes.addFlashAttribute("success", "Ejercicio asignado actualizado correctamente.");
        }
        return "redirect:/ejes/detallar/" + ejeDTO.getIdDia();
    }

    @PostMapping("/ejes/eliminar/{idEje}")
    public String eliminarDia(@PathVariable Long idEje, RedirectAttributes redirectAttributes) {
        EjercicioAsignadoDTO ejeDTO;
        try {
            ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(idEje);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El ejercicio no existe.");
            return "redirect:/rutinas";
        }
        ejercicioAsignadoService.desactivateEjercicioAsignado(idEje);
        redirectAttributes.addFlashAttribute("success", "Ejercicio eliminado correctamente.");
        return "redirect:/ejes/detallar/" + ejeDTO.getIdDia();
    }

    // Nuevo endpoint para alternar estado de completado del ejercicio asignado
    @GetMapping("/ejes/check/{idEje}")
    public String toggleChecked(@PathVariable Long idEje, RedirectAttributes redirectAttributes) {
        try {
            var eje = ejercicioAsignadoService.toggleChecked(idEje);
            redirectAttributes.addFlashAttribute("success", eje.getChecked() ? "Ejercicio marcado como completado." : "Ejercicio marcado como pendiente.");
            return "redirect:/ejes/detallar/" + eje.getIdDia();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el estado del ejercicio.");
            return "redirect:/rutinas";
        }
    }
}
