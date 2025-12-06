package com.sabi.sabi.controller;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.EjercicioAsignadoDTO;
import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.dto.SerieDTO;
import com.sabi.sabi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SerieController {
    @Autowired
    private SerieService serieService;
    @Autowired
    private EjercicioAsignadoService ejercicioAsignadoService;
    @Autowired
    private EjercicioService ejercicioService;
    @Autowired
    private SemanaService semanaService;
    @Autowired
    private DiaService diaService;

    @GetMapping("/series/detallar/{idEje}")
    public String detallarSeries(@PathVariable Long idEje, Model model, RedirectAttributes redirectAttributes) {
        EjercicioAsignadoDTO ejeDTO;
        try {
            ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(idEje);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El ejercicio especificado no existe.");
            return "redirect:/rutinas";
        }

        DiaDTO diaDTO = diaService.getDiaById(ejeDTO.getIdDia());
        SemanaDTO semanaDTO = semanaService.getSemanaById(diaDTO.getIdSemana());
        List<?> semanas = semanaService.getSemanasRutina(semanaDTO.getIdRutina());
        List<?> dias = diaService.getDiasSemana(semanaDTO.getIdSemana());
        List<?> ejes = ejercicioAsignadoService.getEjesDia(diaDTO.getIdDia());
        List<?> series = serieService.getSerieEje(idEje);

        model.addAttribute("dia", diaDTO);
        if (diaDTO != null) {
            model.addAttribute("idSemana", diaDTO.getIdSemana());
        }
        if (semanaDTO != null) {
            model.addAttribute("idRutina", semanaDTO.getIdRutina());
        }
        model.addAttribute("semanas", semanas);
        model.addAttribute("dias", dias);
        model.addAttribute("ejes", ejes);
        model.addAttribute("series", series);
        model.addAttribute("totalSeries", series.size());
        model.addAttribute("ejercicioAsignado", ejeDTO);

        if (ejeDTO != null) {
            model.addAttribute("idDia", ejeDTO.getIdDia());
            if (ejeDTO.getNombreEjercicio() != null && !ejeDTO.getNombreEjercicio().isBlank()) {
                model.addAttribute("nombreEjercicio", ejeDTO.getNombreEjercicio());
            } else if (ejeDTO.getIdEjercicio() != null) {
                try {
                    var ejDTO = ejercicioService.getEjercicioById(ejeDTO.getIdEjercicio());
                    if (ejDTO != null) {
                        model.addAttribute("nombreEjercicio", ejDTO.getNombre());
                    }
                } catch (RuntimeException ignored) { }
            }
        }

        return "series/lista";
    }

    @GetMapping("/series/crear/{idEje}")
    public String crearSerieView(@PathVariable Long idEje, Model model, RedirectAttributes redirectAttributes) {
        EjercicioAsignadoDTO ejeDTO;
        try {
            ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(idEje);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "El ejercicio no existe.");
            return "redirect:/rutinas";
        }

        var series = serieService.getSerieEje(idEje);
        int total = series != null ? series.size() : 0;
        SerieDTO serieDTO = new SerieDTO();
        serieDTO.setIdEjercicioAsignado(idEje);
        serieDTO.setOrden((long) (total + 1));

        model.addAttribute("ejercicioAsignado", ejeDTO);
        model.addAttribute("series", series);
        model.addAttribute("totalSeries", total);
        model.addAttribute("serie", serieDTO);

        if (ejeDTO != null) {
            model.addAttribute("idDia", ejeDTO.getIdDia());
            if (ejeDTO.getNombreEjercicio() != null && !ejeDTO.getNombreEjercicio().isBlank()) {
                model.addAttribute("nombreEjercicio", ejeDTO.getNombreEjercicio());
            } else if (ejeDTO.getIdEjercicio() != null) {
                try {
                    var ejDTO = ejercicioService.getEjercicioById(ejeDTO.getIdEjercicio());
                    if (ejDTO != null) {
                        model.addAttribute("nombreEjercicio", ejDTO.getNombre());
                    }
                } catch (RuntimeException ignored) { }
            }
        }
        return "series/formulario";
    }

    @GetMapping("/series/editar/{idSerie}")
    public String editarSerieView(@PathVariable Long idSerie, Model model, RedirectAttributes redirectAttributes) {
        SerieDTO serieDTO;
        try {
            serieDTO = serieService.getSerieById(idSerie);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La serie no existe.");
            return "redirect:/rutinas";
        }

        EjercicioAsignadoDTO ejeDTO = ejercicioAsignadoService.getEjercicioAsignadoById(serieDTO.getIdEjercicioAsignado());
        var series = serieService.getSerieEje(ejeDTO.getIdEjercicioAsignado());
        int total = series != null ? series.size() : 0;

        model.addAttribute("ejercicioAsignado", ejeDTO);
        model.addAttribute("series", series);
        model.addAttribute("totalSeries", total);
        model.addAttribute("serie", serieDTO);

        if (ejeDTO != null) {
            model.addAttribute("idDia", ejeDTO.getIdDia());
            if (ejeDTO.getNombreEjercicio() != null && !ejeDTO.getNombreEjercicio().isBlank()) {
                model.addAttribute("nombreEjercicio", ejeDTO.getNombreEjercicio());
            } else if (ejeDTO.getIdEjercicio() != null) {
                try {
                    var ejDTO = ejercicioService.getEjercicioById(ejeDTO.getIdEjercicio());
                    if (ejDTO != null) {
                        model.addAttribute("nombreEjercicio", ejDTO.getNombre());
                    }
                } catch (RuntimeException ignored) { }
            }
        }
        return "series/formulario";
    }

    @PostMapping("/series/guardar")
    public String guardarSerie(@ModelAttribute SerieDTO serieDTO, RedirectAttributes redirectAttributes) {
        SerieDTO result = serieService.createSerie(serieDTO);
        Long idEje = result != null && result.getIdEjercicioAsignado() != null
                ? result.getIdEjercicioAsignado()
                : serieDTO.getIdEjercicioAsignado();
        redirectAttributes.addFlashAttribute("success", serieDTO.getId() == null ? "Serie creada correctamente." : "Serie actualizada correctamente.");
        return "redirect:/series/detallar/" + idEje;
    }

    @PostMapping("/series/eliminar/{idSerie}")
    public String eliminarSerie(@PathVariable Long idSerie, RedirectAttributes redirectAttributes) {
        SerieDTO serieDTO;
        try {
            serieDTO = serieService.getSerieById(idSerie);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "La serie no existe.");
            return "redirect:/rutinas";
        }
        serieService.deleteSerie(idSerie);
        redirectAttributes.addFlashAttribute("success", "Serie eliminada correctamente.");
        return "redirect:/series/detallar/" + serieDTO.getIdEjercicioAsignado();
    }
}
