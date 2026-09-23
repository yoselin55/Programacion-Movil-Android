package com.flores.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
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

// =========================================================================
// REQUISITO CUMPLIDO: Perfil del médico
// 1. Recibe los datos del médico elegido por parámetro de navegación (doctorId)
// 2. Muestra avatar, nombre, especialidad, experiencia y reseña
// 3. Incluye botón principal "Agendar cita"

@Composable
fun DoctorDetailScreen(
    doctorId: String, // <- PARÁMETRO DE NAVEGACIÓN
    onBackClick: () -> Unit,
    onAgendarCitaClick: (String) -> Unit
) {
    // Buscar médico enviado por parámetro
    val doctor = sampleDoctors.find { it.id == doctorId } ?: sampleDoctors.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Barra superior de navegación hacia atrás
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = TextDark
                )
            }
            Text(
                text = "← Perfil del médico",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Círculo con el ícono '+' grande del prototipo
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Datos del médico pasados por parámetro
        Text(
            text = doctor.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "${doctor.specialty} · ${doctor.expYears} años exp.",
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB800),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${doctor.rating} (${doctor.reviewsCount} reseñas)",
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = doctor.bio,
            fontSize = 14.sp,
            color = TextMuted,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // REQUISITO CUMPLIDO: Botón "Agendar cita"
        Button(
            onClick = { onAgendarCitaClick(doctor.id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Agendar cita", fontSize = 16.sp, color = Color.White)
        }
    }
}