# Portal Académico - Android Jetpack Compose (Semana05-Laboratorio-IA)

Este repositorio contiene la implementación del **Portal Académico**, una aplicación móvil desarrollada completamente con **Jetpack Compose**, **Material Design 3** y **Kotlin** (100% libre de archivos XML de diseño).

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Navegación:** Navigation Compose
- **Arquitectura:** Single-Activity Architecture con Vistas Modulares

---

## 📝 Prompt Utilizado

```text
Actúa como desarrollador Senior Android experto en Jetpack Compose, Material Design 3 y Kotlin.

Crea la aplicación e interfaz completa del "Portal Académico" construida al 100% en Jetpack Compose (sin utilizar absolutamente ningún archivo layout XML). El código generado debe ser totalmente funcional, autosuficiente y contener todas las pantallas, datos de prueba, tema visual y navegación sin omitir ninguna línea ni usar comentarios de relleno como "// Resto del código...".

---

### 1. ESTRUCTURA Y ARCHIVOS A GENERAR
Debes entregar la implementación completa en código Kotlin para los siguientes 11 archivos:
1. `model/Student.kt` (Modelo de datos y lista de alumnos)
2. `ui/theme/Color.kt` (Paleta de colores exacta)
3. `ui/theme/Theme.kt` (Configuración de MaterialTheme con dynamicColor desactivado)
4. `navigation/Screen.kt` (Rutas de pantalla)
5. `navigation/AppNavigation.kt` (Grafo de navegación Jetpack Compose)
6. `ui/screens/LoginScreen.kt`
7. `ui/screens/HomeScreen.kt`
8. `ui/screens/ListScreen.kt`
9. `ui/screens/DetailScreen.kt`
10. `ui/screens/ProfileScreen.kt`
11. `MainActivity.kt`

---

### 2. RECURSOS DE IMÁGENES (res/drawable)
Todas las fotos de perfil deben referenciar los drawables estándar del proyecto mediante `painterResource`:
- R.drawable.profile_user
- R.drawable.student_1
- R.drawable.student_2
- R.drawable.student_3
- R.drawable.student_4
- R.drawable.student_5

---

### 3. SISTEMA DE COLORES (ui/theme/Color.kt)
Configura exactamente los siguientes valores de color Hex:
- PrimaryPurple = Color(0xFF5B4193) (Morado institucional principal)
- HeaderGradientStart = Color(0xFF5B4193) (Morado de la izquierda del degradado de perfil)
- HeaderGradientEnd = Color(0xFF886378) (Matiz cálido lavanda/salmón/rosa tenue para la derecha del degradado de perfil)
- ScreenBackground = Color(0xFFF3EDF7) (Fondo suave lavanda tenue de las pantallas)
- LoginCardGray = Color(0xFFD3CBDC) (Plomo lavanda un poco más oscuro para la tarjeta central de Login; NO usar blanco puro ni lavanda muy claro)
- CardGray = Color(0xFFE1DAE8) (Gris lavanda estructurado para las tarjetas del directorio y expediente)
- WhiteCard = Color(0xFFFFFFFF) (Blanco puro para las tarjetas de acceso rápido en Home)
- SoftIconPurple = Color(0xFFEADDFF) (Fondo claro cuadrado para los contenedores de íconos)
- IconDarkGray = Color(0xFF49454F) (Plomo/Gris oscuro para todos los íconos de listas y campos)
- TextDark = Color(0xFF1C1B1F) (Texto principal oscuro en negrita)
- TextMuted = Color(0xFF524F58) (Plomo/Gris oscuro visible para subtextos y etiquetas)
- SoftRedBg = Color(0xFFFCE8E6) (Fondo rosa/rojo claro para botones de Cerrar Sesión)
- TextRed = Color(0xFFB3261E) (Rojo para texto e ícono de Cerrar Sesión)

Desactiva dynamicColor en Theme.kt para garantizar que se usen exactamente estos colores.

---

### 4. MODELO DE DATOS (model/Student.kt)
Crea una `data class Student`:
- id: String
- name: String
- career: String
- email: String
- faculty: String
- bio: String
- imageRes: Int

Incluye una lista estática `sampleStudents` que contenga al menos 5 alumnos, donde el primer registro sea:
- id: "2024-0091"
- name: "Yoselin Fabiola Flores Quispe"
- career: "Ingeniería de Software"
- email: "yoselin.flores@tecsup.edu.pe"
- faculty: "Tecnología de la Información"
- bio: "Estudiante destacada apasionada por el desarrollo de aplicaciones móviles con Jetpack Compose."
- imageRes: R.drawable.profile_user

---

### 5. ESPECIFICACIÓN DETALLADA DE PANTALLAS

#### A. LoginScreen.kt ("Portal Académico")
- Fondo completo: ScreenBackground (#F3EDF7).
- Card central: containerColor = LoginCardGray (#D3CBDC - plomo lavanda un poco más oscuro), elevation = 4.dp, shape = RoundedCornerShape(24.dp), padding interno = 24.dp, ancho al 90% de la pantalla.
- Contenido centrado dentro de la tarjeta:
  1. Text("Portal Académico", style = MaterialTheme.typography.headlineMedium, color = PrimaryPurple, fontWeight = FontWeight.Bold)
  2. Text("Accede a tu cuenta", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
  3. OutlinedTextField para correo ("Correo Institucional", leadingIcon = Icons.Default.Email).
  4. OutlinedTextField para contraseña ("Contraseña", leadingIcon = Icons.Default.Lock, trailingIcon con toggle de visibilidad).
  5. Button("INICIAR SESIÓN", containerColor = PrimaryPurple, contentColor = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(50.dp)). Al hacer clic navega a Screen.Home.route.
  6. TextButton("¿Olvidaste tu contraseña?", color = TextMuted).

#### B. HomeScreen.kt ("Bienvenida, Yoselin Fabiola")
- FONDO DEGRADADO VERTICAL: Usar un degradado vertical que vaya de Morado (PrimaryPurple) al fondo claro (ScreenBackground). PROHIBIDO USAR TONOS AMARILLOS O DORADOS EN EL FONDO DE ESTA PANTALLA.
  `Brush.verticalGradient(colors = listOf(PrimaryPurple, ScreenBackground))`
- POSICIÓN Y TEXTOS DEL ENCABEZADO (Ubicación más abajo hacia la mitad superior):
  - Añadir un Spacer(modifier = Modifier.height(180.dp)) superior antes del bloque de texto para situar el mensaje de bienvenida más abajo, casi llegando a la mitad superior de la pantalla.
  - El texto del saludo DEBE estar dividido estrictamente en dos líneas en vertical:
    - Línea 1: Text("Bienvenida,", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
    - Línea 2: Text("Yoselin Fabiola", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
  - Spacer(modifier = Modifier.height(16.dp)).
  - Text 3: "¿Qué deseas gestionar hoy?" (fontSize = 15.sp, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center).
  - Todo el bloque de textos debe estar centrado horizontalmente (Alignment.CenterHorizontally).
- 2 TARJETAS BLANCAS (WhiteCard, shape = RoundedCornerShape(20.dp), elevation = 4.dp):
  - Tarjeta 1 ("Directorio de Alumnos"): Box cuadrado SoftIconPurple con Icons.Default.Group morado + Text "Directorio de Alumnos" (bold, TextDark) + Text "Ver y gestionar estudiantes" (TextMuted). Navega a Screen.List.route.
  - Tarjeta 2 ("Mi Perfil Académico"): Box cuadrado SoftIconPurple con Icons.Default.Person morado + Text "Mi Perfil Académico" + Text "Datos personales y progreso". Navega a Screen.Profile.route.
- PARTE INFERIOR:
  - Row centrado en la parte inferior con Icon(Icons.Default.ExitToApp, tint = TextRed) y Text("Cerrar Sesión Segura", color = TextRed, fontWeight = FontWeight.Bold). Navega a Screen.Login.route limpiando la pila.

#### C. ProfileScreen.kt ("Configuración de Perfil")
- TopAppBar: Fondo transparente/ScreenBackground, título "Configuración de Perfil" en TextDark bold, botón de retorno en TextDark.
- CABECERA SUPERIOR DEGRADADA (Corrección de Gradiente Horizontal):
  - Box superior de altura 170.dp con degradado HORIZONTAL que va desde morado en la izquierda hasta un matiz cálido/salmón tenue en la derecha:
    `Brush.horizontalGradient(colors = listOf(HeaderGradientStart, Color(0xFF6E4D85), HeaderGradientEnd))`
  - Avatar circular al centro con borde blanco y debajo el nombre en texto blanco centrado: "Yoselin Fabiola Flores Quispe" (fontSize = 20.sp, fontWeight = FontWeight.Bold).
- Contenido por secciones sobre fondo ScreenBackground:
  - Subtítulo "INFORMACIÓN PERSONAL" (PrimaryPurple, uppercase, bold, fontSize = 13.sp, padding bottom 8.dp).
  - Ítems con Box SoftIconPurple e íconos en IconDarkGray (#49454F):
    1. Icon(Icons.Default.Person) | Label "Nombre Completo" (TextMuted) / Valor "Yoselin Fabiola Flores Quispe" (TextDark bold)
    2. Icon(Icons.Default.Email) | Label "Correo" (TextMuted) / Valor "yoselin.flores@tecsup.edu.pe" (TextDark bold)
    3. Icon(Icons.Default.Phone) | Label "Teléfono" (TextMuted) / Valor "+51 987 654 321" (TextDark bold)
  - Spacer(16.dp)
  - Subtítulo "ACADÉMICO" (PrimaryPurple, uppercase, bold, fontSize = 13.sp, padding bottom 8.dp).
  - Ítems:
    1. Icon(Icons.Default.School) | Label "Carrera" (TextMuted) / Valor "Ingeniería de Software" (TextDark bold)
    2. Icon(Icons.Default.DateRange) | Label "Ciclo Actual" (TextMuted) / Valor "VI Ciclo" (TextDark bold)
- POSICIÓN DEL BOTÓN CERRAR SESIÓN: Colocar un Spacer(modifier = Modifier.weight(1f)) antes del botón para empujarlo completamente hacia la parte inferior de la pantalla.
- Botón inferior: Card con containerColor = SoftRedBg (#FCE8E6), shape = RoundedCornerShape(20.dp), fillMaxWidth, height = 50.dp.
  - Contenido interno centrado: Icon(Icons.Default.ExitToApp, tint = TextRed) + Text("Cerrar Sesión", color = TextRed, fontWeight = FontWeight.Bold). Navega a LoginScreen limpiando la pila.

#### D. DetailScreen.kt ("Expediente Académico")
- TopAppBar: Fondo ScreenBackground, botón de retroceso (PrimaryPurple), título "Expediente Académico" en PrimaryPurple en negrita.
- CABECERA SUPERIOR: Usar MORADO SÓLIDO (PrimaryPurple = #5B4193). NO USAR NINGÚN DEGRADADO EN ESTA CABECERA.
  - Box de color PrimaryPurple, altura 140.dp, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp).
- Avatar circular superpuesto al centro (size = 110.dp, clip(CircleShape), borde blanco de 3.dp).
- Debajo del avatar (centrado):
  - Text(student.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
  - Text(student.career, fontSize = 15.sp, color = PrimaryPurple)
- Tarjeta contenedora de datos:
  - containerColor = CardGray (#E1DAE8), shape = RoundedCornerShape(20.dp), padding = 16.dp.
  - Fila 1: Box SoftIconPurple con Icon(Icons.Default.DateRange, tint = IconDarkGray) | Label "ID Estudiante" (TextMuted), Valor student.id (TextDark bold).
  - Fila 2: Box SoftIconPurple con Icon(Icons.Default.Email, tint = IconDarkGray) | Label "Correo Electrónico" (TextMuted), Valor student.email (TextDark bold).
  - Fila 3: Box SoftIconPurple con Icon(Icons.Default.School, tint = IconDarkGray) | Label "Facultad" (TextMuted), Valor student.faculty (TextDark bold).
  - HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 12.dp))
  - Text("Biografía", fontWeight = FontWeight.Bold, color = TextDark)
  - Text(student.bio, color = TextMuted, fontSize = 14.sp)

#### E. ListScreen.kt ("Directorio de Alumnos")
- TopAppBar: Fondo ScreenBackground, título "Directorio de Alumnos" en PrimaryPurple negrita.
- LazyColumn con padding = 16.dp cargando la lista de alumnos.
- Tarjetas de alumnos:
  - containerColor = CardGray (#E1DAE8), shape = RoundedCornerShape(16.dp), elevation = 0.dp.
  - Aumentar el alto vertical agregando padding interno amplio: modifier.padding(vertical = 12.dp, horizontal = 16.dp).
  - Avatar circular (52.dp, clip(CircleShape)) a la izquierda.
  - Nombre del alumno (fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark).
  - Carrera del alumno: OBLIGATORIO usar color MORADO/LILA (PrimaryPurple = #5B4193). NO USAR GRIS. Ejemplo exacto: Text(text = student.career, fontSize = 14.sp, color = PrimaryPurple).
  - Ícono ChevronRight a la derecha con tint = IconDarkGray (#49454F).
  - Al hacer clic en la tarjeta, navega a Screen.Detail.route pasando el studentId.

---

### 6. NAVEGACIÓN Y MAINACTIVITY
- `navigation/Screen.kt`: Define `object Login`, `object Home`, `object List`, `object Profile`, y `object Detail : Screen("detail/{studentId}")`.
- `navigation/AppNavigation.kt`: Implementa `NavHost` con `rememberNavController()`, configurando todas las rutas y la extracción de argumentos para `DetailScreen`.
- `MainActivity.kt`: Extiende `ComponentActivity`, establece el tema `PortalAcademicoTheme` dentro de `setContent` e invoca `AppNavigation()`.

---

### REGLAS OBLIGATORIAS DE ENTREGA
1. Genera TODO el código fuente en Kotlin de forma completa e individual para cada uno de los 11 archivos mencionados.
2. Incluye todas las directivas import necesarias (`androidx.compose.*`, `androidx.navigation.compose.*`, `androidx.compose.material.icons.*`, etc.).
3. No resumas ningún archivo ni dejes comentarios incompletos o atajos como `// Resto del código...`.
