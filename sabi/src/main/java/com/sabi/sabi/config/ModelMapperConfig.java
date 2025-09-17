package com.sabi.sabi.config;

import com.sabi.sabi.dto.EjercicioDTO;
import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.Rutina;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(false) // evitar confusiones por campos
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setAmbiguityIgnored(true);

        // Convertidor: Entidad -> DTO (Ejercicio)
        Converter<Ejercicio, EjercicioDTO> ejercicioToDto = ctx -> {
            Ejercicio src = ctx.getSource();
            if (src == null) return null;
            EjercicioDTO dst = new EjercicioDTO();
            dst.setIdEjercicio(src.getId());
            dst.setNombre(src.getNombre());
            dst.setDescripcion(src.getDescripcion());
            dst.setGrupoMuscular(src.getGrupoMuscular());
            dst.setEquipo(src.getEquipo());
            dst.setUrlVideo(src.getUrlVideo());
            dst.setUrlImagen(src.getUrlImagen());
            dst.setTipo(src.getTipo());
            dst.setEstado(src.getEstado());
            if (src.getUsuario() != null) {
                dst.setIdUsuario(src.getUsuario().getId());
            }
            return dst;
        };

        // Convertidor: DTO -> Entidad (Ejercicio)
        Converter<EjercicioDTO, Ejercicio> dtoToEjercicio = ctx -> {
            EjercicioDTO src = ctx.getSource();
            if (src == null) return null;
            Ejercicio dst = new Ejercicio();
            dst.setId(src.getIdEjercicio());
            dst.setNombre(src.getNombre());
            dst.setDescripcion(src.getDescripcion());
            dst.setGrupoMuscular(src.getGrupoMuscular());
            dst.setEquipo(src.getEquipo());
            dst.setUrlVideo(src.getUrlVideo());
            dst.setUrlImagen(src.getUrlImagen());
            dst.setTipo(src.getTipo());
            dst.setEstado(src.getEstado() != null ? src.getEstado() : true);
            return dst;
        };

        // NUEVO: Rutina Entidad -> DTO
        Converter<Rutina, RutinaDTO> rutinaToDto = ctx -> {
            Rutina src = ctx.getSource();
            if (src == null) return null;
            RutinaDTO dst = new RutinaDTO();
            dst.setIdRutina(src.getId());
            dst.setNombre(src.getNombre());
            dst.setObjetivo(src.getObjetivo());
            dst.setDescripcion(src.getDescripcion());
            dst.setFechaCreacion(src.getFechaCreacion());
            dst.setEstadoRutina(src.getEstadoRutina());
            dst.setNumeroSemanas(src.getNumeroSemanas());
            if (src.getCliente() != null) {
                dst.setIdCliente(src.getCliente().getId());
            }
            if (src.getEntrenador() != null) {
                dst.setIdEntrenador(src.getEntrenador().getId());
            }
            dst.setEstado(src.getEstado());
            return dst;
        };

        // NUEVO: Rutina DTO -> Entidad
        Converter<RutinaDTO, Rutina> dtoToRutina = ctx -> {
            RutinaDTO src = ctx.getSource();
            if (src == null) return null;
            Rutina dst = new Rutina();
            dst.setId(src.getIdRutina());
            dst.setNombre(src.getNombre());
            dst.setObjetivo(src.getObjetivo());
            dst.setDescripcion(src.getDescripcion());
            dst.setFechaCreacion(src.getFechaCreacion());
            dst.setEstadoRutina(src.getEstadoRutina());
            dst.setNumeroSemanas(src.getNumeroSemanas());
            dst.setEstado(src.getEstado() != null ? src.getEstado() : true);
            return dst;
        };

        mapper.createTypeMap(Ejercicio.class, EjercicioDTO.class).setConverter(ejercicioToDto);
        mapper.createTypeMap(EjercicioDTO.class, Ejercicio.class).setConverter(dtoToEjercicio);

        mapper.createTypeMap(Rutina.class, RutinaDTO.class).setConverter(rutinaToDto);
        mapper.createTypeMap(RutinaDTO.class, Rutina.class).setConverter(dtoToRutina);

        return mapper;
    }
}
