package com.sabi.sabi.config;

import com.sabi.sabi.dto.EjercicioDTO;
import com.sabi.sabi.entity.Ejercicio;
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

        // Convertidor: Entidad -> DTO
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

        // Convertidor: DTO -> Entidad (el usuario se asigna en el servicio con idUsuario)
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

        mapper.createTypeMap(Ejercicio.class, EjercicioDTO.class).setConverter(ejercicioToDto);
        mapper.createTypeMap(EjercicioDTO.class, Ejercicio.class).setConverter(dtoToEjercicio);

        return mapper;
    }
}
