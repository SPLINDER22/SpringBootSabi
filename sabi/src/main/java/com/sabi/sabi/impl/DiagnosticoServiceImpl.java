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
        DiagnosticoDTO resultDTO = modelMapper.map(diagnostico, DiagnosticoDTO.class);
        // Asegurar que el ID se mapee correctamente
        resultDTO.setIdDiagnostico(diagnostico.getId());
        return resultDTO;
    }

    @Override
    public DiagnosticoDTO createDiagnostico(DiagnosticoDTO diagnosticoDTO) {
        System.out.println("\n🔷 CREANDO/ACTUALIZANDO DIAGNÓSTICO");
        System.out.println("   DTO ID: " + diagnosticoDTO.getIdDiagnostico());
        System.out.println("   Cliente ID: " + diagnosticoDTO.getIdCliente());

        Diagnostico diagnostico = modelMapper.map(diagnosticoDTO, Diagnostico.class);
        
        // Si tiene ID y ya existe, es una actualización
        if (diagnostico.getId() != null && diagnosticoRepository.findById(diagnostico.getId()).isPresent()){
            System.out.println("   ➡️ Es actualización de diagnóstico existente ID: " + diagnostico.getId());
            return updateDiagnostico(diagnostico.getId(), diagnosticoDTO);
        }
        
        // Si no tiene ID o no existe, es creación nueva
        System.out.println("   ➡️ Es creación de NUEVO diagnóstico");
        System.out.println("   📝 Creando NUEVO registro en base de datos");

        diagnostico.setId(null); // Asegurar que sea null para nueva creación
        
        // ⚠️ IMPORTANTE: Asegurar que estado sea true
        diagnostico.setEstado(true);
        System.out.println("   ✅ Estado establecido: true");

        if (diagnosticoDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(diagnosticoDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + diagnosticoDTO.getIdCliente()));
            diagnostico.setCliente(cliente);
            System.out.println("   ✅ Cliente asignado: " + cliente.getId());
        }
        
        // Guardar NUEVO diagnóstico
        diagnostico = diagnosticoRepository.save(diagnostico);
        System.out.println("   ✅ NUEVO Diagnóstico guardado con ID: " + diagnostico.getId());
        System.out.println("   Estado en BD: " + diagnostico.getEstado());
        System.out.println("   Cliente ID en BD: " + (diagnostico.getCliente() != null ? diagnostico.getCliente().getId() : "null"));
        System.out.println("   Fecha: " + diagnostico.getFecha());

        // Verificar cuántos diagnósticos tiene ahora el cliente
        List<Diagnostico> todosLosDelCliente = diagnosticoRepository.findByClienteIdAndEstadoTrue(diagnostico.getCliente().getId());
        System.out.println("   📊 Total diagnósticos del cliente ahora: " + todosLosDelCliente.size());

        DiagnosticoDTO resultDTO = modelMapper.map(diagnostico, DiagnosticoDTO.class);
        // Asegurar que el ID se mapee correctamente
        resultDTO.setIdDiagnostico(diagnostico.getId());

        System.out.println("   📤 DTO resultado ID: " + resultDTO.getIdDiagnostico());
        System.out.println("🔷 FIN CREACIÓN\n");

        return resultDTO;
    }

    @Override
    public DiagnosticoDTO updateDiagnostico(long id, DiagnosticoDTO diagnosticoDTO) {
        System.out.println("\n🔶 ACTUALIZANDO DIAGNÓSTICO ID: " + id);

        Diagnostico existingDiagnostico = diagnosticoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnostico not found with id: " + id));

        System.out.println("   Estado ANTES de actualizar: " + existingDiagnostico.getEstado());

        // ⚠️ IMPORTANTE: Asegurar que estado permanezca true
        existingDiagnostico.setEstado(true);

        if (diagnosticoDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(diagnosticoDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + diagnosticoDTO.getIdCliente()));
            existingDiagnostico.setCliente(cliente);
        }
        // Actualizar campos básicos
        if (diagnosticoDTO.getFecha() != null) {
            existingDiagnostico.setFecha(diagnosticoDTO.getFecha());
        }
        existingDiagnostico.setProximoDiagnostico(diagnosticoDTO.getProximoDiagnostico());
        existingDiagnostico.setObjetivo(diagnosticoDTO.getObjetivo());
        existingDiagnostico.setPeso(diagnosticoDTO.getPeso());
        existingDiagnostico.setEstatura(diagnosticoDTO.getEstatura());
        existingDiagnostico.setNivelExperiencia(diagnosticoDTO.getNivelExperiencia());
        existingDiagnostico.setDisponibilidadTiempo(diagnosticoDTO.getDisponibilidadTiempo());
        existingDiagnostico.setAccesoRecursos(diagnosticoDTO.getAccesoRecursos());
        existingDiagnostico.setLesiones(diagnosticoDTO.getLesiones());
        existingDiagnostico.setCondicionesMedicas(diagnosticoDTO.getCondicionesMedicas());
        
        // Solo actualizar URLs de fotos si no son null (se conservan las existentes si no se pasan nuevas)
        if (diagnosticoDTO.getFotoFrontalUrl() != null) {
            existingDiagnostico.setFotoFrontalUrl(diagnosticoDTO.getFotoFrontalUrl());
        }
        if (diagnosticoDTO.getFotoLateralUrl() != null) {
            existingDiagnostico.setFotoLateralUrl(diagnosticoDTO.getFotoLateralUrl());
        }
        if (diagnosticoDTO.getFotoTraseraUrl() != null) {
            existingDiagnostico.setFotoTraseraUrl(diagnosticoDTO.getFotoTraseraUrl());
        }
        // Actualizar medidas corporales opcionales
        existingDiagnostico.setPorcentajeGrasaCorporal(diagnosticoDTO.getPorcentajeGrasaCorporal());
        existingDiagnostico.setCircunferenciaCintura(diagnosticoDTO.getCircunferenciaCintura());
        existingDiagnostico.setCircunferenciaCadera(diagnosticoDTO.getCircunferenciaCadera());
        existingDiagnostico.setCircunferenciaBrazo(diagnosticoDTO.getCircunferenciaBrazo());
        existingDiagnostico.setCircunferenciaPierna(diagnosticoDTO.getCircunferenciaPierna());
        existingDiagnostico.setFrecuenciaCardiacaReposo(diagnosticoDTO.getFrecuenciaCardiacaReposo());
        existingDiagnostico.setPresionArterial(diagnosticoDTO.getPresionArterial());
        existingDiagnostico.setHorasSueno(diagnosticoDTO.getHorasSueno());
        existingDiagnostico.setHabitosAlimenticios(diagnosticoDTO.getHabitosAlimenticios());
        
        // Actualizar campos adicionales para el entrenador
        existingDiagnostico.setPreferenciasEntrenamiento(diagnosticoDTO.getPreferenciasEntrenamiento());
        existingDiagnostico.setExperienciaPreviaDeportes(diagnosticoDTO.getExperienciaPreviaDeportes());
        existingDiagnostico.setDiasDisponiblesSemana(diagnosticoDTO.getDiasDisponiblesSemana());
        existingDiagnostico.setHorarioPreferido(diagnosticoDTO.getHorarioPreferido());
        existingDiagnostico.setLimitacionesFisicas(diagnosticoDTO.getLimitacionesFisicas());
        existingDiagnostico.setMotivacionPrincipal(diagnosticoDTO.getMotivacionPrincipal());
        existingDiagnostico.setNivelEstres(diagnosticoDTO.getNivelEstres());
        existingDiagnostico.setSuplementosActuales(diagnosticoDTO.getSuplementosActuales());
        existingDiagnostico.setFumador(diagnosticoDTO.getFumador());
        existingDiagnostico.setConsumeAlcohol(diagnosticoDTO.getConsumeAlcohol());
        existingDiagnostico.setFrecuenciaAlcohol(diagnosticoDTO.getFrecuenciaAlcohol());

        existingDiagnostico = diagnosticoRepository.save(existingDiagnostico);

        System.out.println("   ✅ Diagnóstico actualizado");
        System.out.println("   Estado DESPUÉS de guardar: " + existingDiagnostico.getEstado());
        System.out.println("   ID: " + existingDiagnostico.getId());
        System.out.println("   Cliente ID: " + (existingDiagnostico.getCliente() != null ? existingDiagnostico.getCliente().getId() : "null"));
        System.out.println("🔶 FIN ACTUALIZACIÓN\n");

        DiagnosticoDTO resultDTO = modelMapper.map(existingDiagnostico, DiagnosticoDTO.class);
        // Asegurar que el ID se mapee correctamente
        resultDTO.setIdDiagnostico(existingDiagnostico.getId());
        return resultDTO;
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
