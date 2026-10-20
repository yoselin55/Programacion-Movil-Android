package com.flores.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.IconoServicio
import com.flores.lavacampus.ui.components.textoPrecio
import com.flores.lavacampus.viewmodel.ServiciosViewModel

// La View solo observa la lista y avisa los clics (ejemplo resuelto del laboratorio)
@Composable
fun ServiciosScreen(
    vm: ServiciosViewModel,
    onVolver: () -> Unit,
    onEditar: (Int) -> Unit,
    onNuevo: () -> Unit
) {
    val servicios by vm.servicios.collectAsState()
    val primario = MaterialTheme.colorScheme.primary

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Servicios y tarifas", onVolver)
        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                servicios.forEach { s ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) { IconoServicio(s.icono, if (s.activo) primario else Color(0xFF94A3B8), 28.dp) }
                            Column(Modifier.weight(1f)) {
                                Text(s.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    "${s.textoPrecio()} · ${s.horasEntrega} h",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Switch(checked = s.activo, onCheckedChange = { vm.alternar(s) })
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = "Editar",
                                tint = primario,
                                modifier = Modifier.clickable { onEditar(s.id) }.padding(4.dp)
                            )
                        }
                    }
                }
                Box(Modifier.padding(bottom = 80.dp))
            }
            FloatingActionButton(
                onClick = onNuevo,
                containerColor = primario,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
            ) { Icon(Icons.Filled.Add, contentDescription = "Nuevo servicio") }
        }
    }
}
