package com.flores.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Horario mostrado en el detalle, con sus cupos y si el usuario ya lo reservó
data class ScheduleOption(
    val time: String,
    val availableSlots: Int,
    val alreadyReserved: Boolean
) {
    // Un horario solo se puede elegir si tiene cupos y no fue reservado antes
    val isSelectable: Boolean
        get() = availableSlots > 0 && !alreadyReserved
}

@Composable
fun DetailScreen(
    className: String,
    room: String,
    day: String, // "Hoy", "Mañana" o día de la semana
    schedules: List<ScheduleOption>,
    totalSlots: Int,
    onBackClick: () -> Unit,
    onReserveClick: (selectedTime: String) -> Unit
) {
    val primary = Color(0xFF0F6A52)

    // Horario elegido (selección única); null = ninguno seleccionado
    var selectedTime by remember { mutableStateOf<String?>(null) }
    // Controla si se muestra el diálogo de confirmación
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Si el horario elegido deja de estar disponible, se limpia la selección
    val selectedOption = schedules.find { it.time == selectedTime }?.takeIf { it.isSelectable }

    Scaffold(
        topBar = { TecsupTopBar(title = "Detalle de clase", onBackClick = onBackClick) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFFE2F3ED), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(className, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("$day · $room · 45 min", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Entrenamiento funcional de alta intensidad. Cupos limitados.",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text("Elige un horario", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))

                // Lista de horarios con selección única (RadioButton)
                Column(
                    modifier = Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    schedules.forEach { option ->
                        ScheduleRow(
                            option = option,
                            totalSlots = totalSlots,
                            selected = option.time == selectedOption?.time,
                            onSelect = { selectedTime = option.time }
                        )
                    }
                }
            }

            // Mensaje de ayuda mientras no haya un horario elegido
            if (selectedOption == null) {
                Text(
                    "Selecciona un horario para continuar",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Deshabilitado hasta que se elija un horario válido
            Button(
                onClick = { showConfirmDialog = true },
                enabled = selectedOption != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reservar cupo", fontSize = 16.sp, color = Color.White)
            }
        }
    }

    // Diálogo con el resumen de la reserva antes de confirmar
    if (showConfirmDialog && selectedOption != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar reserva", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Clase: $className")
                    Text("Horario: $day, ${selectedOption.time}")
                    Text("Sala: $room")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onReserveClick(selectedOption.time)
                }) {
                    Text("Confirmar", color = primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// Fila de un horario: RadioButton + hora + cupos o estado ("Lleno" / "Reservado")
@Composable
private fun ScheduleRow(
    option: ScheduleOption,
    totalSlots: Int,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val primary = Color(0xFF0F6A52)
    val enabled = option.isSelectable

    val statusText = when {
        option.alreadyReserved -> "Reservado"
        option.availableSlots <= 0 -> "Lleno"
        else -> "${option.availableSlots} de $totalSlots cupos"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onSelect
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFE2F3ED) else Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null, // el clic lo maneja la Card completa
                enabled = enabled,
                colors = RadioButtonDefaults.colors(selectedColor = primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                option.time,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) Color.Black else Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Text(
                statusText,
                fontSize = 13.sp,
                color = when {
                    option.alreadyReserved -> primary
                    option.availableSlots <= 0 -> Color(0xFFB3261E)
                    else -> Color.Gray
                },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}
