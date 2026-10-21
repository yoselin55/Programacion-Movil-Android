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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.R
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.BannerPantalla
import com.flores.lavacampus.ui.components.EstadoVacio
import com.flores.lavacampus.ui.components.iniciales
import com.flores.lavacampus.ui.components.soles
import com.flores.lavacampus.viewmodel.ClientesViewModel

// Estudiantes con su cantidad de pedidos (sin cancelados) y lo gastado en pedidos pagados.
// Al tocar un cliente se abre su ficha con todos sus pedidos
@Composable
fun ClientesScreen(vm: ClientesViewModel, onVolver: () -> Unit, onAbrir: (Int) -> Unit) {
    val clientes by vm.clientes.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val primario = MaterialTheme.colorScheme.primary
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Clientes", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // La foto de clientes doblando su ropa: se muestra la parte de abajo, donde estan las personas
            BannerPantalla(
                R.drawable.foto_clientes,
                "Tus clientes",
                "${clientes?.size ?: 0} estudiantes registrados",
                alto = 160.dp,
                alineacion = BiasAlignment(0f, 0.85f)
            )

            OutlinedTextField(
                value = busqueda,
                onValueChange = vm::buscar,
                placeholder = { Text("Buscar por nombre o código", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = primario) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primario,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (clientes?.isEmpty() == true) {
                EstadoVacio("No hay estudiantes", "Prueba con otro nombre o código")
            }
            (clientes ?: emptyList()).forEach { c ->
                Card(
                    onClick = { onAbrir(c.id) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(58.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                c.nombre.iniciales(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(c.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(c.codigo, fontSize = 12.sp, color = gris)
                            Text(
                                "${c.cantidadPedidos} pedidos",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primario,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(primario.copy(alpha = 0.14f))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(38.dp).clip(CircleShape).background(primario)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Ver cliente",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(c.totalGastado.soles(), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}
