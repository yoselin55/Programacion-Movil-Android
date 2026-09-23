package com.flores.tecsupfit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Rutina de entrenamiento; completed indica si el usuario ya la hizo
data class Routine(
    val name: String,
    val level: String,
    val duration: String,
    val completed: Boolean = false
)

// Rutinas de ejemplo que se cargan al iniciar la app
val sampleRoutines = listOf(
    Routine("Full body principiante", "Básico", "20 min"),
    Routine("Core y abdominales", "Básico", "15 min"),
    Routine("Piernas y glúteos", "Intermedio", "30 min"),
    Routine("Tren superior", "Intermedio", "25 min"),
    Routine("HIIT quema grasa", "Avanzado", "20 min"),
    Routine("Movilidad y estiramiento", "Básico", "10 min")
)

@Composable
fun RoutinesScreen(
    routines: List<Routine>,
    onToggleRoutine: (index: Int) -> Unit
) {
    val completedCount = routines.count { it.completed }

    Scaffold(
        topBar = {
            TecsupTopBar(title = "Rutinas", subtitle = "$completedCount de ${routines.size} completadas")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(routines) { index, routine ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (routine.completed) Color(0xFFE2F3ED) else Color(0xFFF2F2F2)
                    ),
                    onClick = { onToggleRoutine(index) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = routine.completed,
                            onCheckedChange = { onToggleRoutine(index) },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0F6A52))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                routine.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                // Tachado cuando está completada
                                textDecoration = if (routine.completed) TextDecoration.LineThrough else null
                            )
                            Text(
                                "${routine.level} · ${routine.duration}",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
