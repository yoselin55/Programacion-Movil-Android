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

    val nombreCliente = "Yoselin Flores"
    val carrito = Carrito()

    println("Cliente: $nombreCliente\n")

    ProductoElectronico.crear("Laptop HP", 2500.0, 1)?.let { carrito.agregar(it) }
    ProductoAccesorio.crear("Mouse Logitech", 45.5, 2)?.let { carrito.agregar(it) }
    ProductoImportado.crear("Audifonos Sony", 120.0, 1)?.let { carrito.agregar(it) }
    ProductoAccesorio.crear("USB Kingston 64GB", 25.0, 3)?.let { carrito.agregar(it) }

    for (producto in carrito.listar()) {
        println("Producto agregado: ${producto.nombre}")
    }

    println()

    carrito.mostrarDetalle()
    println("Cantidad de productos: ${carrito.cantidadProductos}\n")

    var subtotal = carrito.calcularSubtotal()
    var igv = carrito.calcularIGV(subtotal)
    var total = carrito.calcularTotal(subtotal, igv)

    println(String.format(Locale.getDefault(), "Subtotal : S/ %8.2f", subtotal))
    println(String.format(Locale.getDefault(), "IGV (18%%): S/ %8.2f", igv))
    println(String.format(Locale.getDefault(), "TOTAL    : S/ %8.2f", total))
    println()

    val masCaro = carrito.productoMasCaro()
    if (masCaro != null) {
        println("Producto mas caro: ${masCaro.nombre} " +
                String.format(Locale.getDefault(), "(S/ %.2f)", masCaro.precio))
    }

    var descuento = carrito.calcularDescuento(total)
    var totalConDescuento = total - descuento

    if (descuento > 0.0) {
        val porcentaje = if (total > 5000) "10%" else "5%"
        println(String.format(Locale.getDefault(), "Descuento (%s): S/ %8.2f", porcentaje, descuento))
        println(String.format(Locale.getDefault(), "TOTAL CON DESCUENTO: S/ %8.2f", totalConDescuento))
    } else {
        println("Descuento: No aplica")
    }

    println("\n=========================================")
    println("         RETO ADICIONAL - PRUEBAS        ")
    println("=========================================")

    val nombreBusqueda = "Audifonos Sony"
    println("\n---> Buscando producto: '$nombreBusqueda'")
    val productoEncontrado = carrito.buscarProducto(nombreBusqueda)
    if (productoEncontrado != null) {
        println("Resultado: Encontrado -> ${productoEncontrado.nombre} (S/ ${productoEncontrado.precio})")
    } else {
        println("Resultado: El producto '$nombreBusqueda' no existe.")
    }

    val nombreEliminar = "Mouse Logitech"
    println("\n---> Eliminando producto: '$nombreEliminar'")
    val seElimino = carrito.eliminarProducto(nombreEliminar)

    if (seElimino) {
        println("El producto '$nombreEliminar' fue eliminado con exito.\n")
    } else {
        println("No se encontro el producto '$nombreEliminar' para eliminar.\n")
    }

    println(">>> DETALLE ACTUALIZADO DEL CARRITO <<<")
    carrito.mostrarDetalle()
    println("Cantidad de productos actualizada: ${carrito.cantidadProductos}\n")

    subtotal = carrito.calcularSubtotal()
    igv = carrito.calcularIGV(subtotal)
    total = carrito.calcularTotal(subtotal, igv)
    descuento = carrito.calcularDescuento(total)
    totalConDescuento = total - descuento

    println(String.format(Locale.getDefault(), "Subtotal Actualizado : S/ %8.2f", subtotal))
    println(String.format(Locale.getDefault(), "IGV (18%%) Actualizado : S/ %8.2f", igv))
    println(String.format(Locale.getDefault(), "TOTAL Actualizado    : S/ %8.2f", total))

    if (descuento > 0.0) {
        val porcentaje = if (total > 5000) "10%" else "5%"
        println(String.format(Locale.getDefault(), "Descuento (%s)        : S/ %8.2f", porcentaje, descuento))
        println(String.format(Locale.getDefault(), "TOTAL FINAL          : S/ %8.2f", totalConDescuento))
    } else {
        println("Descuento            : No aplica (Total menor a S/ 3000.00)")
    }
}
