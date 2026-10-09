# PROMPTS.md — Fase 2: mejora con IA (rama `saludplus-IA`)

Proyecto: **Clínica SaludPlus — App Paciente** (Kotlin + Jetpack Compose + Material 3).
Agente de IA usado: **Claude Code**.

Este archivo documenta los **4 prompts** usados en la Fase 2. Para cada uno se indica el prompt completo, un resumen de la respuesta y lo que hubo que corregir. Los prompts se ejecutan en este orden y cada uno corresponde a un commit.

| # | Prompt | Qué se pidió |
|---|--------|--------------|
| 1 | Utilidad de fechas | Crear `util/Fechas.kt` con `LocalDate` (días hábiles, semanas, textos en español) y subir `minSdk` a 26 |
| 2 | Calendario dinámico | Reemplazar los días fijos de Fecha y hora por un calendario generado, con flechas por semana, mes y año dinámicos y horarios que se recalculan |
| 3 | Fecha en español | Mostrar la fecha en texto en español en Confirmar cita, Cita agendada y Mis citas |
| 4 | Fidelidad visual | Dejar Splash, Registro, Inicio, Especialidades, Médicos, Fecha y hora y Confirmar idénticas al diseño de referencia, y adaptar el Login |

---

## Prompt 1 — Utilidad de fechas con `LocalDate`

### Prompt

```text
CONTEXTO DEL PROYECTO
Proyecto Android Studio llamado "SaludPlus": app de agendamiento de citas médicas para pacientes (Clínica SaludPlus). Lenguaje Kotlin, Jetpack Compose, Material 3, Navigation Compose. Paquete base: com.flores.saludplus. La app NO usa base de datos: usuarios, especialidades, médicos y citas viven en colecciones en memoria dentro del object Repositorio (data/repository/Repositorio.kt) y se pierden al cerrar la app. Esto es intencional.

Estructura actual del código (app/src/main/java/com/flores/saludplus):
- MainActivity.kt
- data/model: Usuario.kt, Especialidad.kt, Medico.kt, Cita.kt (la Cita guarda la fecha como String ISO "2026-10-06" y la hora como String "09:30")
- data/repository/Repositorio.kt (object con colecciones y funciones como horariosDisponibles(medicoId, fecha), agendarCita, citasDelUsuario)
- navigation: Rutas.kt, AppNavigation.kt
- ui/theme, ui/components/Componentes.kt
- ui/screens: auth, home, agendamiento (FechaHoraScreen, ConfirmarCitaScreen, CitaExitosaScreen...), citas, perfil, resultados, notificaciones

Estado actual de la pantalla de Fecha y hora (FechaHoraScreen.kt): muestra una lista FIJA de 5 días (Lun 15 a Vie 19 de "Setiembre 2026") escritos a mano. En esta fase se reemplazará por un calendario dinámico con java.time.LocalDate. Este primer paso solo crea la utilidad de fechas que ese calendario usará; todavía NO se modifica ninguna pantalla.

REGLAS OBLIGATORIAS
- Prohibido usar Room, SQLite o Firebase.
- No cambies nombres ni parámetros de las funciones de Repositorio.kt ni de las pantallas.
- No modifiques AppNavigation.kt.
- Lee primero el proyecto para respetar su estilo antes de escribir.
- No ejecutes git commit ni git push.

COMENTARIOS EN EL CÓDIGO (obligatorio, en español, cortos y claros)
- Después de los imports de cada archivo nuevo, un bloque "// Relaciones:" con viñetas que indiquen quién usa ese archivo y de qué depende.
- Encima de CADA función, un comentario de una o dos líneas que explique qué hace y qué devuelve.
- Dentro de las funciones, un comentario breve en cada paso que no sea evidente (por ejemplo, el bucle que salta fines de semana).
- Marca el código nuevo de esta fase con "// Fase 2:".
- Cada prueba unitaria lleva un comentario que diga qué caso verifica.

TAREA
1. Crea app/src/main/java/com/flores/saludplus/util/Fechas.kt con un object Fechas que use java.time.LocalDate y tenga estas funciones:
   - esHabil(fecha: LocalDate): Boolean -> true de lunes a viernes.
   - proximoHabil(desde: LocalDate): LocalDate -> si la fecha cae en sábado o domingo avanza hasta el lunes; si ya es hábil la devuelve igual.
   - diasHabiles(desde: LocalDate, cantidad: Int = 5): List<LocalDate> -> los siguientes días hábiles empezando en proximoHabil(desde), saltando sábados y domingos.
   - semana(hoy: LocalDate, desplazamiento: Int): List<LocalDate> -> desplazamiento 0 = los 5 días hábiles desde hoy; desplazamiento 1 = lo mismo desplazado 7 días, y así sucesivamente. La primera fecha de cada semana siempre es un día hábil.
   - diaCorto(fecha): String -> "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom".
   - mesYAnio(fecha): String -> ejemplo "Octubre 2026" (mes con mayúscula inicial y año). Usa "Setiembre", no "Septiembre".
   - textoLargo(fecha): String -> ejemplo "Miércoles 16 de setiembre 2026" (sin "de" antes del año).
   - textoCorto(fecha): String -> ejemplo "Mié 16 set 2026".
   Usa listas propias de nombres de días y meses en español (meses en minúscula, "setiembre" con "t"); no dependas del Locale del dispositivo.

2. java.time.LocalDate requiere API 26. En app/build.gradle.kts cambia minSdk de 24 a 26 y agrega un comentario corto que explique el motivo.

3. Crea app/src/test/java/com/flores/saludplus/FechasTest.kt con pruebas unitarias (JUnit 4) usando estas fechas exactas:
   - 2026-10-06 es martes: diasHabiles(esa fecha) devuelve los días del mes 6, 7, 8, 9 y 12 (martes a viernes y el lunes siguiente).
   - 2026-10-10 es sábado: diasHabiles(esa fecha) empieza el 2026-10-12.
   - Para desplazamientos del 0 al 8, semana(2026-10-06, n) nunca contiene sábados ni domingos.
   - semana(2026-10-06, 1).first() es exactamente 7 días después de semana(2026-10-06, 0).first().
   - mesYAnio de la primera fecha de semana(2026-10-06, 0) es "Octubre 2026" y la de semana(2026-10-06, 4) es "Noviembre 2026".
   - Para 2026-09-16 (que cae miércoles): textoLargo = "Miércoles 16 de setiembre 2026", textoCorto = "Mié 16 set 2026", mesYAnio = "Setiembre 2026".

4. Ejecuta ./gradlew testDebugUnitTest y ./gradlew assembleDebug (en Windows: gradlew.bat). Corrige cualquier fallo hasta que las pruebas pasen y el proyecto compile.

CRITERIOS DE ACEPTACIÓN
- Existe util/Fechas.kt con las 8 funciones indicadas, usando LocalDate y con un comentario encima de cada una.
- minSdk quedó en 26 con comentario.
- Las pruebas de FechasTest.kt pasan y el proyecto compila.
- No se modificó ninguna pantalla, ni Repositorio.kt, ni AppNavigation.kt.

Al terminar, resume qué archivos creaste o modificaste y el resultado de las pruebas.
```

