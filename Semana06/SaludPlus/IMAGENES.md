# Imágenes de SaludPlus

Las imágenes se buscan **por nombre** con `ImagenPorNombre` (`ui/components/ImagenPorNombre.kt`).
El nombre sale del texto que se ve en la interfaz con `nombreRecurso`: minúsculas, sin tildes, sin ñ,
sin puntos y con `_` entre palabras. Ejemplos: "Medicina General" → `medicina_general`,
"Dra. Ana Torres" → `dra_ana_torres`, "Dr. Ricardo Núñez" → `dr_ricardo_nunez`.

Todas están en `app/src/main/res/drawable`. Al reemplazar una imagen **el código no cambia**.

## Estado actual

- Las **7 imágenes de especialidades** ya existen (`.png` circulares de 256x256 con su fondo pastel).
- Los **12 médicos ya tienen foto real** (`.png` circulares de 256x256 con fondo transparente):
  4 del diseño (`dra_ana_torres`, `dra_claudia_rojas`, `dr_luis_ramirez`, `dra_mariana_soto`) y 8 descargadas
  de Pexels en la Fase 2 (ver **Créditos**). Ya no quedan placeholders `.xml` de médicos.

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
| `dr_carlos_mendoza.png` | Foto de Dr. Carlos Mendoza en Médicos, Fecha y hora y Confirmar | Imagen real (Pexels, ver Créditos) |
| `dra_lucia_vargas.png` | Foto de Dra. Lucía Vargas en Médicos, Fecha y hora y Confirmar | Imagen real (Pexels, ver Créditos) |
| `dra_sofia_paredes.png` | Foto de Dra. Sofía Paredes en Médicos, Fecha y hora y Confirmar | Imagen real (Pexels, ver Créditos) |
| `dr_jorge_salas.png` | Foto de Dr. Jorge Salas en Médicos, Fecha y hora y Confirmar | Imagen real (Pexels, ver Créditos) |
| `dr_ricardo_nunez.png` | Foto de Dr. Ricardo Núñez en Médicos, Fecha y hora y Confirmar | Imagen real (Pexels, ver Créditos) |
| `dra_patricia_leon.png` | Foto de Dra. Patricia León en Médicos, Fecha y hora y Confirmar | Imagen real (Pexels, ver Créditos) |
| `dr_andres_quispe.png` | Foto de Dr. Andrés Quispe en Médicos, Fecha y hora y Confirmar | Imagen real (Pexels, ver Créditos) |
| `dra_elena_campos.png` | Foto de Dra. Elena Campos en Médicos, Fecha y hora y Confirmar | Imagen real (Pexels, ver Créditos) |

## Cómo reemplazar un placeholder de médico

1. Borra el `.xml` del mismo nombre en `app/src/main/res/drawable` (por ejemplo `dr_jorge_salas.xml`).
2. Copia la foto real (`.png`, `.jpg` o `.webp`) con el **mismo nombre en minúsculas** (`dr_jorge_salas.png`).
3. Si quedan los dos archivos, Android da el error **"Duplicate resources"**.

Para cambiar una imagen que ya es real, reemplaza el archivo por otro con el mismo nombre
(si cambias la extensión, borra el anterior). Si una especialidad no tuviera imagen, la app dibuja
un círculo pastel con su ícono.

## Créditos

Las 8 fotos se descargaron de [Pexels](https://www.pexels.com). La [licencia de Pexels](https://www.pexels.com/license/)
permite el uso gratuito, también comercial, sin atribución obligatoria; se dejan los créditos por cortesía.
Cada foto se recortó en cuadrado centrado en el rostro, se redujo a 256x256 px y se le aplicó una máscara
circular con fondo transparente (Python + Pillow).

| Archivo | Página de origen | Fotógrafo | Licencia |
|---|---|---|---|
| `dra_lucia_vargas.png` | https://www.pexels.com/photo/a-woman-wearing-a-stethoscope-6749773/ | Antoni Shkraba | Licencia de Pexels |
| `dra_sofia_paredes.png` | https://www.pexels.com/photo/smiling-doctor-with-a-stethoscope-around-her-neck-18828741/ | Tessy Agbonome | Licencia de Pexels |
| `dra_patricia_leon.png` | https://www.pexels.com/photo/portrait-of-doctor-15752232/ | Yasin Aydın | Licencia de Pexels |
| `dra_elena_campos.png` | https://www.pexels.com/photo/portrait-of-smiling-black-woman-doctor-in-medical-robe-19596247/ | MARTINS JOHN | Licencia de Pexels |
| `dr_carlos_mendoza.png` | https://www.pexels.com/photo/a-doctor-wearing-a-white-coat-8460090/ | Los Muertos Crew | Licencia de Pexels |
| `dr_jorge_salas.png` | https://www.pexels.com/photo/man-in-white-coat-4989142/ | Ivan S | Licencia de Pexels |
| `dr_ricardo_nunez.png` | https://www.pexels.com/photo/middle-aged-doctor-in-white-apron-5531446/ | Kevin Steven Ortega Eliett | Licencia de Pexels |
| `dr_andres_quispe.png` | https://www.pexels.com/photo/doctor-19438560/ | Oys Photography | Licencia de Pexels |

## Fase 3: fotos de los médicos nuevos y de las sedes

Descargadas de [Pexels](https://www.pexels.com) (Licencia de Pexels: uso gratuito sin atribución obligatoria; el
autor aparece en cada enlace). Los médicos van recortados a 256x256 (`.png`); las sedes a 600x400 (`.jpg`) con el
nombre `sede_` + nombre de la sede (`nombreRecurso`). Si falta una foto de sede, se dibuja un recuadro crema con el ícono.

| Archivo | Pexels |
|---|---|
| `dr_miguel_herrera.png` | https://www.pexels.com/photo/6129500/ |
| `dra_valeria_cruz.png` | https://www.pexels.com/photo/7904457/ |
| `dra_camila_ortega.png` | https://www.pexels.com/photo/32254667/ |
| `dr_fernando_diaz.png` | https://www.pexels.com/photo/29995617/ |
| `dra_rosa_medina.png` | https://www.pexels.com/photo/7578811/ |
| `dr_hector_rivas.png` | https://www.pexels.com/photo/6762862/ |
| `dra_natalia_silva.png` | https://www.pexels.com/photo/32115905/ |
| `dr_oscar_benitez.png` | https://www.pexels.com/photo/19438563/ |
| `dr_daniel_flores.png` | https://www.pexels.com/photo/15962798/ |
| `sede_santa_anita.jpg` | https://www.pexels.com/photo/6473188/ |
| `sede_ate.jpg` | https://www.pexels.com/photo/9741531/ |
| `sede_la_molina.jpg` | https://www.pexels.com/photo/36938793/ |
| `sede_san_isidro.jpg` | https://www.pexels.com/photo/20242798/ |

`banner_clinica.jpg` (banner vertical del Inicio): https://www.pexels.com/photo/14438789/ (Pexels).
