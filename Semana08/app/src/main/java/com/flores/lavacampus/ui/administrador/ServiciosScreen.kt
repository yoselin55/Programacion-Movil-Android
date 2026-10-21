package com.flores.lavacampus.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.R
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.BannerPantalla
import com.flores.lavacampus.ui.components.FotoConDegradado
import com.flores.lavacampus.ui.components.fotoDeServicio
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BannerPantalla(
                    R.drawable.foto_toallas,
                    "Tus servicios",
                    "${servicios.size} servicios · ${servicios.count { it.activo }} activos",
                    alto = 130.dp
                )

                servicios.forEach { s ->
                    // Un servicio desactivado se ve apagado
                    FotoConDegradado(
                        fotoDeServicio(s.icono),
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .alpha(if (s.activo) 1f else 0.55f),
                        intensidad = 0.9f
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                "Entrega en ${s.horasEntrega} h",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.92f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable { onEditar(s.id) },
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Outlined.Edit, "Editar", tint = primario) }
                        }
                        Row(
                            Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(start = 18.dp, end = 12.dp, bottom = 14.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(s.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(s.textoPrecio(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Switch(checked = s.activo, onCheckedChange = { vm.alternar(s) })
                                Text(if (s.activo) "Activo" else "Inactivo", fontSize = 11.sp, color = Color.White)
                            }
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
