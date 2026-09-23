# 📱 Proyectos Android — TECSUP-Semana05

Repositorio de proyectos móviles desarrollados en **Kotlin**, utilizando **Jetpack Compose** y **Navigation Compose**, como parte del curso **PRO-MÓVIL III – TECSUP**.

---

# 🏋️ TECSUP Fit

App Android de reserva y gestión de clases de gimnasio, desarrollada en **Kotlin** con **Jetpack Compose** y **Navigation Compose**.

## Funcionalidades

* **Login y Registro:** validación de nombre, correo, contraseña y confirmación.
* **Inicio:** filtros dinámicos por fecha, buscador y lista de clases.
* **Detalle de clase:** selección única de horario antes de reservar.
* **Confirmación:** resumen de la reserva antes de confirmar.
* **Reservas:** lista de reservas con estados `Confirmada`, `Completada` y `Cancelada`.
* **Cancelación:** permite cancelar reservas confirmadas y recuperar el cupo.
* **Rutinas:** lista de rutinas con opción para marcarlas como completadas.
* **Perfil:** información del usuario, estadísticas dinámicas, edición del nombre y cierre de sesión.
* **Navegación:** `bottomBar` con cuatro pestañas:

  * Inicio
  * Reservas
  * Rutinas
  * Perfil

## Ramas

| Rama        | Contenido                         |
| ----------- | --------------------------------- |
| `main`      | Fase 1 – desarrollo sin IA        |
| `mejora-ia` | Fase 2 – mejoras aplicadas con IA |

---

## Fase 2 — Prompts usados con IA

### Prompt 1 — Login y Registro con validaciones

**Commit:** `agregar login y registro con validaciones`

**Descripción:** Se agregó autenticación mediante Login y Registro, validación de campos y almacenamiento temporal de usuarios en memoria.

```text
Tengo una app Android en Kotlin con Jetpack Compose y Navigation Compose llamada "TECSUP Fit"
(paquete com.flores.tecsupfit). La navegación está en navigation/AppNavigation.kt y las rutas en
navigation/Screen.kt. Actualmente la app inicia directo en Screen.Home y el nombre del usuario
("Diego Ramos") está hardcodeado en HomeScreen.kt y ProfileScreen.kt.

Quiero agregar autenticación:

1. Crear screens/LoginScreen.kt y screens/RegisterScreen.kt usando Scaffold con topBar.
2. Agregar las rutas Screen.Login y Screen.Register; Login debe ser el startDestination.
   El bottomBar NO debe mostrarse en Login ni Register.
3. Registro con campos: nombre completo, correo, contraseña y confirmar contraseña.
4. Validar nombre, correo, contraseña y confirmación.
5. No permitir registrar un correo ya existente.
6. Login con correo y contraseña.
7. Mostrar mensaje "Correo o contraseña incorrectos" si las credenciales no coinciden.
8. Permitir mostrar u ocultar la contraseña.
9. Guardar los usuarios en una lista en memoria con estado elevado en AppNavigation.
10. Al iniciar sesión navegar a Home eliminando Login del back stack.
11. Mostrar el nombre del usuario en Home y Profile.

Mantén el estilo visual actual, color principal 0xFF0F6A52,
esquinas de 12.dp y comentarios en español.
```

### Prompt 2 — Selección única de horario y TopBar

**Commit:** `agregar seleccion unica de horario y topBar en todas las pantallas`

**Descripción:** Se implementó la selección obligatoria de un único horario, control de cupos y `TopAppBar` en las diferentes pantallas.

```text
En mi app TECSUP Fit (Jetpack Compose), DetailScreen.kt permite reservar directamente
con el botón "Reservar cupo", sin que el usuario elija nada.

Necesito:

1. Agregar a GymClass una lista de horarios disponibles.
2. Mostrar los horarios como opciones de selección única.
3. Cada horario debe tener sus propios cupos.
4. El botón "Reservar cupo" permanece deshabilitado hasta seleccionar un horario.
5. Los horarios sin cupos deben aparecer deshabilitados con el texto "Lleno".
6. No permitir reservar dos veces la misma clase en el mismo horario.
7. Antes de confirmar mostrar un AlertDialog con:
   - Clase
   - Horario
   - Sala
   - Confirmar
   - Cancelar
8. Pasar el horario seleccionado a ConfirmationScreen mediante Navigation Compose.
9. Utilizar Uri.encode para parámetros con espacios o caracteres especiales.
10. Agregar TopAppBar en las pantallas principales.
11. Aplicar correctamente paddingValues del Scaffold.

No cambies la lógica existente de reservas en AppNavigation,
solo extiéndela.
```

### Prompt 3 — Filtros, reservas, rutinas y perfil dinámico

**Commit:** `agregar filtros, cancelacion de reservas, rutinas y perfil dinamico`

**Descripción:** Se implementaron filtros dinámicos, búsqueda, cancelación de reservas, rutinas y estadísticas calculadas a partir del estado real de la aplicación.

