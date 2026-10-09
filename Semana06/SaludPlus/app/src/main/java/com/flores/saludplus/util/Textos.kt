package com.flores.saludplus.util

// Relaciones:
// - La usan PerfilScreen (avatar de Mis datos) y HomeScreen (encabezado del menú lateral)
// - Es Kotlin puro (sin clases de Android)

// Fase 2: devuelve las iniciales en mayúscula: primera letra del nombre y primera del apellido
// ("Yoselin Flores" -> "YF"); con una sola palabra devuelve solo su primera letra y con texto vacío, ""
fun iniciales(nombre: String): String =
    nombre.trim().split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
