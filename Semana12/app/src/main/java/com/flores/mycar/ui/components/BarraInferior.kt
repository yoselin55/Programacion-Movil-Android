package com.flores.mycar.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/** Barra inferior con las dos pestañas principales: Marcas y Favoritos. */
@Composable
fun BarraInferior(
    rutaActual: String,
    rutaMarcas: String,
    rutaFavoritos: String,
    onIr: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = rutaActual == rutaMarcas,
            onClick = { onIr(rutaMarcas) },
            icon = { Text("🚗") },
            label = { Text("Marcas") }
        )
        NavigationBarItem(
            selected = rutaActual == rutaFavoritos,
            onClick = { onIr(rutaFavoritos) },
            icon = { Text("★") },
            label = { Text("Favoritos") }
        )
    }
}
