package com.flores.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.clinicasalud.ui.theme.*

// REQUISITO CUMPLIDO: Agendar cita

@Composable
fun ScheduleAppointmentScreen(
    doctorId: String,
    onBackClick: () -> Unit,
    onConfirmClick: (String, String) -> Unit
) {
    // ESTADO LOCAL DE SELECCIÓN ÚNICA (Requisito)
    var selectedDate by remember { mutableStateOf("Vie 27") }
    var selectedTime by remember { mutableStateOf("10:30") }

    val dates = listOf("Jue 26", "Vie 27", "Sáb 28")
    val times = listOf("9:00", "10:30", "3:00")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(16.dp)
    ) {
        // TopBar / Botón de retorno
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = TextDark
                )
            }
            Text(
                text = "← Agendar cita",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECCIÓN FECHA
        Text(
            text = "Selecciona fecha",
            fontSize = 13.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botones de selección única para Fecha (Mínimo 3 opciones)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            dates.forEach { date ->
                val isSelected = date == selectedDate
                Button(
                    onClick = { selectedDate = date }, // Selección única
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) PrimaryPurple else ChipUnselected,
                        contentColor = if (isSelected) Color.White else TextDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val parts = date.split(" ")
                        Text(text = parts[0], fontSize = 12.sp, fontWeight = FontWeight.Normal)
                        Text(text = parts[1], fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECCIÓN HORA
        Text(
            text = "Selecciona hora",
            fontSize = 13.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botones de selección única para Hora (Mínimo 3 opciones)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            times.forEach { time ->
                val isSelected = time == selectedTime
                Button(
                    onClick = { selectedTime = time }, // Selección única
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) PrimaryPurple else ChipUnselected,
                        contentColor = if (isSelected) Color.White else TextDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = time, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón Confirmar
        Button(
            onClick = { onConfirmClick(selectedDate, selectedTime) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Confirmar cita", fontSize = 16.sp, color = Color.White)
        }
    }
}