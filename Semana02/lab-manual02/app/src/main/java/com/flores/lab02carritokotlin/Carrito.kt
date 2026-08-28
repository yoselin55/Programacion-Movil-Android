package com.flores.lab02carritokotlin

import com.flores.lab02carritokotlin.model.Carrito
import com.flores.lab02carritokotlin.model.ProductoAccesorio
import com.flores.lab02carritokotlin.model.ProductoElectronico
import com.flores.lab02carritokotlin.model.ProductoImportado
import java.util.Locale

private const val PALABRA_CANCELAR = "cancelar"
private const val SUFIJO_CANCELAR = " (o '$PALABRA_CANCELAR' para volver al menu)"

fun main() {
    println("=========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP   ")
    println("=========================================")

    val nombreCliente = leerTextoNoVacio("Ingrese el nombre del cliente", permitirCancelar = false)
        ?: "Cliente sin nombre"
    println("Cliente: $nombreCliente\n")

    val carrito = Carrito()
    var continuar = true

    while (continuar) {
        mostrarMenu()
        when (leerOpcionMenu()) {
            1 -> agregarProducto(carrito)
            2 -> {
                println()
                carrito.mostrarDetalle()
                println()
            }
            3 -> buscarProductoInteractivo(carrito)
            4 -> eliminarProductoInteractivo(carrito)
            5 -> mostrarTotales(carrito)
            6 -> {
                println("\nGracias por su visita, $nombreCliente. Hasta pronto!")
                continuar = false
            }
            else -> println("\nOpcion invalida: ingrese un numero del 1 al 6.\n")
        }
    }
}

private fun mostrarMenu() {
    println("========== MENU CARRITO ==========")
    println("1) Agregar producto")
    println("2) Ver detalle del carrito")
    println("3) Buscar producto por nombre")
    println("4) Eliminar producto por nombre")
    println("5) Ver totales")
    println("6) Salir")
    println("===================================")
}

private fun leerOpcionMenu(): Int? {
    print("Seleccione una opcion: ")
    return readLine()?.trim()?.toIntOrNull()
}

private fun agregarProducto(carrito: Carrito) {
    println("\n--- Agregar producto ---")
    println("Tipos disponibles: 1) Electronico   2) Accesorio   3) Importado")

    val tipo = leerOpcionEnRango("Seleccione el tipo de producto", 1..3) ?: run {
        println("Operacion cancelada: no se agrego ningun producto.\n")
        return
    }

    val nombre = leerTextoNoVacio("Nombre del producto") ?: run {
        println("Operacion cancelada: no se agrego ningun producto.\n")
        return
    }

    val precio = leerDoublePositivo("Precio unitario") ?: run {
        println("Operacion cancelada: no se agrego ningun producto.\n")
        return
    }

    val cantidad = leerEnteroPositivo("Cantidad") ?: run {
        println("Operacion cancelada: no se agrego ningun producto.\n")
        return
    }

    val producto = when (tipo) {
        1 -> ProductoElectronico.crear(nombre, precio, cantidad)
        2 -> ProductoAccesorio.crear(nombre, precio, cantidad)
        else -> ProductoImportado.crear(nombre, precio, cantidad)
    }

    if (producto != null) {
        carrito.agregar(producto)
        println("Producto agregado correctamente: ${producto.nombre}\n")
    } else {
        println("No se pudo agregar el producto: los datos no pasaron la validacion.\n")
    }
}

private fun buscarProductoInteractivo(carrito: Carrito) {
    if (carrito.estaVacio()) {
        println("\nEl carrito esta vacio. No hay productos para buscar.\n")
        return
    }

    val nombre = leerTextoNoVacio("\nNombre del producto a buscar") ?: run {
        println("Busqueda cancelada.\n")
        return
    }

    val encontrado = carrito.buscarProducto(nombre)
    if (encontrado != null) {
        println("Resultado: Encontrado -> ${encontrado.nombre} (S/ ${encontrado.precio}) x${encontrado.cantidad}\n")
    } else {
        println("Resultado: no existe ningun producto llamado '$nombre' en el carrito.\n")
    }
}

