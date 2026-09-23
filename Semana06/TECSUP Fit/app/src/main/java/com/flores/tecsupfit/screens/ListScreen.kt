package com.flores.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// date tiene el formato "<día>, <hora>" (ej. "Hoy, 6:00 pm")
// status: "Confirmada", "Completada" o "Cancelada"
data class ReservationItem(val name: String, val date: String, val status: String) {
    // Hora de la reserva, usada para devolver el cupo al cancelar
    val time: String
        get() = date.substringAfter(", ")
}

// Colores de cada estado: franja lateral, fondo de la etiqueta y texto
private data class StatusStyle(val stripe: Color, val background: Color, val text: Color)

private fun statusStyle(status: String) = when (status) {
    "Confirmada" -> StatusStyle(Color(0xFF0F6A52), Color(0xFFD4F3E6), Color(0xFF0F6A52))
    "Cancelada" -> StatusStyle(Color(0xFFB3261E), Color(0xFFFDE2E1), Color(0xFFB3261E))
    else -> StatusStyle(Color.Transparent, Color(0xFFE5E5E5), Color.Gray) // Completada
}

@Composable
fun ListScreen(
    reservations: List<ReservationItem>,
    onCancelReservation: (ReservationItem) -> Unit,
    onExploreClick: () -> Unit
) {
    // Reserva que el usuario quiere cancelar (null = no se muestra el diálogo)
    var reservationToCancel by remember { mutableStateOf<ReservationItem?>(null) }

    Scaffold(
        topBar = { TecsupTopBar(title = "Mis reservas") }
    ) { padding ->
        if (reservations.isEmpty()) {
            // Estado vacío
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Aún no tienes reservas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Reserva tu primera clase", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onExploreClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6A52)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Explorar clases", color = Color.White)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reservations) { item ->
                    ReservationCard(
                        item = item,
                        onCancelClick = { reservationToCancel = item }
                    )
                }
            }
        }
    }

    // Diálogo para confirmar la cancelación
    reservationToCancel?.let { item ->
        AlertDialog(
            onDismissRequest = { reservationToCancel = null },
            title = { Text("Cancelar reserva", fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que quieres cancelar ${item.name} (${item.date})? Se liberará tu cupo.") },
            confirmButton = {
                TextButton(onClick = {
                    reservationToCancel = null
                    onCancelReservation(item)
                }) {
                    Text("Sí, cancelar", color = Color(0xFFB3261E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { reservationToCancel = null }) {
                    Text("No", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun ReservationCard(item: ReservationItem, onCancelClick: () -> Unit) {
    val style = statusStyle(item.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(style.stripe)
            )
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.date, color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(style.background, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(item.status, color = style.text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Solo las reservas confirmadas se pueden cancelar
                    if (item.status == "Confirmada") {
                        TextButton(onClick = onCancelClick) {
                            Text("Cancelar", color = Color(0xFFB3261E))
                        }
                    }
                }
            }
        }
    }
}
