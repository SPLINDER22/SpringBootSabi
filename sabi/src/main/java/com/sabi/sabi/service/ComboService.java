package com.sabi.sabi.service;

import com.sabi.sabi.dto.ComboDTO;

import java.util.List;

public interface ComboService {
    List<ComboDTO> getAllCombos();

    List<ComboDTO> getCombosByEjercicioAsignadoId(long idEjercicioAsignado);

    void createCombo(long idEjercicioAsignado, String nombreCombo);

    void clonarCombo(Long comboId, Long idEjercicioAsignado);
}
