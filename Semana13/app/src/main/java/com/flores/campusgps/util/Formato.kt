package com.flores.campusgps.util

import java.util.Locale

// Commit 6: da formato a la distancia: menos de 1000 m en metros enteros (350 m), desde 1000 en km (1.5 km).
fun formatearDistancia(metros: Float): String =
    if (metros < 1000f) {
        "${metros.toInt()} m"
    } else {
        String.format(Locale.US, "%.1f km", metros / 1000f)
    }
