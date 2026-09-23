# TECSUP Fit

App Android de reserva de clases de gimnasio (Opción B), desarrollada en **Kotlin** con **Jetpack Compose** y **Navigation Compose**.

## Funcionalidades

- **Login y Registro** con validaciones de campos.
- **Inicio:** LazyRow de filtros (Hoy / Mañana / Esta semana), buscador y LazyColumn de clases.
- **Detalle de clase:** recibe la clase por parámetro y permite elegir un solo horario antes de reservar.
- **Confirmación:** muestra el resumen de la reserva y un botón para ver las reservas.
- **Reservas:** lista con estados Confirmada / Completada / Cancelada, y opción de cancelar.
- **Rutinas:** lista de rutinas que se pueden marcar como completadas.
- **Perfil:** datos del usuario, estadísticas reales, edición del nombre y cierre de sesión.
- **bottomBar** con 4 pestañas (Inicio, Reservas, Rutinas, Perfil) y el ícono activo resaltado.

## Ramas

| Rama | Contenido |
|------|-----------|
| `main` | Fase 1 – desarrollo sin IA |
| `mejora-ia` | Fase 2 – mejoras aplicadas con IA |

---

## Fase 2 — Prompts usados con IA

### Prompt 1 — Login y Registro con validaciones

**Commit:** `agregar login y registro con validaciones de campos`

**Descripción:** Agregué inicio de sesión y registro con validaciones de nombre, correo, contraseña segura y confirmación. El nombre del usuario logueado aparece en Inicio y Perfil.

```
Tengo una app Android en Kotlin con Jetpack Compose y Navigation Compose llamada "TECSUP Fit"
(paquete com.flores.tecsupfit). La navegación está en navigation/AppNavigation.kt y las rutas en
navigation/Screen.kt. Actualmente la app inicia directo en Screen.Home y el nombre del usuario
("Diego Ramos") está hardcodeado en HomeScreen.kt y ProfileScreen.kt.

Quiero agregar autenticación:
1. Crear screens/LoginScreen.kt y screens/RegisterScreen.kt usando Scaffold con topBar.
2. Agregar las rutas Screen.Login y Screen.Register; Login debe ser el startDestination.
   El bottomBar NO debe mostrarse en Login ni Register.
3. Registro con campos: nombre completo, correo, contraseña y confirmar contraseña.
   Validaciones con mensaje de error debajo de cada OutlinedTextField (isError + supportingText):
   - Nombre: obligatorio, mínimo 3 caracteres, solo letras y espacios.
   - Correo: obligatorio y con formato válido (android.util.Patterns.EMAIL_ADDRESS).
   - Contraseña: mínimo 6 caracteres, al menos una mayúscula y un número.
   - Confirmar contraseña: debe coincidir.
   - No permitir registrar un correo ya existente.
   El botón "Crear cuenta" solo se habilita si todo es válido.
4. Login con correo y contraseña: validar campos vacíos y formato de correo; si las credenciales
   no coinciden con un usuario registrado, mostrar "Correo o contraseña incorrectos".
   Contraseña con icono para mostrar/ocultar (PasswordVisualTransformation).
5. Guardar los usuarios en una lista en memoria con estado elevado en AppNavigation
   (mutableStateListOf<User>), igual que ya se hace con "reservations". Crear data class User.
6. Al iniciar sesión navegar a Home con popUpTo(Login) { inclusive = true } para que "atrás" no
   regrese al login. Mostrar el nombre del usuario logueado en HomeScreen ("Hola, <nombre>")
   y en ProfileScreen (nombre, correo e iniciales en el avatar).
Mantén el estilo visual actual (color principal 0xFF0F6A52, esquinas de 12.dp) y comenta el código
en español.
```

### Prompt 2 — Selección única de horario y topBar en todas las pantallas

**Commit:** `agregar seleccion unica de horario y topBar en todas las pantallas`

**Descripción:** Antes de reservar hay que elegir un solo horario. La app no deja reservar horarios sin cupo ni la misma clase dos veces. Todas las pantallas tienen topBar.

