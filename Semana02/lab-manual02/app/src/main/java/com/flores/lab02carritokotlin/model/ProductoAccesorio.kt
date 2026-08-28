package com.flores.lab02carritokotlin.model

class ProductoAccesorio private constructor(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {

    companion object {
        fun crear(nombre: String, precio: Double, cantidad: Int): ProductoAccesorio? {
            if (!Producto.datosValidos(nombre, precio, cantidad)) return null
            return ProductoAccesorio(nombre, precio, cantidad)
        }
    }
}
