package com.sabi.sabi.impl;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.EjercicioAsignadoDTO;
import com.sabi.sabi.dto.RegistroSerieDTO;
import com.sabi.sabi.dto.SerieDTO;
import com.sabi.sabi.entity.Dia;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.RegistroSerie;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.repository.DiaRepository;
import com.sabi.sabi.repository.EjercicioAsignadoRepository;
import com.sabi.sabi.repository.RegistroSerieRepository;
import com.sabi.sabi.repository.SerieRepository;
import com.sabi.sabi.service.DiaService;
import com.sabi.sabi.service.EjercicioAsignadoService;
import com.sabi.sabi.service.RegistroSerieService;
import com.sabi.sabi.service.SerieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class RegistroSerieServiceImpl implements RegistroSerieService {
    @Autowired
    private RegistroSerieRepository registroSerieRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SerieService serieService;
    @Autowired
    private SerieRepository serieRepository;
    @Autowired
    private EjercicioAsignadoService ejercicioAsignadoService;
    @Autowired
    private EjercicioAsignadoRepository ejercicioAsignadoRepository;
    @Autowired
    private DiaService diaService;
    @Autowired
    private DiaRepository diaRepository;

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
        existingRegistroSerie.setIntensidadReal(registroSerieDTO.getIntensidadReal());
        existingRegistroSerie.setFechaEjecucion(registroSerieDTO.getFechaEjecucion());
        existingRegistroSerie.setComentariosCliente(registroSerieDTO.getComentariosCliente());
        existingRegistroSerie.setEstado(registroSerieDTO.getEstado());

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
        entity.setIntensidadReal(registroSerieDTO.getIntensidadReal());
        entity.setFechaEjecucion(registroSerieDTO.getFechaEjecucion());
        entity.setComentariosCliente(registroSerieDTO.getComentariosCliente());
        entity.setEstado(registroSerieDTO.getEstado());

        entity = registroSerieRepository.save(entity);
        log.debug("[RegistroSerie saveOrUpdate] {} registro id={} serie={} reps={} peso={} descanso={}",
                (creating?"CREADO":"ACTUALIZADO"), entity.getId(), serie.getId(), entity.getRepeticionesReales(), entity.getPesoReal(), entity.getDescansoReal());
        System.out.println("ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ESTADO ");
        System.out.println(registroSerieDTO.getEstado());

        if (registroSerieDTO.getEstado()){
            Optional<Serie> serieR = serieRepository.findById(registroSerieDTO.getIdSerie());
            Optional<EjercicioAsignado> ejercicioAsignado = ejercicioAsignadoRepository.findById(serieR.get().getEjercicioAsignado().getIdEjercicioAsignado());
            Optional<Dia> existingDia = diaRepository.findById(ejercicioAsignado.get().getDia().getId());
            List<EjercicioAsignado> ejes = ejercicioAsignadoRepository.getEjesDia(existingDia.get().getId());
            long ejers = 0;
            for (EjercicioAsignado eje : ejes){
                ejers ++;
                System.out.println("LISTA DE EJERCICIOS ASIGNADOS DEL DIA");
                System.out.println("EJERCICIO ASIGNADO NUMERO: "+ejers+" Y SU ESTADO: " +eje.getEstado());
                System.out.println("EJERCICIO ASIGNADO ID A CONSULTAR: " + eje.getIdEjercicioAsignado());
                List<Serie> seriesEje = serieRepository.getSerieEje(eje.getIdEjercicioAsignado());
                System.out.println("SERIES ENCONTRADAS EN EL EJERCICIO ASIGNADO: "+ seriesEje.size());
                long series = 0;
                for (Serie s : seriesEje){
                    series ++;
                    System.out.println("LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO LISTA DE SERIES DEL EJERCICIO ASIGNADO ");
                    System.out.println("SERIE NUMERO: "+series+" Y SU ESTADO: "+ s.getEstado());
                    List<RegistroSerieDTO> registrosSerie = getRegistroSeriesBySerieId(s.getId());
                    System.out.println("REGISTROS DE SERIE ENCONTRADOS EN LA SERIE: "+ registrosSerie.size());
                    // En este dominio cada Serie tiene a lo sumo 1 RegistroSerie. Comprobamos sólo el primero si existe
                    if (registrosSerie == null || registrosSerie.isEmpty()) {
                        return modelMapper.map(entity, RegistroSerieDTO.class);
                    }
                    RegistroSerieDTO reg = registrosSerie.get(0);
                    System.out.println("REGISTRO NUMERO: 1 Y SU ESTADO: " + reg.getEstado());
                    if (!Boolean.TRUE.equals(reg.getEstado())) {
                        return modelMapper.map(entity, RegistroSerieDTO.class);
                    }
                     serieService.desactivateSerie(s.getId());
                     // No comprobamos `s.getEstado()` aquí porque `s` puede estar stale tras el toggle;
                     // si necesitamos confirmar el nuevo estado habría que recargar la entidad desde el repo.
                }
                ejercicioAsignadoService.desactivateEjercicioAsignado(eje.getIdEjercicioAsignado());
                System.out.println("NUEVO ESTADO DEL EJERCICIO ASIGNADO: "+ejers+" ES: "+ eje.getEstado());
                if (!eje.getEstado()) {
                    return modelMapper.map(entity, RegistroSerieDTO.class);
                }
            }
            diaService.desactivateDia(existingDia.get().getId());
            System.out.println("NUEVO ESTADO DEL DIA ES: "+ existingDia.get().getEstado());
        }

        RegistroSerieDTO resp = modelMapper.map(entity, RegistroSerieDTO.class);
        resp.setIdSerie(serie.getId());
        return resp;
    }

    @Override
    public List<RegistroSerieDTO> getRegistroSeriesBySerieId(Long idSerie) {
        List<RegistroSerie> registroSeries = registroSerieRepository.findBySerie_Id(idSerie);
        return registroSeries.stream()
                .map(registroSerie -> modelMapper.map(registroSerie, RegistroSerieDTO.class))
                .toList();
    }
}
