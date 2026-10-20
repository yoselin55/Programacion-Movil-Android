package com.flores.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.flores.lavacampus.ui.components.iniciales
import com.flores.lavacampus.ui.components.soles
import com.flores.lavacampus.viewmodel.ClientesViewModel

// Estudiantes con su cantidad de pedidos (sin cancelados) y lo gastado en pedidos pagados
@Composable
fun ClientesScreen(vm: ClientesViewModel, onVolver: () -> Unit) {
    val clientes by vm.clientes.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val primario = MaterialTheme.colorScheme.primary
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Clientes", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = vm::buscar,
                placeholder = { Text("Buscar estudiante", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = primario) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primario,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (clientes.isEmpty()) {
                Text("No hay estudiantes para mostrar", color = gris, modifier = Modifier.padding(vertical = 24.dp))
            }
            clientes.forEach { c ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(46.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) { Text(c.nombre.iniciales(), color = primario, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(c.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(c.codigo, fontSize = 11.sp, color = gris)
                            Text(
                                "${c.cantidadPedidos} pedidos",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primario
                            )
                        }
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = gris)
                            Text(c.totalGastado.soles(), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
