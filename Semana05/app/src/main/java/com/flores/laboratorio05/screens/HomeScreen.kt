package com.flores.laboratorio05.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flores.laboratorio05.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    // Contenedor principal centrado
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la pantalla
        Text(
            text = "Pantalla Tecsup",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))
        // Botón para navegar a la Lista
        Button(
            onClick = { navController.navigate(Screen.List.route) }
        ) {
            Text("Ir a Lista")
        }

        Spacer(modifier = Modifier.height(12.dp))
        // Botón para navegar al Perfil
        Button(
            onClick = { navController.navigate(Screen.Profile.route) }
        ) {
            Text("Ir a Perfil")
        }
    }
}