```
En mi app TECSUP Fit (Jetpack Compose), DetailScreen.kt permite reservar directamente con el botón
"Reservar cupo", sin que el usuario elija nada. Necesito cumplir el requisito de "selección de
opción única antes de confirmar" y que el Scaffold tenga topBar en todas las pantallas.

1. Agregar al data class GymClass (HomeScreen.kt) una lista de horarios disponibles
   (ej. "Yoga funcional": 7:00 am, 8:00 am, 9:00 am).
2. En DetailScreen mostrar esos horarios como opciones de selección única (RadioButton o
   FilterChip, solo uno seleccionado a la vez). Cada horario debe tener sus propios cupos.
3. Validaciones:
   - El botón "Reservar cupo" permanece deshabilitado hasta seleccionar un horario, y se muestra
     el texto "Selecciona un horario para continuar".
   - Los horarios sin cupos se muestran deshabilitados con el texto "Lleno".
   - No permitir reservar dos veces la misma clase en el mismo horario.
   - Antes de confirmar, mostrar un AlertDialog con el resumen (clase, horario, sala) y los
     botones "Confirmar" / "Cancelar".
4. Pasar el horario elegido como parámetro de navegación a ConfirmationScreen
   (actualizar Screen.Confirmation.createRoute y usar Uri.encode para los textos con espacios o ":").
   ConfirmationScreen debe mostrar clase, horario elegido y sala.
5. Agregar TopAppBar en HomeScreen, ListScreen, ProfileScreen, ConfirmationScreen y en la pantalla
   de Rutinas, aplicando correctamente el paddingValues del Scaffold para que el contenido no quede
   debajo del topBar ni del bottomBar.
No cambies la lógica existente de reservas en AppNavigation, solo extiéndela.
```

### Prompt 3 — Filtros, gestión de reservas, rutinas y perfil dinámico

**Commit:** `agregar filtros, cancelacion de reservas, rutinas y perfil dinamico`

**Descripción:** Los chips y el buscador filtran las clases, y las reservas se pueden cancelar. Rutinas muestra una lista y el Perfil tiene estadísticas reales, edición del nombre y cierre de sesión.

```
En mi app TECSUP Fit (Jetpack Compose + Navigation) quiero mejorar funcionalmente las pantallas
del bottomBar:

1. HomeScreen: hacer que los chips del LazyRow filtren de verdad. Agregar a cada GymClass un día
   ("Hoy" o un día de la semana). "Hoy" muestra solo las clases de hoy y "Esta semana" todas.
   Agregar un tercer chip "Mañana". Agregar un OutlinedTextField de búsqueda por nombre de clase;
   si no hay resultados, mostrar un mensaje "No se encontraron clases".
2. ListScreen (Reservas): permitir cancelar una reserva "Confirmada" con un AlertDialog de
   confirmación; al cancelar se devuelve el cupo. Las reservas "Completada" no se pueden cancelar.
   Agregar un tercer estado "Cancelada" con su propio color. Si la lista está vacía, mostrar un
   estado vacío con un botón "Explorar clases" que navegue a Inicio.
3. Rutinas: reemplazar el Text de ejemplo por una pantalla RoutinesScreen.kt con una LazyColumn
   de rutinas (nombre, nivel, duración) y un Checkbox para marcarlas como completadas.
4. ProfileScreen: calcular las estadísticas a partir de los datos reales (clases reservadas,
   clases completadas, rutinas completadas) en lugar de los valores fijos "14" y "3".
   Agregar un botón "Editar perfil" que permita cambiar el nombre, con validación (no vacío,
   mínimo 3 letras), y un botón "Cerrar sesión" con AlertDialog de confirmación que regrese al
   Login limpiando el back stack (popUpTo(0)).
5. Mostrar un Snackbar (SnackbarHost en el Scaffold) al reservar, al cancelar y al guardar el perfil.
Mantén el estado elevado en AppNavigation y el estilo visual actual.
```

---

**Autora:** Yoselin Flores — PRO-MÓVIL III, TECSUP