### Respuesta resumida

El agente creó `util/Fechas.kt` con las 8 funciones pedidas (`esHabil`, `proximoHabil`, `diasHabiles`, `semana`, `diaCorto`, `mesYAnio`, `textoLargo`, `textoCorto`), cada una con su comentario en español y el bloque "Relaciones". Subió `minSdk` de 24 a 26 con un comentario y creó `FechasTest.kt` con 6 pruebas unitarias que usan las fechas exactas indicadas. Las pruebas pasaron y el proyecto compiló. No se tocó ninguna pantalla.

### Qué tuve que corregir

No hubo correcciones al código de este prompt: el resultado cumplió los criterios de aceptación. Verifiqué que escribiera "setiembre" con "t", que `minSdk` quedara en 26 y que las pruebas pasaran.

---

## Prompt 2 — Calendario dinámico en Fecha y hora

### Prompt

```text
CONTEXTO DEL PROYECTO
Proyecto Android Studio llamado "SaludPlus": app de agendamiento de citas médicas para pacientes (Clínica SaludPlus). Lenguaje Kotlin, Jetpack Compose, Material 3, Navigation Compose. Paquete base: com.flores.saludplus. La app NO usa base de datos: usuarios, especialidades, médicos y citas viven en colecciones en memoria dentro del object Repositorio (data/repository/Repositorio.kt) y se pierden al cerrar la app. Esto es intencional.

Estado actual relevante (lee estos archivos antes de escribir código):
- util/Fechas.kt (ya existe, creado en el paso anterior): object Fechas con esHabil, proximoHabil, diasHabiles(desde, cantidad = 5), semana(hoy, desplazamiento), diaCorto(fecha), mesYAnio(fecha), textoLargo(fecha), textoCorto(fecha). Usa java.time.LocalDate (minSdk ya es 26).
- ui/screens/agendamiento/FechaHoraScreen.kt: firma FechaHoraScreen(medicoId: Int, onContinuar: (String, String) -> Unit, onBack: () -> Unit). Hoy muestra una lista FIJA de 5 días (Lun 15 a Vie 19 de "Setiembre 2026") escritos a mano, con las flechas < y > desactivadas. Al elegir un día usa Repositorio.horariosDisponibles(medicoId, fecha) y muestra las horas en un LazyVerticalGrid de 3 columnas con el componente ChipSeleccion (ui/components/Componentes.kt). El botón "Continuar" solo se habilita con día y hora elegidos y llama a onContinuar(fecha, hora).
- data/repository/Repositorio.kt: horariosDisponibles(medicoId: Int, fecha: String): List<String> quita de horariosBase las horas ya reservadas por ese médico en esa fecha (fecha como String ISO "2026-10-06"). agendarCita(medicoId, fecha, hora, motivo): Cita? guarda la cita si el horario sigue libre y hay un usuario en sesión (Repositorio.usuarioActual).
- La fecha viaja entre pantallas como String ISO "yyyy-MM-dd" y la hora como "HH:mm". Eso NO debe cambiar.

REGLAS OBLIGATORIAS
- Prohibido usar Room, SQLite o Firebase.
- No cambies nombres ni parámetros de las funciones de Repositorio.kt ni de las pantallas (la firma de FechaHoraScreen debe quedar idéntica).
- No modifiques AppNavigation.kt ni Repositorio.kt.
- Mantén el diseño visual actual de la pantalla (colores, tarjeta del médico, chips, botón azul); solo cambia de dónde salen los días y cómo se comportan las flechas.
- No ejecutes git commit ni git push.

COMENTARIOS EN EL CÓDIGO (obligatorio, en español, cortos y claros)
- Actualiza el bloque "// Relaciones:" de FechaHoraScreen.kt para mencionar que ahora usa util/Fechas.kt.
- Comenta cada estado (remember/rememberSaveable) y cada bloque de lógica nuevo explicando qué hace.
- Marca el código nuevo con "// Fase 2:".
- Cada prueba unitaria lleva un comentario que diga qué caso verifica.

TAREA
Convierte la pantalla de Fecha y hora en un calendario dinámico con java.time.LocalDate, usando las funciones de util/Fechas.kt:

1. Días: elimina la lista fija de días. Muestra los 5 días hábiles de la semana mostrada con Fechas.semana(LocalDate.now(), desplazamientoSemana), sin sábados, domingos ni días pasados. Cada chip muestra Fechas.diaCorto(fecha) y el número del día.
2. Estado: guarda en rememberSaveable el desplazamiento de semana (Int, empieza en 0), la fecha seleccionada (LocalDate o su String ISO) y la hora seleccionada (String?).
3. Flechas: la flecha ">" suma 1 al desplazamiento (avanza una semana) y la flecha "<" resta 1 (retrocede una semana). La flecha "<" debe estar deshabilitada cuando el desplazamiento es 0: no se puede retroceder antes de la semana actual. Ambas flechas deben estar habilitadas/deshabilitadas visualmente según corresponda.
4. Mes y año dinámicos: el texto entre las flechas es Fechas.mesYAnio(primer día de la semana mostrada) y cambia al avanzar o retroceder semanas (por ejemplo "Octubre 2026" y luego "Noviembre 2026").
5. Al cambiar de semana, selecciona automáticamente el primer día de la nueva semana y reinicia la hora seleccionada.
6. Al cambiar de día, los horarios disponibles se recalculan solos llamando a Repositorio.horariosDisponibles(medicoId, fecha.toString()) y la hora seleccionada se reinicia (queda en null).
7. El LazyVerticalGrid de horarios sigue mostrando solo las horas disponibles. Si no hay ninguna, muestra el mensaje "No hay horarios disponibles este día".
8. El botón "Continuar" solo se habilita con día y hora elegidos y llama a onContinuar(fecha.toString(), hora) con la fecha en formato ISO "yyyy-MM-dd".
9. IMPORTANTE: el calendario dinámico no debe romper el bloqueo de horarios ya reservados. Una hora que ya fue agendada para ese médico en esa fecha NO debe aparecer, aunque el usuario cambie de día o de semana y luego vuelva. No dupliques esa lógica: reutiliza Repositorio.horariosDisponibles tal como está.

PRUEBAS
Crea app/src/test/java/com/flores/saludplus/HorariosReservadosTest.kt (JUnit 4) que verifique que el bloqueo sigue funcionando:
- Antes de cada prueba limpia Repositorio.citas y deja Repositorio.usuarioActual con un Usuario de prueba (revisa data/model/Usuario.kt para construirlo).
- Después de agendar con Repositorio.agendarCita(medicoId = 1, fecha = "2026-10-06", hora = "09:30", motivo = ""), la hora "09:30" ya no está en horariosDisponibles(1, "2026-10-06").
- Esa misma hora SÍ sigue disponible para el médico 2 en esa fecha y para el médico 1 en otra fecha.
- Intentar agendar de nuevo el mismo médico, fecha y hora devuelve null.

VERIFICACIÓN
Ejecuta ./gradlew testDebugUnitTest y ./gradlew assembleDebug (en Windows: gradlew.bat). Corrige cualquier fallo hasta que las pruebas pasen y el proyecto compile.

CRITERIOS DE ACEPTACIÓN
- No queda ninguna lista fija de días en FechaHoraScreen.kt.
- Se ven los próximos 5 días hábiles desde hoy, generados con LocalDate.
- La flecha "<" está deshabilitada en la semana actual y ">" avanza una semana.
- El mes y año del encabezado cambian según la semana mostrada.
- Al cambiar de día se recalculan los horarios y la hora se reinicia.
- "Continuar" solo se habilita con día y hora elegidos.
- Los horarios reservados siguen sin aparecer (prueba pasando).
- La firma de FechaHoraScreen, Repositorio.kt y AppNavigation.kt no cambiaron.

Al terminar, resume qué archivos modificaste o creaste y el resultado de las pruebas.
```