private fun eliminarProductoInteractivo(carrito: Carrito) {
    if (carrito.estaVacio()) {
        println("\nEl carrito esta vacio. No hay productos para eliminar.\n")
        return
    }

    val nombre = leerTextoNoVacio("\nNombre del producto a eliminar") ?: run {
        println("Eliminacion cancelada.\n")
        return
    }

    val seElimino = carrito.eliminarProducto(nombre)
    if (seElimino) {
        println("El producto '$nombre' fue eliminado con exito.\n")
    } else {
        println("No se encontro ningun producto llamado '$nombre' para eliminar.\n")
    }
}

private fun mostrarTotales(carrito: Carrito) {
    if (carrito.estaVacio()) {
        println("\nEl carrito esta vacio. No hay totales que mostrar.\n")
        return
    }

    val subtotal = carrito.calcularSubtotal()
    val igv = carrito.calcularIGV(subtotal)
    val total = carrito.calcularTotal(subtotal, igv)
    val descuento = carrito.calcularDescuento(total)
    val totalConDescuento = total - descuento

    println()
    println(String.format(Locale.getDefault(), "Subtotal : S/ %8.2f", subtotal))
    println(String.format(Locale.getDefault(), "IGV (18%%): S/ %8.2f", igv))
    println(String.format(Locale.getDefault(), "TOTAL    : S/ %8.2f", total))

    if (descuento > 0.0) {
        val porcentaje = if (total > 5000) "10%" else "5%"
        println(String.format(Locale.getDefault(), "Descuento (%s): S/ %8.2f", porcentaje, descuento))
        println(String.format(Locale.getDefault(), "TOTAL CON DESCUENTO: S/ %8.2f", totalConDescuento))
    } else {
        println("Descuento: No aplica")
    }
    println()
}

// --- Lectura de entrada con reintento y cancelacion opcional ---

private fun esCancelacion(texto: String): Boolean = texto.equals(PALABRA_CANCELAR, ignoreCase = true)

private fun leerTextoNoVacio(mensaje: String, permitirCancelar: Boolean = true): String? {
    val sufijo = if (permitirCancelar) SUFIJO_CANCELAR else ""
    while (true) {
        print("$mensaje$sufijo: ")
        val entrada = readLine()?.trim().orEmpty()
        if (permitirCancelar && esCancelacion(entrada)) return null
        if (entrada.isNotBlank()) return entrada
        println("Entrada invalida: el texto no puede quedar vacio. Intente nuevamente.")
    }
}

private fun leerOpcionEnRango(mensaje: String, rango: IntRange): Int? {
    while (true) {
        print("$mensaje$SUFIJO_CANCELAR: ")
        val entrada = readLine()?.trim().orEmpty()
        if (esCancelacion(entrada)) return null
        val valor = entrada.toIntOrNull()
        if (valor != null && valor in rango) return valor
        println("Entrada invalida: ingrese un numero entre ${rango.first} y ${rango.last}. Intente nuevamente.")
    }
}

private fun leerEnteroPositivo(mensaje: String): Int? {
    while (true) {
        print("$mensaje$SUFIJO_CANCELAR: ")
        val entrada = readLine()?.trim().orEmpty()
        if (esCancelacion(entrada)) return null
        val valor = entrada.toIntOrNull()
        when {
            valor == null -> println("Entrada invalida: ingrese un numero entero. Intente nuevamente.")
            valor <= 0 -> println("Entrada invalida: la cantidad debe ser mayor a cero. Intente nuevamente.")
            else -> return valor
        }
    }
}

private fun leerDoublePositivo(mensaje: String): Double? {
    while (true) {
        print("$mensaje$SUFIJO_CANCELAR: ")
        val entrada = readLine()?.trim().orEmpty()
        if (esCancelacion(entrada)) return null
        val valor = entrada.toDoubleOrNull()
        when {
            valor == null -> println("Entrada invalida: ingrese un numero valido (ej. 19.90). Intente nuevamente.")
            valor <= 0.0 -> println("Entrada invalida: el precio debe ser mayor a cero. Intente nuevamente.")
            else -> return valor
        }
    }
}
