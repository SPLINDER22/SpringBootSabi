package com.sabi.sabi.config;

import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.entity.enums.TipoEjercicio;
import com.sabi.sabi.repository.EjercicioRepository;
import com.sabi.sabi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EjercicioRepository ejercicioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearClienteSiNoExiste("Cliente", "cliente@sabi.com", "1234");
        crearEntrenadorSiNoExiste("Entrenador", "entrenador@sabi.com", "1234");
        crearEjerciciosSiNoExisten();
    }

    private void crearClienteSiNoExiste(String nombre, String email, String rawPassword) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Cliente cliente = Cliente.builder()
                    .nombre(nombre)
                    .email(email)
                    .contraseña(passwordEncoder.encode(rawPassword))
                    .rol(Rol.CLIENTE)
                    .estado(true)
                    .objetivos("Mejorar resistencia")
                    .sexo("M/F")
                    .telefono("3144153367")
                    .build();
            usuarioRepository.save(cliente);
        }
    }

    private void crearEntrenadorSiNoExiste(String nombre, String email, String rawPassword) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Entrenador entrenador = Entrenador.builder()
                    .nombre(nombre)
                    .email(email)
                    .contraseña(passwordEncoder.encode(rawPassword))
                    .rol(Rol.ENTRENADOR)
                    .estado(true)
                    .especialidades("Fuerza, HIIT")
                    .experiencia("3 años entrenando atletas")
                    .calificacionPromedio(0.0)
                    .build();
            usuarioRepository.save(entrenador);
        }
    }

    private void crearEjerciciosSiNoExisten() {
        // Ejercicio global (no asignado a ningún usuario)
        crearEjercicioGlobalSiNoExiste(
                "Sentadilla con barra",
                "Ejercicio compuesto para piernas y glúteos.",
                "Piernas",
                "Barra"
        );

        // Ejercicio privado (asignado a un usuario existente)
        Usuario usuario = usuarioRepository.findByEmail("entrenador@sabi.com")
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        crearEjercicioPrivadoSiNoExiste(
                "Circuito HIIT personalizado",
                "Rutina HIIT diseñada por el entrenador.",
                "Full body",
                "Peso corporal",
                usuario.getId()
        );
    }


    private void crearEjercicioGlobalSiNoExiste(String nombre, String descripcion, String grupoMuscular, String equipo) {
        if (!ejercicioRepository.existsByNombreAndTipo(nombre, TipoEjercicio.GLOBAL)) {
            Ejercicio e = Ejercicio.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .grupoMuscular(grupoMuscular)
                    .equipo(equipo)
                    .tipo(TipoEjercicio.GLOBAL)
                    .estado(true)
                    .build();
            ejercicioRepository.save(e);
        }
    }

    private void crearEjercicioPrivadoSiNoExiste(String nombre, String descripcion,
                                                 String grupoMuscular, String equipo, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (ejercicioRepository.existsByNombreAndTipoAndUsuario(nombre, TipoEjercicio.PRIVADO, usuario)) {
            return;
        }

        Ejercicio ejercicio = Ejercicio.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .grupoMuscular(grupoMuscular)
                .equipo(equipo)
                .tipo(TipoEjercicio.PRIVADO)
                .usuario(usuario)
                .estado(true)
                .build();

        ejercicioRepository.save(ejercicio);
    }
}
