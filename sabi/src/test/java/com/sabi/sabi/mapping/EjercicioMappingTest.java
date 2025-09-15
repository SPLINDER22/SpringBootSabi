package com.sabi.sabi.mapping;

import com.sabi.sabi.config.ModelMapperConfig;
import com.sabi.sabi.dto.EjercicioDTO;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.entity.enums.TipoEjercicio;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

public class EjercicioMappingTest {

    @Test
    void entidadAtoDTO_conUsuarioEntrenador_noLanzaExcepcion() {
        // Arrange
        ModelMapper mapper = new ModelMapperConfig().modelMapper();
        Entrenador entrenador = Entrenador.builder()
                .id(10L)
                .nombre("Entrenador")
                .email("e@sabi.com")
                .contraseña("x")
                .rol(Rol.ENTRENADOR)
                .estado(true)
                .calificacionPromedio(0.0)
                .build();
        Ejercicio e = Ejercicio.builder()
                .id(5L)
                .nombre("Prensa")
                .descripcion("Piernas")
                .grupoMuscular("Piernas")
                .equipo("Maquina")
                .tipo(TipoEjercicio.PRIVADO)
                .usuario(entrenador)
                .estado(true)
                .build();

        // Act
        EjercicioDTO dto = mapper.map(e, EjercicioDTO.class);

        // Assert
        assertNotNull(dto);
        assertEquals(5L, dto.getIdEjercicio());
        assertEquals(10L, dto.getIdUsuario());
        assertEquals(TipoEjercicio.PRIVADO, dto.getTipo());
    }
}

