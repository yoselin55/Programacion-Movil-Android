# Imágenes de SaludPlus

Las imágenes se buscan **por nombre** con `ImagenPorNombre` (`ui/components/ImagenPorNombre.kt`).
El nombre sale del texto que se ve en la interfaz con `nombreRecurso`: minúsculas, sin tildes, sin ñ,
sin puntos y con `_` entre palabras. Ejemplos: "Medicina General" → `medicina_general`,
"Dra. Ana Torres" → `dra_ana_torres`, "Dr. Ricardo Núñez" → `dr_ricardo_nunez`.

Todas están en `app/src/main/res/drawable`. Al reemplazar una imagen **el código no cambia**.

## Estado actual

- Las **7 imágenes de especialidades** ya existen (`.png` circulares de 256x256 con su fondo pastel).
- Las **4 fotos reales de médicos** ya existen: `dra_ana_torres`, `dra_claudia_rojas`, `dr_luis_ramirez`, `dra_mariana_soto`.
- Los **otros 8 médicos** usan placeholders `.xml` (círculo azul claro con silueta): `dr_carlos_mendoza`,
  `dra_lucia_vargas`, `dra_sofia_paredes`, `dr_jorge_salas`, `dr_ricardo_nunez`, `dra_patricia_leon`,
  `dr_andres_quispe`, `dra_elena_campos`.

| Archivo | Corresponde en la interfaz a | Estado |
|---|---|---|
| `logo_saludplus.webp` | Logo de la clínica en Splash | Imagen real |
| `ilustracion_doctor.png` | Ilustración del doctor en Splash | Imagen real |
| `medicina_general.png` | Imagen de Medicina General en Inicio y Especialidades | Imagen real |
| `pediatria.png` | Imagen de Pediatría en Inicio y Especialidades | Imagen real |
| `ginecologia.png` | Imagen de Ginecología en Inicio y Especialidades | Imagen real |
| `cardiologia.png` | Imagen de Cardiología en Inicio y Especialidades | Imagen real |
| `dermatologia.png` | Imagen de Dermatología en Inicio y Especialidades | Imagen real |
| `traumatologia.png` | Imagen de Traumatología en Inicio y Especialidades | Imagen real |
| `oftalmologia.png` | Imagen de Oftalmología en Inicio y Especialidades | Imagen real |
| `dra_ana_torres.png` | Foto de Dra. Ana Torres en Médicos, Fecha y hora y Confirmar | Imagen real |
| `dra_claudia_rojas.png` | Foto de Dra. Claudia Rojas en Médicos, Fecha y hora y Confirmar | Imagen real |
| `dr_luis_ramirez.png` | Foto de Dr. Luis Ramírez en Médicos, Fecha y hora y Confirmar | Imagen real |
| `dra_mariana_soto.png` | Foto de Dra. Mariana Soto en Médicos, Fecha y hora y Confirmar | Imagen real |
| `dr_carlos_mendoza.xml` | Foto de Dr. Carlos Mendoza en Médicos, Fecha y hora y Confirmar | Placeholder (reemplazar) |
| `dra_lucia_vargas.xml` | Foto de Dra. Lucía Vargas en Médicos, Fecha y hora y Confirmar | Placeholder (reemplazar) |
| `dra_sofia_paredes.xml` | Foto de Dra. Sofía Paredes en Médicos, Fecha y hora y Confirmar | Placeholder (reemplazar) |
| `dr_jorge_salas.xml` | Foto de Dr. Jorge Salas en Médicos, Fecha y hora y Confirmar | Placeholder (reemplazar) |
| `dr_ricardo_nunez.xml` | Foto de Dr. Ricardo Núñez en Médicos, Fecha y hora y Confirmar | Placeholder (reemplazar) |
| `dra_patricia_leon.xml` | Foto de Dra. Patricia León en Médicos, Fecha y hora y Confirmar | Placeholder (reemplazar) |
| `dr_andres_quispe.xml` | Foto de Dr. Andrés Quispe en Médicos, Fecha y hora y Confirmar | Placeholder (reemplazar) |
| `dra_elena_campos.xml` | Foto de Dra. Elena Campos en Médicos, Fecha y hora y Confirmar | Placeholder (reemplazar) |

## Cómo reemplazar un placeholder de médico

1. Borra el `.xml` del mismo nombre en `app/src/main/res/drawable` (por ejemplo `dr_jorge_salas.xml`).
2. Copia la foto real (`.png`, `.jpg` o `.webp`) con el **mismo nombre en minúsculas** (`dr_jorge_salas.png`).
3. Si quedan los dos archivos, Android da el error **"Duplicate resources"**.

Para cambiar una imagen que ya es real, reemplaza el archivo por otro con el mismo nombre
(si cambias la extensión, borra el anterior). Si una especialidad no tuviera imagen, la app dibuja
un círculo pastel con su ícono.
