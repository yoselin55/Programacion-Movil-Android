package com.flores.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.clinicasalud.navigation.sampleDoctors
import com.flores.clinicasalud.ui.theme.*
// REQUISITO CUMPLIDO: Confirmación
@Composable
fun ConfirmationScreen(
    doctorId: String,
    date: String,
    time: String,
    onVerMisCitasClick: () -> Unit
) {
    val doctor = sampleDoctors.find { it.id == doctorId } ?: sampleDoctors.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Círculo con el ícono de verificación (Check verde)
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(SuccessGreenBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Cita agendada!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        // REQUISITO: Resumen de la cita agendada
        Text(
            text = doctor.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted
        )

        val fullDateStr = if (date.contains("27")) "Viernes 27" else date
        Text(
            text = "$fullDateStr, $time",
            fontSize = 14.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(32.dp))

        // REQUISITO: Botón para volver/ver citas
        Button(
            onClick = onVerMisCitasClick,
            colors = ButtonDefaults.buttonColors(containerColor = ChipUnselected),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
        ) {
            Text(text = "Ver mis citas", color = TextDark, fontSize = 14.sp)
        }
    }
}