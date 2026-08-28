package com.flores.lab02carritokotlin.model

class ProductoImportado private constructor(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {

    companion object {
        fun crear(nombre: String, precio: Double, cantidad: Int): ProductoImportado? {
            if (!Producto.datosValidos(nombre, precio, cantidad)) return null
            return ProductoImportado(nombre, precio, cantidad)
        }
    }
}
