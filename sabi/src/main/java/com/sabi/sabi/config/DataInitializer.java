package com.sabi.sabi.config;

import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.entity.Semana;
import com.sabi.sabi.entity.Dia;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.entity.enums.TipoEjercicio;
import com.sabi.sabi.entity.enums.EstadoRutina;
import com.sabi.sabi.repository.EjercicioRepository;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.repository.RutinaRepository;
import com.sabi.sabi.repository.SemanaRepository;
import com.sabi.sabi.repository.DiaRepository;
import com.sabi.sabi.repository.EjercicioAsignadoRepository;
import com.sabi.sabi.repository.SerieRepository;
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
    private final RutinaRepository rutinaRepository;
    private final SemanaRepository semanaRepository;
    private final DiaRepository diaRepository;
    private final EjercicioAsignadoRepository ejercicioAsignadoRepository;
    private final SerieRepository serieRepository;

    @Override
    public void run(String... args) {
        crearClienteSiNoExiste("Cliente", "cliente@sabi.com", "1234567");
        crearEntrenadorSiNoExiste("Entrenador", "entrenador@sabi.com", "1234567");
        crearEjerciciosSiNoExisten();
        crearRutinaDeEjemplo();

        // Mostrar en consola un resumen de los usuarios creados / existentes
        System.out.println("----- Usuarios en la base de datos -----");
        usuarioRepository.findAll().forEach(u -> {
            System.out.println("ID: " + u.getId()
                    + " | Nombre: " + u.getNombre()
                    + " | Email: " + u.getEmail()
                    + " | Rol: " + u.getRol()
                    + " | Estado: " + u.getEstado()
                    + " | Contraseña(encrypted): " + u.getContraseña());
        });
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
            System.out.println("Usuario creado: " + nombre + " | " + email + " | contraseña (raw): " + rawPassword);
        } else {
            System.out.println("Usuario ya existe: " + email + " (no se muestra contraseña raw)");
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
            System.out.println("Usuario creado: " + nombre + " | " + email + " | contraseña (raw): " + rawPassword);
        } else {
            System.out.println("Usuario ya existe: " + email + " (no se muestra contraseña raw)");
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

    private void crearRutinaDeEjemplo() {
        // Obtener cliente y entrenador
        Cliente cliente = (Cliente) usuarioRepository.findByEmail("cliente@sabi.com").orElse(null);
        Entrenador entrenador = (Entrenador) usuarioRepository.findByEmail("entrenador@sabi.com").orElse(null);
        if (cliente == null || entrenador == null) return;
        // Verificar si ya existe la rutina
        if (rutinaRepository.findAll().stream().anyMatch(r -> r.getNombre().equals("Rutina de ejemplo"))) return;
        // Obtener ejercicio global
        Ejercicio ejercicio = ejercicioRepository.findAll().stream().filter(e -> e.getTipo().name().equals("GLOBAL")).findFirst().orElse(null);
        if (ejercicio == null) return;
        // Crear rutina
        Rutina rutina = Rutina.builder()
                .nombre("Rutina de ejemplo")
                .objetivo("Hipertrofia")
                .descripcion("Rutina de prueba para desarrollo.")
                .fechaCreacion(java.time.LocalDate.now())
                .estadoRutina(EstadoRutina.ACTIVA)
                .numeroSemanas(1L)
                .cliente(cliente)
                .entrenador(entrenador)
                .estado(true)
                .build();
        rutinaRepository.save(rutina);
        // Crear semana
        Semana semana = Semana.builder()
                .numeroSemana(1L)
                .descripcion("Semana 1 de la rutina de ejemplo")
                .rutina(rutina)
                .estado(true)
                .build();
        semanaRepository.save(semana);
        // Crear día
        Dia dia = Dia.builder()
                .numeroDia(1L)
                .descripcion("Día 1: Piernas y glúteos")
                .semana(semana)
                .estado(true)
                .build();
        diaRepository.save(dia);
        // Crear ejercicio asignado
        EjercicioAsignado ejercicioAsignado = EjercicioAsignado.builder()
                .orden(1L)
                .comentarios("Ejecutar con buena técnica")
                .dia(dia)
                .ejercicio(ejercicio)
                .estado(true)
                .build();
        ejercicioAsignadoRepository.save(ejercicioAsignado);
        // Crear serie
        Serie serie = Serie.builder()
                .orden(1L)
                .repeticiones(12L)
                .peso(60.0)
                .descanso(90L)
                .intensidad(null)
                .comentarios("Peso moderado")
                .ejercicioAsignado(ejercicioAsignado)
                .estado(true)
                .build();
        serieRepository.save(serie);
    }
}
