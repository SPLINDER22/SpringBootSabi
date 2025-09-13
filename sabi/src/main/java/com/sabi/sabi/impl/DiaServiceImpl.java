package com.sabi.sabi.impl;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.entity.Dia;
import com.sabi.sabi.entity.Semana;
import com.sabi.sabi.repository.DiaRepository;
import com.sabi.sabi.repository.SemanaRepository;
import com.sabi.sabi.service.DiaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaServiceImpl implements DiaService {
    @Autowired
    private DiaRepository diaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SemanaRepository semanaRepository;

    @Override
    public List<DiaDTO> getAllDia() {
        List<Dia> dia = diaRepository.findAll();
        return dia.stream()
                .map(diaItem -> modelMapper.map(diaItem, DiaDTO.class))
                .toList();
    }

    @Override
    public List<DiaDTO> getAllActiveDia() {
        List<Dia> dia = diaRepository.findByEstadoTrue();
        return dia.stream()
                .map(diaItem -> modelMapper.map(diaItem, DiaDTO.class))
                .toList();
    }

    @Override
    public DiaDTO getDiaById(long id) {
        Dia dia = diaRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + id));
        return modelMapper.map(dia, DiaDTO.class);
    }

    @Override
    public DiaDTO createDia(DiaDTO diaDTO) {
        Dia dia = modelMapper.map(diaDTO, Dia.class);
        if (dia.getId() != null && diaRepository.findById(dia.getId()).isPresent()){
            updateDia(dia.getId(), diaDTO);
        }
        if (diaDTO.getIdSemana() != null) {
            Semana semana = semanaRepository.findById(diaDTO.getIdSemana())
                    .orElseThrow(() -> new RuntimeException("Semana not found with id: " + diaDTO.getIdSemana()));
            dia.setSemana(semana);
        }
        dia = diaRepository.save(dia);
        return modelMapper.map(dia, DiaDTO.class);
    }

    @Override
    public DiaDTO updateDia(long id, DiaDTO diaDTO) {
        Dia existingDia = diaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + id));
        if (diaDTO.getIdSemana() != null) {
            Semana semana = semanaRepository.findById(diaDTO.getIdSemana())
                    .orElseThrow(() -> new RuntimeException("Semana not found with id: " + diaDTO.getIdSemana()));
            existingDia.setSemana(semana);
        }
        existingDia.setNumeroDia(diaDTO.getNumeroDia());
        existingDia.setDescripcion(diaDTO.getDescripcion());

        existingDia = diaRepository.save(existingDia);
        return modelMapper.map(existingDia, DiaDTO.class);
    }

    @Override
    public boolean deleteDia(long id) {
        if (!diaRepository.findById(id).isPresent()) {
            throw new RuntimeException("Dia not found with id: " + id);
        }
        diaRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateDia(long id) {
        Dia dia = diaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + id));
        dia.setEstado(!dia.getEstado());
        diaRepository.save(dia);
        return true;
    }
}
