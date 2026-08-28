package com.flores.lab02carritokotlin.model

class ProductoElectronico private constructor(
    nombre: String,
    precio: Double,
    cantidad: Int
) : Producto(nombre, precio, cantidad) {

    companion object {
        fun crear(nombre: String, precio: Double, cantidad: Int): ProductoElectronico? {
            if (!Producto.datosValidos(nombre, precio, cantidad)) return null
            return ProductoElectronico(nombre, precio, cantidad)
        }
    }
}
