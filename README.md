---

## Prompt 5 — Validaciones, citas sin cruces, resultados, menú lateral e imágenes

### Prompt

```text
CONTEXTO DEL PROYECTO
Proyecto Android Studio llamado "SaludPlus": app de agendamiento de citas médicas para pacientes (Clínica SaludPlus). Lenguaje Kotlin, Jetpack Compose, Material 3, Navigation Compose. Paquete base: com.flores.saludplus. La app NO usa base de datos: usuarios, especialidades, médicos y citas viven en colecciones en memoria dentro del object Repositorio (data/repository/Repositorio.kt) y se pierden al cerrar la app. Esto es intencional.

Estado actual (lee estos archivos antes de escribir código):
- util/Fechas.kt: object Fechas con esHabil, proximoHabil, diasHabiles, semana, diaCorto, mesYAnio, textoLargo, textoCorto, textoLargoDesdeIso(iso), textoCortoDesdeIso(iso) y posiblemente rangoHora(hora). La fecha de una cita se guarda como String ISO "yyyy-MM-dd" y la hora como "HH:mm".
- ui/components/Componentes.kt: BotonPrincipal, CampoTexto, CampoFila, BarraSuperior, LogoClinica, TarjetaAccion, BarraInferior, ItemEspecialidad, TarjetaMedico, CampoBusqueda, ChipSeleccion, FilaDetalle, TarjetaCita, PantallaEnConstruccion. ui/components/ImagenPorNombre.kt: ImagenPorNombre, nombreRecurso(texto), ImagenEspecialidad(especialidad, tamano), FotoMedico(nombre, tamano).
- ui/theme/Color.kt con AzulPrimario, AzulClaro, AzulOscuro, TextoPrincipal, TextoSecundario, GrisMarcado, BordeSuave, BordeCampo, Divisor, LineaSeparadora, RojoAviso, VerdeDisponible, VerdeClaro y otros.
- Pantallas con diseño ya terminado: Splash, Registro, Login, Inicio, Especialidades, Médicos, Fecha y hora, Confirmar cita.
- data/repository/Repositorio.kt: agendarCita(medicoId, fecha, hora, motivo): Cita?, citasDelUsuario(), obtenerCita, obtenerMedico, obtenerEspecialidad, horariosDisponibles(medicoId, fecha), cancelarCita(id) y las demás.
- IMÁGENES en app/src/main/res/drawable: logo_saludplus, ilustracion_doctor, imágenes de las 7 especialidades y fotos de médicos (nombreRecurso(nombre del médico).png). No modifiques ni borres los archivos existentes, salvo lo indicado en la Parte G.
- Pruebas existentes: FechasTest, HorariosReservadosTest, NombreRecursoTest (y otras que ya existan en app/src/test).
- IMPORTANTE: es posible que ya existan versiones parciales de este trabajo de un intento anterior (por ejemplo util/Validaciones.kt, data/model/Resultado.kt, pantallas Detalle de cita, Resultados, Notificaciones, Términos, Perfil, Mis citas y Cita agendada ya rediseñadas, fotos PNG de médicos y la implementación de cancelarCita). REVISA cada parte antes de hacerla: si ya existe y cumple lo pedido, consérvala; si existe pero no cumple, ajústala; no la rehagas desde cero, y no vuelvas a descargar ninguna foto que ya exista como PNG en res/drawable.

OBJETIVO
Completar lo que falta para entregar la tarea: (A) reforzar las validaciones de Registro y Login, (B) implementar cancelarCita y evitar que el paciente tenga dos citas a la misma fecha y hora, (C) terminar las 4 pantallas pendientes (Resultados dependiendo de las citas del paciente), (D) mejorar Perfil, Mis citas y Cita agendada con el estilo del diseño, (E) hacer funcionar el NavigationDrawer del Inicio, (F) garantizar que todas las listas se puedan desplazar, (G) agregar las fotos de los médicos que falten. Conserva TODA la funcionalidad existente.

ESCALA DE REFERENCIA
El diseño está tomado sobre una pantalla de 411 x 913 dp. Las medidas están en dp y sp; el diseño usa textos, botones y tarjetas GRANDES (cuerpo ~20sp, títulos ~34-40sp). Respétalas; no las reduzcas.

REGLAS OBLIGATORIAS
- Prohibido usar Room, SQLite o Firebase.
- No cambies nombres ni parámetros de las funciones existentes de Repositorio.kt ni de las pantallas ni de los modelos existentes (las firmas deben quedar idénticas). Modificaciones permitidas en Repositorio.kt: implementar el cuerpo de cancelarCita, modificar el cuerpo de agendarCita para rechazar cruces de horario del paciente y agregar la función NUEVA citaDelUsuarioEn. Nada más.
- No modifiques AppNavigation.kt.
- Los componentes existentes pueden ajustarse sin cambiar los parámetros que ya reciben; los parámetros nuevos llevan valor por defecto.
- Estilo visual: el mismo del resto de la app (fondo blanco, azul marino para títulos, azul AzulPrimario para botones, tarjetas de esquinas redondeadas, barra superior con flecha).
- No ejecutes git commit ni git push.

COMENTARIOS EN EL CÓDIGO (obligatorio, en español, cortos y claros)
- Mantén y actualiza el bloque "// Relaciones:" de cada archivo que modifiques o crees.
- Encima de cada función nueva (incluidos los composables), un comentario que explique qué hace y qué devuelve o dibuja.
- Comenta cada regla de validación indicando qué valida.
- Marca el código nuevo con "// Fase 2:".
- Cada prueba unitaria lleva un comentario que diga qué caso verifica.

PARTE A: VALIDACIONES (muy importantes)
1. util/Validaciones.kt con un object Validaciones de funciones puras de Kotlin (sin clases de Android, para probarlas con JUnit); cada una devuelve el mensaje de error o null si el dato es válido:
   - errorNombre(nombre): se evalúa sin espacios al inicio y al final; al menos nombre y apellido (2 palabras), cada palabra de 2 o más letras; solo letras (incluidas tildes y ñ), espacios y guion; máximo 40 caracteres. Mensajes: "Ingresa tu nombre completo" (vacío o una sola palabra), "El nombre solo puede tener letras" (si tiene números o símbolos).
   - errorTelefono(telefono): exactamente 9 dígitos y debe empezar con 9. Mensaje: "El teléfono debe tener 9 dígitos y empezar con 9".
   - errorCorreo(correo): OPCIONAL (vacío es válido). Si no está vacío debe cumplir texto@dominio.extension (usuario con letras, números, punto, guion o guion bajo; dominio con al menos un punto y extensión de 2 o más letras; sin espacios; máximo 60 caracteres). Mensaje: "Correo no válido". Detecta errores frecuentes de dominio (gamil.com, gmial.com, gmai.com, hotmial.com, hotmal.com, outlok.com) y responde, por ejemplo, "¿Quisiste decir gmail.com?".
   - errorContrasena(contrasena): mínimo 6 caracteres, al menos una letra y un número, sin espacios. Mensaje: "Mínimo 6 caracteres, con letras y números".
   Define las expresiones regulares como constantes con nombre y comenta cada una.
2. RegistroScreen.kt: usa Validaciones para los cuatro campos, conservando el diseño exacto del Registro, la llamada a Repositorio.registrarUsuario y el aviso "Este teléfono ya está registrado". El campo Nombre solo deja escribir letras, espacios y guion (máx. 40); el campo Teléfono solo deja escribir dígitos (máx. 9). El error de cada campo aparece cuando el usuario ya escribió algo en ese campo o cuando pulsó "Registrarme". "Registrarme" no registra mientras exista algún error. Al registrar, guarda nombre y correo sin espacios sobrantes.
3. LoginScreen.kt: valida con errorTelefono y que la contraseña no esté vacía ("Ingresa tu contraseña"); si los datos son válidos pero no coinciden con ningún usuario, muestra "Teléfono o contraseña incorrectos". Conserva el diseño y la llamada a Repositorio.iniciarSesion.
4. app/src/test/java/com/flores/saludplus/ValidacionesTest.kt (JUnit 4): "Juan Pérez" y "María José Quispe" válidos; "Yoseli8", "Juan", "J P" y "" no; "987654321" válido; "887654321", "98765432" y "98765432a" no; "" y "juan@correo.com" válidos como correo; "juan@", "juan@correo", "juan correo.com" y "yoselin@gamil.com" (con el aviso de dominio) no; "abc123" válida como contraseña; "abcdef", "123456", "ab1" y "abc 123" no.

PARTE B: REPOSITORIO Y CITAS SIN CRUCES
1. cancelarCita(id: Int): Boolean con removeIf sobre la lista de citas: true si se eliminó una cita y false si no existía. Sin comentario TODO.
2. Agrega la función NUEVA citaDelUsuarioEn(fecha: String, hora: String): Cita? que devuelve, usando citasDelUsuario(), la cita del usuario en sesión que tenga exactamente esa fecha y hora (de cualquier médico), o null si no hay ninguna.
3. Modifica el cuerpo de agendarCita: además de las comprobaciones actuales (hay sesión y el horario del médico sigue libre), devuelve null si el paciente ya tiene una cita en esa misma fecha y hora con cualquier médico (usa citaDelUsuarioEn). Conserva la firma.
4. FechaHoraScreen.kt: para cada hora de la cuadrícula comprueba con Repositorio.citaDelUsuarioEn(fecha seleccionada, hora) si el paciente ya tiene una cita a esa hora. Esas horas se dibujan atenuadas (texto gris y fondo más claro; agrega a ChipSeleccion el parámetro opcional atenuado = false). Si el usuario toca una hora atenuada, NO se selecciona y aparece en rojo (16sp) bajo la cuadrícula el mensaje "Ya tienes una cita el {Fechas.textoLargoDesdeIso(fecha)} a las {hora} con {nombre del médico de esa cita}. Elige otro horario." El mensaje se borra al elegir una hora normal o al cambiar de día o de semana. Reserva el espacio del mensaje para que la cuadrícula no cambie de tamaño al aparecer. El tamaño de los botones, el calendario dinámico y el bloqueo de horarios reservados no cambian.
5. ConfirmarCitaScreen.kt: antes de llamar a agendarCita, comprueba con citaDelUsuarioEn si hay cruce; si lo hay, no agenda y muestra en rojo el mismo mensaje "Ya tienes una cita el ... a las ... con ...". Conserva el aviso de horario ya reservado por otro paciente.
6. Pruebas (en HorariosReservadosTest.kt o archivos nuevos CancelarCitaTest.kt y CrucesDeCitasTest.kt): tras agendar y cancelar una cita, la hora vuelve a aparecer en horariosDisponibles; cancelar un id inexistente devuelve false; con una cita del paciente el 2026-10-09 a las "08:30" con el médico 9, intentar agendar el 2026-10-09 a las "08:30" con el médico 10 devuelve null y citaDelUsuarioEn("2026-10-09", "08:30") devuelve la primera cita; a las "09:00" del mismo día SÍ se puede agendar.

PARTE C: PANTALLAS PENDIENTES (sin PantallaEnConstruccion; conserva las firmas)
1. DetalleCitaScreen(citaId: Int, onBack: () -> Unit): BarraSuperior "Detalle de cita". Carga la cita con Repositorio.obtenerCita y el médico con obtenerMedico; si la cita no existe muestra "Esta cita ya no existe" y un botón "Volver". Tarjeta del médico (fondo azul claro, esquinas 20dp, padding 16dp): FotoMedico de 84dp, nombre 20sp negrita, especialidad 18sp gris. Filas FilaDetalle (título 18sp, valor 22sp) con una línea separadora de 1.5dp (LineaSeparadora) después de cada una: Fecha (Fechas.textoLargoDesdeIso), Hora (rango, por ejemplo "09:30 a 10:00"), Tipo de atención "Consulta presencial", Dirección "Av. Los Olivos 123, Lima", Motivo de consulta (o "Sin motivo" si está vacío). Botón "Cancelar cita": ancho completo, 64dp, esquinas 16dp, borde rojo 1.5dp, texto rojo 22sp SemiBold. Abre un AlertDialog "¿Cancelar esta cita?" con "Sí, cancelar" y "No"; si confirma, llama a Repositorio.cancelarCita(citaId) y luego a onBack().
2. ResultadosScreen(onNavegar): los resultados DEPENDEN de las citas del paciente y se muestran en orden según con quién tuvo la cita.
   - Ajusta el modelo propio data/model/Resultado.kt (Resultado con id, titulo, especialidadId, fecha ISO y estado) y define una lista FIJA de exámenes por especialidad, de 2 exámenes cada una: Medicina General (Hemograma completo, Examen de orina), Pediatría (Control de crecimiento, Tamizaje de hemoglobina), Ginecología (Papanicolaou, Ecografía pélvica), Cardiología (Electrocardiograma, Perfil lipídico), Dermatología (Prueba de alergias, Biopsia de piel), Traumatología (Radiografía de columna, Resonancia de rodilla), Oftalmología (Agudeza visual, Fondo de ojo).
   - Crea una función pura resultadosDeCita(especialidadId: Int, fechaIso: String, hoy: LocalDate): List<Resultado> que devuelve los exámenes de esa especialidad con estado "Disponible" si la fecha de la cita es anterior a hoy, "En proceso" si es hoy y "Pendiente" si es posterior; devuelve lista vacía si la especialidad no existe. Pruébala en ResultadosTest.kt (Ginecología con fecha pasada, de hoy y futura; especialidad inexistente).
   - La pantalla usa Repositorio.citasDelUsuario() (ordenadas de la más reciente a la más antigua) y, para cada cita, dibuja un encabezado con el nombre de la especialidad y del médico ("Ginecología · Dra. Ana Torres", 20sp negrita azul marino) y la fecha de la cita (Fechas.textoCortoDesdeIso, 17sp gris), seguido de las tarjetas de sus exámenes (fondo blanco, borde 1dp BordeSuave, esquinas 18dp, padding 16dp): ícono de documento en círculo azul claro de 56dp, título 20sp negrita y una etiqueta de estado (verde claro "Disponible", naranja claro "En proceso", gris claro "Pendiente", 15sp SemiBold). Título de pantalla "Resultados" 34sp negrita azul marino a la izquierda y BarraInferior con Rutas.RESULTADOS.
   - Si el paciente no tiene citas, muestra centrado "Aún no tienes resultados" y debajo "Tus resultados aparecerán después de tus citas".
3. NotificacionesScreen(onBack): BarraSuperior "Notificaciones". Usa map sobre Repositorio.citasDelUsuario() para construir los mensajes, por ejemplo "Tienes una cita con Dra. Ana Torres el Mar 6 oct 2026 a las 09:30". LazyColumn de tarjetas con ícono de campana en círculo azul claro y el texto de 18sp. Si no hay citas, muestra centrado "No tienes notificaciones".
4. TerminosScreen(onBack): BarraSuperior "Términos y condiciones". Contenido desplazable (verticalScroll) con 6 secciones de título 20sp negrita azul marino y texto 18sp: Uso de la aplicación, Datos personales, Citas médicas, Cancelaciones y reprogramaciones, Responsabilidad, Contacto (textos breves y coherentes con una clínica, sin datos reales de contacto). Botón fijo abajo "Entendido" (BotonPrincipal de 68dp) que llama a onBack.

PARTE D: PERFIL, MIS CITAS Y CITA AGENDADA
1. PerfilScreen(onNavegar, onCerrarSesion): fondo blanco, margen horizontal 20dp, desplazable, con BarraInferior (Rutas.PERFIL). Título "Mis datos" 34sp negrita azul marino a la izquierda. Avatar circular de 120dp, fondo AzulClaro, con las iniciales del usuario (primera letra del nombre y del apellido, mayúsculas) en 44sp negrita AzulPrimario; debajo, nombre completo 28sp negrita azul marino centrado y teléfono 20sp gris. Tarjeta de datos (fondo blanco, borde 1dp BordeSuave, esquinas 20dp, padding 16dp) con tres FilaDetalle (título 18sp, valor 22sp): Teléfono, Correo ("No registrado" si está vacío) y Citas agendadas (cantidad), separadas por líneas de 1dp (Divisor). Botón "Cerrar sesión": ancho completo, 64dp, esquinas 16dp, borde rojo 1.5dp, texto rojo 22sp SemiBold con ícono de salida a la izquierda; llama a Repositorio.cerrarSesion() y luego onCerrarSesion().
2. MisCitasScreen(onDetalle, onNavegar): título "Mis citas" 34sp negrita azul marino a la izquierda y BarraInferior. Ajusta TarjetaCita (sin cambiar su firma; parámetros nuevos opcionales) para que cada cita sea una tarjeta blanca de esquinas 18dp, borde 1dp BordeSuave y padding 16dp: FotoMedico de 72dp a la izquierda; a su derecha nombre del médico (20sp negrita), especialidad (17sp gris) y una fila con ícono de calendario de 22dp + fecha (Fechas.textoCortoDesdeIso, 18sp) e ícono de reloj + hora (18sp); chevron ">" a la derecha. Si la fecha y la hora no caben en una fila, ponlas en dos filas, nunca recortadas. Estado vacío centrado y desplazable: círculo AzulClaro de 140dp con ícono de calendario de 72dp, "Aún no tienes citas agendadas" 22sp negrita, "Agenda una desde el Inicio" 18sp gris y un botón "Agendar cita" de 64dp que navega a Rutas.ESPECIALIDADES con onNavegar.
3. CitaExitosaScreen(citaId, onVerMisCitas, onIrInicio): centrada y desplazable. Círculo VerdeClaro de 120dp con ícono de check de 88dp en VerdeDisponible; "¡Cita agendada!" 34sp negrita azul marino; "Te esperamos en la clínica" 20sp gris. Tarjeta de resumen (borde 1dp BordeSuave, esquinas 20dp, padding 16dp): arriba FotoMedico de 72dp con nombre (22sp negrita) y especialidad (18sp gris); debajo FilaDetalle de Fecha (Fechas.textoLargoDesdeIso), Hora (rango) y Dirección, separadas por líneas de 1dp. Botones: "Ver mis citas" (BotonPrincipal de 68dp) y "Ir al inicio" (22sp SemiBold azul).
4. Para que el rango de hora sea igual en Confirmar cita, Cita agendada y Detalle de cita, usa Fechas.rangoHora(hora: String): String (por ejemplo "09:30" -> "09:30 a 10:00"; si la hora es inválida devuelve el mismo texto sin lanzar excepción). Si no existe, créala en util/Fechas.kt y agrega sus pruebas en FechasTest.kt.

PARTE E: NAVIGATIONDRAWER FUNCIONAL EN EL INICIO (HomeScreen.kt)
El ícono de menú (tres rayas) del Inicio hoy es solo decorativo; debe abrir un menú lateral que funcione.
- Envuelve el Scaffold del Inicio en un ModalNavigationDrawer con drawerState = rememberDrawerState(DrawerValue.Closed) y un CoroutineScope para abrir y cerrar. El ícono de menú (30dp, con zona táctil de 48dp) lo abre. El botón Atrás del sistema cierra el drawer si está abierto (BackHandler).
- Contenido (ModalDrawerSheet de fondo blanco, ancho 320dp): ENCABEZADO de 170dp con fondo AzulClaro, avatar circular de 72dp con las iniciales del usuario, nombre completo (22sp negrita azul marino) y teléfono (16sp gris), tomados de Repositorio.usuarioActual. Debajo, ítems (NavigationDrawerItem de 56dp de alto, íconos de 28dp, texto de 20sp): Inicio (ítem ACTIVO), Agendar cita (onAgendar), Mis citas (onNavegar Rutas.MIS_CITAS), Resultados (Rutas.RESULTADOS), Mis datos (Rutas.PERFIL), una línea divisoria, Notificaciones (onNotificaciones) y Términos y condiciones (onNavegar Rutas.TERMINOS).
- El ítem activo se resalta con fondo AzulClaro, texto en AzulPrimario y negrita; los demás en gris oscuro, sin fondo. Al tocar un ítem se cierra el drawer y luego se navega. Tocar "Inicio" solo cierra el drawer.
- No cambies la firma de HomeScreen ni AppNavigation.kt (por eso el drawer no incluye "Cerrar sesión": esa opción sigue en Mis datos).

PARTE F: TODAS LAS LISTAS SE PUEDEN DESPLAZAR
En Mis citas, Resultados, Notificaciones, Médicos, Especialidades y cualquier otra lista, la cantidad de elementos no debe tener límite: cada LazyColumn debe ocupar el espacio libre de la pantalla (Modifier.weight(1f) o fillMaxSize dentro de un Column con altura acotada) y llevar contentPadding(bottom = 24.dp) para que el último elemento no quede tapado por la BarraInferior. Las pantallas con contenido fijo (Perfil, Detalle de cita, Cita agendada, Términos, estados vacíos) deben usar verticalScroll. Verifica en el código que ninguna lista o pantalla recorte su contenido cuando hay más de 10 elementos.

PARTE G: FOTOS DE LOS MÉDICOS (descarga desde internet, solo las que falten)
Cada médico debe tener foto en res/drawable con el nombre nombreRecurso(nombre del médico).png. Los 12 médicos son:
- Mujeres (Dra.): dra_ana_torres, dra_claudia_rojas, dra_mariana_soto, dra_lucia_vargas, dra_sofia_paredes, dra_patricia_leon, dra_elena_campos.
- Hombres (Dr.): dr_luis_ramirez, dr_carlos_mendoza, dr_jorge_salas, dr_ricardo_nunez, dr_andres_quispe.
Revisa cuáles ya tienen PNG: no los toques. Para los que aún tengan solo placeholder .xml:
1. Descarga solo desde bancos de imágenes con licencia gratuita para uso comercial y sin atribución obligatoria (Pexels, Unsplash o Pixabay). No descargues desde otros sitios ni ejecutes nada de lo descargado; verifica que cada archivo sea una imagen real.
2. Retrato amable de un médico o profesional de la salud, rostro bien visible, GÉNERO CORRECTO según el tratamiento (Dra. = mujer, Dr. = hombre), sin marcas de agua ni texto, y distinta a las demás. Mira cada imagen antes de usarla.
3. Recorte cuadrado centrado en el rostro, 256x256 px, máscara circular con fondo transparente (Python con Pillow; instálalo con pip si falta), PNG con el nombre exacto. Elimina el placeholder .xml del mismo nombre para evitar "Duplicate resources".
4. Actualiza IMAGENES.md (tabla de los 12 médicos y sección "Créditos" con archivo, URL de origen, fotógrafo y licencia de cada foto descargada).
5. Si no puedes descargar alguna, deja su placeholder y dime exactamente cuáles quedaron pendientes.

VERIFICACIÓN
Ejecuta ./gradlew testDebugUnitTest y ./gradlew assembleDebug (en Windows: gradlew.bat). Todas las pruebas (FechasTest, HorariosReservadosTest, NombreRecursoTest, ValidacionesTest, ResultadosTest y las de cancelar cita y cruces de citas) deben pasar y el proyecto debe compilar. Busca "TODO" y "PantallaEnConstruccion(" en app/src/main/java: ninguna pantalla debe seguir usándola y Repositorio.kt no debe tener ningún TODO. Repasa cada medida de este prompt contra tu código.

CRITERIOS DE ACEPTACIÓN
- Validaciones: nombre con nombre y apellido solo con letras; teléfono de 9 dígitos que empieza con 9; correo opcional con formato válido y aviso de dominio mal escrito; contraseña de mínimo 6 caracteres con letras y números. ValidacionesTest pasa.
- cancelarCita implementado; al cancelar, la hora vuelve a estar disponible.
- El paciente no puede tener dos citas a la misma fecha y hora con médicos distintos: en Fecha y hora la hora aparece atenuada y al tocarla se muestra el mensaje "Ya tienes una cita el ... a las ... con ..."; Confirmar cita también lo comprueba; agendarCita devuelve null en ese caso; las pruebas pasan.
- Resultados dependen de las citas: muestran, por cada cita (de la más reciente a la más antigua), un encabezado con especialidad y médico y los exámenes de esa especialidad con su estado según la fecha; sin citas, mensaje vacío.
- Detalle de cita con AlertDialog de confirmación; Notificaciones con map sobre las citas; Términos con scroll y botón "Entendido".
- Perfil, Mis citas y Cita agendada con las medidas indicadas.
- El ícono de menú del Inicio abre un NavigationDrawer con encabezado de usuario, íconos e ítem activo resaltado; el botón Atrás lo cierra; sus ítems navegan.
- Todas las listas se desplazan con cualquier cantidad de elementos y el último no queda tapado por la barra inferior.
- Los 12 médicos tienen foto con el género correcto y créditos en IMAGENES.md; no hay recursos duplicados.
- Ninguna pantalla usa PantallaEnConstruccion; ninguna firma existente cambió; AppNavigation.kt no se modificó; en Repositorio.kt solo cambiaron cancelarCita, el cuerpo de agendarCita y la función nueva citaDelUsuarioEn.
- Todas las pruebas pasan y el proyecto compila.

Al terminar, resume qué archivos creaste, modificaste o eliminaste, el resultado de las pruebas y las fuentes de las fotos.
```

