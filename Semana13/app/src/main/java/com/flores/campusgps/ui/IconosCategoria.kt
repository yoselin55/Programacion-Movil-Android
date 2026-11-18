package com.flores.campusgps.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

// Mejora 3: cada categoría tiene su ícono, que se usa en los chips y en las tarjetas de la lista.
fun iconoDeCategoria(categoria: String): ImageVector = when (categoria) {
    "Estudio" -> Icons.Filled.MenuBook
    "Comida" -> Icons.Filled.Restaurant
    "Eventos" -> Icons.Filled.Event
    "Servicios" -> Icons.Filled.LocalParking
    else -> Icons.Filled.Place
}
