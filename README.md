# Programacion-Movil-Android
Repositorio de laboratorios y proyectos del curso de Desarrollo de Aplicaciones Móviles en Android.

# SEMANA O2:
## Preguntas de Análisis - Parte 2

### 1. ¿Por qué `nombre` y `precio` son `val` pero `cantidad` es `var`?

* **`val` (Inmutable):** Define propiedades de solo lectura. El nombre de un producto y su precio unitario son datos fijos del catálogo que no deben ser modificados por error durante el proceso de compra.
* **`var` (Mutable):** Define propiedades cuyo valor puede cambiar. La cantidad de artículos elegidos por el usuario es dinámica, ya que este puede agregar o quitar unidades del carrito en cualquier momento.

---

### 2. ¿Qué pasaría si intentas cambiar el precio después de crear el producto?

El compilador de Kotlin lanzará un error de compilación inmediato:

> `Val cannot be reassigned`

El programa no se ejecutará, lo cual garantiza la integridad de los datos al impedir que los precios se alteren de forma no autorizada o accidental en memoria.
