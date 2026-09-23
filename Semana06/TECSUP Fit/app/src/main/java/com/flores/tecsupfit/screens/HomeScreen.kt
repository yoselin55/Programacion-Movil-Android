package com.flores.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Horario de una clase con sus cupos iniciales
data class ClassSchedule(val time: String, val initialSlots: Int)

// schedule = horario principal que se muestra en la lista del Home
data class GymClass(
    val name: String,
    val schedule: String,
    val room: String,
    val schedules: List<ClassSchedule>
)

// Catálogo de clases compartido entre Home y Detalle
val gymClasses = listOf(
    GymClass(
        "Yoga funcional", "7:00 am", "Sala 2",
        listOf(ClassSchedule("7:00 am", 8), ClassSchedule("8:00 am", 3), ClassSchedule("9:00 am", 0))
    ),
    GymClass(
        "Cross Training", "6:00 pm", "Sala 1",
        listOf(ClassSchedule("5:00 pm", 6), ClassSchedule("6:00 pm", 8), ClassSchedule("7:00 pm", 2))
    ),
    GymClass(
        "Spinning", "7:30 pm", "Sala 3",
        listOf(ClassSchedule("6:30 pm", 0), ClassSchedule("7:30 pm", 8), ClassSchedule("8:30 pm", 5))
    )
)

@Composable
fun HomeScreen(userName: String, onClassClick: (GymClass) -> Unit) {
    var selectedFilter by remember { mutableStateOf("Hoy") }
    val filters = listOf("Hoy", "Esta semana")

    val classList = gymClasses

    Scaffold(
        topBar = { TecsupTopBar(title = "TECSUP Fit", subtitle = "Hola, $userName") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // LazyRow de Filtros
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0F6A52),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Clases disponibles", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // LazyColumn de Clases
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(classList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClassClick(item) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color(0xFF0F6A52),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    "${item.room} · ${item.schedules.size} horarios",
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
}