```text
En mi app TECSUP Fit (Jetpack Compose + Navigation) quiero mejorar
funcionalmente las pantallas del bottomBar:

1. HomeScreen:
   - Hacer funcionales los filtros.
   - Agregar "Hoy", "Mañana" y "Esta semana".
   - Agregar buscador por nombre de clase.
   - Mostrar "No se encontraron clases" cuando corresponda.

2. ListScreen:
   - Permitir cancelar reservas Confirmadas.
   - Utilizar AlertDialog para confirmar.
   - Devolver el cupo al cancelar.
   - Las reservas Completadas no se pueden cancelar.
   - Agregar estado Cancelada.
   - Crear estado vacío cuando no existan reservas.

3. Rutinas:
   - Crear RoutinesScreen.kt.
   - Utilizar LazyColumn.
   - Mostrar nombre, nivel y duración.
   - Permitir marcar rutinas como completadas.

4. ProfileScreen:
   - Calcular estadísticas utilizando datos reales.
   - Permitir editar el nombre.
   - Validar que el nombre no esté vacío.
   - Agregar cierre de sesión con confirmación.
   - Limpiar el back stack al regresar al Login.

5. Mostrar Snackbar al:
   - Reservar.
   - Cancelar.
   - Guardar el perfil.

Mantén el estado elevado en AppNavigation y conserva
el estilo visual existente.
```

---

# 🏥 Clínica Salud+

App Android de gestión de citas médicas y atención al paciente, desarrollada en **Kotlin** con **Jetpack Compose** y **Navigation Compose**.

## Funcionalidades

* **Inicio:** saludo personalizado, filtros por especialidad y médicos disponibles.
* **Agendamiento:** selección de médico, fecha y horario AM/PM.
* **Validación:** el sistema exige seleccionar un horario antes de confirmar.
* **Navegación:** menú lateral mediante `ModalNavigationDrawer`.
* **Mis Citas:** lista de citas programadas y menú contextual.
* **Historial Médico:** registros de atenciones completadas.
* **Perfil:** información personal y clínica del paciente.
* **Diseño:** interfaz basada en una paleta celeste/azul médico.
* **Interactividad:** `DropdownMenu`, `AlertDialog`, estados y navegación mediante `Navigation Compose`.

---

## Fase 1 — Rediseño visual, paciente y navegación

### Prompt 1 — Rediseño Celeste, Datos del Paciente y Menú Lateral

**Commit:** `style & fix: rediseño a tema celeste, corrección de filtros y actualización a Yoselyn Flores`

**Descripción:** Se actualizó la paleta visual hacia tonalidades celeste/azul médico, se personalizaron los datos del paciente y se corrigió el filtrado por especialidad. También se estandarizó el menú lateral utilizando componentes oficiales de Material 3.

```text
# ROL Y CONTEXTO

Eres un Desarrollador Android Senior experto en Kotlin,
Jetpack Compose y Material 3.

Realizarás la primera fase de refactorización de la aplicación
"Clínica Salud+".

# OBJETIVO

Cambiar la paleta visual de morado a Celeste/Azul Médico,
actualizar los datos del paciente principal a
"Yoselyn Flores Quispe" y corregir la lógica de filtrado
por especialidad en la pantalla principal.

# REQUERIMIENTOS TÉCNICOS

1. Paleta de Colores Celeste:
   - Header/Primary: #0284C7 o #0EA5E9
   - Tarjetas/Chips: #E0F2FE o #F0F9FF
   - Textos e Íconos: #0F172A y #0284C7

2. Nombre del Paciente y Filtrado:
   - Mostrar "Yoselyn Flores Quispe".
   - Utilizar "Hola, Yoselyn" en la cabecera.
   - Actualizar el Drawer.
   - Corregir el LazyRow y LazyColumn.
   - Cardiología:
     * Dra. Ana Torres
     * Dra. Rosa Díaz
   - Pediatría:
     * Dr. Luis Vega

3. Menú Lateral:
   - Implementar ModalNavigationDrawer.
   - Utilizar ModalDrawerSheet.
   - Utilizar NavigationDrawerItem.
   - Controlar el estado con rememberDrawerState().
   - Utilizar rememberCoroutineScope().

# RESTRICCIONES

No crear paquetes nuevos como data, models o repository.
Mantener el código dentro de los archivos existentes.
```

---

## Fase 2 — Validaciones y menú contextual

### Prompt 2 — Safe Area, Validaciones AM/PM y Menú Flotante

**Commit:** `feat: validación de horario AM/PM, statusBarsPadding y DropdownMenu en citas`

**Descripción:** Se implementó el área segura de las cabeceras, la selección obligatoria de horarios AM/PM y el menú contextual de tres puntos en la pantalla de citas.

