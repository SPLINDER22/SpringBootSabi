package com.sabi.sabi.impl;

import com.sabi.sabi.dto.EjercicioDTO;
import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.repository.EjercicioRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.service.EjercicioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EjercicioServiceImpl implements EjercicioService {
    @Autowired
    private EjercicioRepository ejercicioRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<EjercicioDTO> getEjerciciosPorUsuario(Long usuarioId) {
        List<Ejercicio> ejercicios = ejercicioRepository.findActivosPorUsuario(usuarioId);
        return ejercicios.stream()
                .map(e -> modelMapper.map(e, EjercicioDTO.class))
                .toList();
    }


    @Override
    public EjercicioDTO getEjercicioById(long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + id));
        return modelMapper.map(ejercicio, EjercicioDTO.class);
    }

    @Override
    public EjercicioDTO createEjercicio(EjercicioDTO ejercicioDTO, Long usuarioId) {
        Ejercicio ejercicio = modelMapper.map(ejercicioDTO, Ejercicio.class);

        // Si existe el ID, se actualiza
        if (ejercicio.getId() != null && ejercicioRepository.findById(ejercicio.getId()).isPresent()) {
            return updateEjercicio(ejercicio.getId(), ejercicioDTO);
        }

        // Asignar usuario logueado como creador
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
        ejercicio.setUsuario(usuario);

        // Guardar
        ejercicio = ejercicioRepository.save(ejercicio);

        return modelMapper.map(ejercicio, EjercicioDTO.class);
    }


    @Override
    public EjercicioDTO updateEjercicio(long id, EjercicioDTO ejercicioDTO) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + id));
        if (ejercicioDTO.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(ejercicioDTO.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + ejercicioDTO.getIdUsuario()));
            ejercicio.setUsuario(usuario);
        }
        ejercicio.setNombre(ejercicioDTO.getNombre());
        ejercicio.setDescripcion(ejercicioDTO.getDescripcion());
        ejercicio.setGrupoMuscular(ejercicioDTO.getGrupoMuscular());
        ejercicio.setEquipo(ejercicioDTO.getEquipo());
        ejercicio.setUrlVideo(ejercicioDTO.getUrlVideo());
        ejercicio.setUrlImagen(ejercicioDTO.getUrlImagen());
        ejercicio.setTipo(ejercicioDTO.getTipo());

        ejercicio = ejercicioRepository.save(ejercicio);
        return modelMapper.map(ejercicio, EjercicioDTO.class);
    }

    @Override
    public boolean deleteEjercicio(long id) {
        if (!ejercicioRepository.findById(id).isPresent()){
            throw new RuntimeException("Ejercicio not found with id: " + id);
        }
        ejercicioRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateEjercicio(long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + id));
        ejercicio.setEstado(!ejercicio.getEstado());
        ejercicioRepository.save(ejercicio);
        return true;
    }
}
