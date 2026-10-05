package com.flores.saludplus.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Relaciones:
// - Lo usan todas las pantallas de ui/screens (por ahora PantallaEnConstruccion)
// - Aquí se irán creando los componentes reutilizables (botones, tarjetas, barras)

// Contenido temporal de cada pantalla pendiente, con botones para seguir el flujo.
// TODO: quitar su uso en cada pantalla al terminarla
@Composable
fun PantallaEnConstruccion(
    titulo: String,
    acciones: List<Pair<String, () -> Unit>> = emptyList()
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, style = MaterialTheme.typography.headlineSmall)
        Text(text = "Pantalla en construcción", style = MaterialTheme.typography.bodyMedium)
        acciones.forEach { (texto, accion) ->
            Button(onClick = accion) { Text(texto) }
        }
    }
}
