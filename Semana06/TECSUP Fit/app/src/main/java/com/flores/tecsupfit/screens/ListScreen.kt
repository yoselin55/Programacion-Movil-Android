package com.example.semana05_navegacion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ReservationItem(val name: String, val date: String, val status: String)

@Composable
fun ListScreen() {
    val reservations = listOf(
        ReservationItem("Cross Training", "Hoy, 6:00 pm", "Confirmada"),
        ReservationItem("Yoga funcional", "Ayer, 7:00 am", "Completada")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Mis reservas", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(reservations.size) { index ->
                val item = reservations[index]
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
                                .background(
                                    if (item.status == "Confirmada") Color(0xFF0F6A52) else Color.Transparent
                                )
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(item.date, color = Color.Gray, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            val statusBg = if (item.status == "Confirmada") Color(0xFFD4F3E6) else Color(0xFFE5E5E5)
                            val statusColor = if (item.status == "Confirmada") Color(0xFF0F6A52) else Color.Gray

                            Box(
                                modifier = Modifier
                                    .background(statusBg, shape = RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(item.status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}