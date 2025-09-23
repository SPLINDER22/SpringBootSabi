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
        private final DiagnosticoRepository diagnosticoRepository;

        @Override
        public void run(String... args) {
                crearClienteSiNoExiste("Carlos Colmenares", "cliente@sabi.com", "1234567");
                crearEntrenadorSiNoExiste("Ernesto Espinel", "entrenador@sabi.com", "1234567");
                crearEjerciciosSiNoExisten();
                crearRutinaDeEjemplo();
                crearRutinaGlobalLibre(); // Nueva rutina global sin cliente ni entrenador ni estadoRutina

                // Mostrar en consola un resumen de los usuarios creados / existentes
                System.out.println("");
                System.out.println("Resumen de usuarios iniciales:");
                System.out.println("");
                System.out.println("Cliente - cliente@sabi.com - Contraseña (raw): 1234567");
                System.out.println("entrenador - entrenador@sabi.com - Contraseña (raw): 1234567");
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
                    .objetivos("Mejorar resistencia")
                    .sexo("M/F")
                    .telefono("3144153367")
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
        // Ejercicios globales (no asignados a ningún usuario)
        crearEjercicioGlobalSiNoExiste(
                "Sentadilla con barra",
                "Ejercicio compuesto para piernas y glúteos.",
                "Piernas",
                "Barra"
        );
        crearEjercicioGlobalSiNoExiste(
                "Press de banca",
                "Ejercicio compuesto para pectoral, tríceps y hombros.",
                "Pecho",
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
                                .repeticiones(numSerie == 1 ? 12L : 10L)
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
                .repeticiones(10L)
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
