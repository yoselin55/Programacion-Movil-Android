package com.flores.laboratorio05.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flores.laboratorio05.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, itemId: Int) {
    // Scaffold provee la estructura base de la pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del elemento") },
                // Botón de flecha para regresar en la pila de navegación
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // Column con doble padding para respetar la barra superior y dar margen interior
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            // Muestra el ID recibido desde el NavHost
            Text(
                text = "Elemento #$itemId",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))
            // Card: contenedor elevado que agrupa la información del argumento
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ID recibido: $itemId",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Este valor llegó como argumento tipado Int " +
                                "desde el NavHost.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}