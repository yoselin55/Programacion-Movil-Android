package com.flores.lavacampus.model

// Descuento por volumen: si el lavado (servicios cobrados por kilo) supera 5 kg,
// se descuenta 10 % del subtotal de esos servicios.
// Una sola regla para el resumen (ViewModel) y para lo que se guarda en el pedido (Repository)
object Descuento {
    const val UMBRAL_KG = 5.0
    const val PORCENTAJE = 0.10

    fun aplica(kilos: Double): Boolean = kilos > UMBRAL_KG

    // Monto a descontar, redondeado a centimos
    fun sobreLavado(kilos: Double, subtotalLavado: Double): Double =
        if (aplica(kilos)) Math.round(subtotalLavado * PORCENTAJE * 100.0) / 100.0 else 0.0
}
