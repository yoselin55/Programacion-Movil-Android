package com.flores.myapplication

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)

fun calcularSubtotal(productos: List<Producto>): Double {
    var subtotal = 0.0
    for (p in productos) {
        subtotal += p.precio * p.cantidad
    }
    return subtotal
}

fun calcularIGV(subtotal: Double): Double {
    return subtotal * 0.18
}

fun calcularTotal(subtotal: Double, igv: Double): Double {
    return subtotal + igv
}

fun calcularDescuento(total: Double): Double {
    return when {
        total > 5000 -> total * 0.10
        total > 3000 -> total * 0.05
        else -> 0.0
    }
}

fun mostrarDetalle(productos: List<Producto>) {
    println("--------- DETALLE DEL CARRITO ---------")
    var i = 1
    for (p in productos) {
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d S/ %8.2f", i, p.nombre, p.cantidad, importe))
        i++
    }
    println("---------------------------------------")
}

fun buscarProducto(productos: List<Producto>, nombre: String): Producto? {
    return productos.find { it.nombre.equals(nombre, ignoreCase = true) }
}

fun main() {
    println("=========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP   ")
    println("=========================================")

    val nombreCliente = "Yoselin Flores"
    val carrito = mutableListOf<Producto>()

    println("Cliente: $nombreCliente\n")

    carrito.add(Producto("Laptop HP", 2500.0, 1))
    carrito.add(Producto("Mouse Logitech", 45.5, 2))
    carrito.add(Producto("Audifonos Sony", 120.0, 1))
    carrito.add(Producto("USB Kingston 64GB", 25.0, 3))

    for (producto in carrito) {
        println("Producto agregado: ${producto.nombre}")
    }

    println()

    mostrarDetalle(carrito)
    println("Cantidad de productos: ${carrito.size}\n")

    var subtotal = calcularSubtotal(carrito)
    var igv = calcularIGV(subtotal)
    var total = calcularTotal(subtotal, igv)

    println(String.format("Subtotal : S/ %8.2f", subtotal))
    println(String.format("IGV (18%%): S/ %8.2f", igv))
    println(String.format("TOTAL    : S/ %8.2f", total))
    println()

    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null) {
        println("Producto mas caro: ${masCaro.nombre} " +
                String.format("(S/ %.2f)", masCaro.precio))
    }

    var descuento = calcularDescuento(total)
    var totalConDescuento = total - descuento

    if (descuento > 0.0) {
        val porcentaje = if (total > 5000) "10%" else "5%"
        println(String.format("Descuento (%s): S/ %8.2f", porcentaje, descuento))
        println(String.format("TOTAL CON DESCUENTO: S/ %8.2f", totalConDescuento))
    } else {
        println("Descuento: No aplica")
    }

    println("\n=========================================")
    println("         RETO ADICIONAL - PRUEBAS        ")
    println("=========================================")

    val nombreBusqueda = "Audifonos Sony"
    println("\n---> Buscando producto: '$nombreBusqueda'")
    val productoEncontrado = buscarProducto(carrito, nombreBusqueda)
    if (productoEncontrado != null) {
        println("Resultado: Encontrado -> ${productoEncontrado.nombre} (S/ ${productoEncontrado.precio})")
    } else {
        println("Resultado: El producto '$nombreBusqueda' no existe.")
    }

    val nombreEliminar = "Mouse Logitech"
    println("\n---> Eliminando producto: '$nombreEliminar'")
    val seElimino = carrito.removeIf { it.nombre.equals(nombreEliminar, ignoreCase = true) }

    if (seElimino) {
        println("El producto '$nombreEliminar' fue eliminado con exito.\n")
    } else {
        println("No se encontro el producto '$nombreEliminar' para eliminar.\n")
    }

    println(">>> DETALLE ACTUALIZADO DEL CARRITO <<<")
    mostrarDetalle(carrito)
    println("Cantidad de productos actualizada: ${carrito.size}\n")

    subtotal = calcularSubtotal(carrito)
    igv = calcularIGV(subtotal)
    total = calcularTotal(subtotal, igv)
    descuento = calcularDescuento(total)
    totalConDescuento = total - descuento

    println(String.format("Subtotal Actualizado : S/ %8.2f", subtotal))
    println(String.format("IGV (18%%) Actualizado : S/ %8.2f", igv))
    println(String.format("TOTAL Actualizado    : S/ %8.2f", total))

    if (descuento > 0.0) {
        val porcentaje = if (total > 5000) "10%" else "5%"
        println(String.format("Descuento (%s)        : S/ %8.2f", porcentaje, descuento))
        println(String.format("TOTAL FINAL          : S/ %8.2f", totalConDescuento))
    } else {
        println("Descuento            : No aplica (Total menor a S/ 3000.00)")
    }
}