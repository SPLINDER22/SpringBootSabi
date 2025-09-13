package com.sabi.sabi.impl;

import com.sabi.sabi.dto.RegistroSerieDTO;
import com.sabi.sabi.entity.RegistroSerie;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.repository.RegistroSerieRepository;
import com.sabi.sabi.repository.SerieRepository;
import com.sabi.sabi.service.RegistroSerieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroSerieServiceImpl implements RegistroSerieService {
    @Autowired
    private RegistroSerieRepository registroSerieRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SerieRepository serieRepository;

    @Override
    public List<RegistroSerieDTO> getAllRegistroSeries() {
        List<RegistroSerie> registroSeries = registroSerieRepository.findAll();
        return registroSeries.stream()
                .map(registroSerie -> modelMapper.map(registroSerie, RegistroSerieDTO.class))
                .toList();
    }

    @Override
    public List<RegistroSerieDTO> getAllActiveRegistroSeries() {
        List<RegistroSerie> registroSeries = registroSerieRepository.findByEstadoTrue();
        return registroSeries.stream()
                .map(registroSerie -> modelMapper.map(registroSerie, RegistroSerieDTO.class))
                .toList();
    }

    @Override
    public RegistroSerieDTO getRegistroSerieById(long id) {
        RegistroSerie registroSerie = registroSerieRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("RegistroSerie not found with id: " + id));
        return modelMapper.map(registroSerie, RegistroSerieDTO.class);
    }

    @Override
    public RegistroSerieDTO createRegistroSerie(RegistroSerieDTO registroSerieDTO) {
        RegistroSerie registroSerie = modelMapper.map(registroSerieDTO, RegistroSerie.class);
        if (registroSerie.getId() != null && registroSerieRepository.findById(registroSerie.getId()).isPresent()){
            updateRegistroSerie(registroSerie.getId(), registroSerieDTO);
        }
        if (registroSerieDTO.getIdSerie() != null) {
            Serie serie = serieRepository.findById(registroSerieDTO.getIdSerie())
                    .orElseThrow(() -> new RuntimeException("Serie not found with id: " + registroSerieDTO.getIdSerie()));
            registroSerie.setSerie(serie);
        }
        registroSerie = registroSerieRepository.save(registroSerie);
        return modelMapper.map(registroSerie, RegistroSerieDTO.class);
    }

    @Override
    public RegistroSerieDTO updateRegistroSerie(long id, RegistroSerieDTO registroSerieDTO) {
        RegistroSerie existingRegistroSerie = registroSerieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegistroSerie not found with id: " + id));
        if (registroSerieDTO.getIdSerie() != null) {
            Serie serie = serieRepository.findById(registroSerieDTO.getIdSerie())
                    .orElseThrow(() -> new RuntimeException("Serie not found with id: " + registroSerieDTO.getIdSerie()));
            existingRegistroSerie.setSerie(serie);
        }
        existingRegistroSerie.setRepeticionesReales(registroSerieDTO.getRepeticionesReales());
        existingRegistroSerie.setPesoReal(registroSerieDTO.getPesoReal());
        existingRegistroSerie.setDescansoReal(registroSerieDTO.getDescansoReal());
        existingRegistroSerie.setFechaEjecucion(registroSerieDTO.getFechaEjecucion());
        existingRegistroSerie.setComentariosCliente(registroSerieDTO.getComentariosCliente());

        existingRegistroSerie = registroSerieRepository.save(existingRegistroSerie);
        return modelMapper.map(existingRegistroSerie, RegistroSerieDTO.class);
    }

    @Override
    public boolean deleteRegistroSerie(long id) {
        if (!registroSerieRepository.findById(id).isPresent()){
            throw new RuntimeException("RegistroSerie not found with id: " + id);
        }
        registroSerieRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateRegistroSerie(long id) {
        RegistroSerie registroSerie = registroSerieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegistroSerie not found with id: " + id));
        registroSerie.setEstado(!registroSerie.getEstado());
        registroSerieRepository.save(registroSerie);
        return true;
    }
}
