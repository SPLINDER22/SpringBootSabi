package com.sabi.sabi.impl;

import com.sabi.sabi.dto.DiagnosticoDTO;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Diagnostico;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.DiagnosticoRepository;
import com.sabi.sabi.service.DiagnosticoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticoServiceImpl implements DiagnosticoService {
    @Autowired
    private DiagnosticoRepository diagnosticoRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public List<DiagnosticoDTO> getAllDiagnosticos() {
        List<Diagnostico> diagnosticos = diagnosticoRepository.findAll();
        return diagnosticos.stream()
                .map(diagnostico -> modelMapper.map(diagnostico, DiagnosticoDTO.class))
                .toList();
    }

    @Override
    public List<DiagnosticoDTO> getAllActiveDiagnosticos() {
        List<Diagnostico> diagnosticos = diagnosticoRepository.findByEstadoTrue();
        return diagnosticos.stream()
                .map(diagnostico -> modelMapper.map(diagnostico, DiagnosticoDTO.class))
                .toList();
    }

    @Override
    public DiagnosticoDTO getDiagnosticoById(long id) {
        Diagnostico diagnostico = diagnosticoRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Diagnostico not found with id: " + id));
        return modelMapper.map(diagnostico, DiagnosticoDTO.class);
    }

    @Override
    public DiagnosticoDTO createDiagnostico(DiagnosticoDTO diagnosticoDTO) {
        Diagnostico diagnostico = modelMapper.map(diagnosticoDTO, Diagnostico.class);
        if (diagnostico.getId() != null && diagnosticoRepository.findById(diagnostico.getId()).isPresent()){
            updateDiagnostico(diagnostico.getId(), diagnosticoDTO);
        }
        if (diagnosticoDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(diagnosticoDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + diagnosticoDTO.getIdCliente()));
            diagnostico.setCliente(cliente);
        }
        diagnostico = diagnosticoRepository.save(diagnostico);
        return modelMapper.map(diagnostico, DiagnosticoDTO.class);
    }

    @Override
    public DiagnosticoDTO updateDiagnostico(long id, DiagnosticoDTO diagnosticoDTO) {
        Diagnostico existingDiagnostico = diagnosticoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnostico not found with id: " + id));
        if (diagnosticoDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(diagnosticoDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + diagnosticoDTO.getIdCliente()));
            existingDiagnostico.setCliente(cliente);
        }
        existingDiagnostico.setFecha(diagnosticoDTO.getFecha());
        existingDiagnostico.setProximoDiagnostico(diagnosticoDTO.getProximoDiagnostico());
        existingDiagnostico.setPeso(diagnosticoDTO.getPeso());
        existingDiagnostico.setEstatura(diagnosticoDTO.getEstatura());
        existingDiagnostico.setNivelExperiencia(diagnosticoDTO.getNivelExperiencia());
        existingDiagnostico.setDisponibilidadTiempo(diagnosticoDTO.getDisponibilidadTiempo());
        existingDiagnostico.setAccesoRecursos(diagnosticoDTO.getAccesoRecursos());
        existingDiagnostico.setLesiones(diagnosticoDTO.getLesiones());
        existingDiagnostico.setCondicionesMedicas(diagnosticoDTO.getCondicionesMedicas());
        existingDiagnostico.setFotoFrontalUrl(diagnosticoDTO.getFotoFrontalUrl());
        existingDiagnostico.setFotoLateralUrl(diagnosticoDTO.getFotoLateralUrl());
        existingDiagnostico.setFotoTraseraUrl(diagnosticoDTO.getFotoTraseraUrl());
        existingDiagnostico.setPorcentajeGrasaCorporal(diagnosticoDTO.getPorcentajeGrasaCorporal());
        existingDiagnostico.setCircunferenciaCintura(diagnosticoDTO.getCircunferenciaCintura());
        existingDiagnostico.setCircunferenciaCadera(diagnosticoDTO.getCircunferenciaCadera());
        existingDiagnostico.setCircunferenciaBrazo(diagnosticoDTO.getCircunferenciaBrazo());
        existingDiagnostico.setCircunferenciaPierna(diagnosticoDTO.getCircunferenciaPierna());
        existingDiagnostico.setFrecuenciaCardiacaReposo(diagnosticoDTO.getFrecuenciaCardiacaReposo());
        existingDiagnostico.setPresionArterial(diagnosticoDTO.getPresionArterial());
        existingDiagnostico.setHorasSueno(diagnosticoDTO.getHorasSueno());
        existingDiagnostico.setHabitosAlimenticios(diagnosticoDTO.getHabitosAlimenticios());

        existingDiagnostico = diagnosticoRepository.save(existingDiagnostico);
        return modelMapper.map(existingDiagnostico, DiagnosticoDTO.class);
    }

    @Override
    public boolean deleteDiagnostico(long id) {
        Diagnostico diagnostico = diagnosticoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnostico not found with id: " + id));
        diagnosticoRepository.delete(diagnostico);
        return true;
    }

    @Override
    public boolean desactivateDiagnostico(long id) {
        Diagnostico diagnostico = diagnosticoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnostico not found with id: " + id));
        diagnostico.setEstado(!diagnostico.getEstado());
        diagnosticoRepository.save(diagnostico);
        return true;
    }
}
