# Portal Académico - Android Jetpack Compose (Semana05-Laboratorio-IA)

Este repositorio contiene la implementación del **Portal Académico**, una aplicación móvil desarrollada completamente con **Jetpack Compose**, **Material Design 3** y **Kotlin** (100% libre de archivos XML de diseño).

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Navegación:** Navigation Compose
- **Arquitectura:** Single-Activity Architecture con Vistas Modulares

---
## 🖼️ Importación y Asignación de Imágenes de Perfil

### Instrucciones para Agregar las Imágenes en Android Studio

1. **Preparación de archivos:** Copia tus archivos de imagen desde tu computadora (`.png`, `.jpg` o `.webp`).
   > **Importante:** Los nombres deben estar estrictamente en minúsculas, sin espacios ni caracteres especiales (ejemplo: `profile_user.png`, `student_1.png`, `student_2.png`, etc.).
2. **Abrir el proyecto:** Abre el proyecto en Android Studio.
3. **Ubicación del recurso:** En el explorador lateral izquierdo, navega hasta la carpeta:
   `app/src/main/res/drawable`
4. **Pegar imágenes:** Haz clic derecho sobre la carpeta `drawable` y selecciona **Paste** (o presiona `Ctrl + V` / `Cmd + V`).
5. **Confirmación:** En la ventana emergente, confirma que el destino sea la carpeta `drawable` y presiona **OK**.

## 📝 Prompt Utilizado

