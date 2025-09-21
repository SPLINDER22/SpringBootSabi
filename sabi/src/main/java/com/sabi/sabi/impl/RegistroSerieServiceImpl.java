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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class RegistroSerieServiceImpl implements RegistroSerieService {
    @Autowired
    private RegistroSerieRepository registroSerieRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SerieRepository serieRepository;

    private static final Logger log = LoggerFactory.getLogger(RegistroSerieServiceImpl.class);

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

    @Override
    public RegistroSerieDTO saveOrUpdateRegistroSerie(RegistroSerieDTO registroSerieDTO) {
        if (registroSerieDTO.getIdSerie() == null) {
            throw new IllegalArgumentException("Id de serie requerido");
        }
        // Asegurar fecha ejecucion
        if (registroSerieDTO.getFechaEjecucion() == null) {
            registroSerieDTO.setFechaEjecucion(java.time.LocalDateTime.now());
        }
        Serie serie = serieRepository.findById(registroSerieDTO.getIdSerie())
                .orElseThrow(() -> new RuntimeException("Serie not found with id: " + registroSerieDTO.getIdSerie()));

        RegistroSerie entity = null;
        // Si viene id intentar cargar
        if (registroSerieDTO.getIdRegistroSerie() != null) {
            entity = registroSerieRepository.findById(registroSerieDTO.getIdRegistroSerie()).orElse(null);
            if (entity == null) {
                log.debug("[RegistroSerie saveOrUpdate] Id {} no existe, se creará nuevo registro para serie {}", registroSerieDTO.getIdRegistroSerie(), registroSerieDTO.getIdSerie());
            }
        }
        // Si no se obtuvo entidad por id, buscar por serie (único registro por serie)
        if (entity == null) {
            entity = registroSerieRepository.findFirstBySerie_Id(registroSerieDTO.getIdSerie()).orElse(null);
        }
        boolean creating = (entity == null);
        if (creating) {
            entity = new RegistroSerie();
            entity.setSerie(serie);
        } else {
            // Reasignar serie por si acaso (normalmente no cambia)
            entity.setSerie(serie);
        }
        entity.setRepeticionesReales(registroSerieDTO.getRepeticionesReales());
        entity.setPesoReal(registroSerieDTO.getPesoReal());
        entity.setDescansoReal(registroSerieDTO.getDescansoReal());
        entity.setFechaEjecucion(registroSerieDTO.getFechaEjecucion());
        entity.setComentariosCliente(registroSerieDTO.getComentariosCliente());
        if (entity.getEstado() == null) entity.setEstado(true);

        entity = registroSerieRepository.save(entity);
        log.debug("[RegistroSerie saveOrUpdate] {} registro id={} serie={} reps={} peso={} descanso={}",
                (creating?"CREADO":"ACTUALIZADO"), entity.getId(), serie.getId(), entity.getRepeticionesReales(), entity.getPesoReal(), entity.getDescansoReal());

        RegistroSerieDTO resp = modelMapper.map(entity, RegistroSerieDTO.class);
        resp.setIdSerie(serie.getId());
        return resp;
    }
}
