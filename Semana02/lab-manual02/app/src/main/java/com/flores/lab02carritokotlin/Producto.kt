package com.flores.lab02carritokotlin

class Producto private constructor(
    nombre: String,
    precio: Double,
    cantidad: Int
) {
    private val _nombre: String = nombre
    private val _precio: Double = precio
    private var _cantidad: Int = cantidad

    val nombre: String get() = _nombre
    val precio: Double get() = _precio
    val cantidad: Int get() = _cantidad

    companion object {
        fun crear(nombre: String, precio: Double, cantidad: Int): Producto? {
            if (precio <= 0.0) {
                println("Error: no se pudo crear '$nombre'. El precio debe ser mayor a cero (recibido: $precio).")
                return null
            }
            if (cantidad <= 0) {
                println("Error: no se pudo crear '$nombre'. La cantidad debe ser mayor a cero (recibida: $cantidad).")
                return null
            }
            return Producto(nombre, precio, cantidad)
        }
    }
}