### Respuesta resumida

El agente reemplazó la lista fija de días de `FechaHoraScreen` por un calendario generado con `Fechas.semana(LocalDate.now(), desplazamiento)`. Guardó en `rememberSaveable` el desplazamiento de semana, la fecha y la hora elegidas. La flecha `<` quedó deshabilitada en la semana actual y `>` avanza una semana. El encabezado muestra el mes y año de la primera fecha de la semana. Al cambiar de semana se selecciona el primer día, y al cambiar de día se recalculan los horarios con `Repositorio.horariosDisponibles` y se reinicia la hora. "Continuar" solo se habilita con día y hora elegidos. Creó `HorariosReservadosTest.kt` para confirmar que el bloqueo de horarios reservados no se rompió. Las pruebas pasaron.

### Qué tuve que corregir

La lógica del calendario funcionó desde el primer intento: en el emulador se vieron los 5 días hábiles desde el martes 6 de octubre de 2026, la flecha `<` deshabilitada en la primera semana y el cambio de semana. Lo que hubo que ajustar después fue el **aspecto visual** de esta pantalla (días y horas con el color y el tamaño del diseño), que se corrigió en el Prompt 4.

---

## Prompt 3 — Fecha en español

### Prompt

```text
CONTEXTO DEL PROYECTO
Proyecto Android Studio llamado "SaludPlus": app de agendamiento de citas médicas para pacientes (Clínica SaludPlus). Lenguaje Kotlin, Jetpack Compose, Material 3, Navigation Compose. Paquete base: com.flores.saludplus. La app NO usa base de datos: usuarios, especialidades, médicos y citas viven en colecciones en memoria dentro del object Repositorio (data/repository/Repositorio.kt) y se pierden al cerrar la app. Esto es intencional.

Estado actual relevante (lee estos archivos antes de escribir código):
- util/Fechas.kt: object Fechas con esHabil, proximoHabil, diasHabiles, semana, diaCorto(fecha), mesYAnio(fecha), textoLargo(fecha) -> ejemplo "Miércoles 16 de setiembre 2026", textoCorto(fecha) -> ejemplo "Mié 16 set 2026". Todas reciben java.time.LocalDate. Ya tiene pruebas en app/src/test/java/com/flores/saludplus/FechasTest.kt.
- La fecha de una cita viaja y se guarda como String ISO "yyyy-MM-dd" (por ejemplo "2026-10-06") y la hora como "HH:mm". Eso NO debe cambiar: el Repositorio y la navegación siguen usando ese formato.
- ui/screens/agendamiento/ConfirmarCitaScreen.kt: firma ConfirmarCitaScreen(medicoId: Int, fecha: String, hora: String, onCitaAgendada: (Int) -> Unit, onBack: () -> Unit). Hoy muestra la fecha cruda en una FilaDetalle con título "Fecha", por ejemplo "2026-10-06".
- ui/screens/agendamiento/CitaExitosaScreen.kt: firma CitaExitosaScreen(citaId: Int, onVerMisCitas: () -> Unit, onIrInicio: () -> Unit). Muestra cita.fecha cruda en el resumen.
- ui/components/Componentes.kt: TarjetaCita(cita: Cita, medico: String, especialidad: String, onClick: () -> Unit) muestra cita.fecha cruda; la usa ui/screens/citas/MisCitasScreen.kt.
- FechaHoraScreen.kt ya es un calendario dinámico con LocalDate y envía la fecha a la siguiente pantalla en formato ISO.

REGLAS OBLIGATORIAS
- Prohibido usar Room, SQLite o Firebase.
- No cambies nombres ni parámetros de las funciones de Repositorio.kt ni de las pantallas ni de los componentes existentes (todas las firmas deben quedar idénticas).
- No modifiques AppNavigation.kt ni Repositorio.kt.
- Mantén el diseño visual actual; solo cambia el texto de la fecha.
- No ejecutes git commit ni git push.

COMENTARIOS EN EL CÓDIGO (obligatorio, en español, cortos y claros)
- Actualiza el bloque "// Relaciones:" de cada archivo que modifiques para mencionar que usa util/Fechas.kt.
- Encima de cada función nueva, un comentario de una o dos líneas que explique qué hace y qué devuelve.
- Marca el código nuevo con "// Fase 2:".
- Cada prueba unitaria nueva lleva un comentario que diga qué caso verifica.

TAREA
Mostrar las fechas en texto en español (por ejemplo "Martes 6 de octubre 2026") en lugar del formato "2026-10-06":

1. En util/Fechas.kt agrega dos funciones que reciben el String ISO y devuelven el texto en español:
   - textoLargoDesdeIso(iso: String): String -> equivale a textoLargo(LocalDate.parse(iso)). Si el texto no se puede convertir, devuelve el mismo texto recibido sin lanzar excepción.
   - textoCortoDesdeIso(iso: String): String -> equivale a textoCorto(LocalDate.parse(iso)), con el mismo comportamiento seguro ante texto inválido.
   No cambies las funciones existentes de Fechas.
2. Pantalla 7 (ConfirmarCitaScreen.kt): en la fila "Fecha" muestra Fechas.textoLargoDesdeIso(fecha), por ejemplo "Martes 6 de octubre 2026". El parámetro fecha sigue llegando en formato ISO y la cita se sigue guardando en formato ISO al llamar a Repositorio.agendarCita.
3. CitaExitosaScreen.kt: en el resumen muestra la fecha con textoLargoDesdeIso(cita.fecha).
4. TarjetaCita en Componentes.kt: muestra la fecha con textoCortoDesdeIso(cita.fecha), por ejemplo "Mar 6 oct 2026". Si el texto largo no cabe en la fila junto a la hora, ajústalo (por ejemplo con dos filas) sin cambiar la firma del componente.
5. Revisa que no quede ninguna pantalla mostrando una fecha en formato "yyyy-MM-dd" al usuario.

PRUEBAS
En app/src/test/java/com/flores/saludplus/FechasTest.kt agrega pruebas para las dos funciones nuevas:
- textoLargoDesdeIso("2026-10-06") = "Martes 6 de octubre 2026".
- textoLargoDesdeIso("2026-09-16") = "Miércoles 16 de setiembre 2026".
- textoCortoDesdeIso("2026-10-06") = "Mar 6 oct 2026".
- textoLargoDesdeIso("fecha-invalida") devuelve "fecha-invalida" sin lanzar excepción.
Debe seguir pasando HorariosReservadosTest.kt: el bloqueo de horarios ya reservados no debe romperse.

VERIFICACIÓN
Ejecuta ./gradlew testDebugUnitTest y ./gradlew assembleDebug (en Windows: gradlew.bat). Corrige cualquier fallo hasta que todas las pruebas pasen y el proyecto compile.

CRITERIOS DE ACEPTACIÓN
- La Pantalla 7 muestra la fecha como "Martes 6 de octubre 2026" (día de la semana real, mes en español, sin "de" antes del año).
- Cita agendada y las tarjetas de Mis citas también muestran la fecha en español.
- Los datos guardados y los parámetros de navegación siguen en formato ISO.
- Las firmas de pantallas, componentes y del Repositorio no cambiaron; AppNavigation.kt y Repositorio.kt no se modificaron.
- Todas las pruebas pasan y el proyecto compila.

Al terminar, resume qué archivos modificaste y el resultado de las pruebas.
```

