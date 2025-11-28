Resumen de la vista: `cliente/suscripcion.html`

Objetivo
- Mostrar al cliente la información de su suscripción actual: estado, precio, duración, rutina asignada (si existe), progreso, contacto del entrenador y acciones (pagar, cancelar, ver rutinas, ver historial).

Datos esperados en el model (atributos Thymeleaf)
- suscripcion: objeto SuscripcionDTO con campos (idSuscripcion, precio, duracionSemanas, estadoSuscripcion, idEntrenador, ...)
- rutina: objeto RutinaDTO (idRutina, nombre, descripcion, idEntrenador, ...)
- nombreEntrenador: String (nombre + apellido) — proporcionado por el controller si existe entrenador
- fotoEntrenador: URL String para imagen de perfil
- telefonoEntrenador: String
- emailEntrenador: String
- diaActual: DiaDTO (semana.numeroSemana, numeroDia, idDia, ...)
- porcentajeCompletado: Integer (0..100)
- semanaActual, primerEjercicioDia: ids usados para enlaces a la vista unificada
- _csrf: token CSRF para formularios POST (Spring Security)

EndPoints / enlaces usados por la vista
- /cliente/listaEntrenadores — buscar entrenadores
- /cliente/rutinas — ver rutinas del cliente
- /rutina/cliente/vista-unificada/{idRutina} — vista unificada de la rutina (requiere ids adicionales por query/params)
- /suscripciones/pagar/{id} — POST para pagar
- /cliente/suscripcion/cancelar/{id} — POST para cancelar
- /cliente/suscripcion/historial — historial de suscripciones

Reglas de visualización y lógica importante
- Mostrar la sección de rutina solo si `rutina != null && rutina.idEntrenador != null` (rutina asignada por un entrenador)
- Si `suscripcion == null`: mostrar CTA para buscar entrenadores
- Mostrar nombre y apellido del entrenador (si existe) y teléfono/email como texto y como acciones (WhatsApp / mail). Botones deben estar deshabilitados si faltan datos.
- Precio y duración deben destacarse visualmente. Mostrar ambos en la misma línea (lado a lado) con iconos.
- Barra de progreso con número porcentual (p. ej. "50%") al lado.

Acciones recomendadas cuando se reestructura
1. Evitar posicionamientos absolutos en elementos del contenido (causan solapamientos con footer). Usar Flexbox o Grid con `margin-left:auto` para colocar bloques a la derecha.
2. Mantener la lógica de Thymeleaf mínima y clara: `th:if`, `th:text`, `th:href`, `th:action`.
3. Extraer estilos CSS a un archivo separado (por ejemplo `static/css/suscripcion.css`) en la siguiente iteración.
4. Añadir tests visuales manuales para los siguientes casos:
   - sin suscripción
   - suscripción sin rutina asignada
   - suscripción con rutina asignada y progreso parcial
   - caso con títulos largos (saneamiento/truncado)

Notas sobre problemas detectados previamente
- Se habían usado posicionamientos `absolute` para el bloque de precio, lo que provocó solapamiento con el footer y comportamiento errático en responsivo. En la nueva versión se debe evitar eso.

Contacto
- Si necesitas que además cree el CSS separado y un pequeño mock de datos para ver la vista estática, lo hago en la siguiente pasada.

