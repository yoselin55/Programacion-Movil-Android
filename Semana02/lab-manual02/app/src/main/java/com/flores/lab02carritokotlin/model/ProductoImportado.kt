package com.flores.lab02carritokotlin.model

class ProductoImportado private constructor(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {

    override fun calcularImporte(): Double {
        // Recargo del 8% por aranceles de importacion
        return precio * cantidad * 1.08
    }

    companion object {
        fun crear(nombre: String, precio: Double, cantidad: Int): ProductoImportado? {
            if (!Producto.datosValidos(nombre, precio, cantidad)) return null
            return ProductoImportado(nombre, precio, cantidad)
        }
    }
}