### Respuesta resumida

El agente agregó a `Fechas` las funciones `textoLargoDesdeIso` y `textoCortoDesdeIso`, que convierten el texto ISO y devuelven el mismo texto si no se puede convertir, sin lanzar excepción. La Pantalla 7 (Confirmar cita) ahora muestra la fecha como "Martes 6 de octubre 2026", y la misma conversión se aplicó en Cita agendada y en las tarjetas de Mis citas. Los datos guardados y los parámetros de navegación se mantuvieron en formato ISO. Agregó pruebas para las dos funciones nuevas y `HorariosReservadosTest` siguió pasando.

### Qué tuve que corregir

No hubo correcciones al código de este prompt. Comprobé que ninguna pantalla mostrara ya `yyyy-MM-dd` y que las firmas no cambiaran. Una observación: el diseño de referencia muestra "Martes 16 de setiembre", pero el 16 de setiembre de 2026 cae **miércoles**; la app muestra el día real porque usa `LocalDate`.

---

## Prompt 4 — Fidelidad visual al diseño de referencia

### Prompt

```text
CONTEXTO DEL PROYECTO
Proyecto Android Studio llamado "SaludPlus": app de agendamiento de citas médicas para pacientes (Clínica SaludPlus). Lenguaje Kotlin, Jetpack Compose, Material 3, Navigation Compose. Paquete base: com.flores.saludplus. La app NO usa base de datos: usuarios, especialidades, médicos y citas viven en colecciones en memoria dentro del object Repositorio (data/repository/Repositorio.kt) y se pierden al cerrar la app. Esto es intencional.

Estado actual (lee estos archivos antes de escribir código):
- ui/theme: Color.kt, Theme.kt, Type.kt.
- ui/components/Componentes.kt (BotonPrincipal, CampoTexto, BarraSuperior, LogoClinica, TarjetaAccion, BarraInferior, ItemEspecialidad, TarjetaMedico, CampoBusqueda, ChipSeleccion, FilaDetalle, TarjetaCita, PantallaEnConstruccion) y ui/components/ImagenPorNombre.kt (ImagenPorNombre(nombre, descripcion, modifier, contentScale, respaldo) que busca un drawable por nombre, y nombreRecurso(texto) que convierte "Dra. Ana Torres" en "dra_ana_torres").
- ui/screens/auth/SplashScreen.kt, RegistroScreen.kt (con validaciones), LoginScreen.kt; ui/screens/home/HomeScreen.kt; ui/screens/agendamiento/EspecialidadesScreen.kt, MedicosScreen.kt, FechaHoraScreen.kt (calendario dinámico con LocalDate y util/Fechas.kt) y ConfirmarCitaScreen.kt.
- data/model/Especialidad.kt: Especialidad(id, nombre, descripcion, icono: ImageVector). 7 especialidades: Medicina General, Pediatría, Ginecología, Cardiología, Dermatología, Traumatología, Oftalmología.
- IMÁGENES YA EXISTENTES en app/src/main/res/drawable (úsalas, no las regeneres):
  - logo_saludplus.webp: logo (cruz azul con corazón blanco), fondo transparente.
  - ilustracion_doctor.png (292x267 px): doctor con bata blanca, estetoscopio y tablilla, con hojas y fondo celeste claro.
  - medicina_general.png, pediatria.png, ginecologia.png, cardiologia.png, dermatologia.png, traumatologia.png, oftalmologia.png: imagen CIRCULAR a color de cada especialidad (256x256, ya con su fondo pastel). Se buscan con nombreRecurso(especialidad.nombre).
  - Fotos circulares de médicos (dra_ana_torres.png, dra_claudia_rojas.png, dr_luis_ramirez.png, dra_mariana_soto.png) y placeholders XML para los demás médicos.
  No modifiques ni borres ninguno de estos archivos.

OBJETIVO
Hacer que las pantallas Splash, Registro, Inicio, Especialidades, Médicos, Seleccionar fecha y hora y Confirmar cita queden IDÉNTICAS al diseño de referencia, y adaptar el Login al mismo estilo, conservando TODA la funcionalidad (validaciones, navegación, sesión, búsqueda, calendario dinámico y bloqueo de horarios reservados). El diseño usa textos, botones, íconos y tarjetas GRANDES. NO reduzcas las medidas de este prompt; respétalas tal cual.

ESCALA DE REFERENCIA
El diseño está tomado sobre una pantalla de 411 x 913 dp. Las medidas están en dp (tamaños) y sp (textos). Usa esos valores exactos (puedes definirlos como constantes con nombre). Cuerpo ~20sp, títulos ~38-40sp. Si algún contenido no cabe en pantallas más bajas, reduce primero los espacios verticales o permite desplazamiento vertical; NUNCA recortes imágenes ni reduzcas las fuentes.

REGLAS OBLIGATORIAS
- Prohibido usar Room, SQLite o Firebase.
- No cambies nombres ni parámetros de las funciones de Repositorio.kt ni de las pantallas (las firmas deben quedar idénticas). No cambies los modelos.
- No modifiques AppNavigation.kt. En Repositorio.kt la ÚNICA modificación permitida es cambiar el texto de descripción de la especialidad Cardiología a "Corazón y presión sanguínea" (es solo un dato de texto).
- Cambia solo la parte visual. Los componentes existentes pueden ajustarse sin cambiar los parámetros que ya reciben; los parámetros nuevos llevan valor por defecto, y las demás pantallas que los usan deben seguir funcionando.
- No ejecutes git commit ni git push.

COMENTARIOS EN EL CÓDIGO (obligatorio, en español, cortos y claros)
- Mantén y actualiza el bloque "// Relaciones:" de cada archivo que modifiques o crees.
- Encima de cada función composable nueva, un comentario que explique qué dibuja.
- Comenta los bloques de diseño importantes (fondo, tarjeta, fila) e indica la medida elegida cuando no sea obvia.
- Marca el código nuevo con "// Fase 2:".

COLORES NUEVOS (Color.kt)
Agrega: BordeSuave #E3E8F0, CelesteSplash #DCEBFD, RojoAviso #E53935, GrisMarcado #4A5363, BordeCampo #D8DEE9, FondoBusqueda #EEF3FA, Divisor #E6EAF0, LineaSeparadora #C5CEDB y pares pastel/fuerte para los respaldos de especialidades (RosaClaro/Rosa, RojoClaro/Rojo, CelesteClaro/Celeste).

COMPONENTES COMPARTIDOS
- En ImagenPorNombre.kt agrega dos composables: ImagenEspecialidad(especialidad, tamano), que dibuja la imagen nombreRecurso(nombre) en círculo con ContentScale.Crop o, si no existe, un círculo pastel distinto por especialidad con Especialidad.icono en el color fuerte; y FotoMedico(nombre, tamano), que dibuja la foto nombreRecurso(nombre) en círculo o, si no existe, un círculo azul claro con la silueta de persona.
- BotonPrincipal: parámetros nuevos alto (56dp), radio (14dp) y tamanoTexto (16sp), todos con valor por defecto; color azul (AzulPrimario) y texto blanco.
- BarraSuperior: por defecto fondo blanco, flecha de volver de 28dp y título centrado de 26sp negrita azul marino, en una sola línea (Ellipsis). Parámetros nuevos opcionales: colorFondo y tamanoTitulo.
- ChipSeleccion: parámetros nuevos opcionales alto (56dp) y tamanoTexto.
- FilaDetalle: parámetros nuevos opcionales tamanoTitulo y tamanoValor.
- TarjetaMedico: parámetro nuevo opcional modifier, para que la lista le asigne el alto.

PANTALLA 1, SPLASH (SplashScreen.kt)
Firma: SplashScreen(onComenzar: () -> Unit, onYaTengoCuenta: () -> Unit).
- Fondo: degradado vertical de celeste claro (arriba) a blanco (abajo), a pantalla completa, respetando la barra de estado y la de navegación.
- Columna centrada. Espacio superior bajo la barra de estado: 40dp.
- Logo (ImagenPorNombre "logo_saludplus", ContentScale.Fit): 150dp de ANCHO (alto proporcional ~137dp). Respaldo: LogoClinica(150).
- 8dp de espacio. "Clínica": 46sp, negrita, azul marino oscuro (AzulOscuro). Justo debajo "SaludPlus": 58sp, ExtraBold, mismo color, lineHeight 58sp (líneas pegadas). Debajo, 6dp de espacio y "Tu salud, nuestra prioridad": 24sp, gris (TextoSecundario).
- Ilustración del doctor (ImagenPorNombre "ilustracion_doctor"): ocupa TODO EL ANCHO de la pantalla, de borde a borde (sin padding horizontal), con Modifier.fillMaxWidth().aspectRatio(292f / 267f) y ContentScale.FillWidth. IMPORTANTE: la imagen debe verse COMPLETA, incluida la cabeza y el cabello del doctor: no le pongas una altura fija ni heightIn ni ContentScale.Crop. Colócala pegada justo encima del botón, empujada hacia abajo por un Spacer con weight(1f). La columna mide al menos el alto de la pantalla (BoxWithConstraints + verticalScroll + heightIn(min = maxHeight)); si en una pantalla baja no cabe todo, se desplaza en vez de recortar la imagen.
- Botón "Comenzar": azul (AzulPrimario), margen horizontal de 20dp, alto 68dp, esquinas 16dp, texto 22sp SemiBold blanco. Debajo "Ya tengo una cuenta": 22sp, SemiBold, azul, centrado, con 12dp de espacio. Margen inferior 16dp.

PANTALLA 2, REGISTRO (RegistroScreen.kt)
Firma: RegistroScreen(onRegistrado, onTerminos, onIrLogin, onBack). Conserva TODAS las validaciones (nombre obligatorio, teléfono de 9 dígitos solo números, correo opcional con formato válido, contraseña de mínimo 6 caracteres, aviso de teléfono repetido, llamada a Repositorio.registrarUsuario).
- Fondo blanco, margen horizontal de 20dp, contenido desplazable (imePadding para el teclado). Espacio superior bajo la barra de estado: 64dp.
- "Crear cuenta": 38sp, negrita, azul marino, centrado. Debajo, "Regístrate para agendar tus citas": 20sp, gris, centrado. Luego 36dp de espacio.
- CUATRO FILAS de campo (Nombre completo, Teléfono, Correo (opcional), Contraseña), con 26dp de separación vertical entre filas. NO uses un contenedor externo único ni un campo gris. Cada fila tiene EXACTAMENTE esta forma, de 80dp de alto:
  - Izquierda: recuadro cuadrado de 78dp, esquinas de 16dp, fondo blanco, borde de 1dp gris claro (#D8DEE9), con el ícono azul de 34dp centrado (persona, teléfono, sobre, candado).
  - Pegado a su derecha, SIN espacio entre ambos: una columna que ocupa el resto del ancho. Arriba, la etiqueta ("Nombre completo", "Teléfono", "Correo (opcional)", "Contraseña") en gris de 19sp. Debajo, la caja de escritura: fondo blanco, borde de 1dp gris claro (#D8DEE9), esquinas de 14dp, 54dp de alto, texto de 22sp oscuro con 14dp de padding horizontal. La contraseña se muestra con puntos. La caja queda alineada con el borde inferior del recuadro del ícono.
  - Los mensajes de error aparecen bajo la fila, en rojo, 15sp. La caja se pinta con borde rojo cuando hay error.
  - Implementa el campo con BasicTextField en un componente propio CampoFila, y haz que CampoTexto lo use conservando sus parámetros; LoginScreen debe seguir funcionando.
- 34dp de espacio y botón "Registrarme": ancho completo, alto 72dp, esquinas 18dp, azul, texto 24sp SemiBold blanco.
- 28dp de espacio. "Al registrarte aceptas nuestros": 18sp, gris oscuro (GrisMarcado), centrado. Debajo "Términos y Condiciones": 20sp, SemiBold, azul, centrado (lleva a onTerminos).
- Anclado al final de la pantalla con 40dp de margen inferior: "¿Ya tienes cuenta?" 20sp normal y "Iniciar sesión" 20sp SemiBold azul, en la misma línea, centrados (lleva a onIrLogin).

PANTALLA 3, INICIO (HomeScreen.kt)
Firma: HomeScreen(onAgendar, onEspecialidad, onNotificaciones, onNavegar). Conserva la navegación y los datos.
- Fondo blanco, margen horizontal de 16dp.
- Fila superior bajo la barra de estado: a la izquierda ícono de menú de tres rayas de 30dp (solo decorativo); a la derecha la campana de 32dp con un punto rojo de 10dp en su esquina superior derecha (al tocarla llama a onNotificaciones).
- "¡Hola, {primer nombre}!": 40sp, negrita, azul marino, maxLines = 1 con overflow Ellipsis. Debajo "¿Qué deseas hacer hoy?": 22sp, gris. 20dp de espacio.
- Cuadrícula 2x2 de tarjetas: cada una de 155dp de alto, esquinas de 20dp, sin sombra, 14dp de separación. Contenido centrado: ícono de 60dp arriba y debajo el texto de 22sp SemiBold, ambos del color fuerte. "Agendar cita": fondo azul claro, ícono de calendario azul. "Mis citas": fondo verde claro, ícono de calendario con check verde (Icons.Filled.EventAvailable). "Mis datos": fondo morado claro, ícono de persona morado. "Resultados": fondo naranja claro, ícono de documento naranja. (Mis citas -> Citas, Mis datos -> Perfil, Resultados -> Resultados.)
- 20dp de espacio. Fila: "Especialidades destacadas" 22sp negrita a la izquierda y "Ver todas" 22sp SemiBold azul a la derecha (lleva a onAgendar). 12dp de espacio.
- LazyRow de las 3 primeras especialidades con 12dp de separación: cada tarjeta de 120dp de ancho y 160dp de alto, blanca, esquinas de 18dp, borde de 1dp gris claro (#E3E8F0), sin sombra. Dentro, centrado: ImagenEspecialidad de 72dp y debajo el nombre en 19sp negrita, centrado, hasta 2 líneas.
- Barra inferior (BarraInferior): alto 90dp, fondo blanco con una línea superior de 1dp muy fina; íconos de 34dp y etiquetas de 18sp; destino activo en azul con etiqueta en negrita y SIN fondo de píldora (indicatorColor transparente); los demás en gris.

PANTALLA 4, ESPECIALIDADES (EspecialidadesScreen.kt)
Firma: EspecialidadesScreen(onEspecialidad, onBack). Conserva la búsqueda en tiempo real con Repositorio.buscarEspecialidades, el mensaje de lista vacía y el paso de id a la pantalla de médicos.
- Fondo blanco (toda la pantalla), SIN tarjetas ni sombras: es una lista plana.
- Barra superior: BarraSuperior con flecha de 28dp y el título "Especialidades" centrado, 26sp, negrita, azul marino.
- Campo de búsqueda (CampoBusqueda): ancho completo con margen de 16dp, alto 52dp, esquinas de 14dp, fondo celeste muy claro (#EEF3FA), SIN borde, ícono de lupa gris a la izquierda y placeholder "Buscar especialidad..." de 18sp gris. 12dp de espacio debajo.
- LazyColumn: cada fila (ItemEspecialidad) mide 96dp de alto, con margen horizontal de 16dp. De izquierda a derecha: ImagenEspecialidad de 88dp; 14dp de espacio; una columna con el nombre (22sp, negrita, azul marino) y debajo la descripción (18sp, gris); a la derecha un chevron ">" oscuro de 28dp. Entre filas, una línea divisoria de 1dp (#E6EAF0) que empieza donde empieza el texto (118dp desde el borde) y llega al borde derecho.
- Descripciones (datos del Repositorio): Medicina General "Atención integral", Pediatría "Niños y adolescentes", Ginecología "Salud de la mujer", Cardiología "Corazón y presión sanguínea", Dermatología "Piel, cabello y uñas", Traumatología "Huesos y articulaciones", Oftalmología "Salud visual".

PANTALLA 5, MÉDICOS (MedicosScreen.kt)
Firma: MedicosScreen(especialidadId, onMedico, onBack). Conserva la búsqueda con Repositorio.buscarMedicos, la lupa que muestra u oculta el buscador, el mensaje de lista vacía y el paso de medicoId a Fecha y hora.
- Las tarjetas ocupan TODO el alto libre de la pantalla: se ven 4 médicos a la vez. El alto de cada tarjeta se calcula con BoxWithConstraints: (alto disponible - 3 separaciones de 12dp - 16dp de margen inferior) / 4, mínimo 110dp. Si hay más de 4 médicos, los demás se ven con scroll vertical (LazyColumn).
- Cada tarjeta (TarjetaMedico): blanca, esquinas 16dp, padding 14dp. Foto del médico con FotoMedico de 88dp a la izquierda; a su derecha el nombre (20sp negrita), la especialidad (16sp gris) y la calificación con estrella de 20dp y texto de 16sp. La etiqueta verde de disponibilidad ("Disponible hoy"), de 14sp SemiBold, va A LA DERECHA y ABAJO de la tarjeta.

PANTALLA 6, SELECCIONAR FECHA Y HORA (FechaHoraScreen.kt)
Firma: FechaHoraScreen(medicoId, onContinuar, onBack). Conserva el calendario dinámico (LocalDate, util/Fechas.kt, flechas de semana, mes y año), el reinicio de la hora al cambiar de día y Repositorio.horariosDisponibles para que las horas reservadas no aparezcan.
- Tarjeta del médico (fondo azul claro): un poco más alta, con padding horizontal de 14dp y vertical de 18dp, FotoMedico de 68dp y el nombre en 18sp negrita.
- Botones de los días (Lun 15, Mar 16...): ChipSeleccion de 100dp de alto, más altos que los de las horas.
- Botones de las horas: la cuadrícula de 3 columnas (LazyVerticalGrid) ocupa TODO el espacio vertical libre hasta el botón "Continuar", dejando solo 16dp de separación. El alto de cada botón se calcula con BoxWithConstraints repartiendo ese espacio entre las filas de Repositorio.horariosBase (3 filas de 3; mínimo 52dp), así el tamaño no cambia aunque haya horas reservadas. Texto de la hora de 20sp. Separación horizontal de 6dp y vertical de 8dp.
- Botón "Continuar" abajo, solo habilitado con día y hora elegidos.

PANTALLA 7, CONFIRMAR CITA (ConfirmarCitaScreen.kt)
Firma: ConfirmarCitaScreen(medicoId, fecha, hora, onCitaAgendada, onBack). Conserva la fecha en español (Fechas.textoLargoDesdeIso), el rango de hora, el motivo opcional, el aviso de horario ya reservado y la llamada a Repositorio.agendarCita con la fecha en ISO.
- La pantalla ocupa todo el alto: arriba el contenido (desplazable si no cabe, con BoxWithConstraints + heightIn(min = maxHeight)) y el botón "Agendar cita" FIJO abajo, igual que "Continuar" en Seleccionar fecha y hora.
- Tarjeta del médico (fondo azul claro): más alta, con padding horizontal de 16dp y vertical de 22dp, FotoMedico de 84dp, nombre de 20sp negrita, especialidad de 18sp gris y CMP de 16sp.
- Datos de la cita (Fecha, Hora, Tipo de atención, Dirección): se reparten el alto libre de la pantalla (mínimo 280dp en total); cada dato ocupa la misma altura (weight(1f)). Usa FilaDetalle con título de 18sp y valor de 22sp. Después de CADA dato (después de Fecha, de Hora, de Tipo de atención y de Dirección) hay una línea separadora de 1.5dp en color LineaSeparadora (#C5CEDB).
- "Motivo de consulta (opcional)": etiqueta de 18sp SemiBold; el campo es bajo, de 80dp de alto, con texto y placeholder de 18sp.
- El aviso "Ese horario ya fue reservado. Vuelve y elige otro." en rojo de 16sp.

LOGIN (LoginScreen.kt)
Firma: LoginScreen(onLoginExitoso, onIrRegistro, onBack). Adáptalo al estilo del Registro: BarraSuperior con flecha y título "Iniciar sesión" (26sp negrita azul marino), título "Bienvenido de nuevo" 34sp negrita y subtítulo "Ingresa con tu teléfono y contraseña" 20sp gris, los campos Teléfono y Contraseña con el MISMO estilo de fila de 80dp (recuadro de ícono de 78dp + etiqueta y caja de 54dp), botón "Ingresar" de 72dp, y abajo "¿No tienes cuenta?" con "Regístrate" 20sp SemiBold azul. Conserva las validaciones y la llamada a Repositorio.iniciarSesion.

IMAGENES.md
Actualiza IMAGENES.md (raíz del proyecto) para reflejar el estado real: las 7 imágenes de especialidades y las 4 fotos de médicos YA existen en res/drawable; los otros 8 médicos usan placeholders XML (nombres: dr_carlos_mendoza, dra_lucia_vargas, dra_sofia_paredes, dr_jorge_salas, dr_ricardo_nunez, dra_patricia_leon, dr_andres_quispe, dra_elena_campos) y se reemplazan borrando el .xml y copiando una imagen con el MISMO nombre (si quedan ambos, Android da el error "Duplicate resources").

VERIFICACIÓN
Ejecuta ./gradlew testDebugUnitTest y ./gradlew assembleDebug (en Windows: gradlew.bat). Todas las pruebas deben pasar (incluidas FechasTest, HorariosReservadosTest y NombreRecursoTest) y el proyecto debe compilar. Antes de terminar, repasa cada medida de este prompt contra tu código y corrige las que no coincidan.

CRITERIOS DE ACEPTACIÓN
- Splash: logo de 150dp de ancho, "Clínica" 46sp y "SaludPlus" 58sp, ilustración del doctor a todo el ancho y COMPLETA (se ve la cabeza y el cabello), botón de 68dp.
- Registro: cada fila es un recuadro de ícono de 78dp pegado a una columna con etiqueta arriba y caja blanca con borde de 54dp; botón de 72dp; textos de términos y enlace inferior con los tamaños indicados.
- Inicio: tarjetas de 155dp con íconos de 60dp; tarjetas de especialidades de 120x160dp con la imagen real en círculo de 72dp; nombre de usuario en una sola línea; barra inferior de 90dp.
- Especialidades: lista plana sobre fondo blanco con filas de 96dp, imagen circular de 88dp, divisores finos, chevron a la derecha y buscador celeste claro sin borde; la descripción de Cardiología dice "Corazón y presión sanguínea".
- Médicos: 4 tarjetas llenan el alto de la pantalla y el resto se ve con scroll; foto de 88dp, textos grandes y "Disponible hoy" abajo a la derecha.
- Seleccionar fecha y hora: tarjeta del médico más alta, días de 100dp y horas que llenan todo el espacio hasta "Continuar" con poca separación.
- Confirmar cita: tarjeta del médico más alta, datos de la cita repartidos en vertical con letras grandes y una línea después de cada dato, motivo de 80dp y "Agendar cita" fijo abajo.
- Validaciones, login, sesión, búsqueda, calendario, bloqueo de horarios y navegación siguen funcionando; ninguna firma cambió; AppNavigation.kt no se modificó; en Repositorio.kt solo cambió ese texto.
- Todas las pruebas pasan y el proyecto compila.

Al terminar, resume qué archivos creaste, modificaste o eliminaste.
```

