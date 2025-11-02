package com.sabi.sabi.controller;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.EjercicioAsignadoDTO;
import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.service.*;
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
    @Autowired
    private SemanaService semanaService;

    private boolean hasRole(UserDetails userDetails, String role) {
        if (userDetails == null) return false;
        return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role) || a.getAuthority().equals(role));
    }

    @GetMapping("/ejes/detallar/{idDia}")
    public String detallarDia(@PathVariable Long idDia,
                              @org.springframework.web.bind.annotation.RequestParam(value = "readonly", required = false) Boolean readonly,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model, RedirectAttributes redirectAttributes) {
        DiaDTO diaDTO;
        try {
            diaDTO = diaService.getDiaById(idDia);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El dia especificado no existe.");
            return "redirect:/dias/detallar/" + idDia;
        }

        SemanaDTO semanaDTO = semanaService.getSemanaById(diaDTO.getIdSemana());
        List<?> semanas = semanaService.getSemanasRutina(semanaDTO.getIdRutina());
        List<?> dias = diaService.getDiasSemana(semanaDTO.getIdSemana());
        List<?> ejes = ejercicioAsignadoService.getEjesDia(idDia);
        boolean esEntrenador = hasRole(userDetails, "ENTRENADOR");
        boolean readonlyEffective = Boolean.TRUE.equals(readonly) && esEntrenador;

        model.addAttribute("idRutina", semanaDTO.getIdRutina());
        model.addAttribute("semanas", semanas);
        model.addAttribute("dias", dias);
        model.addAttribute("ejes", ejes);
        model.addAttribute("totalEjes", ejes.size());
        model.addAttribute("dia", diaDTO);
        if (diaDTO != null) {
            model.addAttribute("idSemana", diaDTO.getIdSemana());
        }
        model.addAttribute("readonly", readonlyEffective);
        return "ejercicios-asignados/lista";
    }

    @GetMapping("/ejes/crear/{idDia}")
    public String crearEjeView(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable Long idDia, Model model, RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            return "redirect:/ejes/detallar/" + idDia; // ya no forzamos readonly
        }
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
    public String editarEjeView(@PathVariable Long idEje, Model model, RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (hasRole(userDetails, "CLIENTE")) {
            EjercicioAsignadoDTO ejeDTO;
            try { ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(idEje); } catch (RuntimeException ex) { return "redirect:/rutinas"; }
            return "redirect:/ejes/detallar/" + ejeDTO.getIdDia();
        }
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
    public String guardarEje(@ModelAttribute EjercicioAsignadoDTO ejeDTO,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            return "redirect:/ejes/detallar/" + ejeDTO.getIdDia();
        }
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
    public String eliminarDia(@PathVariable Long idEje,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (hasRole(userDetails, "CLIENTE")) {
            EjercicioAsignadoDTO ejeDTO;
            try { ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(idEje); } catch (RuntimeException ex) { return "redirect:/rutinas"; }
            return "redirect:/ejes/detallar/" + ejeDTO.getIdDia();
        }
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

    // El cliente ahora SÍ puede togglear; el entrenador no (se mantiene sólo como gestión estructural)
    @GetMapping("/ejes/check/{idEje}")
    public String toggleChecked(@PathVariable Long idEje,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (!hasRole(userDetails, "CLIENTE")) { // no cliente: redirigir a vista sin acción
            EjercicioAsignadoDTO ejeDTO;
            try { ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(idEje); } catch (RuntimeException ex) { return "redirect:/rutinas"; }
            return "redirect:/ejes/detallar/" + ejeDTO.getIdDia() + "?readonly=true";
        }
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
