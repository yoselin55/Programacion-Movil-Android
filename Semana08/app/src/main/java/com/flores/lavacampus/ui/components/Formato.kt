package com.flores.lavacampus.ui.components

import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatoFecha = DateTimeFormatter.ofPattern("EEE d MMM", Locale("es", "PE"))

// "2026-10-21" -> "mié 21 oct"
fun String.fechaCorta(): String =
    runCatching { LocalDate.parse(this).format(formatoFecha).replace(".", "") }
        .getOrDefault(this)

// 40.5 -> "S/ 40.50"
fun Double.soles(): String = "S/ " + String.format(Locale.US, "%.2f", this)

// 12 -> "#012"
fun Int.codigoPedido(): String = "#" + toString().padStart(3, '0')

// Resumen de cantidades: "2.0 kg" o "5 prendas" o "3.0 kg + 4 prendas"
fun PedidoConDetalle.resumenCantidad(): String {
    val kilos = detalles.filter { it.servicio.unidad == "KG" }.sumOf { it.detalle.cantidad }
    val piezas = detalles.filter { it.servicio.unidad != "KG" }.sumOf { it.detalle.cantidad }.toInt()
    return listOfNotNull(
        if (kilos > 0) String.format(Locale.US, "%.1f kg", kilos) else null,
        if (piezas > 0) "$piezas prendas" else null
    ).joinToString(" + ")
}