### Respuesta resumida

El agente dejó las pantallas con las medidas del diseño en dp y sp:
- **Splash:** fondo degradado, logo, "Clínica SaludPlus" grande y la ilustración del doctor a todo el ancho y completa (`aspectRatio`).
- **Registro:** filas con el recuadro del ícono pegado al campo (componente `CampoFila`).
- **Inicio:** tarjetas grandes, `LazyRow` de especialidades con `ImagenEspecialidad` y barra inferior de 90dp.
- **Especialidades:** lista plana con divisores y buscador celeste.
- **Médicos:** 4 tarjetas que llenan la pantalla, con `FotoMedico` y la etiqueta de disponibilidad a la derecha.
- **Fecha y hora y Confirmar cita:** tarjetas más altas y botones fijos abajo.
- **Login:** adaptado al estilo del Registro.

También creó `ImagenEspecialidad`, `FotoMedico` y los colores nuevos, y actualizó `IMAGENES.md`. Se conservaron las validaciones, la sesión, la búsqueda, el calendario dinámico y el bloqueo de horarios. Todas las pruebas pasaron.

### Qué tuve que corregir

Este prompt se ejecutó en varias vueltas, porque el resultado inicial no coincidía con el diseño:

1. **Medidas demasiado pequeñas.** En el primer intento el prompt pedía fidelidad "aproximada" y el agente dejó todo más pequeño que el diseño (logo y "Clínica SaludPlus", tarjetas, botones, textos). Corregí el prompt con medidas exactas en dp y sp sobre una pantalla de 411×913 dp.
2. **Ilustración del doctor recortada.** Tenía una altura fija y se cortaba la cabeza. Se corrigió con `fillMaxWidth().aspectRatio(292f/267f)` y `ContentScale.FillWidth`, sin altura fija.
3. **Campos del Registro.** El agente dibujó una caja gris dentro de un solo contenedor. Se corrigió pidiendo la forma exacta del diseño: recuadro del ícono de 78dp pegado a una etiqueta y una caja blanca con borde.
4. **Imágenes de especialidades.** Aparecían íconos genéricos y figuras sin sentido. Se recortaron las imágenes reales del diseño, se guardaron en `res/drawable` con los nombres de la interfaz (`ginecologia`, `dra_ana_torres`…) y se añadió `nombreRecurso` para buscarlas por nombre.
5. **Especialidades como tarjetas.** El diseño es una lista plana; se corrigió con filas de 96dp, divisores y fondo blanco.
6. **Médicos y pantallas siguientes.** La etiqueta "Disponible hoy" debía ir a la derecha, las tarjetas debían ocupar todo el alto, y los días y horas debían ser más grandes y con color gris claro, como el diseño. Se agregaron esas medidas al prompt.
7. **Detalles.** Un nombre de usuario muy largo desbordaba el saludo; se resolvió con `maxLines = 1`. El botón flotante con tres rayas que se veía en las capturas es una herramienta del emulador, no de la app.