### Respuesta resumida

El agente reforzó las validaciones con funciones puras en `util/Validaciones.kt` (nombre y apellido solo con letras, teléfono de 9 dígitos que empieza con 9, correo opcional con formato válido y aviso de dominios mal escritos, contraseña de mínimo 6 caracteres con letras y números) y las aplicó en Registro y Login. Implementó `cancelarCita` con `removeIf`, agregó `citaDelUsuarioEn` y modificó `agendarCita` para que rechace dos citas del mismo paciente a la misma fecha y hora; en Fecha y hora esas horas se muestran atenuadas y, al tocarlas, aparece el aviso "Ya tienes una cita el ... a las ... con ...", y Confirmar cita también lo comprueba. Los resultados se generan con `resultadosDeCita` (en `util/Examenes.kt`) a partir de las citas del paciente, agrupados por especialidad y médico, con estado según la fecha. Completó Detalle de cita (con `AlertDialog` para cancelar), Notificaciones (con `map` sobre las citas) y Términos (con scroll), y rediseñó Perfil, Mis citas y Cita agendada. El ícono de menú del Inicio abre un `ModalNavigationDrawer` con encabezado de usuario, íconos e ítem activo resaltado. Las listas se desplazan con cualquier cantidad de elementos. Descargó fotos con licencia libre para los médicos que faltaban (los 12 médicos quedaron con foto PNG) y registró los créditos en `IMAGENES.md`. Creó las pruebas `ValidacionesTest`, `CancelarCitaTest`, `CrucesDeCitasTest` y `ResultadosTest`.

