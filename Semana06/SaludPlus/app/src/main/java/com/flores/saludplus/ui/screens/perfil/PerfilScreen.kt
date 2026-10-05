package com.flores.saludplus.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.FilaDetalle
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario

// Relaciones:
// - La llama AppNavigation en Rutas.PERFIL
// - Usa BarraInferior y FilaDetalle (Componentes.kt)
// - Llama a Repositorio.usuarioActual, citasDelUsuario y cerrarSesion
// - Al cerrar sesión vuelve al Splash (onCerrarSesion)

// Commit 9: datos de la sesión actual y botón para cerrar sesión
@Composable
fun PerfilScreen(onNavegar: (String) -> Unit, onCerrarSesion: () -> Unit) {
    val usuario = Repositorio.usuarioActual
    val totalCitas = Repositorio.citasDelUsuario().size

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BarraInferior(Rutas.PERFIL, onNavegar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Mis datos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            // Avatar y nombre
            Box(
                modifier = Modifier.size(96.dp).background(AzulClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(56.dp))
            }
            Text(usuario?.nombre ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Datos de la sesión
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FilaDetalle(Icons.Filled.Phone, "Teléfono", usuario?.telefono ?: "")
                    FilaDetalle(
                        Icons.Filled.Email, "Correo",
                        usuario?.correo?.ifBlank { "No registrado" } ?: ""
                    )
                    FilaDetalle(Icons.Filled.Event, "Citas agendadas", totalCitas.toString())
                }
            }

            Spacer(Modifier.height(8.dp))
            // Cierra la sesión en el Repositorio y vuelve al Splash
            OutlinedButton(
                onClick = {
                    Repositorio.cerrarSesion()
                    onCerrarSesion()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
