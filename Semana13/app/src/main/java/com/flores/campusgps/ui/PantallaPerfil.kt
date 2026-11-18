package com.flores.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.campusgps.ui.theme.Azul
import com.flores.campusgps.ui.theme.AzulClaro
import com.flores.campusgps.ui.theme.Fondo
import com.flores.campusgps.ui.theme.Tinta
import com.flores.campusgps.viewmodel.MapaViewModel

// Mejora 3 (perfil): avatar, nombre editable, resumen de la actividad y botón para borrar el historial.
@Composable
fun PantallaPerfil(
    nombre: String,
    vm: MapaViewModel,
    onGuardarNombre: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val recientes by vm.recientes.collectAsState()
    val busquedas by vm.busquedasRecientes.collectAsState()

    var texto by remember(nombre) { mutableStateOf(nombre) }
    var guardado by remember { mutableStateOf(false) }
    var confirmarBorrado by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar: la inicial del nombre dentro de un círculo azul (o un ícono si todavía no hay nombre).
        Box(
            modifier = Modifier.size(104.dp).clip(CircleShape).background(Azul),
            contentAlignment = Alignment.Center
        ) {
            if (nombre.isNotBlank()) {
                Text(
                    nombre.first().uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 44.sp),
                    color = Tinta
                )
            } else {
                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(56.dp), tint = Tinta)
            }
        }
        Text(
            if (nombre.isNotBlank()) nombre else "Tu perfil",
            style = MaterialTheme.typography.titleLarge
        )

        // Nombre editable.
        OutlinedTextField(
            value = texto,
            onValueChange = {
                texto = it
                guardado = false
            },
            label = { Text("Tu nombre") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Tinta,
                unfocusedBorderColor = Azul,
                focusedLabelColor = Tinta,
                cursorColor = Tinta
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                onGuardarNombre(texto.trim())
                guardado = true
            },
            enabled = texto.trim().isNotEmpty() && texto.trim() != nombre,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("Guardar cambios") }
        if (guardado) {
            Text("Nombre actualizado", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Resumen de la actividad: tres tarjetas pequeñas.
        // IntrinsicSize.Min hace que las tres tarjetas midan lo mismo de alto.
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            Dato("Lugares recientes", "${recientes.size}", Modifier.weight(1f).fillMaxHeight())
            Dato("Búsquedas guardadas", "${busquedas.size}", Modifier.weight(1f).fillMaxHeight())
            Dato("Último lugar", recientes.firstOrNull()?.nombre ?: "—", Modifier.weight(1f).fillMaxHeight())
        }

        OutlinedButton(
            onClick = { confirmarBorrado = true },
            enabled = recientes.isNotEmpty() || busquedas.isNotEmpty(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Tinta),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Filled.DeleteOutline, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Borrar historial")
        }
    }

    // Antes de borrar se pide confirmación, porque no se puede deshacer.
    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            containerColor = Fondo,
            title = { Text("¿Borrar el historial?") },
            text = { Text("Se borrarán tus lugares recientes y tus búsquedas guardadas. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.borrarHistorial()
                    confirmarBorrado = false
                }) { Text("Borrar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar", color = Tinta) }
            }
        )
    }
}

// Tarjeta pequeña con un número o un texto corto y su etiqueta.
@Composable
private fun Dato(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulClaro)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                valor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(Modifier.height(4.dp))
            Text(
                etiqueta,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
