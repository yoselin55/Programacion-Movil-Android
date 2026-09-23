package com.flores.tecsupfit.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Horario de una clase con sus cupos iniciales
data class ClassSchedule(val time: String, val initialSlots: Int)

// schedule = horario principal que se muestra en la lista del Home
// day = "Hoy", "Mañana" o un día de la semana
data class GymClass(
    val name: String,
    val schedule: String,
    val room: String,
    val day: String,
    val schedules: List<ClassSchedule>
)

// Catálogo de clases compartido entre Home y Detalle (los nombres no se repiten)
val gymClasses = listOf(
    GymClass(
        "Yoga funcional", "7:00 am", "Sala 2", "Hoy",
        listOf(ClassSchedule("7:00 am", 8), ClassSchedule("8:00 am", 3), ClassSchedule("9:00 am", 0))
    ),
    GymClass(
        "Cross Training", "6:00 pm", "Sala 1", "Hoy",
        listOf(ClassSchedule("5:00 pm", 6), ClassSchedule("6:00 pm", 8), ClassSchedule("7:00 pm", 2))
    ),
    GymClass(
        "Spinning", "7:30 pm", "Sala 3", "Hoy",
        listOf(ClassSchedule("6:30 pm", 0), ClassSchedule("7:30 pm", 8), ClassSchedule("8:30 pm", 5))
    ),
    GymClass(
        "Pilates", "8:00 am", "Sala 2", "Mañana",
        listOf(ClassSchedule("8:00 am", 10), ClassSchedule("10:00 am", 4))
    ),
    GymClass(
        "Box funcional", "7:00 pm", "Sala 1", "Mañana",
        listOf(ClassSchedule("6:00 pm", 5), ClassSchedule("7:00 pm", 1))
    ),
    GymClass(
        "Zumba", "6:00 pm", "Sala 3", "Jueves",
        listOf(ClassSchedule("6:00 pm", 12), ClassSchedule("7:30 pm", 7))
    ),
    GymClass(
        "HIIT", "7:00 am", "Sala 1", "Viernes",
        listOf(ClassSchedule("7:00 am", 6), ClassSchedule("6:00 pm", 0))
    )
)

@Composable
fun HomeScreen(userName: String, onClassClick: (GymClass) -> Unit) {
    // rememberSaveable conserva el filtro y la búsqueda al cambiar de pestaña
    var selectedFilter by rememberSaveable { mutableStateOf("Hoy") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filters = listOf("Hoy", "Mañana", "Esta semana")

    // Filtra por día (chip) y por nombre (búsqueda)
    val classList = gymClasses.filter { gymClass ->
        val matchesDay = when (selectedFilter) {
            "Hoy" -> gymClass.day == "Hoy"
            "Mañana" -> gymClass.day == "Mañana"
            else -> true // "Esta semana" muestra todas
        }
        matchesDay && gymClass.name.contains(searchQuery.trim(), ignoreCase = true)
    }

    Scaffold(
        topBar = { TecsupTopBar(title = "TECSUP Fit", subtitle = "Hola, $userName") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Búsqueda por nombre de clase
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar clase") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // LazyRow de Filtros por día
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

            if (classList.isEmpty()) {
                // Sin resultados para el filtro o la búsqueda
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron clases", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
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
                                        "${item.day} · ${item.room} · ${item.schedules.size} horarios",
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
}
