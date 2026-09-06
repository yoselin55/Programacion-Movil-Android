
# 📱 Registro de Notas - Android App

Aplicación móvil desarrollada en **Android** con **Jetpack Compose** (Material Design 3) que permite calcular el promedio ponderado de cuatro cursos del ciclo académico, mostrando un desglose detallado del aporte de cada asignatura y su estado final.

---

## 📸 Captura de Pantalla

> [!NOTE]
> Reemplaza `captura.png` por la ruta o nombre exacto de la imagen que subas a tu repositorio.

<p center="align">
  <img width="475" height="952" alt="image" src="https://github.com/user-attachments/assets/65692590-5a98-4986-a70a-673f0d896ab0" /> <img width="500" height="967" alt="image" src="https://github.com/user-attachments/assets/5843aad9-e9d5-470e-adad-4f96f23af14f" />

</p>

---

## ✨ Características Principales

* **Sliders con Semáforo de Calificaciones:** Ajuste dinámico de notas de 0 a 20 con un badge de color intuitivo (Verde para notas $\ge 13$ y Rojo para notas $< 13$).
* **Cálculo Ponderado Dinámico:** Distribución del peso académico por asignatura:
  * **Fundamentos de Programación:** 20%
  * **Programación Orientada a Objetos:** 25%
  * **Programación en Móviles:** 30%
  * **Base de Datos:** 25%
* **Switch de Redondeo:** Opción para alternar entre el promedio exacto con decimales y el entero redondeado en tiempo real.
* **Confirmación de Datos:** Checkbox de seguridad que habilita el botón de cálculo tras verificar las notas.
* **Botón LIMPIAR:** Restablece instantáneamente todos los valores del formulario a cero.
* **Tarjeta de Resultados:** Muestra el cálculo matemático transparente (nota × porcentaje) junto con un indicador de estado (*EXCELENTE*, *APROBADO*, *EN RECUPERACIÓN* o *DESAPROBADO*).
* **Diseño Responsivo:** Implementación con `Scaffold` para mantener la barra superior fija mientras el contenido se desplaza fluidamente sin recortar información.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Componentes clave:** `Scaffold`, `TopAppBar`, `Slider`, `Switch`, `Checkbox`, `Card`, `Surface`
* **Gestión de Estado:** `remember`, `mutableFloatStateOf`, `mutableStateOf`

---

## 👤 Autora

**Desarrollado por:** Yoselin Fabiola Flores
