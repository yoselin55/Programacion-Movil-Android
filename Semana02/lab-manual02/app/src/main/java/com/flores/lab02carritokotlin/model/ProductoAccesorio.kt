package com.flores.lab02carritokotlin.model

class ProductoAccesorio private constructor(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {

    override fun calcularImporte(): Double {
        val base = precio * cantidad
        // Descuento del 5% al comprar 3 o mas unidades del mismo accesorio
        return if (cantidad >= 3) base * 0.95 else base
    }

    companion object {
        fun crear(nombre: String, precio: Double, cantidad: Int): ProductoAccesorio? {
            if (!Producto.datosValidos(nombre, precio, cantidad)) return null
            return ProductoAccesorio(nombre, precio, cantidad)
        }
    }
}
