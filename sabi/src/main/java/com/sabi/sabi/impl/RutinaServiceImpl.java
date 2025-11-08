package com.sabi.sabi.impl;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.entity.*;
import com.sabi.sabi.entity.enums.EstadoRutina;
import com.sabi.sabi.repository.*;
import com.sabi.sabi.service.RutinaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RutinaServiceImpl implements RutinaService {
    @Autowired
    private RutinaRepository rutinaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EntrenadorRepository entrenadorRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private SemanaRepository semanaRepository;
    @Autowired
    private DiaRepository diaRepository;
    @Autowired
    private EjercicioAsignadoRepository ejercicioAsignadoRepository;
    @Autowired
    private SerieRepository serieRepository;

    // Helper centralizado para mapear entidad -> DTO (id vs idRutina)
    private RutinaDTO mapToDTO(Rutina r) {
        if (r == null) return null;
        RutinaDTO dto = modelMapper.map(r, RutinaDTO.class);
        dto.setIdRutina(r.getId());
        if (r.getCliente() != null) dto.setIdCliente(r.getCliente().getId());
        if (r.getEntrenador() != null) dto.setIdEntrenador(r.getEntrenador().getId());
        return dto;
    }

    private void applyDtoIdToEntity(RutinaDTO dto, Rutina entidad) {
        if (dto.getIdRutina() != null) entidad.setId(dto.getIdRutina());
    }

    @Override
    public List<RutinaDTO> getAllRutinas() {
        return rutinaRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<RutinaDTO> getAllActiveRutinas() {
        return rutinaRepository.findByEstadoTrue().stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<RutinaDTO> getRutinasPorUsuario(Long usuarioId) {
        return rutinaRepository.getRutinasPorUsuario(usuarioId).stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<Rutina> findByClienteAndEstado(Cliente cliente) {
        return rutinaRepository.findByClienteAndEstado(cliente);
    }

    @Override
    public RutinaDTO getRutinaActivaCliente(Long clienteId) {
        if (clienteId == null) return null;
        return rutinaRepository.findActiveByClienteId(clienteId).map(this::mapToDTO).orElse(null);
    }

    @Override
    public List<RutinaDTO> getRutinasGlobales() {
        return rutinaRepository.findByClienteIsNullAndEntrenadorIsNullAndEstadoTrue()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public void adoptarRutina(long idRutina, long idCliente) {
        if (idRutina == 0L || idCliente == 0L) {
            throw new IllegalArgumentException("Ids no pueden ser cero");
        }
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + idCliente));
        Rutina origen = rutinaRepository.findById(idRutina)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada: " + idRutina));
        if (origen.getCliente() != null && !origen.getCliente().getId().equals(idCliente)) {
            throw new IllegalArgumentException("La rutina origen pertenece a otro cliente");
        }
        rutinaRepository.findActiveByClienteId(idCliente).ifPresent(prev -> {
            prev.setEstadoRutina(EstadoRutina.FINALIZADA);
            rutinaRepository.save(prev);
        });

        Rutina nueva = new Rutina();
        nueva.setId(null);
        nueva.setNombre(origen.getNombre());
        nueva.setObjetivo(origen.getObjetivo());
        nueva.setDescripcion(origen.getDescripcion());
        nueva.setFechaCreacion(origen.getFechaCreacion());
        nueva.setEstadoRutina(EstadoRutina.ACTIVA);
        nueva.setNumeroSemanas(origen.getNumeroSemanas());
        nueva.setCliente(cliente);
        nueva.setEntrenador(origen.getEntrenador());
        nueva.setEstado(true);
        nueva.setSemanas(null);
        nueva = rutinaRepository.save(nueva);

        List<Semana> semanasOrigen = semanaRepository.getSemanasRutina(origen.getId());
        if (semanasOrigen != null) {
            for (Semana sOri : semanasOrigen) {
                if (sOri == null) continue; // copiar siempre la semana aunque su estado sea false
                Semana sNueva = new Semana();
                sNueva.setId(null);
                sNueva.setNumeroSemana(sOri.getNumeroSemana());
                sNueva.setDescripcion(sOri.getDescripcion());
                sNueva.setNumeroDias(sOri.getNumeroDias());
                sNueva.setRutina(nueva);
                sNueva.setEstado(false);
                sNueva.setDias(null);
                sNueva = semanaRepository.save(sNueva);

                List<Dia> diasOrigen = diaRepository.getDiasSemana(sOri.getId());
                if (diasOrigen != null) {
                    for (Dia dOri : diasOrigen) {
                        if (dOri == null) continue; // copiar siempre el día aunque su estado sea false
                        Dia dNuevo = new Dia();
                        dNuevo.setId(null);
                        dNuevo.setNumeroDia(dOri.getNumeroDia());
                        dNuevo.setDescripcion(dOri.getDescripcion());
                        dNuevo.setNumeroEjercicios(dOri.getNumeroEjercicios());
                        dNuevo.setSemana(sNueva);
                        dNuevo.setEstado(false);
                        dNuevo.setEjerciciosAsignados(null);
                        dNuevo = diaRepository.save(dNuevo);

                        List<EjercicioAsignado> ejesOrigen = ejercicioAsignadoRepository.findByDia(dOri);
                        if (ejesOrigen != null) {
                            for (EjercicioAsignado ejeOri : ejesOrigen) {
                                if (ejeOri == null) continue; // copiar siempre el ejercicio aunque su estado sea false
                                EjercicioAsignado ejeNuevo = new EjercicioAsignado();
                                ejeNuevo.setIdEjercicioAsignado(null);
                                ejeNuevo.setOrden(ejeOri.getOrden());
                                ejeNuevo.setComentarios(ejeOri.getComentarios());
                                ejeNuevo.setNumeroSeries(ejeOri.getNumeroSeries());
                                ejeNuevo.setDia(dNuevo);
                                ejeNuevo.setEjercicio(ejeOri.getEjercicio());
                                ejeNuevo.setSeries(null);
                                ejeNuevo.setUrlVideoCliente(null);
                                ejeNuevo.setEstado(false);
                                ejeNuevo = ejercicioAsignadoRepository.save(ejeNuevo);

                                List<Serie> seriesOrigen = serieRepository.findByEjercicioAsignado(ejeOri);
                                if (seriesOrigen != null) {
                                    for (Serie serOri : seriesOrigen) {
                                        if (serOri == null) continue; // copiar siempre la serie aunque su estado sea false
                                        Serie serNueva = new Serie();
                                        serNueva.setId(null);
                                        serNueva.setOrden(serOri.getOrden());
                                        serNueva.setRepeticiones(serOri.getRepeticiones());
                                        serNueva.setPeso(serOri.getPeso());
                                        serNueva.setDescanso(serOri.getDescanso());
                                        serNueva.setIntensidad(serOri.getIntensidad());
                                        serNueva.setComentarios(serOri.getComentarios());
                                        serNueva.setEjercicioAsignado(ejeNuevo);
                                        serNueva.setEstado(false);
                                        serieRepository.save(serNueva);
                                    }
                                }
                                ejercicioAsignadoRepository.save(ejeNuevo);
                            }
                        }
                        diaRepository.save(dNuevo);
                    }
                }
                semanaRepository.save(sNueva);
            }
        }
        rutinaRepository.save(nueva);
    }

    @Override
    public RutinaDTO getRutinaById(long id) {
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + id));
        return mapToDTO(rutina);
    }

    @Override
    public RutinaDTO createRutina(RutinaDTO rutinaDTO) {
        Rutina rutina = modelMapper.map(rutinaDTO, Rutina.class);
        applyDtoIdToEntity(rutinaDTO, rutina);
        if (rutina.getId() != null && rutinaRepository.findById(rutina.getId()).isPresent()) {
            return updateRutina(rutina.getId(), rutinaDTO);
        }
        if (rutinaDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(rutinaDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + rutinaDTO.getIdCliente()));
            rutina.setCliente(cliente);
            if (rutinaDTO.getEstadoRutina() == EstadoRutina.ACTIVA) {
                rutinaRepository.findActiveByClienteId(cliente.getId()).ifPresent(prev -> {
                    prev.setEstadoRutina(EstadoRutina.FINALIZADA);
                    rutinaRepository.save(prev);
                });
            }
        }
        if (rutinaDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(rutinaDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + rutinaDTO.getIdEntrenador()));
            rutina.setEntrenador(entrenador);
        }
        if (rutina.getFechaCreacion() == null) {
            rutina.setFechaCreacion(java.time.LocalDate.now());
        }
        rutina = rutinaRepository.save(rutina);
        return mapToDTO(rutina);
    }

    @Override
    public RutinaDTO updateRutina(long id, RutinaDTO rutinaDTO) {
        Rutina existingRutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + id));
        if (rutinaDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(rutinaDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + rutinaDTO.getIdEntrenador()));
            existingRutina.setEntrenador(entrenador);
        }
        existingRutina.setNombre(rutinaDTO.getNombre());
        existingRutina.setObjetivo(rutinaDTO.getObjetivo());
        existingRutina.setDescripcion(rutinaDTO.getDescripcion());
        if (rutinaDTO.getFechaCreacion() != null) {
            existingRutina.setFechaCreacion(rutinaDTO.getFechaCreacion());
        }
        if (rutinaDTO.getEstadoRutina() != null) {
            existingRutina.setEstadoRutina(rutinaDTO.getEstadoRutina());
        }
        if (rutinaDTO.getNumeroSemanas() != null) {
            existingRutina.setNumeroSemanas(rutinaDTO.getNumeroSemanas());
        }
        existingRutina = rutinaRepository.save(existingRutina);
        return mapToDTO(existingRutina);
    }

    @Override
    public boolean deleteRutina(long id) {
        if (!rutinaRepository.findById(id).isPresent()){
            throw new RuntimeException("Rutina not found with id: " + id);
        }
        rutinaRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateRutina(long id) {
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + id));
        rutina.setEstado(!rutina.getEstado());
        rutinaRepository.save(rutina);
        return true;
    }

    @Override
    @Deprecated
    public void finalizarRutinaCliente(long idRutina) {
        // Deprecated: no valida cliente. Mantener compatibilidad mínima.
        Rutina rutina = rutinaRepository.findById(idRutina)
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + idRutina));
        rutina.setEstadoRutina(EstadoRutina.FINALIZADA);
        rutinaRepository.save(rutina);
    }

    @Override
    @Transactional
    public void finalizarRutinaCliente(long idRutina, long idCliente) {
        // Finaliza sólo si la rutina corresponde al cliente y está activa.
        Rutina rutina = rutinaRepository.findById(idRutina)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada: " + idRutina));
        if (rutina.getCliente() == null || !rutina.getCliente().getId().equals(idCliente)) {
            // Si la id enviada no corresponde, intentar finalizar la activa real del cliente.
            rutinaRepository.findActiveByClienteId(idCliente).ifPresent(r -> {
                r.setEstadoRutina(EstadoRutina.FINALIZADA);
                rutinaRepository.save(r);
            });
            return;
        }
        if (rutina.getEstadoRutina() != EstadoRutina.FINALIZADA) {
            rutina.setEstadoRutina(EstadoRutina.FINALIZADA);
            rutinaRepository.save(rutina);
        }
    }
}