```text
Actúa como desarrollador Senior Android experto en Jetpack Compose, Material Design 3 y Kotlin.

Crea la aplicación e interfaz completa del "Portal Académico" adaptada a la estructura base del proyecto actual. Todo debe construirse 100% en Jetpack Compose (sin layouts XML de diseño). El código generado debe ser totalmente funcional, limpio, ejecutable y autosuficiente.

====================================================================
1. REGLAS ESTRUCTURALES Y DE ARCHIVOS (ESTRICTAS)
====================================================================
Usa el paquete base del proyecto (ej. com.example.semana05_navegacion o el equivalente según la app):

[paquete_base]/
├── navigation/
│   ├── Screen.kt
│   └── AppNavigation.kt
├── screens/
│   ├── LoginScreen.kt
│   ├── HomeScreen.kt
│   ├── ListScreen.kt
│   ├── DetailScreen.kt
│   └── ProfileScreen.kt
├── ui.theme/
│   ├── Color.kt
│   └── Theme.kt
└── MainActivity.kt

RESTRICCIONES OBLIGATORIAS:
- QUEDA PROHIBIDO crear la carpeta/paquete 'model' o subcarpetas dentro de 'screens'.
- La 'data class Student' y la lista estática 'sampleStudents' deben incluirse directamente en 'screens/ListScreen.kt'.
- NO usar la carpeta 'mipmap'. En el AndroidManifest.xml, los atributos 'android:icon' y 'android:roundIcon' deben configurarse a '@drawable/profile_user' para evitar referencias nulas.

====================================================================
2. RECURSOS DE IMÁGENES (res/drawable)
====================================================================
Acceder a las imágenes mediante painterResource con los siguientes identificadores en 'res/drawable':
- R.drawable.profile_user
- R.drawable.student_1
- R.drawable.student_2
- R.drawable.student_3
- R.drawable.student_4
- R.drawable.student_5

====================================================================
3. PALETA DE COLORES Y TEMA (ui.theme/Color.kt)
====================================================================
Define las siguientes constantes de color en 'ui.theme/Color.kt':
- val PrimaryPurple = Color(0xFF5B4193)       // Morado principal
- val HeaderGradientStart = Color(0xFF5B4193) // Degradado inicio
- val HeaderGradientEnd = Color(0xFF886378)   // Degradado fin
- val ScreenBackground = Color(0xFFF3EDF7)    // Fondo pantalla
- val LoginCardGray = Color(0xFFD3CBDC)       // Fondo tarjeta Login
- val CardGray = Color(0xFFE1DAE8)            // Tarjetas de lista y expediente
- val WhiteCard = Color(0xFFFFFFFF)           // Tarjetas blancas en Home
- val SoftIconPurple = Color(0xFFEADDFF)      // Contenedor claro para íconos
- val IconDarkGray = Color(0xFF49454F)        // Íconos secundarios
- val TextDark = Color(0xFF1C1B1F)            // Texto principal
- val TextMuted = Color(0xFF524F58)           // Subtextos
- val SoftRedBg = Color(0xFFFCE8E6)           // Fondo botón cerrar sesión
- val TextRed = Color(0xFFB3261E)             // Texto/ícono cerrar sesión

En 'ui.theme/Theme.kt', configura el tema desactivando 'dynamicColor' (dynamicColor = false).

====================================================================
4. MODELO Y DATOS (screens/ListScreen.kt)
====================================================================
Dentro de ListScreen.kt declara:

data class Student(
    val id: String,
    val name: String,
    val career: String,
    val email: String,
    val faculty: String,
    val bio: String,
    val imageRes: Int
)

Crea 'sampleStudents' con 5 alumnos:
1. id: "2024-0091", name: "Carlos Eduardo Mendoza", career: "Ingeniería de Software", email: "carlos.mendoza@tecsup.edu.pe", faculty: "Tecnología de la Información", bio: "Estudiante apasionado por el desarrollo móvil.", imageRes: R.drawable.student_1
2. id: "2024-0092", name: "María Fernanda Torres", career: "Diseño y Desarrollo de Software", email: "maria.torres@tecsup.edu.pe", faculty: "Tecnología de la Información", bio: "Especialista en experiencia de usuario UI/UX.", imageRes: R.drawable.student_2
3. id: "2024-0093", name: "Juan Pedro Gómez", career: "Redes y Comunicaciones", email: "juan.gomez@tecsup.edu.pe", faculty: "Ingeniería", bio: "Entusiasta de la ciberseguridad e infraestructura.", imageRes: R.drawable.student_3
4. id: "2024-0094", name: "Ana Lucía Benítez", career: "Big Data y Analítica", email: "ana.benitez@tecsup.edu.pe", faculty: "Tecnología de la Información", bio: "Interesada en analítica de datos e IA.", imageRes: R.drawable.student_4
5. id: "2024-0095", name: "Diego Alonso Ruiz", career: "Ingeniería de Software", email: "diego.ruiz@tecsup.edu.pe", faculty: "Tecnología de la Información", bio: "Desarrollador backend explorando soluciones nativas.", imageRes: R.drawable.student_5

====================================================================
5. ESPECIFICACIÓN DE PANTALLAS
====================================================================

A. LoginScreen.kt
- Fondo: ScreenBackground.
- Card flotante central: containerColor = LoginCardGray, shape = RoundedCornerShape(24.dp).
- Título "Portal Académico" en PrimaryPurple.
- Campos OutlinedTextField para usuario y contraseña.
- Botón "INICIAR SESIÓN" (PrimaryPurple) que navega a Screen.Home.route.

B. HomeScreen.kt
- Fondo: Brush.verticalGradient(listOf(PrimaryPurple, ScreenBackground)).
- Spacer superior de 180.dp.
- Mensaje: "Bienvenida," y "Yoselin Fabiola" en texto blanco y negrita.
- Subtítulo: "¿Qué deseas gestionar hoy?".
- 2 tarjetas WhiteCard con acceso a "Directorio de Alumnos" (Screen.List.route) y "Mi Perfil Académico" (Screen.Profile.route).
- Botón inferior "Cerrar Sesión Segura" en TextRed.

C. ProfileScreen.kt
- TopAppBar transparente con botón de retorno.
- Header con Brush.horizontalGradient(listOf(HeaderGradientStart, Color(0xFF6E4D85), HeaderGradientEnd)).
- Avatar de R.drawable.profile_user, nombre "Yoselin Fabiola Flores Quispe" y carrera.
- Secciones "INFORMACIÓN PERSONAL" y "ACADÉMICO".
- Botón "Cerrar Sesión" al fondo con SoftRedBg y TextRed.

D. DetailScreen.kt
- Recibe studentId como parámetro.
- TopAppBar en ScreenBackground con retroceso.
- Bloque superior en morado sólido (PrimaryPurple) de 140.dp de alto.
- Avatar circular superpuesto (110.dp) con borde blanco.
- Tarjeta CardGray con ID, Correo, Facultad y Biografía.

E. ListScreen.kt
- TopAppBar "Directorio de Alumnos" en PrimaryPurple.
- LazyColumn con padding 16.dp.
- Tarjetas CardGray con bordes redondeados.
- Foto a la izquierda, Nombre en negrita y Carrera resaltada en PrimaryPurple.
- Clic en la tarjeta navega a Screen.Detail.createRoute(student.id).

====================================================================
6. NAVEGACIÓN Y MAINACTIVITY
====================================================================
- navigation/Screen.kt: Define sealed class Screen con las rutas login, home, list, profile, detail/{studentId}.
- navigation/AppNavigation.kt: Configura el NavHost con navController iniciando en Screen.Login.route.
- MainActivity.kt: Configura setContent { PortalAcademicoTheme { AppNavigation() } }.
