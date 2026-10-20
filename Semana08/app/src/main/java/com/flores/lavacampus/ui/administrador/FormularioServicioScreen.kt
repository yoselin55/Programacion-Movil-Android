package com.flores.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.model.Unidad
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.IconoServicio
import com.flores.lavacampus.ui.components.LavaButton
import com.flores.lavacampus.ui.components.LavaTextField
import com.flores.lavacampus.ui.components.OpcionPildora
import com.flores.lavacampus.viewmodel.ServiciosViewModel
import java.util.Locale

// Sirve para crear (servicioId = 0) y para editar un servicio
@Composable
fun FormularioServicioScreen(vm: ServiciosViewModel, servicioId: Int, onVolver: () -> Unit) {
    val servicios by vm.servicios.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val existente = servicios.firstOrNull { it.id == servicioId }
    val primario = MaterialTheme.colorScheme.primary

    // Al abrir se limpia cualquier mensaje anterior
    LaunchedEffect(Unit) { vm.limpiarMensaje() }

    // Los campos se cargan cuando llega el servicio a editar
    var nombre by remember(existente) { mutableStateOf(existente?.nombre ?: "") }
    var unidad by remember(existente) { mutableStateOf(existente?.unidad ?: Unidad.KG.name) }
    var precio by remember(existente) {
        mutableStateOf(existente?.precio?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }
    var horas by remember(existente) { mutableStateOf(existente?.horasEntrega ?: 24) }
    var descripcion by remember(existente) { mutableStateOf(existente?.descripcion ?: "") }
    var icono by remember(existente) { mutableStateOf(existente?.icono ?: "wash") }
    var activo by remember(existente) { mutableStateOf(existente?.activo ?: true) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(if (servicioId == 0) "Nuevo servicio" else "Editar servicio", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LavaTextField(nombre, { nombre = it }, "Nombre del servicio", Modifier.fillMaxWidth())

            Text("Se cobra por", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OpcionPildora("Kilo", unidad == Unidad.KG.name, { unidad = Unidad.KG.name }, Modifier.weight(1f))
                OpcionPildora("Prenda", unidad == Unidad.PRENDA.name, { unidad = Unidad.PRENDA.name }, Modifier.weight(1f))
                OpcionPildora("Unidad", unidad == Unidad.UNIDAD.name, { unidad = Unidad.UNIDAD.name }, Modifier.weight(1f))
            }

            LavaTextField(
                precio, { precio = it.filter { c -> c.isDigit() || c == '.' } }, "Precio (S/)",
                Modifier.fillMaxWidth(), teclado = KeyboardType.Decimal
            )

            Text("Tiempo de entrega", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(24, 48, 72).forEach { h ->
                    OpcionPildora("$h h", horas == h, { horas = h }, Modifier.weight(1f))
                }
            }

            LavaTextField(descripcion, { descripcion = it }, "Descripción", Modifier.fillMaxWidth())

            Text("Ícono", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("wash", "iron", "shirt").forEach { nombreIcono ->
                    val elegido = icono == nombreIcono
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (elegido) MaterialTheme.colorScheme.primaryContainer else Color.White)
                            .border(
                                if (elegido) 1.5.dp else 1.dp,
                                if (elegido) primario else Color(0xFFE2E8F0),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { icono = nombreIcono }
                    ) { IconoServicio(nombreIcono, if (elegido) primario else Color(0xFF64748B), 30.dp) }
                }
            }

            // Servicio activo
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Servicio activo", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        "Los estudiantes podrán pedirlo",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(checked = activo, onCheckedChange = { activo = it })
            }

            mensaje?.let {
                Text(it, color = EstadoPedido.CANCELADO.color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }

            LavaButton(
                texto = "Guardar servicio",
                onClick = {
                    vm.guardar(
                        Servicio(
                            id = existente?.id ?: 0,
                            nombre = nombre,
                            descripcion = descripcion.trim(),
                            unidad = unidad,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            horasEntrega = horas,
                            activo = activo,
                            icono = icono
                        ),
                        onVolver
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedButton(
                onClick = onVolver,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, primario),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Cancelar", fontWeight = FontWeight.SemiBold) }

            // Eliminar: solo si el servicio no esta en ningun pedido (si no, solo se desactiva)
            if (existente != null) {
                TextButton(
                    onClick = { vm.eliminar(existente, onVolver) },
                    colors = ButtonDefaults.textButtonColors(contentColor = EstadoPedido.CANCELADO.color),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Eliminar servicio") }
            }
        }
    }
}
