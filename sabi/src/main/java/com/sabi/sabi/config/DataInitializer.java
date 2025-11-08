package com.sabi.sabi.config;

import com.sabi.sabi.entity.Diagnostico;
import com.sabi.sabi.entity.enums.NivelExperiencia;
import com.sabi.sabi.repository.DiagnosticoRepository;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.entity.Semana;
import com.sabi.sabi.entity.Dia;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.entity.enums.*;
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

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class    DataInitializer implements CommandLineRunner {

        private final UsuarioRepository usuarioRepository;
        private final EjercicioRepository ejercicioRepository;
        private final PasswordEncoder passwordEncoder;
        private final RutinaRepository rutinaRepository;
        private final SemanaRepository semanaRepository;
        private final DiaRepository diaRepository;
        private final EjercicioAsignadoRepository ejercicioAsignadoRepository;
        private final SerieRepository serieRepository;
        private final DiagnosticoRepository diagnosticoRepository;

        @Override
        public void run(String... args) {
                // Clientes
                crearClienteSiNoExiste("Carlos Colmenares", "cliente@sabi.com", "1234567");
                // Entrenadores
                crearEntrenadorDetalladoSiNoExiste(
                        "Ernesto", "Espinel", "entrenador@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1987, 5, 14), Departamento.CUNDINAMARCA, "Bogotá",
                        TipoDocumento.CC, "1012456789", "3101234567",
                        "Fuerza e hipertrofia", 4.6, 8,
                        "Entrenador con enfoque en fuerza e hipertrofia, planificación y técnica correcta."
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Ana", "García", "entrenador1@sabi.com", "1234567",
                        Sexo.FEMENINO, LocalDate.of(1990, 9, 22), Departamento.ANTIOQUIA, "Medellín",
                        TipoDocumento.CC, "1033345698", "3129876543",
                        "CrossFit y HIIT", 4.3, 6,
                        "Especialista en entrenamiento metabólico y acondicionamiento de alta intensidad."
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Luis", "Martínez", "entrenador2@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1985, 3, 8), Departamento.VALLE_DEL_CAUCA, "Cali",
                        TipoDocumento.CC, "1009876523", "3004567890",
                        "Pérdida de peso y funcional", 4.1, 10,
                        "Acompañamiento integral para reducción de grasa y hábitos saludables."
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "María", "López", "entrenador3@sabi.com", "1234567",
                        Sexo.FEMENINO, LocalDate.of(1992, 12, 11), Departamento.ATLANTICO, "Barranquilla",
                        TipoDocumento.CC, "1015678923", "3011237894",
                        "Funcional y movilidad", 4.4, 4,
                        "Trabajo de movilidad articular, estabilidad y funcionalidad para la vida diaria."
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Carlos", "Pérez", "entrenador4@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1988, 7, 19), Departamento.SANTANDER, "Bucaramanga",
                        TipoDocumento.CC, "1098765432", "3159988776",
                        "Resistencia y running", 4.2, 7,
                        "Planificación de carreras 5K-21K, técnica de carrera y fortalecimiento específico."
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Sofía", "Rodríguez", "entrenador5@sabi.com", "1234567",
                        Sexo.FEMENINO, LocalDate.of(1995, 4, 30), Departamento.BOLIVAR, "Cartagena",
                        TipoDocumento.CC, "1012349987", "3176655443",
                        "Pilates y core", 4.5, 5,
                        "Fortalecimiento de core, postura y prevención de dolor lumbar con métodos de pilates."
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Jorge", "Hernández", "entrenador6@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1984, 2, 2), Departamento.CUNDINAMARCA, "Chía",
                        TipoDocumento.CC, "1004567812", "3114455667",
                        "Halterofilia y olímpicos", 4.7, 12,
                        "Entrenamientos de levantamiento olímpico, técnica de arranque y envión."
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Lucía", "Fernández", "entrenador7@sabi.com", "1234567",
                        Sexo.FEMENINO, LocalDate.of(1993, 11, 5), Departamento.BOGOTA, "Bogotá",
                        TipoDocumento.CC, "1023456780", "3193344556",
                        "Yoga y movilidad", 4.0, 3,
                        "Sesiones de yoga enfocadas en flexibilidad, respiración y control postural."
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Diego", "Gómez", "entrenador8@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1989, 1, 17), Departamento.ANTIOQUIA, "Envigado",
                        TipoDocumento.CC, "1001234599", "3135566778",
                        "Calistenia y street workout", 4.3, 6,
                        "Progresiones de calistenia, dominadas, fondos y trabajo de fuerza con peso corporal."
                );
                crearEjerciciosSiNoExisten();
                crearRutinaDeEjemplo();
                crearRutinaGlobalLibre(); // Nueva rutina global sin cliente ni entrenador ni estadoRutina

                // Mostrar en consola un resumen de los usuarios creados / existentes
                System.out.println("");
                System.out.println("Resumen de usuarios iniciales:");
                System.out.println("");
                System.out.println("Cliente - cliente@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador1@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador2@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador3@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador4@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador5@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador6@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador7@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador8@sabi.com - Contraseña (raw): 1234567");
                System.out.println("");
        }

    private void crearClienteSiNoExiste(String nombre, String email, String rawPassword) {
        Cliente cliente;
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            cliente = Cliente.builder()
                    .nombre(nombre)
                    .email(email)
                    .contraseña(passwordEncoder.encode(rawPassword))
                    .rol(Rol.CLIENTE)
                    .estado(true)
                    .objetivo("Mejorar resistencia")
                    .build();
            usuarioRepository.save(cliente);
            System.out.println("Usuario creado: " + nombre + " | " + email + " | contraseña (raw): " + rawPassword);
        } else {
            cliente = (Cliente) usuarioRepository.findByEmail(email).get();
            System.out.println("Usuario ya existe: " + email + " (no se muestra contraseña raw)");
        }
        // Crear diagnóstico obligatorio si no existe
        if (diagnosticoRepository.findByClienteIdAndEstadoTrue(cliente.getId()).isEmpty()) {
            Diagnostico diagnostico = Diagnostico.builder()
                    .cliente(cliente)
                    .fecha(java.time.LocalDate.now())
                    .peso(70.0)
                    .estatura(170.0)
                    .nivelExperiencia(NivelExperiencia.PRINCIPIANTE)
                    .disponibilidadTiempo("3 veces por semana, 45 min")
                    .accesoRecursos("casa")
                    .lesiones("ninguna")
                    .condicionesMedicas("ninguna")
                    .horasSueno(8L)
                    .habitosAlimenticios("Dieta balanceada")
                    .estado(true)
                    .build();
            diagnosticoRepository.save(diagnostico);
            System.out.println("Diagnóstico creado para cliente: " + email);
        }
    }

        private void crearEntrenadorSiNoExiste(String nombre, String email, String rawPassword) {
                // Método legado: se mantiene por compatibilidad pero no se usa para los 9 entrenadores
                if (usuarioRepository.findByEmail(email).isEmpty()) {
                        Entrenador entrenador = Entrenador.builder()
                                        .nombre(nombre)
                                        .email(email)
                                        .contraseña(passwordEncoder.encode(rawPassword))
                                        .rol(Rol.ENTRENADOR)
                                        .estado(true)
                                        .especialidad("Fitness general")
                                        .calificacionPromedio(0.0)
                                        .build();
                        usuarioRepository.save(entrenador);
                        System.out.println("Usuario creado: " + nombre + " | " + email + " | contraseña (raw): " + rawPassword);
                } else {
                        System.out.println("Usuario ya existe: " + email + " (no se muestra contraseña raw)");
                }
        }

        private void crearEntrenadorDetalladoSiNoExiste(
                        String nombre, String apellido, String email, String rawPassword,
                        Sexo sexo, LocalDate fechaNacimiento, Departamento departamento, String ciudad,
                        TipoDocumento tipoDocumento, String numeroDocumento, String telefono,
                        String especialidad, Double calificacionPromedio, Integer aniosExperiencia,
                        String descripcion) {

                if (usuarioRepository.findByEmail(email).isPresent()) {
                        System.out.println("Usuario ya existe: " + email + " (no se muestra contraseña raw)");
                        return;
                }

                Entrenador entrenador = Entrenador.builder()
                                .nombre(nombre)
                                .apellido(apellido)
                                .email(email)
                                .contraseña(passwordEncoder.encode(rawPassword))
                                .rol(Rol.ENTRENADOR)
                                .sexo(sexo)
                                .fechaNacimiento(fechaNacimiento)
                                .departamento(departamento)
                                .ciudad(ciudad)
                                .tipoDocumento(tipoDocumento)
                                .numeroDocumento(numeroDocumento)
                                .telefono(telefono)
                                .descripcion(descripcion)
                                .estado(true)
                                .especialidad(especialidad)
                                .calificacionPromedio(calificacionPromedio)
                                .aniosExperiencia(aniosExperiencia)
                                .build();
                usuarioRepository.save(entrenador);
                System.out.println("Usuario creado: " + nombre + " " + apellido + " | " + email + " | contraseña (raw): " + rawPassword);
        }

    private void crearEjerciciosSiNoExisten() {
        // Ejercicios globales (no asignados a ningún usuario)
        crearEjercicioGlobalSiNoExiste(
                "Sentadilla con barra",
                "Ejercicio compuesto para piernas y glúteos.",
                "Piernas",
                "Barra",
                "https://www.youtube.com/embed/Z00Rl4raGkM?si=IxiQDy__iJVOeu2b"
        );
        crearEjercicioGlobalSiNoExiste(
                "Press de banca",
                "Ejercicio compuesto para pectoral, tríceps y hombros.",
                "Pecho",
                "Barra",
                "https://www.youtube.com/embed/TAH8RxOS0VI?si=tSCIs2gf8pQ4uEb-"
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

    private void crearEjercicioGlobalSiNoExiste(String nombre, String descripcion, String grupoMuscular, String equipo, String video) {
        if (!ejercicioRepository.existsByNombreAndTipo(nombre, TipoEjercicio.GLOBAL)) {
            Ejercicio e = Ejercicio.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .grupoMuscular(grupoMuscular)
                    .equipo(equipo)
                    .urlVideo(video)
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
        // Verificar si ya existe la rutina (nombre consistente con el que se crea)
        final String nombreRutina = "Rutina del entrenador";
        if (rutinaRepository.findAll().stream().anyMatch(r -> nombreRutina.equals(r.getNombre()))) return;
        // Obtener ejercicios globales necesarios
        Ejercicio sentadilla = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Sentadilla con barra"))
                .findFirst().orElse(null);
        Ejercicio pressBanca = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Press de banca"))
                .findFirst().orElse(null);
        if (sentadilla == null || pressBanca == null) return; // No crear si faltan ejercicios base

        // Crear rutina con 2 semanas
        Rutina rutina = Rutina.builder()
                .nombre(nombreRutina)
                .objetivo("Hipertrofia")
                .descripcion("Rutina de prueba para desarrollo con 2 semanas, 2 días y 2 ejercicios por día.")
                .fechaCreacion(java.time.LocalDate.now())
                .numeroSemanas(2L)
                .entrenador(entrenador)
                .estado(true)
                .build();
        rutinaRepository.save(rutina);

        // Crear 2 semanas
        for (long numSemana = 1L; numSemana <= 2L; numSemana++) {
            Semana semana = Semana.builder()
                    .numeroSemana(numSemana)
                    .descripcion("Semana " + numSemana + " de la rutina de ejemplo")
                    .numeroDias(2L)
                    .rutina(rutina)
                    .estado(true)
                    .build();
            semanaRepository.save(semana);

            // Crear 2 días por semana
            for (long numDia = 1L; numDia <= 2L; numDia++) {
                Dia dia = Dia.builder()
                        .numeroDia(numDia)
                        .descripcion("Semana " + numSemana + " - Día " + numDia + ": Sentadilla y Press de banca")
                        .numeroEjercicios(2L)
                        .semana(semana)
                        .estado(true)
                        .build();
                diaRepository.save(dia);

                Ejercicio[] ejerciciosDelDia = {sentadilla, pressBanca};
                long ordenEjercicio = 1L;
                for (Ejercicio ejercicio : ejerciciosDelDia) {
                    EjercicioAsignado ejercicioAsignado = EjercicioAsignado.builder()
                            .orden(ordenEjercicio)
                            .comentarios("Mantener buena técnica")
                            .numeroSeries(2L)
                            .dia(dia)
                            .ejercicio(ejercicio)
                            .estado(true)
                            .build();
                    ejercicioAsignadoRepository.save(ejercicioAsignado);

                    // Crear 2 series por ejercicio asignado
                    for (long numSerie = 1L; numSerie <= 2L; numSerie++) {
                        Serie serie = Serie.builder()
                                .orden(numSerie)
                                .repeticiones(numSerie == 1 ? "12" : "10")
                                .peso(ejercicio.getNombre().toLowerCase().contains("sentadilla") ? (numSerie == 1 ? 60.0 : 65.0) : (numSerie == 1 ? 40.0 : 45.0))
                                .descanso("60 - 90 segundos")
                                .intensidad(null)
                                .comentarios(numSerie == 1 ? "Serie de entrada" : "Aumentar ligeramente el peso")
                                .ejercicioAsignado(ejercicioAsignado)
                                .estado(true)
                                .build();
                        serieRepository.save(serie);
                    }
                    ordenEjercicio++;
                }
            }
        }
    }

    private void crearRutinaGlobalLibre() {
        // Verificar si ya existe para no duplicar
        final String nombreRutina = "Rutina Global Base";
        boolean existe = rutinaRepository.findAll().stream()
                .anyMatch(r -> nombreRutina.equalsIgnoreCase(r.getNombre()));
        if (existe) return;

        // Tomar un ejercicio global existente (si no hay, abortar)
        Ejercicio ejercicioGlobal = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL"))
                .findFirst()
                .orElse(null);
        if (ejercicioGlobal == null) return; // No se puede crear sin al menos un ejercicio global

        // Crear rutina sin cliente, sin entrenador y sin estadoRutina explícito
        Rutina rutina = Rutina.builder()
                .nombre(nombreRutina)
                .objetivo("General")
                .descripcion("Rutina global base sin asignar.")
                .fechaCreacion(java.time.LocalDate.now())
                .numeroSemanas(1L)
                .estado(true)
                .build();
        rutinaRepository.save(rutina);

        // Crear semana asociada
        Semana semana = Semana.builder()
                .numeroSemana(1L)
                .descripcion("Semana 1 - Global")
                .numeroDias(1L)
                .rutina(rutina)
                .estado(true)
                .build();
        semanaRepository.save(semana);

        // Crear día
        Dia dia = Dia.builder()
                .numeroDia(1L)
                .descripcion("Día 1: Trabajo general")
                .numeroEjercicios(1L)
                .semana(semana)
                .estado(true)
                .build();
        diaRepository.save(dia);

        // Crear ejercicio asignado
        EjercicioAsignado ejercicioAsignado = EjercicioAsignado.builder()
                .orden(1L)
                .comentarios("Mantener técnica controlada")
                .numeroSeries(1L)
                .dia(dia)
                .ejercicio(ejercicioGlobal)
                .estado(true)
                .build();
        ejercicioAsignadoRepository.save(ejercicioAsignado);

        // Crear serie base
        Serie serie = Serie.builder()
                .orden(1L)
                .repeticiones("10")
                .peso(0.0)
                .descanso("60 segundos")
                .intensidad(null) // sin intensidad definida
                .comentarios("Serie introductoria")
                .ejercicioAsignado(ejercicioAsignado)
                .estado(true)
                .build();
        serieRepository.save(serie);
    }
}
