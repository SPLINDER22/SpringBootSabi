package com.sabi.sabi.service;

import com.sabi.sabi.dto.DiagnosticoDTO;

import java.util.List;

public interface DiagnosticoService {
    List<DiagnosticoDTO> getAllDiagnosticos();
    List<DiagnosticoDTO> getAllActiveDiagnosticos();

    DiagnosticoDTO getDiagnosticoById(long id);

    DiagnosticoDTO createDiagnostico(DiagnosticoDTO diagnosticoDTO);

    DiagnosticoDTO updateDiagnostico(long id, DiagnosticoDTO diagnosticoDTO);

    boolean deleteDiagnostico(long id);

    boolean desactivateDiagnostico(long id);
}