---

## Prompt 6 — Sedes, menú lateral, horarios por doctor y confirmaciones

### Prompt

```
Con la app ClinicaSaludPlus agrega lo siguiente (en 3 partes, una por commit):
1. Al registrarse o iniciar sesión debe salir un mensaje de confirmación con el logo.
2. Menú lateral con SEDES, DOCTORES, AGENDA y CERRAR SESIÓN. Lo que está en el menú ya no se repite en el
   Inicio (se quita "Agendar cita" del Inicio).
3. Para agendar: Sedes > elegir sede (Santa Anita, Ate, La Molina, San Isidro) > Agendar cita > especialidad >
   doctor de esa sede > fecha y hora > confirmación. El paciente NO elige cualquier día y hora: cada doctor
   tiene sus días y horarios de atención. Una cita agendada (por ejemplo, 9 de octubre a las 5 pm) ya no debe
   aparecer disponible para ese doctor.
4. Cada doctor tiene nombre, especialidad, código, sede y teléfono; Doctores > Especialidad > fichas.
5. Validaciones comprobadas para que la app no se rompa y tamaños de texto iguales en cada sección.
Mantén los colores. Ejecuta ./gradlew testDebugUnitTest y ./gradlew assembleDebug.
```

### Respuesta resumida

- **Parte 1:** modelos `Sede` y `Medico` (sede, código, teléfono y horario semanal); `horariosDisponibles` y
  `agendarCita` respetan el horario del doctor, quitan las horas reservadas y las que ya pasaron; mensaje de
  confirmación (`DialogoConfirmacion`) al registrarse e iniciar sesión.
- **Parte 2:** un único menú lateral en `AppNavigation` (`MenuLateral`); pantallas Sedes, Sede, Doctores y ficha
  por especialidad; el agendamiento pasa por la sede; Inicio sin "Agendar cita".
- **Parte 3:** guarda de sesión, doctor inexistente, límite del motivo, escala única de tamaños en `Type.kt`
  y pruebas nuevas.

### Qué tuve que corregir

- La tarjeta del doctor quedaba apretada al añadir la sede: la etiqueta de disponibilidad pasó bajo la calificación.
- "Cita agendada" y "Detalle de cita" seguían con una dirección fija: ahora usan la sede del doctor.
- Las pruebas usaban fechas fijas: ahora reciben un "ahora" fijo (`AHORA_PRUEBA`) y no dependen del día.
