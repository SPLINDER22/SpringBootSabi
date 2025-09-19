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
import org.springframework.transaction.annotation.Transactional;

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
    public List<Semana> getSemanasRutina(Long idRutina) {
        List<Semana> semanas = semanaRepository.getSemanasRutina(idRutina);
        return semanas.stream().toList();
    }

    @Override
    public SemanaDTO getSemanaById(long id) {
        Semana semana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        return modelMapper.map(semana, SemanaDTO.class);
    }

    @Override
    @Transactional
    public SemanaDTO createSemana(SemanaDTO semanaDTO) {
        if (semanaDTO.getIdSemana() != null) {
            return updateSemana(semanaDTO.getIdSemana(), semanaDTO);
        }
        if (semanaDTO.getIdRutina() == null) {
            throw new IllegalArgumentException("Debe indicar la rutina a la que pertenece la semana");
        }
        Rutina rutina = rutinaRepository.findById(semanaDTO.getIdRutina())
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + semanaDTO.getIdRutina()));

        // Calcular siguiente posición si no viene numeroSemana
        Long nextPos;
        List<Semana> actuales = semanaRepository.getSemanasRutina(rutina.getId());
        if (semanaDTO.getNumeroSemana() == null || semanaDTO.getNumeroSemana() <= 0) {
            nextPos = (long) (actuales.size() + 1); // append
        } else {
            // Insertar en una posición específica desplazando las existentes hacia abajo
            nextPos = semanaDTO.getNumeroSemana();
            if (nextPos > actuales.size() + 1) {
                nextPos = (long) (actuales.size() + 1); // clamp
            }
            // Desplazar si inserta en medio
            if (nextPos <= actuales.size()) {
                for (Semana s : actuales) {
                    if (s.getNumeroSemana() >= nextPos) {
                        s.setNumeroSemana(s.getNumeroSemana() + 1);
                    }
                }
                semanaRepository.saveAll(actuales);
            }
        }

        Semana nueva = new Semana();
        nueva.setRutina(rutina);
        nueva.setNumeroSemana(nextPos);
        nueva.setDescripcion(semanaDTO.getDescripcion());
        nueva.setNumeroDias(semanaDTO.getNumeroDias() != null ? semanaDTO.getNumeroDias() : 0L); // default
        nueva.setEstado(true);

        nueva = semanaRepository.save(nueva);

        // Incrementar contador de la rutina solo en creación
        Long numSemanas = rutina.getNumeroSemanas();
        if (numSemanas == null) numSemanas = 0L;
        rutina.setNumeroSemanas(numSemanas + 1);
        rutinaRepository.save(rutina);

        return modelMapper.map(nueva, SemanaDTO.class);
    }

    @Override
    @Transactional
    public SemanaDTO updateSemana(long id, SemanaDTO semanaDTO) {
        Semana existingSemana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));

        if (semanaDTO.getIdRutina() != null && !semanaDTO.getIdRutina().equals(existingSemana.getRutina().getId())) {
            throw new IllegalArgumentException("No se puede cambiar la rutina de una semana usando este método.");
        }

        Long rutinaId = existingSemana.getRutina().getId();
        List<Semana> semanas = semanaRepository.getSemanasRutina(rutinaId); // ordenadas ASC

        Long oldPos = existingSemana.getNumeroSemana();
        Long newPos = semanaDTO.getNumeroSemana();
        if (newPos == null) newPos = oldPos;
        if (newPos < 1) newPos = 1L;
        if (newPos > semanas.size()) newPos = (long) semanas.size();

        existingSemana.setDescripcion(semanaDTO.getDescripcion());
        if (semanaDTO.getNumeroDias() != null) {
            existingSemana.setNumeroDias(semanaDTO.getNumeroDias());
        }

        if (!newPos.equals(oldPos)) {
            if (newPos < oldPos) {
                for (Semana s : semanas) {
                    Long num = s.getNumeroSemana();
                    if (!s.getId().equals(existingSemana.getId()) && num >= newPos && num < oldPos) {
                        s.setNumeroSemana(num + 1);
                    }
                }
                existingSemana.setNumeroSemana(newPos);
            } else { // newPos > oldPos
                for (Semana s : semanas) {
                    Long num = s.getNumeroSemana();
                    if (!s.getId().equals(existingSemana.getId()) && num <= newPos && num > oldPos) {
                        s.setNumeroSemana(num - 1);
                    }
                }
                existingSemana.setNumeroSemana(newPos);
            }
        }

        semanaRepository.saveAll(semanas); // incluye potencialmente otra instancia de la semana
        semanaRepository.save(existingSemana); // garantizar persistencia de cambios si instancia difiere
        return modelMapper.map(existingSemana, SemanaDTO.class);
    }

    @Override
    @Transactional
    public boolean deleteSemana(long id) {
        Semana semana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        Rutina rutina = semana.getRutina();
        Long posEliminada = semana.getNumeroSemana();
        semanaRepository.delete(semana);

        // Recompactar orden (evitar huecos) y decrementar contador
        if (rutina != null) {
            List<Semana> restantes = semanaRepository.getSemanasRutina(rutina.getId());
            for (Semana s : restantes) {
                if (s.getNumeroSemana() > posEliminada) {
                    s.setNumeroSemana(s.getNumeroSemana() - 1);
                }
            }
            semanaRepository.saveAll(restantes);
            Long numSemanas = rutina.getNumeroSemanas();
            if (numSemanas == null || numSemanas <= 1) {
                rutina.setNumeroSemanas((long) restantes.size());
            } else {
                rutina.setNumeroSemanas(numSemanas - 1);
            }
            rutinaRepository.save(rutina);
        }
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
