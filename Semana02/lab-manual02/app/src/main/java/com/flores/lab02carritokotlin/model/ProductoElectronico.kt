package com.flores.lab02carritokotlin.model

class ProductoElectronico private constructor(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {

    override fun calcularImporte(): Double {
        // Recargo del 5% por garantia extendida incluida en electronicos
        return precio * cantidad * 1.05
    }

    companion object {
        fun crear(nombre: String, precio: Double, cantidad: Int): ProductoElectronico? {
            if (!Producto.datosValidos(nombre, precio, cantidad)) return null
            return ProductoElectronico(nombre, precio, cantidad)
        }
    }
}
