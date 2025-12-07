package com.sabi.sabi.config;

import com.sabi.sabi.entity.*;
import com.sabi.sabi.entity.enums.Intensidad;
import com.sabi.sabi.entity.enums.TipoEjercicio;
import com.sabi.sabi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2) // Se ejecuta después del DataInitializer principal
@RequiredArgsConstructor
public class DataInitializerRutinas implements CommandLineRunner {

    private final EjercicioRepository ejercicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RutinaRepository rutinaRepository;
    private final SemanaRepository semanaRepository;
    private final DiaRepository diaRepository;
    private final EjercicioAsignadoRepository ejercicioAsignadoRepository;
    private final SerieRepository serieRepository;

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  💪 INICIALIZANDO EJERCICIOS Y RUTINAS GLOBALES       ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        crearEjerciciosSiNoExisten();
        crearRutinaDeEjemplo();
        crearRutinaDeErnesto4Semanas();
        crearRutinaGlobalLibre();
        crearRutinaCompuesta();
        crearRutinaGlobal5Semanas(); // Nueva rutina global de 5 semanas

        System.out.println("\n✅ Ejercicios y rutinas globales creados exitosamente\n");
    }

    private void crearEjerciciosSiNoExisten() {
        System.out.println("📋 Creando ejercicios globales de SABI...\n");

        // Ejercicios globales originales (no asignados a ningún usuario)
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

        crearEjerciciosGlobalesSabi();

        // Ejercicio privado (asignado a un usuario existente)
        Usuario usuario = usuarioRepository.findByEmail("entrenador@sabi.com")
                .orElse(null);

        if (usuario != null) {
            crearEjercicioPrivadoSiNoExiste(
                    "Circuito HIIT personalizado",
                    "Rutina HIIT diseñada por el entrenador.",
                    "Full body",
                    "Peso corporal",
                    usuario.getId()
            );
        }
    }

    private void crearEjerciciosGlobalesSabi() {
        // 1. Curl de bíceps
        crearEjercicioGlobalSiNoExiste(
                "Curl de bíceps",
                "Ejercicio de aislamiento para el desarrollo de los bíceps.",
                "Brazos",
                "Mancuernas",
                "https://www.youtube.com/embed/M67E8xSxrsA?si=CTp8FU9JeTpYTkGc"
        );

        // 2. Prensa de pierna
        crearEjercicioGlobalSiNoExiste(
                "Prensa de pierna",
                "Ejercicio compuesto para el desarrollo de cuádriceps, glúteos y femorales.",
                "Piernas",
                "Máquina",
                "https://www.youtube.com/embed/bNsrqXUIJqc?si=t6sTk4gWw8Lotxpe"
        );

        // 3. Extensión en polea tras nuca
        crearEjercicioGlobalSiNoExiste(
                "Extensión en polea tras nuca",
                "Ejercicio de aislamiento para tríceps con polea.",
                "Brazos",
                "Polea",
                "https://www.youtube.com/embed/xfS2-dkcC1k?si=-P_0fRifZNuCbw47"
        );

        // 4. Dominadas
        crearEjercicioGlobalSiNoExiste(
                "Dominadas",
                "Ejercicio compuesto para el desarrollo de espalda y bíceps.",
                "Espalda",
                "Peso corporal",
                "https://www.youtube.com/embed/GScM1sXnbjI?si=u-ZxRCt8Xx0t3y-j"
        );

        // 5. Press inclinado
        crearEjercicioGlobalSiNoExiste(
                "Press inclinado",
                "Ejercicio compuesto para el desarrollo del pectoral superior.",
                "Pecho",
                "Barra",
                "https://www.youtube.com/embed/SuFOcPiynmg?si=cgTKzMrsfh8blgQ0"
        );
    }

    private void crearEjercicioGlobalSiNoExiste(String nombre, String descripcion, String grupoMuscular, String equipo, String video) {
        if (!ejercicioRepository.existsByNombreAndTipo(nombre, TipoEjercicio.GLOBAL)) {
            Ejercicio ejercicio = Ejercicio.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .grupoMuscular(grupoMuscular)
                    .equipo(equipo)
                    .urlVideo(video)
                    .tipo(TipoEjercicio.GLOBAL)
                    .estado(true)
                    .build();
            ejercicioRepository.save(ejercicio);
            System.out.println("✅ Ejercicio creado: " + nombre + " (" + grupoMuscular + ")");
        } else {
            System.out.println("ℹ️  Ejercicio ya existe: " + nombre);
        }
    }

    private void crearEjercicioPrivadoSiNoExiste(String nombre, String descripcion,
                                                 String grupoMuscular, String equipo, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (ejercicioRepository.existsByNombreAndTipoAndUsuario(nombre, TipoEjercicio.PRIVADO, usuario)) {
            System.out.println("ℹ️  Ejercicio privado ya existe: " + nombre);
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
        System.out.println("✅ Ejercicio privado creado: " + nombre + " (usuario: " + usuario.getEmail() + ")");
    }

    private void crearRutinaDeEjemplo() {
        System.out.println("\n📋 Creando rutina de ejemplo...\n");

        // Obtener cliente y entrenador
        Cliente cliente = (Cliente) usuarioRepository.findByEmail("cliente@sabi.com").orElse(null);
        Entrenador entrenador = (Entrenador) usuarioRepository.findByEmail("entrenador@sabi.com").orElse(null);
        if (cliente == null || entrenador == null) return;

        // Verificar si ya existe la rutina
        final String nombreRutina = "Rutina del entrenador";
        if (rutinaRepository.findAll().stream().anyMatch(r -> nombreRutina.equals(r.getNombre()))) {
            System.out.println("ℹ️  Rutina ya existe: " + nombreRutina);
            return;
        }


        Ejercicio sentadilla = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Sentadilla con barra"))
                .findFirst().orElse(null);
        Ejercicio pressBanca = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Press de banca"))
                .findFirst().orElse(null);
        if (sentadilla == null || pressBanca == null) return;

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

        for (long numSemana = 1L; numSemana <= 2L; numSemana++) {
            Semana semana = Semana.builder()
                    .numeroSemana(numSemana)
                    .descripcion("Semana " + numSemana + " de la rutina de ejemplo")
                    .numeroDias(2L)
                    .rutina(rutina)
                    .estado(true)
                    .build();
            semanaRepository.save(semana);

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

                    for (long numSerie = 1L; numSerie <= 2L; numSerie++) {
                        Serie serie = Serie.builder()
                                .orden(numSerie)
                                .repeticiones(numSerie == 1 ? "12" : "10")
                                .peso(ejercicio.getNombre().toLowerCase().contains("sentadilla") ? (numSerie == 1 ? 60.0 : 65.0) : (numSerie == 1 ? 40.0 : 45.0))
                                .descanso("60 - 90 segundos")
                                .intensidad(numSerie == 1 ? Intensidad.MEDIA : Intensidad.ALTA)
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
        System.out.println("✅ Rutina creada: " + nombreRutina);
    }

    private void crearRutinaDeErnesto4Semanas() {
        System.out.println("\n📋 Creando rutina de 4 semanas para Ernesto...\n");


        Entrenador entrenador = (Entrenador) usuarioRepository.findByEmail("entrenador@sabi.com").orElse(null);
        if (entrenador == null) return;

        final String nombreRutina = "Rutina Fuerza 4 Semanas";
        if (rutinaRepository.findAll().stream().anyMatch(r -> nombreRutina.equals(r.getNombre()))) {
            System.out.println("ℹ️  Rutina ya existe: " + nombreRutina);
            return;
        }

        Ejercicio sentadilla = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Sentadilla con barra"))
                .findFirst().orElse(null);
        Ejercicio pressBanca = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Press de banca"))
                .findFirst().orElse(null);
        Ejercicio pesoMuerto = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Peso muerto"))
                .findFirst().orElse(null);

        if (sentadilla == null || pressBanca == null) return;
        if (pesoMuerto == null) pesoMuerto = sentadilla;

        Rutina rutina = Rutina.builder()
                .nombre(nombreRutina)
                .objetivo("Fuerza y Acondicionamiento")
                .descripcion("Rutina de fuerza progresiva de 4 semanas enfocada en powerlifting y desarrollo muscular.")
                .fechaCreacion(java.time.LocalDate.now())
                .numeroSemanas(4L)
                .entrenador(entrenador)
                .estado(true)
                .build();
        rutinaRepository.save(rutina);

        for (long numSemana = 1L; numSemana <= 4L; numSemana++) {
            Semana semana = Semana.builder()
                    .numeroSemana(numSemana)
                    .descripcion("Semana " + numSemana + " - Progresión de fuerza")
                    .numeroDias(3L)
                    .rutina(rutina)
                    .estado(true)
                    .build();
            semanaRepository.save(semana);

            for (long numDia = 1L; numDia <= 3L; numDia++) {
                String descripcionDia;
                Ejercicio[] ejerciciosDelDia;

                if (numDia == 1L) {
                    descripcionDia = "Semana " + numSemana + " - Día 1: Piernas y espalda";
                    ejerciciosDelDia = new Ejercicio[]{sentadilla, pesoMuerto};
                } else if (numDia == 2L) {
                    descripcionDia = "Semana " + numSemana + " - Día 2: Pecho y brazos";
                    ejerciciosDelDia = new Ejercicio[]{pressBanca, pressBanca};
                } else {
                    descripcionDia = "Semana " + numSemana + " - Día 3: Full body";
                    ejerciciosDelDia = new Ejercicio[]{sentadilla, pressBanca};
                }

                Dia dia = Dia.builder()
                        .numeroDia(numDia)
                        .descripcion(descripcionDia)
                        .numeroEjercicios(2L)
                        .semana(semana)
                        .estado(true)
                        .build();
                diaRepository.save(dia);

                long ordenEjercicio = 1L;
                for (Ejercicio ejercicio : ejerciciosDelDia) {
                    EjercicioAsignado ejercicioAsignado = EjercicioAsignado.builder()
                            .orden(ordenEjercicio)
                            .comentarios("Progresión semanal - Mantener técnica estricta")
                            .numeroSeries(3L)
                            .dia(dia)
                            .ejercicio(ejercicio)
                            .estado(true)
                            .build();
                    ejercicioAsignadoRepository.save(ejercicioAsignado);

                    for (long numSerie = 1L; numSerie <= 3L; numSerie++) {
                        double pesoBase = ejercicio.getNombre().toLowerCase().contains("sentadilla") ? 70.0 :
                                         (ejercicio.getNombre().toLowerCase().contains("muerto") ? 80.0 : 50.0);
                        double pesoProgresivo = pesoBase + (numSemana - 1) * 5.0 + (numSerie - 1) * 2.5;

                        String repeticiones = numSerie == 1L ? "10" : (numSerie == 2L ? "8" : "6");
                        Intensidad intensidad = numSerie == 1L ? Intensidad.MEDIA : Intensidad.ALTA;

                        Serie serie = Serie.builder()
                                .orden(numSerie)
                                .repeticiones(repeticiones)
                                .peso(pesoProgresivo)
                                .descanso(numSerie == 3L ? "120 segundos" : "90 segundos")
                                .intensidad(intensidad)
                                .comentarios("Serie " + numSerie + " - Semana " + numSemana)
                                .ejercicioAsignado(ejercicioAsignado)
                                .estado(true)
                                .build();
                        serieRepository.save(serie);
                    }
                    ordenEjercicio++;
                }
            }
        }
        System.out.println("✅ Rutina de 4 semanas creada para Ernesto Espinel: " + nombreRutina);
    }

    private void crearRutinaGlobalLibre() {
        System.out.println("\n📋 Creando rutina global base...\n");

        final String nombreRutina = "Rutina Global Base";
        boolean existe = rutinaRepository.findAll().stream()
                .anyMatch(r -> nombreRutina.equalsIgnoreCase(r.getNombre()));
        if (existe) {
            System.out.println("ℹ️  Rutina ya existe: " + nombreRutina);
            return;
        }

        Ejercicio ejercicioGlobal = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL"))
                .findFirst()
                .orElse(null);
        if (ejercicioGlobal == null) return;

        Rutina rutina = Rutina.builder()
                .nombre(nombreRutina)
                .objetivo("General")
                .descripcion("Rutina global base sin asignar.")
                .fechaCreacion(java.time.LocalDate.now())
                .numeroSemanas(1L)
                .estado(true)
                .build();
        rutinaRepository.save(rutina);

        Semana semana = Semana.builder()
                .numeroSemana(1L)
                .descripcion("Semana 1 - Global")
                .numeroDias(1L)
                .rutina(rutina)
                .estado(true)
                .build();
        semanaRepository.save(semana);

        Dia dia = Dia.builder()
                .numeroDia(1L)
                .descripcion("Día 1: Trabajo general")
                .numeroEjercicios(1L)
                .semana(semana)
                .estado(true)
                .build();
        diaRepository.save(dia);

        EjercicioAsignado ejercicioAsignado = EjercicioAsignado.builder()
                .orden(1L)
                .comentarios("Mantener técnica controlada")
                .numeroSeries(1L)
                .dia(dia)
                .ejercicio(ejercicioGlobal)
                .estado(true)
                .build();
        ejercicioAsignadoRepository.save(ejercicioAsignado);

        Serie serie = Serie.builder()
                .orden(1L)
                .repeticiones("10")
                .peso(0.0)
                .descanso("60 segundos")
                .intensidad(Intensidad.BAJA)
                .comentarios("Serie introductoria")
                .ejercicioAsignado(ejercicioAsignado)
                .estado(true)
                .build();
        serieRepository.save(serie);

        System.out.println("✅ Rutina creada: " + nombreRutina);
    }

    private void crearRutinaCompuesta() {
        System.out.println("\n📋 Creando rutina compuesta...\n");

        final String nombreRutina = "Rutina Compuesta";
        boolean existe = rutinaRepository.findAll().stream()
                .anyMatch(r -> nombreRutina.equalsIgnoreCase(r.getNombre()));
        if (existe) {
            System.out.println("ℹ️  Rutina ya existe: " + nombreRutina);
            return;
        }

        Ejercicio ejercicioGlobal1 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL"))
                .findFirst()
                .orElse(null);
        Ejercicio ejercicioGlobal2 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && (ejercicioGlobal1 == null || !e.getNombre().equalsIgnoreCase(ejercicioGlobal1.getNombre())))
                .findFirst()
                .orElse(null);
        if (ejercicioGlobal1 == null || ejercicioGlobal2 == null) return;

        Rutina rutina = Rutina.builder()
                .nombre(nombreRutina)
                .objetivo("Definición muscular")
                .descripcion("Rutina compuesta gratuita con 2 semanas, 2 días por semana, 2 ejercicios por día y 2 series por ejercicio.")
                .fechaCreacion(java.time.LocalDate.now())
                .numeroSemanas(2L)
                .estado(true)
                .build();
        rutinaRepository.save(rutina);

        for (long numSemana = 1L; numSemana <= 2L; numSemana++) {
            Semana semana = Semana.builder()
                    .numeroSemana(numSemana)
                    .descripcion("Semana " + numSemana + " - Rutina compuesta")
                    .numeroDias(2L)
                    .rutina(rutina)
                    .estado(true)
                    .build();
            semanaRepository.save(semana);

            for (long numDia = 1L; numDia <= 2L; numDia++) {
                Dia dia = Dia.builder()
                        .numeroDia(numDia)
                        .descripcion("Semana " + numSemana + " - Día " + numDia + ": Entrenamiento compuesto")
                        .numeroEjercicios(2L)
                        .semana(semana)
                        .estado(true)
                        .build();
                diaRepository.save(dia);

                Ejercicio[] ejerciciosDelDia = {ejercicioGlobal1, ejercicioGlobal2};
                for (int i = 0; i < 2; i++) {
                    Ejercicio ejercicio = ejerciciosDelDia[i];
                    EjercicioAsignado ejercicioAsignado = EjercicioAsignado.builder()
                            .orden((long) (i + 1))
                            .comentarios("Mantener técnica controlada")
                            .numeroSeries(2L)
                            .dia(dia)
                            .ejercicio(ejercicio)
                            .estado(true)
                            .build();
                    ejercicioAsignadoRepository.save(ejercicioAsignado);

                    for (long numSerie = 1L; numSerie <= 2L; numSerie++) {
                        Serie serie = Serie.builder()
                                .orden(numSerie)
                                .repeticiones(numSerie == 1 ? "12" : "10")
                                .peso(ejercicio.getNombre().toLowerCase().contains("sentadilla") ? (numSerie == 1 ? 50.0 : 55.0) : (numSerie == 1 ? 35.0 : 40.0))
                                .descanso("60 segundos")
                                .intensidad(numSerie == 1 ? Intensidad.BAJA : Intensidad.MEDIA)
                                .comentarios(numSerie == 1 ? "Serie de calentamiento" : "Serie de trabajo")
                                .ejercicioAsignado(ejercicioAsignado)
                                .estado(true)
                                .build();
                        serieRepository.save(serie);
                    }
                }
            }
        }
        System.out.println("✅ Rutina creada: " + nombreRutina);
    }

    private void crearRutinaGlobal5Semanas() {
        System.out.println("\n📋 Creando rutina global de 5 semanas...\n");

        final String nombreRutina = "Rutina Global 5 Semanas";
        boolean existe = rutinaRepository.findAll().stream()
                .anyMatch(r -> nombreRutina.equalsIgnoreCase(r.getNombre()));
        if (existe) {
            System.out.println("ℹ️  Rutina ya existe: " + nombreRutina);
            return;
        }

        // Obtener los 7 ejercicios globales en orden
        Ejercicio ej1 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Sentadilla con barra"))
                .findFirst().orElse(null);
        Ejercicio ej2 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Press de banca"))
                .findFirst().orElse(null);
        Ejercicio ej3 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Curl de bíceps"))
                .findFirst().orElse(null);
        Ejercicio ej4 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Prensa de pierna"))
                .findFirst().orElse(null);
        Ejercicio ej5 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Extensión en polea tras nuca"))
                .findFirst().orElse(null);
        Ejercicio ej6 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Dominadas"))
                .findFirst().orElse(null);
        Ejercicio ej7 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Press inclinado"))
                .findFirst().orElse(null);

        if (ej1 == null || ej2 == null || ej3 == null || ej4 == null || ej5 == null || ej6 == null || ej7 == null) {
            System.out.println("⚠️  No se encontraron todos los ejercicios globales necesarios");
            return;
        }

        // Crear rutina global de 5 semanas
        Rutina rutina = Rutina.builder()
                .nombre(nombreRutina)
                .objetivo("Acondicionamiento General")
                .descripcion("Rutina global completa de 5 semanas con 5 días de entrenamiento por semana, usando los 7 ejercicios globales de SABI.")
                .fechaCreacion(java.time.LocalDate.now())
                .numeroSemanas(5L)
                .estado(true)
                .build();
        rutinaRepository.save(rutina);

        // Crear 5 semanas
        for (long numSemana = 1L; numSemana <= 5L; numSemana++) {
            Semana semana = Semana.builder()
                    .numeroSemana(numSemana)
                    .descripcion("Semana " + numSemana + " - Entrenamiento completo")
                    .numeroDias(5L)
                    .rutina(rutina)
                    .estado(true)
                    .build();
            semanaRepository.save(semana);

            // Crear 5 días por semana
            for (long numDia = 1L; numDia <= 5L; numDia++) {
                String descripcionDia;
                Ejercicio[] ejerciciosDelDia;

                // Distribuir ejercicios según el día
                if (numDia == 1L) {
                    descripcionDia = "Día 1: Piernas, Pecho y Brazos";
                    ejerciciosDelDia = new Ejercicio[]{ej1, ej2, ej3}; // Sentadilla, Press banca, Curl
                } else if (numDia == 2L) {
                    descripcionDia = "Día 2: Piernas y Brazos";
                    ejerciciosDelDia = new Ejercicio[]{ej4, ej5}; // Prensa pierna, Extensión polea
                } else if (numDia == 3L) {
                    descripcionDia = "Día 3: Espalda y Pecho";
                    ejerciciosDelDia = new Ejercicio[]{ej6, ej7}; // Dominadas, Press inclinado
                } else if (numDia == 4L) {
                    descripcionDia = "Día 4: Piernas, Pecho y Brazos";
                    ejerciciosDelDia = new Ejercicio[]{ej1, ej2, ej3}; // Sentadilla, Press banca, Curl
                } else {
                    descripcionDia = "Día 5: Piernas y Brazos";
                    ejerciciosDelDia = new Ejercicio[]{ej4, ej5}; // Prensa pierna, Extensión polea
                }

                Dia dia = Dia.builder()
                        .numeroDia(numDia)
                        .descripcion("Semana " + numSemana + " - " + descripcionDia)
                        .numeroEjercicios((long) ejerciciosDelDia.length)
                        .semana(semana)
                        .estado(true)
                        .build();
                diaRepository.save(dia);

                // Crear ejercicios asignados para cada día
                long ordenEjercicio = 1L;
                for (Ejercicio ejercicio : ejerciciosDelDia) {
                    EjercicioAsignado ejercicioAsignado = EjercicioAsignado.builder()
                            .orden(ordenEjercicio)
                            .comentarios("Mantener buena forma y técnica")
                            .numeroSeries(3L) // 3 series por ejercicio
                            .dia(dia)
                            .ejercicio(ejercicio)
                            .estado(true)
                            .build();
                    ejercicioAsignadoRepository.save(ejercicioAsignado);

                    // Crear 3 series por ejercicio con progresión
                    for (long numSerie = 1L; numSerie <= 3L; numSerie++) {
                        // Calcular peso según ejercicio y semana
                        double pesoBase;
                        if (ejercicio.getNombre().toLowerCase().contains("sentadilla")) {
                            pesoBase = 60.0;
                        } else if (ejercicio.getNombre().toLowerCase().contains("press de banca") || ejercicio.getNombre().toLowerCase().contains("press inclinado")) {
                            pesoBase = 40.0;
                        } else if (ejercicio.getNombre().toLowerCase().contains("prensa de pierna")) {
                            pesoBase = 80.0;
                        } else if (ejercicio.getNombre().toLowerCase().contains("dominadas")) {
                            pesoBase = 0.0; // Peso corporal
                        } else if (ejercicio.getNombre().toLowerCase().contains("curl")) {
                            pesoBase = 10.0;
                        } else {
                            pesoBase = 15.0; // Extensión polea
                        }

                        // Progresión: aumentar peso según semana y serie
                        double peso = pesoBase + (numSemana - 1) * 2.5 + (numSerie - 1) * 2.5;

                        // Repeticiones decrecientes por serie
                        String repeticiones = numSerie == 1L ? "12" : (numSerie == 2L ? "10" : "8");

                        // Intensidad creciente por serie
                        Intensidad intensidad = numSerie == 1L ? Intensidad.BAJA : (numSerie == 2L ? Intensidad.MEDIA : Intensidad.ALTA);

                        Serie serie = Serie.builder()
                                .orden(numSerie)
                                .repeticiones(repeticiones)
                                .peso(peso)
                                .descanso(numSerie == 3L ? "90 segundos" : "60 segundos")
                                .intensidad(intensidad)
                                .comentarios("Semana " + numSemana + " - Serie " + numSerie)
                                .ejercicioAsignado(ejercicioAsignado)
                                .estado(true)
                                .build();
                        serieRepository.save(serie);
                    }
                    ordenEjercicio++;
                }
            }
        }
        System.out.println("✅ Rutina global de 5 semanas creada: " + nombreRutina);
        System.out.println("   📊 5 semanas × 5 días = 25 días de entrenamiento");
        System.out.println("   💪 Usando los 7 ejercicios globales de SABI");
    }
}
