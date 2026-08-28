package com.flores.lab02carritokotlin.model

import java.util.Locale

class Carrito {
    private val productos = mutableListOf<Producto>()

    val cantidadProductos: Int get() = productos.size

    fun agregar(producto: Producto) {
        productos.add(producto)
    }

    fun listar(): List<Producto> = productos.toList()

    fun calcularSubtotal(): Double {
        var subtotal = 0.0
        for (p in productos) {
            subtotal += p.calcularImporte()
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

    fun productoMasCaro(): Producto? = productos.maxByOrNull { it.precio }

    fun mostrarDetalle() {
        println("--------- DETALLE DEL CARRITO ---------")
        var i = 1
        for (p in productos) {
            val importe = p.calcularImporte()
            println(String.format(Locale.getDefault(), "%d. %-20s x%d S/ %8.2f", i, p.nombre, p.cantidad, importe))
            i++
        }
        println("---------------------------------------")
    }

    fun buscarProducto(nombre: String): Producto? {
        return productos.find { it.nombre.equals(nombre, ignoreCase = true) }
    }

    fun eliminarProducto(nombre: String): Boolean {
        return productos.removeIf { it.nombre.equals(nombre, ignoreCase = true) }
    }
}
