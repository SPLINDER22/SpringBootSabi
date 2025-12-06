package com.sabi.sabi.service;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.ProgresoSemanaDTO;
import com.sabi.sabi.entity.Dia;

import java.util.List;

public interface DiaService {
    List<DiaDTO> getAllDia();
    List<DiaDTO> getAllActiveDia();
    List<Dia> getDiasSemana(Long idSemana);

    DiaDTO getDiaById(long id);

    DiaDTO createDia(DiaDTO diaDTO);

    DiaDTO updateDia(long id, DiaDTO diaDTO);

    boolean deleteDia(long id);

    boolean desactivateDia(long id);

    DiaDTO toggleChecked(long idDia);

    DiaDTO getDiaActual(long idCliente);

    long calcularProgresoRutina(long idCliente);

    List<ProgresoSemanaDTO> calcularProgresoPorSemana(long idCliente);
}
