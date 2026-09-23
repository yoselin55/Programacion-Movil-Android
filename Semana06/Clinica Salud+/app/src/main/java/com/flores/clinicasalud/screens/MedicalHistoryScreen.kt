package com.flores.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.clinicasalud.ui.theme.*

// REQUISITO CUMPLIDO: Historial médico de la paciente

private data class MedicalRecord(
    val id: String,
    val doctorName: String,
    val specialty: String,
    val date: String,
    val status: String, // "Confirmada" o "Completada"
    val diagnosis: String,
    val details: String
)

private val medicalRecords = listOf(
    MedicalRecord(
        id = "1",
        doctorName = "Dra. Ana Torres",
        specialty = "Cardióloga",
        date = "Viernes 27, 10:30 AM",
        status = "Confirmada",
        diagnosis = "Control cardiológico programado",
        details = "Cita próxima. El diagnóstico estará disponible después de la atención."
    ),
    MedicalRecord(
        id = "2",
        doctorName = "Dr. Luis Vega",
        specialty = "Pediatra",
        date = "Miércoles 15, 3:00 PM",
        status = "Completada",
        diagnosis = "Faringitis aguda",
        details = "Tratamiento con antibiótico por 7 días, abundante hidratación y reposo."
    ),
    MedicalRecord(
        id = "3",
        doctorName = "Dra. Rosa Díaz",
        specialty = "Cardióloga",
        date = "Lunes 06, 9:00 AM",
        status = "Completada",
        diagnosis = "Evaluación cardíaca normal",
        details = "Electrocardiograma sin alteraciones. Se recomienda actividad física moderada."
    ),
    MedicalRecord(
        id = "4",
        doctorName = "Dra. Ana Torres",
        specialty = "Cardióloga",
        date = "Jueves 21, 10:30 AM",
        status = "Completada",
        diagnosis = "Presión arterial ligeramente elevada",
        details = "Reducir el consumo de sal, caminar 30 minutos al día y control en 3 meses."
    )
)

@Composable
fun MedicalHistoryScreen(onOpenDrawer: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeaderBlue)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial Médico",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = Color.White
                    )
                }
            }
        }

        // REQUISITO: LazyColumn con los registros médicos
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(medicalRecords, key = { it.id }) { record ->
                MedicalRecordCard(record = record)
            }
        }
    }
}

@Composable
private fun MedicalRecordCard(record: MedicalRecord) {
    val isConfirmed = record.status == "Confirmada"
    var showDiagnosis by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightBlueCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.doctorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                    Text(
                        text = record.specialty,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = record.date,
                        fontSize = 13.sp,
                        color = TextDark
                    )
                }

                // Badge de estado (Verde = Confirmada / Gris = Completada)
                Surface(
                    color = if (isConfirmed) SuccessGreenBg else CompletedGrayBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = record.status,
                        color = if (isConfirmed) SuccessGreen else CompletedGrayText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Diagnóstico breve
            Text(
                text = "Diagnóstico: ${record.diagnosis}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Chip interactivo "Ver Diagnóstico"
            AssistChip(
                onClick = { showDiagnosis = !showDiagnosis },
                label = { Text(if (showDiagnosis) "Ocultar Diagnóstico" else "Ver Diagnóstico") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color.White,
                    labelColor = AccentBlue
                ),
                border = null,
                shape = RoundedCornerShape(20.dp)
            )

            if (showDiagnosis) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = record.details,
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