### Qué tuve que corregir

El prompt se ejecutó en dos vueltas. El primer resultado tenía estos problemas y el prompt se corrigió:

1. **Dos citas a la misma hora.** La app dejaba agendar dos citas a la misma fecha y hora con médicos distintos (en Mis citas aparecían el Dr. Ricardo Núñez y la Dra. Elena Campos el viernes 9 de octubre a las 08:30). Se agregó la regla de no permitir cruces: la hora aparece atenuada, se avisa con quién ya hay cita, y `agendarCita` devuelve `null` en ese caso.
2. **Resultados independientes de las citas.** Eran una lista fija igual para todos. Se corrigió para que dependan de las citas del paciente, agrupados según la especialidad y el médico, con el estado según la fecha de la cita.
3. **Menú del Inicio solo decorativo.** El ícono de tres rayas no hacía nada, porque el primer prompt lo pedía "solo decorativo". Se pidió un `NavigationDrawer` funcional con encabezado de usuario, íconos e ítem activo resaltado.
4. **Listas con desplazamiento.** Se pidió que todas las listas se pudieran desplazar con cualquier cantidad de elementos y que el último no quedara tapado por la barra inferior.
5. **Validaciones débiles.** El perfil mostraba el nombre "Yoseli8" y el correo "yoselin@gamil.com", y el teléfono no exigía empezar con 9. Se reforzaron las reglas y se agregaron pruebas.
6. **Pendientes de la entrega.** `cancelarCita` seguía con TODO y cuatro pantallas (Términos, Detalle de cita, Resultados y Notificaciones) seguían "en construcción"; el prompt exigió completarlas.
