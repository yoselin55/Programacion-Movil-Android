package com.flores.mycar.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Barra inferior con las pestañas principales: Marcas y Favoritos.
 * La pestaña Admin solo aparece si se pasa "rutaAdmin" (la persona tiene rol admin).
 */
@Composable
fun BarraInferior(
    rutaActual: String,
    rutaMarcas: String,
    rutaFavoritos: String,
    rutaAdmin: String?,
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
        if (rutaAdmin != null) {
            NavigationBarItem(
                selected = rutaActual == rutaAdmin,
                onClick = { onIr(rutaAdmin) },
                icon = { Text("⚙") },
                label = { Text("Admin") }
            )
        }
    }
}
