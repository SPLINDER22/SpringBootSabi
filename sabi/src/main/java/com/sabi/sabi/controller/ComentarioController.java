package com.sabi.sabi.controller;

import com.sabi.sabi.dto.ComentarioDTO;
import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.EstadoRutina;
import com.sabi.sabi.service.ComentarioService;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;
    @Autowired
    private RutinaService rutinaService;
    @Autowired
    private UsuarioService usuarioService;

    // LISTA: según rol muestra comentarios propios (cliente) o sobre el entrenador
    @GetMapping("/comentarios")
    public String listarComentarios(@AuthenticationPrincipal UserDetails userDetails,
                                    Model model,
                                    @RequestParam(value = "rutinaId", required = false) Long rutinaId,
                                    @RequestParam(value = "clienteId", required = false) Long clienteId,
                                    @RequestParam(value = "entrenadorId", required = false) Long entrenadorId) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario usuario;
        try { usuario = usuarioService.obtenerPorEmail(userDetails.getUsername()); } catch (RuntimeException ex) { return "redirect:/auth/login"; }

        List<ComentarioDTO> comentarios;
        // Prioridades de filtro explícito
        if (rutinaId != null) {
            comentarios = comentarioService.getComentariosPorRutina(rutinaId);
        } else if (clienteId != null) {
            comentarios = comentarioService.getComentariosPorCliente(clienteId);
        } else if (entrenadorId != null) {
            comentarios = comentarioService.getComentariosPorEntrenador(entrenadorId);
        } else {
            // Deducción por rol
            switch (usuario.getRol()) {
                case CLIENTE -> comentarios = comentarioService.getComentariosPorCliente(usuario.getId());
                case ENTRENADOR -> comentarios = comentarioService.getComentariosPorEntrenador(usuario.getId());
                default -> comentarios = comentarioService.getAllActiveComentarios();
            }
        }
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("idUsuarioActual", usuario.getId());
        model.addAttribute("esCliente", usuario.getRol() == com.sabi.sabi.entity.enums.Rol.CLIENTE);
        model.addAttribute("esEntrenador", usuario.getRol() == com.sabi.sabi.entity.enums.Rol.ENTRENADOR);
        model.addAttribute("rutinaId", rutinaId);
        return "comentarios/lista";
    }

    // creacion de comentario cuando finaliza rutina (cliente)
    @GetMapping("/comentarios/nuevo")
    public String nuevoComentarioView(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam(value = "rutinaId", required = false) Long rutinaId,
                                      Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        ComentarioDTO dto = new ComentarioDTO();
        dto.setIdCliente(usuario.getId());
        if (rutinaId != null) {
            RutinaDTO r = rutinaService.getRutinaById(rutinaId);
            if (r == null || r.getEstadoRutina() != EstadoRutina.FINALIZADA || !Objects.equals(r.getIdCliente(), usuario.getId())) {
                return "redirect:/comentarios?error=rutina_no_valida";
            }
            dto.setIdRutina(rutinaId);
            if (r.getIdEntrenador() != null) dto.setIdEntrenador(r.getIdEntrenador());
        }
        model.addAttribute("comentario", dto);
        model.addAttribute("esEdicion", false);
        return "comentarios/formulario";
    }

    // FORM EDITAR comentario existente (solo cliente propietario)
    @GetMapping("/comentarios/editar/{id}")
    public String editarComentarioView(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        ComentarioDTO existente = comentarioService.getComentarioById(id);
        if (existente == null) return "redirect:/comentarios?error=notfound";
        if (!Objects.equals(existente.getIdCliente(), usuario.getId())) {
            return "redirect:/comentarios?error=forbidden";
        }
        model.addAttribute("comentario", existente);
        model.addAttribute("esEdicion", true);
        return "comentarios/formulario";
    }

    // GUARDAR (crear o actualizar). Se diferencia por idComentario presente.
    @PostMapping("/comentarios/guardar")
    public String guardarComentario(@AuthenticationPrincipal UserDetails userDetails,
                                    @ModelAttribute("comentario") @Valid ComentarioDTO comentarioDTO) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        // Asegurar propiedad
        if (comentarioDTO.getIdComentario() != null) { // update
            ComentarioDTO existente = comentarioService.getComentarioById(comentarioDTO.getIdComentario());
            if (existente == null) return "redirect:/comentarios?error=notfound";
            if (!Objects.equals(existente.getIdCliente(), usuario.getId())) {
                return "redirect:/comentarios?error=forbidden";
            }
            // Mantener cliente/entrenador/rutina originales
            comentarioDTO.setIdCliente(existente.getIdCliente());
            comentarioDTO.setIdEntrenador(existente.getIdEntrenador());
            comentarioDTO.setIdRutina(existente.getIdRutina());
            comentarioService.actualizarComentario(existente.getIdComentario(), comentarioDTO);
        } else { // create
            comentarioDTO.setIdCliente(usuario.getId());
            // Validar rutina finalizada si se provee
            if (comentarioDTO.getIdRutina() != null) {
                RutinaDTO r = rutinaService.getRutinaById(comentarioDTO.getIdRutina());
                if (r == null || r.getEstadoRutina() != EstadoRutina.FINALIZADA || !Objects.equals(r.getIdCliente(), usuario.getId())) {
                    return "redirect:/comentarios?error=rutina_no_valida";
                }
                if (comentarioDTO.getIdEntrenador() == null && r.getIdEntrenador() != null) {
                    comentarioDTO.setIdEntrenador(r.getIdEntrenador());
                }
            }
            comentarioService.crearComentario(comentarioDTO);
        }
        return "redirect:/comentarios";
    }

    @PostMapping("/comentarios/eliminar/{id}")
    public String eliminarComentario(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/auth/login";
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        ComentarioDTO existente = comentarioService.getComentarioById(id);
        if (existente == null) return "redirect:/comentarios?error=notfound";
        if (!Objects.equals(existente.getIdCliente(), usuario.getId())) return "redirect:/comentarios?error=forbidden";
        comentarioService.eliminarComentario(id);
        return "redirect:/comentarios";
    }

    // DETALLE SOLO LECTURA
    @GetMapping("/comentarios/detalle/{id}")
    public String detalleComentario(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        ComentarioDTO c = comentarioService.getComentarioById(id);
        if (c == null) return "redirect:/comentarios?error=notfound";
        model.addAttribute("comentario", c);
        return "comentarios/detalle";
    }
}
