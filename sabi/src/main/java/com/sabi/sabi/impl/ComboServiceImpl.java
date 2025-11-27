package com.sabi.sabi.impl;

import com.sabi.sabi.dto.ComboDTO;
import com.sabi.sabi.entity.Combo;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.repository.ComboRepository;
import com.sabi.sabi.repository.EjercicioAsignadoRepository;
import com.sabi.sabi.repository.SerieRepository;
import com.sabi.sabi.service.ComboService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComboServiceImpl implements ComboService {
    @Autowired
    private ComboRepository comboRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EjercicioAsignadoRepository ejercicioAsignadoRepository;
    @Autowired
    private SerieRepository serieRepository;

    @Override
    public List<ComboDTO> getAllCombos() {
        List<Combo> combos = comboRepository.findAll();
        return combos.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ComboDTO> getCombosByEjercicioAsignadoId(long idEjercicioAsignado) {
        List<Combo> combos = comboRepository.findByEjercicioAsignadoId(idEjercicioAsignado);
        return combos.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private ComboDTO convertToDTO(Combo combo) {
        ComboDTO dto = new ComboDTO();
        dto.setId(combo.getId());
        dto.setNombre(combo.getNombre());
        dto.setIdEjercicio(combo.getEjercicio() != null ? combo.getEjercicio().getId() : null);
        dto.setNombreEjercicio(combo.getEjercicio() != null ? combo.getEjercicio().getNombre() : "Sin ejercicio");
        dto.setCantidadSeries(combo.getSeries() != null ? combo.getSeries().size() : 0);
        return dto;
    }

    @Override
    public void createCombo(long idEjercicioAsignado, String nombreCombo) {
        Optional<EjercicioAsignado> ejercicioAsignadoDTO = ejercicioAsignadoRepository.findById(idEjercicioAsignado);
        Combo combo = new Combo();
        combo.setNombre(nombreCombo);
        combo.setEjercicio(ejercicioAsignadoDTO.get().getEjercicio());
        Combo savedCombo = comboRepository.save(combo);
        List<Serie> series = serieRepository.getSerieEje(idEjercicioAsignado);
        for (Serie serie : series) {
            Serie newSerie = new Serie();
            newSerie.setOrden(serie.getOrden());
            newSerie.setRepeticiones(serie.getRepeticiones());
            newSerie.setDescanso(serie.getDescanso());
            newSerie.setPeso(serie.getPeso());
            newSerie.setIntensidad(serie.getIntensidad());
            newSerie.setComentarios(serie.getComentarios());
            newSerie.setCombo(savedCombo);
            serieRepository.save(newSerie);
        }
    }

    @Override
    public void clonarCombo(Long comboId, Long idEjercicioAsignado) {
        // Buscar el combo
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new IllegalArgumentException("Combo no encontrado con ID: " + comboId));

        // Buscar el ejercicio asignado destino
        EjercicioAsignado ejercicioAsignado = ejercicioAsignadoRepository.findById(idEjercicioAsignado)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio asignado no encontrado con ID: " + idEjercicioAsignado));

        // Obtener las series del combo
        List<Serie> seriesCombo = combo.getSeries();

        if (seriesCombo == null || seriesCombo.isEmpty()) {
            throw new IllegalStateException("El combo no tiene series para clonar");
        }

        // Obtener el orden máximo de las series existentes en el ejercicio asignado
        List<Serie> seriesExistentes = serieRepository.getSerieEje(idEjercicioAsignado);
        Long ordenMaximo = 0L;
        if (seriesExistentes != null && !seriesExistentes.isEmpty()) {
            ordenMaximo = seriesExistentes.stream()
                    .map(Serie::getOrden)
                    .max(Long::compare)
                    .orElse(0L);
        }

        // Clonar cada serie del combo al ejercicio asignado (sin combo_id)
        // Continuar la numeración desde el orden máximo + 1
        long ordenInicial = ordenMaximo;
        for (Serie serieOriginal : seriesCombo) {
            ordenInicial++;
            Serie nuevaSerie = new Serie();
            nuevaSerie.setOrden(ordenInicial);
            nuevaSerie.setRepeticiones(serieOriginal.getRepeticiones());
            nuevaSerie.setPeso(serieOriginal.getPeso());
            nuevaSerie.setDescanso(serieOriginal.getDescanso());
            nuevaSerie.setIntensidad(serieOriginal.getIntensidad());
            nuevaSerie.setComentarios(serieOriginal.getComentarios());
            nuevaSerie.setEjercicioAsignado(ejercicioAsignado);
            // NO se asigna combo (combo = null) para que sea una serie independiente
            nuevaSerie.setCombo(null);
            nuevaSerie.setEstado(false);

            serieRepository.save(nuevaSerie);
        }
    }
}
