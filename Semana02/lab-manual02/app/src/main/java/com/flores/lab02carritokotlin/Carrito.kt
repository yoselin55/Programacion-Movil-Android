package com.flores.lab02carritokotlin

import com.flores.lab02carritokotlin.model.Carrito
import com.flores.lab02carritokotlin.model.ProductoAccesorio
import com.flores.lab02carritokotlin.model.ProductoElectronico
import com.flores.lab02carritokotlin.model.ProductoImportado
import java.util.Locale

fun main() {
    println("=========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP   ")
    println("=========================================")

    print("Ingrese el nombre del cliente: ")
    val nombreCliente = readLine()?.trim().orEmpty().ifBlank { "Cliente sin nombre" }
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
                println("\nGracias por usar el carrito, $nombreCliente. Hasta pronto!")
                continuar = false
            }
            null -> println("\nOpcion invalida: debe ingresar un numero.\n")
            else -> println("\nOpcion invalida: elija un numero entre 1 y 6.\n")
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
    println("Tipo de producto:")
    println("1) Electronico")
    println("2) Accesorio")
    println("3) Importado")
    print("Seleccione un tipo (1-3): ")
    val tipo = readLine()?.trim()?.toIntOrNull()
    if (tipo == null || tipo !in 1..3) {
        println("Tipo invalido. Operacion cancelada.\n")
        return
    }

    print("Nombre del producto: ")
    val nombre = readLine()?.trim().orEmpty()
    if (nombre.isBlank()) {
        println("El nombre no puede estar vacio. Operacion cancelada.\n")
        return
    }

    print("Precio unitario: ")
    val precio = readLine()?.trim()?.toDoubleOrNull()
    if (precio == null) {
        println("Precio invalido: debe ser un numero. Operacion cancelada.\n")
        return
    }

    print("Cantidad: ")
    val cantidad = readLine()?.trim()?.toIntOrNull()
    if (cantidad == null) {
        println("Cantidad invalida: debe ser un numero entero. Operacion cancelada.\n")
        return
    }

    val producto = when (tipo) {
        1 -> ProductoElectronico.crear(nombre, precio, cantidad)
        2 -> ProductoAccesorio.crear(nombre, precio, cantidad)
        else -> ProductoImportado.crear(nombre, precio, cantidad)
    }

    if (producto != null) {
        carrito.agregar(producto)
        println("Producto agregado: ${producto.nombre}\n")
    }
}

private fun buscarProductoInteractivo(carrito: Carrito) {
    print("\nNombre del producto a buscar: ")
    val nombre = readLine()?.trim().orEmpty()
    if (nombre.isBlank()) {
        println("Debe ingresar un nombre.\n")
        return
    }

    val encontrado = carrito.buscarProducto(nombre)
    if (encontrado != null) {
        println("Resultado: Encontrado -> ${encontrado.nombre} (S/ ${encontrado.precio}) x${encontrado.cantidad}\n")
    } else {
        println("Resultado: El producto '$nombre' no existe.\n")
    }
}

private fun eliminarProductoInteractivo(carrito: Carrito) {
    print("\nNombre del producto a eliminar: ")
    val nombre = readLine()?.trim().orEmpty()
    if (nombre.isBlank()) {
        println("Debe ingresar un nombre.\n")
        return
    }

    val seElimino = carrito.eliminarProducto(nombre)
    if (seElimino) {
        println("El producto '$nombre' fue eliminado con exito.\n")
    } else {
        println("No se encontro el producto '$nombre' para eliminar.\n")
    }
}

private fun mostrarTotales(carrito: Carrito) {
    if (carrito.cantidadProductos == 0) {
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
