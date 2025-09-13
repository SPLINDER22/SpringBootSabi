package com.sabi.sabi.impl;

import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.entity.Semana;
import com.sabi.sabi.repository.RutinaRepository;
import com.sabi.sabi.repository.SemanaRepository;
import com.sabi.sabi.service.SemanaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemanaServiceImpl implements SemanaService {
    @Autowired
    private SemanaRepository semanaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RutinaRepository rutinaRepository;

    @Override
    public List<SemanaDTO> getAllSemanas() {
        List<Semana> semanas = semanaRepository.findAll();
        return semanas.stream()
                .map(semana -> modelMapper.map(semana, SemanaDTO.class))
                .toList();
    }

    @Override
    public List<SemanaDTO> getAllActiveSemanas() {
        List<Semana> semanas = semanaRepository.findByEstadoTrue();
        return semanas.stream()
                .map(semana -> modelMapper.map(semana, SemanaDTO.class))
                .toList();
    }

    @Override
    public SemanaDTO getSemanaById(long id) {
        Semana semana = semanaRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        return modelMapper.map(semana, SemanaDTO.class);
    }

    @Override
    public SemanaDTO createSemana(SemanaDTO semanaDTO) {
        Semana semana = modelMapper.map(semanaDTO, Semana.class);
        if (semana.getId() != null && semanaRepository.findById(semana.getId()).isPresent()){
            updateSemana(semana.getId(), semanaDTO);
        }
        if (semanaDTO.getIdRutina() != null){
            Rutina rutina = rutinaRepository.findById(semanaDTO.getIdRutina())
                    .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + semanaDTO.getIdRutina()));
            semana.setRutina(rutina);
        }
        semana = semanaRepository.save(semana);
        return modelMapper.map(semana, SemanaDTO.class);
    }

    @Override
    public SemanaDTO updateSemana(long id, SemanaDTO semanaDTO) {
        Semana existingSemana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        if (semanaDTO.getIdRutina() != null){
            Rutina rutina = rutinaRepository.findById(semanaDTO.getIdRutina())
                    .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + semanaDTO.getIdRutina()));
            existingSemana.setRutina(rutina);
        }
        existingSemana.setNumeroSemana(semanaDTO.getNumeroSemana());
        existingSemana.setDescripcion(semanaDTO.getDescripcion());

        existingSemana = semanaRepository.save(existingSemana);
        return modelMapper.map(existingSemana, SemanaDTO.class);
    }

    @Override
    public boolean deleteSemana(long id) {
        Semana semana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        semanaRepository.delete(semana);
        return true;
    }

    @Override
    public boolean desactivateSemana(long id) {
        Semana existingSemana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        existingSemana.setEstado(!existingSemana.getEstado());
        semanaRepository.save(existingSemana);
        return true;
    }
}
