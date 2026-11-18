package com.flores.campusgps.util

import java.util.Locale
import kotlin.math.roundToInt

// Commit 6: da formato a la distancia: menos de 1000 m en metros enteros (350 m), desde 1000 en km (1.5 km).
fun formatearDistancia(metros: Float): String =
    if (metros < 1000f) {
        "${metros.toInt()} m"
    } else {
        String.format(Locale.US, "%.1f km", metros / 1000f)
    }

// Mejora 3 (tiempo a pie): se camina a unos 4.8 km/h, es decir 80 metros por minuto.
private const val METROS_POR_MINUTO = 80f

// Minutos que toma llegar caminando; mínimo 1 para que nunca diga "0 min".
fun minutosAPie(metros: Float): Int = maxOf(1, (metros / METROS_POR_MINUTO).roundToInt())

// Texto listo para mostrar: "1 min a pie" o "1 h 5 min a pie".
fun formatearTiempoAPie(metros: Float): String {
    val min = minutosAPie(metros)
    return if (min < 60) "$min min a pie" else "${min / 60} h ${min % 60} min a pie"
}
