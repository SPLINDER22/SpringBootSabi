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
import com.sabi.sabi.entity.Suscripcion;
import com.sabi.sabi.repository.SuscripcionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

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
        private final SuscripcionRepository suscripcionRepository;

        @Override
        public void run(String... args) {
                // Clientes con diagnósticos detallados
                crearClienteConDiagnosticoDetallado("Carlos Colmenares", "rojasmena1222@gmail.com", "1234567",
                        70.0, 170.0, NivelExperiencia.PRINCIPIANTE, "3 veces por semana, 45 min",
                        "casa", "ninguna", "ninguna", 8L,
                        "Dieta balanceada");

                crearClienteConDiagnosticoDetallado("Laura Torres", "cliente2@sabi.com", "1234567",
                        55.0, 162.0, NivelExperiencia.PRINCIPIANTE, "4 veces por semana, 40 min",
                        "gimnasio básico", "ninguna", "ninguna", 7L,
                        "Dieta baja en carbohidratos");

                // Cliente con HISTORIAL de 2 diagnósticos para comparativa
                crearClienteConHistorialDiagnosticos("Miguel Ángel", "cliente3@sabi.com", "1234567");

                crearClienteConDiagnosticoDetallado("Paula Ruiz", "cliente4@sabi.com", "1234567",
                        68.0, 168.0, NivelExperiencia.AVANZADO, "6 veces por semana, 75 min",
                        "gimnasio completo", "ninguna", "ninguna", 8L,
                        "Dieta flexible (IIFYM)");

                crearClienteConDiagnosticoDetallado("Andrés Castro", "cliente5@sabi.com", "1234567",
                        90.0, 182.0, NivelExperiencia.INTERMEDIO, "4 veces por semana, 60 min",
                        "gimnasio y casa", "Tendinitis en codo", "Diabetes tipo 2 controlada", 7L,
                        "Dieta para diabéticos");

                // 5 clientes adicionales with varied diagnoses
                crearClienteConDiagnosticoDetallado("Roberto Gómez", "cliente6@sabi.com", "1234567",
                        85.0, 180.0, NivelExperiencia.INTERMEDIO, "5 veces por semana, 60 min",
                        "gimnasio completo", "Lesión antigua de rodilla", "ninguna", 7L,
                        "Dieta alta en proteínas");

                // Cliente con HISTORIAL de 2 diagnósticos para comparativa
                crearClienteConHistorialDiagnosticos("Valentina Morales", "cliente7@sabi.com", "1234567");

                crearClienteConDiagnosticoDetallado("Fernando Silva", "cliente8@sabi.com", "1234567",
                        95.0, 175.0, NivelExperiencia.PRINCIPIANTE, "4 veces por semana, 45 min",
                        "parque y casa", "ninguna", "Presión alta controlada", 6L,
                        "Dieta baja en sodio");

                crearClienteConDiagnosticoDetallado("Carolina Vargas", "cliente9@sabi.com", "1234567",
                        58.0, 160.0, NivelExperiencia.AVANZADO, "6 veces por semana, 90 min",
                        "gimnasio completo", "Tendinitis en hombro derecho", "ninguna", 8L,
                        "Dieta cetogénica");

                crearClienteConDiagnosticoDetallado("Sebastián Rojas", "cliente10@sabi.com", "1234567",
                        78.0, 178.0, NivelExperiencia.INTERMEDIO, "4 veces por semana, 50 min",
                        "gimnasio básico", "Dolor lumbar ocasional", "ninguna", 7L,
                        "Dieta mediterránea");

                // Entrenadores con especialidades múltiples y precios
                crearEntrenadorDetalladoSiNoExiste(
                        "Ernesto", "Espinel", "entrenador@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1987, 5, 14), Departamento.CUNDINAMARCA, "Bogotá",
                        TipoDocumento.CC, "1012456789", "3101234567",
                        "Fuerza y Acondicionamiento, Ganancia Muscular, Powerlifting", 4.6, 8,
                        "Entrenador con enfoque en fuerza e hipertrofia, planificación y técnica correcta.",
                        50000.0, 80000.0
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Ana", "García", "entrenador1@sabi.com", "1234567",
                        Sexo.FEMENINO, LocalDate.of(1990, 9, 22), Departamento.ANTIOQUIA, "Medellín",
                        TipoDocumento.CC, "1033345698", "3129876543",
                        "CrossFit, Cardio y Resistencia, Entrenamiento Funcional", 4.3, 6,
                        "Especialista en entrenamiento metabólico y acondicionamiento de alta intensidad.",
                        45000.0, 75000.0
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Luis", "Martínez", "entrenador2@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1985, 3, 8), Departamento.VALLE_DEL_CAUCA, "Cali",
                        TipoDocumento.CC, "1009876523", "3004567890",
                        "Pérdida de Peso, Entrenamiento Funcional, Nutrición Deportiva", 4.1, 10,
                        "Acompañamiento integral para reducción de grasa y hábitos saludables.",
                        40000.0, 70000.0
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "María", "López", "entrenador3@sabi.com", "1234567",
                        Sexo.FEMENINO, LocalDate.of(1992, 12, 11), Departamento.ATLANTICO, "Barranquilla",
                        TipoDocumento.CC, "1015678923", "3011237894",
                        "Entrenamiento Funcional, Movilidad y Flexibilidad, Rehabilitación", 4.4, 4,
                        "Trabajo de movilidad articular, estabilidad y funcionalidad para la vida diaria.",
                        35000.0, 60000.0
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Carlos", "Pérez", "entrenador4@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1988, 7, 19), Departamento.SANTANDER, "Bucaramanga",
                        TipoDocumento.CC, "1098765432", "3159988776",
                        "Running, Cardio y Resistencia, Deportes Específicos", 4.2, 7,
                        "Planificación de carreras 5K-21K, técnica de carrera y fortalecimiento específico.",
                        30000.0, 55000.0
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Sofía", "Rodríguez", "entrenador5@sabi.com", "1234567",
                        Sexo.FEMENINO, LocalDate.of(1995, 4, 30), Departamento.BOLIVAR, "Cartagena",
                        TipoDocumento.CC, "1012349987", "3176655443",
                        "Pilates, Yoga, Movilidad y Flexibilidad", 4.5, 5,
                        "Fortalecimiento de core, postura y prevención de dolor lumbar con métodos de pilates.",
                        40000.0, 65000.0
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Jorge", "Hernández", "entrenador6@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1984, 2, 2), Departamento.CUNDINAMARCA, "Chía",
                        TipoDocumento.CC, "1004567812", "3114455667",
                        "Powerlifting, Fuerza y Acondicionamiento, Ganancia Muscular", 4.7, 12,
                        "Entrenamientos de levantamiento olímpico, técnica de arranque y envión.",
                        60000.0, 100000.0
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Lucía", "Fernández", "entrenador7@sabi.com", "1234567",
                        Sexo.FEMENINO, LocalDate.of(1993, 11, 5), Departamento.BOGOTA, "Bogotá",
                        TipoDocumento.CC, "1023456780", "3193344556",
                        "Yoga, Pilates, Movilidad y Flexibilidad", 4.0, 3,
                        "Sesiones de yoga enfocadas en flexibilidad, respiración y control postural.",
                        35000.0, 55000.0
                );
                crearEntrenadorDetalladoSiNoExiste(
                        "Diego", "Gómez", "entrenador8@sabi.com", "1234567",
                        Sexo.MASCULINO, LocalDate.of(1989, 1, 17), Departamento.ANTIOQUIA, "Envigado",
                        TipoDocumento.CC, "1001234599", "3135566778",
                        "Calistenia, Entrenamiento Funcional, Fuerza y Acondicionamiento", 4.3, 6,
                        "Progresiones de calistenia, dominadas, fondos y trabajo de fuerza con peso corporal.",
                        38000.0, 62000.0
                );

                // Asignar fotos de perfil a todos los entrenadores (tras crearlos)
                asignarFotoPerfilPorEmail("entrenador@sabi.com");
                asignarFotoPerfilPorEmail("entrenador1@sabi.com");
                asignarFotoPerfilPorEmail("entrenador2@sabi.com");
                asignarFotoPerfilPorEmail("entrenador3@sabi.com");
                asignarFotoPerfilPorEmail("entrenador4@sabi.com");
                asignarFotoPerfilPorEmail("entrenador5@sabi.com");
                asignarFotoPerfilPorEmail("entrenador6@sabi.com");
                asignarFotoPerfilPorEmail("entrenador7@sabi.com");
                asignarFotoPerfilPorEmail("entrenador8@sabi.com");

                // Asignar fotos de perfil a clientes creados por email
                asignarFotoPerfilPorEmail("rojasmena1222@gmail.com"); // Carlos Colmenares
                asignarFotoPerfilPorEmail("cliente2@sabi.com"); // Laura Torres
                asignarFotoPerfilPorEmail("cliente3@sabi.com"); // Miguel Ángel
                asignarFotoPerfilPorEmail("cliente4@sabi.com"); // Paula Ruiz
                asignarFotoPerfilPorEmail("cliente5@sabi.com"); // Andrés Castro
                asignarFotoPerfilPorEmail("cliente6@sabi.com"); // Roberto Gómez
                asignarFotoPerfilPorEmail("cliente7@sabi.com"); // Valentina Morales
                asignarFotoPerfilPorEmail("cliente8@sabi.com"); // Fernando Silva
                asignarFotoPerfilPorEmail("cliente9@sabi.com"); // Carolina Vargas
                asignarFotoPerfilPorEmail("cliente10@sabi.com"); // Sebastián Rojas

                crearEjerciciosSiNoExisten();
                crearRutinaDeEjemplo();
                crearRutinaDeErnesto4Semanas(); // Rutina de 4 semanas para Ernesto Espinel
                crearRutinaGlobalLibre(); // Nueva rutina global sin cliente ni entrenador ni estadoRutina
                crearRutinaCompuesta(); // Nueva rutina compuesta gratuita

                // Crear suscripciones de ejemplo (mezcla de estados)
                inicializarSuscripcionesDemo();

                // Mostrar en consola un resumen de los usuarios creados / existentes
                System.out.println("");
                System.out.println("Resumen de usuarios iniciales:");
                System.out.println("");
                System.out.println("Cliente - cliente@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente2@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente3@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente4@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente5@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente6@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente7@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente8@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente9@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Cliente - cliente10@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador1@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador2@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador3@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador4@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador5@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador6@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador7@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Entrenador - entrenador8@sabi.com - Contraseña (raw): 1234567");
                System.out.println("Admin - admin@sabi.com - Contraseña (raw): 1234567");
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
            // Copiar el objetivo del perfil al diagnóstico (historial)
            String objetivoDiagnostico = cliente.getObjetivo() != null ? cliente.getObjetivo() : "Mejorar condición física general";

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
                    .objetivo(objetivoDiagnostico) // Guardar objetivo para historial
                    .estado(true)
                    .build();
            diagnosticoRepository.save(diagnostico);
            System.out.println("Diagnóstico creado para cliente: " + email);
            System.out.println("  🎯 Con objetivo: " + objetivoDiagnostico);
        }
    }

    private void crearClienteConDiagnosticoDetallado(String nombre, String email, String rawPassword,
                                                     Double peso, Double estatura, NivelExperiencia nivelExperiencia,
                                                     String disponibilidadTiempo, String accesoRecursos,
                                                     String lesiones, String condicionesMedicas, Long horasSueno,
                                                     String habitosAlimenticios) {
        Cliente cliente;
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            cliente = Cliente.builder()
                    .nombre(nombre)
                    .email(email)
                    .contraseña(passwordEncoder.encode(rawPassword))
                    .rol(Rol.CLIENTE)
                    .estado(true)
                    .objetivo("Mejorar condición física")
                    .build();
            usuarioRepository.save(cliente);
            System.out.println("Usuario creado: " + nombre + " | " + email + " | contraseña (raw): " + rawPassword);
        } else {
            cliente = (Cliente) usuarioRepository.findByEmail(email).get();
            System.out.println("Usuario ya existe: " + email + " (no se muestra contraseña raw)");
        }

        // Crear diagnóstico personalizado si no existe
        if (diagnosticoRepository.findByClienteIdAndEstadoTrue(cliente.getId()).isEmpty()) {
            // Definir objetivo específico según el peso y nivel del cliente
            String objetivoParaPerfil = determinarObjetivoSegunPerfil(peso, estatura, nivelExperiencia);

            // Actualizar el objetivo en el PERFIL del cliente
            cliente.setObjetivo(objetivoParaPerfil);
            usuarioRepository.save(cliente);

            Diagnostico diagnostico = Diagnostico.builder()
                    .cliente(cliente)
                    .fecha(java.time.LocalDate.now())
                    .peso(peso)
                    .estatura(estatura)
                    .nivelExperiencia(nivelExperiencia)
                    .disponibilidadTiempo(disponibilidadTiempo)
                    .accesoRecursos(accesoRecursos)
                    .lesiones(lesiones)
                    .condicionesMedicas(condicionesMedicas)
                    .horasSueno(horasSueno)
                    .habitosAlimenticios(habitosAlimenticios)
                    .objetivo(objetivoParaPerfil) // Guardar en diagnóstico también (historial)
                    .estado(true)
                    .build();
            diagnosticoRepository.save(diagnostico);
            System.out.println("✅ Diagnóstico creado para " + email);
            System.out.println("   🎯 Objetivo guardado en PERFIL: " + objetivoParaPerfil);
        }
    }

    // Método auxiliar para determinar un objetivo realista según el perfil
    private String determinarObjetivoSegunPerfil(Double peso, Double estatura, NivelExperiencia nivel) {
        if (peso == null || estatura == null) return "Mejorar condición física general";

        // Calcular IMC aproximado
        double estaturaM = estatura / 100.0;
        double imc = peso / (estaturaM * estaturaM);

        // Determinar objetivo según IMC y nivel de experiencia
        if (imc < 18.5) {
            return nivel == NivelExperiencia.PRINCIPIANTE
                ? "Ganar peso y masa muscular saludable"
                : "Aumentar masa muscular y fuerza";
        } else if (imc >= 18.5 && imc < 25) {
            return nivel == NivelExperiencia.AVANZADO
                ? "Definición muscular y rendimiento deportivo"
                : "Mantener peso y mejorar composición corporal";
        } else if (imc >= 25 && imc < 30) {
            return "Perder grasa y tonificar el cuerpo";
        } else {
            return "Perder peso de forma saludable y mejorar salud cardiovascular";
        }
    }

    // Método para crear cliente con historial de 2 diagnósticos para comparativa
    private void crearClienteConHistorialDiagnosticos(String nombre, String email, String rawPassword) {
        Cliente cliente;
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            cliente = Cliente.builder()
                    .nombre(nombre)
                    .email(email)
                    .contraseña(passwordEncoder.encode(rawPassword))
                    .rol(Rol.CLIENTE)
                    .estado(true)
                    .build();
            usuarioRepository.save(cliente);
            System.out.println("Usuario creado con historial: " + nombre + " | " + email);
        } else {
            cliente = (Cliente) usuarioRepository.findByEmail(email).get();
            System.out.println("Usuario ya existe: " + email);
        }

        // Verificar si ya tiene diagnósticos
        if (!diagnosticoRepository.findByClienteIdAndEstadoTrue(cliente.getId()).isEmpty()) {
            System.out.println("   ⏭️ Cliente ya tiene diagnósticos, saltando creación");
            return;
        }

        // Datos del diagnóstico ANTIGUO (hace 3 meses) - estado inicial
        if (email.equals("cliente3@sabi.com")) {
            // Miguel Ángel - Progreso de pérdida de peso
            // Diagnóstico 1: hace 3 meses - sobrepeso
            Diagnostico diagnostico1 = Diagnostico.builder()
                    .cliente(cliente)
                    .fecha(java.time.LocalDate.now().minusMonths(3))
                    .peso(90.0) // peso inicial más alto
                    .estatura(177.0)
                    .nivelExperiencia(NivelExperiencia.PRINCIPIANTE) // era principiante
                    .disponibilidadTiempo("3 veces por semana, 40 min")
                    .accesoRecursos("casa con mancuernas")
                    .lesiones("Dolor de rodilla leve")
                    .condicionesMedicas("ninguna")
                    .horasSueno(6L) // dormía menos
                    .habitosAlimenticios("Dieta irregular, comida rápida frecuente")
                    .objetivo("Perder grasa y tonificar el cuerpo")
                    .estado(false) // inactivo porque es antiguo
                    .build();
            diagnosticoRepository.save(diagnostico1);

            // Diagnóstico 2: actual - mejoría visible
            Diagnostico diagnostico2 = Diagnostico.builder()
                    .cliente(cliente)
                    .fecha(java.time.LocalDate.now())
                    .peso(82.0) // perdió 8kg
                    .estatura(177.0)
                    .nivelExperiencia(NivelExperiencia.INTERMEDIO) // mejoró nivel
                    .disponibilidadTiempo("5 veces por semana, 55 min") // más tiempo
                    .accesoRecursos("gimnasio completo") // mejor acceso
                    .lesiones("Recuperado, sin dolor") // mejoró
                    .condicionesMedicas("ninguna")
                    .horasSueno(7L) // duerme más
                    .habitosAlimenticios("Dieta alta en proteínas, controlada")
                    .objetivo("Mantener peso y mejorar composición corporal")
                    .estado(true) // activo
                    .build();
            diagnosticoRepository.save(diagnostico2);

            // Actualizar objetivo en perfil con el más reciente
            cliente.setObjetivo("Mantener peso y mejorar composición corporal");
            usuarioRepository.save(cliente);

            System.out.println("✅ Historial creado para Miguel Ángel:");
            System.out.println("   📊 Diagnóstico 1 (hace 3 meses): 90kg - Principiante");
            System.out.println("   📊 Diagnóstico 2 (actual): 82kg - Intermedio");
            System.out.println("   💪 Progreso: -8kg, mejoró nivel");

        } else if (email.equals("cliente7@sabi.com")) {
            // Valentina Morales - Progreso de ganancia muscular
            // Diagnóstico 1: hace 3 meses - bajo peso
            Diagnostico diagnostico1 = Diagnostico.builder()
                    .cliente(cliente)
                    .fecha(java.time.LocalDate.now().minusMonths(3))
                    .peso(56.0) // peso inicial más bajo
                    .estatura(165.0)
                    .nivelExperiencia(NivelExperiencia.PRINCIPIANTE)
                    .disponibilidadTiempo("2 veces por semana, 30 min")
                    .accesoRecursos("casa sin equipamiento")
                    .lesiones("ninguna")
                    .condicionesMedicas("ninguna")
                    .horasSueno(7L)
                    .habitosAlimenticios("Vegetariana, comidas pequeñas")
                    .objetivo("Ganar peso y masa muscular saludable")
                    .estado(false) // inactivo
                    .build();
            diagnosticoRepository.save(diagnostico1);

            // Diagnóstico 2: actual - ganó masa muscular
            Diagnostico diagnostico2 = Diagnostico.builder()
                    .cliente(cliente)
                    .fecha(java.time.LocalDate.now())
                    .peso(62.0) // ganó 6kg
                    .estatura(165.0)
                    .nivelExperiencia(NivelExperiencia.PRINCIPIANTE) // aún aprendiendo
                    .disponibilidadTiempo("3 veces por semana, 40 min") // más tiempo
                    .accesoRecursos("casa con mancuernas") // mejor equipo
                    .lesiones("ninguna")
                    .condicionesMedicas("ninguna")
                    .horasSueno(8L) // mejor descanso
                    .habitosAlimenticios("Vegetariana, comida balanceada, más proteína")
                    .objetivo("Aumentar masa muscular y fuerza")
                    .estado(true) // activo
                    .build();
            diagnosticoRepository.save(diagnostico2);

            // Actualizar objetivo en perfil
            cliente.setObjetivo("Aumentar masa muscular y fuerza");
            usuarioRepository.save(cliente);

            System.out.println("✅ Historial creado para Valentina Morales:");
            System.out.println("   📊 Diagnóstico 1 (hace 3 meses): 56kg - Principiante");
            System.out.println("   📊 Diagnóstico 2 (actual): 62kg - Principiante");
            System.out.println("   💪 Progreso: +6kg masa muscular");
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
                        String especialidades, Double calificacionPromedio, Integer aniosExperiencia,
                        String descripcion, Double precioMinimo, Double precioMaximo) {

                if (usuarioRepository.findByEmail(email).isPresent()) {
                        System.out.println("Usuario ya existe: " + email + " (no se muestra contraseña raw)");
                        return;
                }

                // La primera especialidad se guarda también en el campo legacy
                String primeraEspecialidad = especialidades.contains(",")
                        ? especialidades.split(",")[0].trim()
                        : especialidades;

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
                                .especialidad(primeraEspecialidad) // Campo legacy
                                .especialidades(especialidades) // Nuevo campo con todas
                                .precioMinimo(precioMinimo)
                                .precioMaximo(precioMaximo)
                                .calificacionPromedio(calificacionPromedio)
                                .aniosExperiencia(aniosExperiencia)
                                .build();
                usuarioRepository.save(entrenador);
                System.out.println("Usuario creado: " + nombre + " " + apellido + " | " + email + " | Especialidades: " + especialidades + " | Precio: $" + precioMinimo + " - $" + precioMaximo);
        }

    // Helpers para obtener clientes/entrenadores por email
    private Optional<Cliente> getClientePorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(u -> (Cliente) u);
    }
    private Optional<Entrenador> getEntrenadorPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(u -> (Entrenador) u);
    }

    private void crearSuscripcionSiNoExiste(Cliente cliente, Entrenador entrenador,
                                            EstadoSuscripcion estado,
                                            Double precio, Integer duracionSemanas) {
        if (cliente == null || entrenador == null) return;
        boolean existe = suscripcionRepository.findAll().stream()
                .anyMatch(s -> s.getCliente().getId().equals(cliente.getId())
                        && s.getEntrenador().getId().equals(entrenador.getId())
                        && s.getEstadoSuscripcion() == estado
                        && java.util.Objects.equals(s.getDuracionSemanas(), duracionSemanas)
                        && java.util.Objects.equals(s.getPrecio(), precio));
        if (existe) return;
        Suscripcion s = Suscripcion.builder()
                .cliente(cliente)
                .entrenador(entrenador)
                .estadoSuscripcion(estado)
                .precio(precio)
                .duracionSemanas(duracionSemanas)
                .estado(true)
                .build();
        suscripcionRepository.save(s);
    }

    private void inicializarSuscripcionesDemo() {
        Cliente c1 = getClientePorEmail("cliente@sabi.com").orElse(null);
        Cliente c2 = getClientePorEmail("cliente2@sabi.com").orElse(null);
        Cliente c3 = getClientePorEmail("cliente3@sabi.com").orElse(null);
        Cliente c4 = getClientePorEmail("cliente4@sabi.com").orElse(null);
        Cliente c5 = getClientePorEmail("cliente5@sabi.com").orElse(null);

        // Todas las suscripciones se asignarán al mismo entrenador: Ernesto (entrenador@sabi.com)
        Entrenador e0 = getEntrenadorPorEmail("entrenador@sabi.com").orElse(null);

        // PENDIENTE sin precio/fechas
        crearSuscripcionSiNoExiste(c2, e0, EstadoSuscripcion.PENDIENTE, null, null);
        crearSuscripcionSiNoExiste(c3, e0, EstadoSuscripcion.PENDIENTE, null, null);

        // COTIZADA con precio y duración propuestas
        crearSuscripcionSiNoExiste(c1, e0, EstadoSuscripcion.COTIZADA, 180.0, 4);
        crearSuscripcionSiNoExiste(c4, e0, EstadoSuscripcion.COTIZADA, 220.0, 8);

        // ACEPTADA activa
        crearSuscripcionSiNoExiste(c5, e0, EstadoSuscripcion.ACEPTADA, 200.0, 4);

        // RECHAZADA (para historial)
        crearSuscripcionSiNoExiste(c3, e0, EstadoSuscripcion.RECHAZADA, 190.0, 6);
        crearSuscripcionSiNoExiste(c4, e0, EstadoSuscripcion.RECHAZADA, 210.0, 6);
        crearSuscripcionSiNoExiste(c2, e0, EstadoSuscripcion.RECHAZADA, 175.0, 5);
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
    }

    private void crearRutinaDeErnesto4Semanas() {
        // Obtener entrenador Ernesto Espinel
        Entrenador entrenador = (Entrenador) usuarioRepository.findByEmail("entrenador@sabi.com").orElse(null);
        if (entrenador == null) return;

        // Verificar si ya existe la rutina
        final String nombreRutina = "Rutina Fuerza 4 Semanas";
        if (rutinaRepository.findAll().stream().anyMatch(r -> nombreRutina.equals(r.getNombre()))) return;

        // Obtener ejercicios globales necesarios
        Ejercicio sentadilla = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Sentadilla con barra"))
                .findFirst().orElse(null);
        Ejercicio pressBanca = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Press de banca"))
                .findFirst().orElse(null);
        Ejercicio pesoMuerto = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && e.getNombre().equalsIgnoreCase("Peso muerto"))
                .findFirst().orElse(null);

        if (sentadilla == null || pressBanca == null) return; // No crear si faltan ejercicios base

        // Usar sentadilla como alternativa si no existe peso muerto
        if (pesoMuerto == null) pesoMuerto = sentadilla;

        // Crear rutina con 4 semanas
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

        // Crear 4 semanas
        for (long numSemana = 1L; numSemana <= 4L; numSemana++) {
            Semana semana = Semana.builder()
                    .numeroSemana(numSemana)
                    .descripcion("Semana " + numSemana + " - Progresión de fuerza")
                    .numeroDias(3L)
                    .rutina(rutina)
                    .estado(true)
                    .build();
            semanaRepository.save(semana);

            // Crear 3 días por semana
            for (long numDia = 1L; numDia <= 3L; numDia++) {
                String descripcionDia;
                Ejercicio[] ejerciciosDelDia;

                // Variar ejercicios según el día
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

                    // Crear 3 series por ejercicio asignado con progresión semanal
                    for (long numSerie = 1L; numSerie <= 3L; numSerie++) {
                        // Calcular peso progresivo según la semana
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
                .intensidad(Intensidad.BAJA)
                .comentarios("Serie introductoria")
                .ejercicioAsignado(ejercicioAsignado)
                .estado(true)
                .build();
        serieRepository.save(serie);
    }

    private void crearRutinaCompuesta() {
        // Verificar si ya existe para no duplicar
        final String nombreRutina = "Rutina Compuesta";
        boolean existe = rutinaRepository.findAll().stream()
                .anyMatch(r -> nombreRutina.equalsIgnoreCase(r.getNombre()));
        if (existe) return;

        // Tomar dos ejercicios globales existentes (si no hay, abortar)
        Ejercicio ejercicioGlobal1 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL"))
                .findFirst()
                .orElse(null);
        Ejercicio ejercicioGlobal2 = ejercicioRepository.findAll().stream()
                .filter(e -> e.getTipo().name().equals("GLOBAL") && (ejercicioGlobal1 == null || !e.getNombre().equalsIgnoreCase(ejercicioGlobal1.getNombre())))
                .findFirst()
                .orElse(null);
        if (ejercicioGlobal1 == null || ejercicioGlobal2 == null) return; // No se puede crear sin al menos dos ejercicios globales

        // Crear rutina sin cliente, sin entrenador y sin estadoRutina explícito
        Rutina rutina = Rutina.builder()
                .nombre(nombreRutina)
                .objetivo("Definición muscular")
                .descripcion("Rutina compuesta gratuita con 2 semanas, 2 días por semana, 2 ejercicios por día y 2 series por ejercicio.")
                .fechaCreacion(java.time.LocalDate.now())
                .numeroSemanas(2L)
                .estado(true)
                .build();
        rutinaRepository.save(rutina);

        // Crear 2 semanas
        for (long numSemana = 1L; numSemana <= 2L; numSemana++) {
            Semana semana = Semana.builder()
                    .numeroSemana(numSemana)
                    .descripcion("Semana " + numSemana + " - Rutina compuesta")
                    .numeroDias(2L)
                    .rutina(rutina)
                    .estado(true)
                    .build();
            semanaRepository.save(semana);

            // Crear 2 días por semana
            for (long numDia = 1L; numDia <= 2L; numDia++) {
                Dia dia = Dia.builder()
                        .numeroDia(numDia)
                        .descripcion("Semana " + numSemana + " - Día " + numDia + ": Entrenamiento compuesto")
                        .numeroEjercicios(2L)
                        .semana(semana)
                        .estado(true)
                        .build();
                diaRepository.save(dia);

                // Crear 2 ejercicios por día
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

                    // Crear 2 series por ejercicio asignado
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
    }

        // Helper: asigna fotoPerfilUrl segun email conocido
        private void asignarFotoPerfilPorEmail(String email) {
            usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                String ruta = resolverRutaFotoPorEmail(email, usuario.getRol());
                if (ruta != null) {
                    usuario.setFotoPerfilUrl(ruta);
                    usuarioRepository.save(usuario);
                    System.out.println("🖼️ Foto de perfil asignada a " + email + ": " + ruta);
                }
            });
        }

        // Mapeo de emails a archivos en static/img/perfildata
        private String resolverRutaFotoPorEmail(String email, Rol rol) {
            switch (email) {
                // Clientes
                case "rojasmena1222@gmail.com":
                    return "/img/perfildata/Carlos Colmenares Cliente.jpg";
                case "cliente2@sabi.com":
                    return "/img/perfildata/Laura Torres Cliente.png";
                case "cliente3@sabi.com":
                    return "/img/perfildata/Miguel Angel Cliente.png";
                case "cliente4@sabi.com":
                    return "/img/perfildata/Paula Ruiz Cliente.png";
                case "cliente5@sabi.com":
                    return "/img/perfildata/Andres Castro Cliente.jpg";
                case "cliente6@sabi.com":
                    return "/img/perfildata/Roberto Gomez Cliente.jpg";
                case "cliente7@sabi.com":
                    return "/img/perfildata/Valentina Morales Cliente.jpg";
                case "cliente8@sabi.com":
                    return "/img/perfildata/Fernando Silva Cliente.jpg";
                case "cliente9@sabi.com":
                    return "/img/perfildata/Carolina Vargas Clienta.jpg";
                case "cliente10@sabi.com":
                    return "/img/perfildata/Sebastian Rojas Cliente.jpg";

                // Entrenadores
                case "entrenador@sabi.com":
                    return "/img/perfildata/Ernesto EspinelEntrenador.webp"; // archivo existente en carpeta
                case "entrenador1@sabi.com":
                    return "/img/perfildata/Ana Garcia Entrenadora.webp";
                case "entrenador2@sabi.com":
                    return "/img/perfildata/Luis Martínez Entrenador.jpg";
                case "entrenador3@sabi.com":
                    return "/img/perfildata/Maria Lopez Entrenadora.webp";
                case "entrenador4@sabi.com":
                    return "/img/perfildata/Carlos Pérez Entrenador.jpg";
                case "entrenador5@sabi.com":
                    return "/img/perfildata/Lucia Fernandez Entrenadora.jpg"; // carpeta tiene Lucia Fernandez Entrenadora.jpg
                case "entrenador6@sabi.com":
                    return "/img/perfildata/Jorge Hernández Entrenador.png";
                case "entrenador7@sabi.com":
                    return "/img/perfildata/Lucia Fernandez Entrenadora.jpg";
                case "entrenador8@sabi.com":
                    return "/img/perfildata/Diego Gómez Entrenador.webp";
                case "admin@sabi.com":
                    return "/img/perfildata/Admin Sabi Admin.png"; // usa la foto creada para el admin
                default:
                    return rol == Rol.ENTRENADOR ? "/img/entrenador.jpg" : (rol == Rol.ADMIN ? "/img/fotoPerfil.png" : "/img/cliente.jpg");
            }
        }
}
