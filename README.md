# Programacion-Movil-Android
Repositorio de laboratorios y proyectos del curso de Desarrollo de Aplicaciones Móviles en Android.

---

# SEMANA 02: Laboratorio - Carrito de Compras en Kotlin

**Estudiante:** Yoselin Flores  
**Curso:** Programación Móvil Android - Tecsup  

---

## 📝 Descripción del Programa

Programa desarrollado en **Kotlin** para simular la gestión de un carrito de compras interactivo por consola. Utiliza un modelo de datos (`data class`) y un conjunto de funciones modulares para procesar los ítems, calcular subtotal, IGV (18%), descuentos por volumen de compra, imprimir el reporte en formato tabular alineado, además de implementar la búsqueda y eliminación dinámica de productos.

### 🛠️ Funciones Implementadas:
* `calcularSubtotal(productos: List<Producto>): Double`: Calcula la suma de multiplicaciones de `precio * cantidad` para cada ítem.
* `calcularIGV(subtotal: Double): Double`: Calcula el 18% del subtotal.
* `calcularTotal(subtotal: Double, igv: Double): Double`: Devuelve la suma del subtotal más el IGV.
* `calcularDescuento(total: Double): Double`: Evalúa mediante la estructura `when` si aplica un 5% (si el total supera S/ 3000) o un 10% (si supera S/ 5000).
* `mostrarDetalle(productos: List<Producto>)`: Muestra el detalle del carrito ordenado en formato de tabla alineada con `String.format`.
* `buscarProducto(productos: List<Producto>, nombre: String): Producto?`: *(Reto Adicional)* Utiliza `.find` para localizar un producto por nombre y retornar el objeto o `null`.
* `carrito.removeIf`: *(Reto Adicional)* Permite remover un ítem mutable del carrito y recalcular los totales actualizados.

---

## ❓ Preguntas de Análisis - Parte 2

### 1. ¿Por qué `nombre` y `precio` son `val` pero `cantidad` es `var`?

* **`val` (Inmutable):** Define propiedades de solo lectura. El nombre de un producto y su precio unitario son datos fijos del catálogo que no deben ser modificados por error durante el proceso de compra.
* **`var` (Mutable):** Define propiedades cuyo valor puede cambiar. La cantidad de artículos elegidos por el usuario es dinámica, ya que este puede agregar o quitar unidades del carrito en cualquier momento.

---

### 2. ¿Qué pasaría si intentas cambiar el precio después de crear el producto?

El compilador de Kotlin lanzará un error de compilación inmediato:

> `Val cannot be reassigned`

El programa no se ejecutará, lo cual garantiza la integridad de los datos al impedir que los precios se alteren de forma no autorizada o accidental en memoria.

---

## 📸 Captura de Consola

<img width="527" height="875" alt="image" src="https://github.com/user-attachments/assets/c4632476-96e5-4c86-88d9-1b2ca7f21f0d" />
<img width="547" height="452" alt="image" src="https://github.com/user-attachments/assets/cdb01b1f-a36a-4b27-add1-f78748ca790c" />