```text
# ROL Y CONTEXTO

Eres un Desarrollador Android Senior experto en Kotlin,
Jetpack Compose y Material 3.

Realizarás la segunda fase de mejora para la aplicación
"Clínica Salud+".

# OBJETIVO

Evitar solapamientos con la barra de sistema,
formatear las horas con AM/PM, implementar validaciones
en el agendamiento e integrar un DropdownMenu.

# REQUERIMIENTOS

1. Área segura:
   - Agregar .statusBarsPadding() en los contenedores superiores
     de las pantallas.
   - Aplicarlo en HomeScreen, ScheduleAppointmentScreen,
     MyAppointmentsScreen y demás pantallas.
   - Utilizar una sola flecha de retorno.
   - Utilizar Icons.AutoMirrored.Filled.ArrowBack.

2. Agendamiento:
   - Inicializar selectedTime con "".
   - Mostrar horarios como:
     9:00 AM
     10:30 AM
     3:00 PM
   - Impedir confirmar si no se seleccionó horario.
   - Mostrar:
     "⚠️ Debes seleccionar un horario"
   - Limpiar el mensaje al seleccionar un horario.

3. Menú de tres puntos:
   - Utilizar Icons.Default.MoreVert.
   - Implementar DropdownMenu.
   - Agregar:
     * Actualizar lista
     * Soporte y Ayuda

# RESTRICCIONES

No agregar bases de datos persistentes.
La lista de citas se gestiona en memoria durante la ejecución.
```

---

## Fase 3 — Historial, Perfil e Interacción

### Prompt 3 — Historial Médico, Perfil Completo, Interacción y Navegación

**Commit:** `fix & feat: corregir navegacion drawer, menu 3 puntos, perfil de Yoselyn e historial medico`

**Descripción:** Se corrigieron los flujos de navegación del Drawer, se implementaron las acciones del menú de tres puntos, se agregó un historial médico con registros de atención y se actualizó el perfil del paciente.

```text
# ROL Y CONTEXTO

Eres un Desarrollador Android Senior experto en Kotlin,
Jetpack Compose y Material 3.

Realizarás las correcciones finales y la entrega de la
Fase 3 de la aplicación "Clínica Salud+".

# OBJETIVO

Corregir la navegación del menú lateral, implementar
el menú de tres puntos, agregar historial médico
y reestructurar el perfil del paciente.

# REQUERIMIENTOS

1. Navegación del Drawer:
   - Revisar el handler onClick.
   - Cerrar el drawer con:
     scope.launch { drawerState.close() }
   - Navegar a Screen.Home.route.
   - Utilizar popUpTo para evitar duplicados.

2. Perfil:
   - Nombre: Yoselyn Flores Quispe.
   - Correo: yoselyn.flores@clinicasalud.com
   - Avatar: YF
   - DNI: 74839201
   - Edad: 26 años
   - Tipo de sangre: O+
   - Teléfono: +51 987 654 321
   - Mostrar métricas:
     * 3 Citas Asistidas
     * Estado: Activo
   - Cambiar "Cerrar Sesión" por "Volver al Inicio".
   - El botón debe navegar a Screen.Home.route.

3. Menú de tres puntos:
   - "Actualizar lista":
     mostrar Toast:
     "Lista de citas actualizada".
   - "Soporte y Ayuda":
     mostrar AlertDialog.
   - Incluir datos de contacto de la clínica.

4. Historial Médico:
   - Utilizar LazyColumn.
   - Agregar mínimo 3 registros.
   - Cada registro debe mostrar:
     * Médico
     * Especialidad
     * Fecha
     * Diagnóstico
     * Estado
   - Estados:
     * Completada
     * Confirmada
   - Agregar botón "Ver Diagnóstico".

# RESTRICCIONES

Mantener la paleta:

HeaderPrimary = #0284C7
Background = #F0F9FF

Modificar únicamente los archivos dentro de:
screens/
navigation/

No crear paquetes adicionales.
```

---

# 🛠️ Tecnologías utilizadas

| Tecnología                | Uso                           |
| ------------------------- | ----------------------------- |
| **Kotlin**                | Lenguaje principal            |
| **Jetpack Compose**       | Desarrollo de interfaces      |
| **Material 3**            | Componentes y diseño          |
| **Navigation Compose**    | Navegación entre pantallas    |
| **LazyColumn / LazyRow**  | Listas dinámicas              |
| **Scaffold**              | Estructura de las pantallas   |
| **AlertDialog**           | Confirmaciones e información  |
| **DropdownMenu**          | Menús contextuales            |
| **ModalNavigationDrawer** | Navegación lateral            |
| **Snackbar**              | Mensajes de interacción       |
| **State Management**      | Gestión de estados en memoria |

---

# 📚 Objetivo académico

Estos proyectos permiten aplicar conceptos de desarrollo de aplicaciones móviles utilizando Kotlin y Jetpack Compose, incluyendo:

* Arquitectura y organización de pantallas.
* Navegación entre interfaces.
* Manejo de estados.
* Validación de formularios.
* Componentes Material 3.
* Listas dinámicas.
* Interacción con el usuario.
* Diseño de interfaces móviles.
* Uso de herramientas de Inteligencia Artificial como apoyo durante el desarrollo.

---

## 👩‍💻 Autora

**Yoselin Flores**
**Diseño y Desarrollo de Software — TECSUP**
**PRO-MÓVIL III**
