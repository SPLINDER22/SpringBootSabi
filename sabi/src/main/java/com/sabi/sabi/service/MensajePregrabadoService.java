package com.sabi.sabi.service;

import com.sabi.sabi.dto.MensajePregrabadoDTO;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.MensajePregrabado;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.repository.MensajePregrabadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MensajePregrabadoService {

    @Autowired
    private MensajePregrabadoRepository mensajePregrabadoRepository;

    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Transactional(readOnly = true)
    public List<MensajePregrabadoDTO> obtenerMensajesPorEntrenador(Long entrenadorId) {
        return mensajePregrabadoRepository.findByEntrenadorIdOrderByFechaCreacionDesc(entrenadorId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MensajePregrabadoDTO> obtenerMensajesActivosPorEntrenador(Long entrenadorId) {
        return mensajePregrabadoRepository.findByEntrenadorIdAndActivoTrue(entrenadorId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MensajePregrabadoDTO obtenerMensajePorId(Long id) {
        return mensajePregrabadoRepository.findById(id)
                .map(this::convertirADTO)
                .orElse(null);
    }

    @Transactional
    public MensajePregrabadoDTO crearMensaje(MensajePregrabadoDTO dto) {
        Entrenador entrenador = entrenadorRepository.findById(dto.getEntrenadorId())
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

        MensajePregrabado mensaje = MensajePregrabado.builder()
                .entrenador(entrenador)
                .titulo(dto.getTitulo())
                .contenido(dto.getContenido())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();

        mensaje = mensajePregrabadoRepository.save(mensaje);
        return convertirADTO(mensaje);
    }

    @Transactional
    public MensajePregrabadoDTO actualizarMensaje(Long id, MensajePregrabadoDTO dto) {
        MensajePregrabado mensaje = mensajePregrabadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));

        mensaje.setTitulo(dto.getTitulo());
        mensaje.setContenido(dto.getContenido());
        if (dto.getActivo() != null) {
            mensaje.setActivo(dto.getActivo());
        }

        mensaje = mensajePregrabadoRepository.save(mensaje);
        return convertirADTO(mensaje);
    }

    @Transactional
    public void eliminarMensaje(Long id) {
        mensajePregrabadoRepository.deleteById(id);
    }

    @Transactional
    public void desactivarMensaje(Long id) {
        MensajePregrabado mensaje = mensajePregrabadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        mensaje.setActivo(false);
        mensajePregrabadoRepository.save(mensaje);
    }

    @Transactional
    public void activarMensaje(Long id) {
        MensajePregrabado mensaje = mensajePregrabadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        mensaje.setActivo(true);
        mensajePregrabadoRepository.save(mensaje);
    }

    /**
     * Procesa el contenido del mensaje reemplazando variables
     * @param contenido El contenido con variables como {NOMBRE}, {DESCUENTO}, etc.
     * @param variables Map con los valores a reemplazar
     * @return El contenido procesado
     */
    public String procesarMensaje(String contenido, java.util.Map<String, String> variables) {
        String resultado = contenido;
        for (java.util.Map.Entry<String, String> entry : variables.entrySet()) {
            resultado = resultado.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resultado;
    }

    private MensajePregrabadoDTO convertirADTO(MensajePregrabado mensaje) {
        return MensajePregrabadoDTO.builder()
                .id(mensaje.getId())
                .entrenadorId(mensaje.getEntrenador().getId())
                .titulo(mensaje.getTitulo())
                .contenido(mensaje.getContenido())
                .fechaCreacion(mensaje.getFechaCreacion())
                .fechaActualizacion(mensaje.getFechaActualizacion())
                .activo(mensaje.getActivo())
                .build();
    }
}
