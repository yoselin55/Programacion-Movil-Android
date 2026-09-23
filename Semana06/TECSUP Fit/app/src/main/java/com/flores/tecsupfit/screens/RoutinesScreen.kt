package com.flores.tecsupfit.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// Pantalla de Rutinas (contenido pendiente)
@Composable
fun RoutinesScreen() {
    Scaffold(
        topBar = { TecsupTopBar(title = "Rutinas") }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Sección de Rutinas", color = Color.Gray)
        }
    }
}
