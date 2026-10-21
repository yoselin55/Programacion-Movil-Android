package com.flores.lavacampus.ui.estudiante

import com.flores.lavacampus.ui.components.FotoServicio
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.model.Descuento
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.model.Unidad
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.IconoServicio
import com.flores.lavacampus.ui.components.LavaButton
import com.flores.lavacampus.ui.components.LavaTextField
import com.flores.lavacampus.ui.components.SelectorCantidad
import com.flores.lavacampus.ui.components.fechaCorta
import com.flores.lavacampus.ui.components.soles
import com.flores.lavacampus.ui.components.textoPrecio
import com.flores.lavacampus.viewmodel.NuevoPedidoViewModel
import java.util.Locale

// La View solo muestra el estado y avisa los clics; las reglas estan en el ViewModel
@Composable
fun NuevoPedidoScreen(vm: NuevoPedidoViewModel, onVolver: () -> Unit, onPedidoCreado: () -> Unit) {
    val servicios by vm.servicios.collectAsState()
    val cantidades by vm.cantidades.collectAsState()
    val total by vm.total.collectAsState()
    val subtotal by vm.subtotal.collectAsState()
    val descuento by vm.descuento.collectAsState()
    val kilos by vm.kilosLavado.collectAsState()
    val fechaEntrega by vm.fechaEntrega.collectAsState()
    val nota by vm.nota.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val aviso by vm.aviso.collectAsState()
    val puedeConfirmar by vm.puedeConfirmar.collectAsState()
    val recibiendo by vm.recibiendoPedidos.collectAsState()
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val primario = MaterialTheme.colorScheme.primary

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Nuevo pedido", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!recibiendo) {
                Text(
                    "La lavandería no está recibiendo pedidos",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEE2E2))
                        .padding(12.dp)
                )
            }
            // Aviso al repetir un pedido: que se cargo y que cambio
            aviso?.let {
                Text(
                    it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primario,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(12.dp)
                )
            }
            Text("¿Qué necesitas?", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            servicios.forEach { s ->
                val unidad = Unidad.de(s.unidad)
                FilaServicio(
                    servicio = s,
                    cantidad = cantidades[s.id] ?: 0.0,
                    onMenos = { vm.cambiarCantidad(s.id, -unidad.paso) },
                    onMas = { vm.cambiarCantidad(s.id, unidad.paso) }
                )
            }

            LavaTextField(
                value = nota,
                onValueChange = vm::cambiarNota,
                etiqueta = "Nota (opcional)",
                modifier = Modifier.fillMaxWidth()
            )

            // Resumen: una linea por servicio elegido
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, primario.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Resumen", fontWeight = FontWeight.Bold, color = primario, fontSize = 13.sp)
                    val elegidos = servicios.filter { (cantidades[it.id] ?: 0.0) > 0 }
                    if (elegidos.isEmpty()) Text("Aún no elegiste servicios", color = gris, fontSize = 12.sp)
                    elegidos.forEach { s ->
                        val c = cantidades.getValue(s.id)
                        val u = Unidad.de(s.unidad)
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(s.nombre, fontSize = 12.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${texto(c)} ${if (c == 1.0) u.texto else u.plural}",
                                fontSize = 12.sp,
                                color = gris,
                                modifier = Modifier.weight(1f)
                            )
                            Text((c * s.precio).soles(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider(color = primario.copy(alpha = 0.25f))
                    // Descuento por volumen: visible solo cuando aplica
                    if (descuento > 0) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Subtotal", fontSize = 12.sp, color = gris, modifier = Modifier.weight(1f))
                            Text(subtotal.soles(), fontSize = 12.sp, color = gris)
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Descuento por volumen (10 %)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EstadoPedido.LISTO.color,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "-" + descuento.soles(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EstadoPedido.LISTO.color
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Total", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(total.soles(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = primario)
                    }
                }
            }

            // Pista del descuento: aparece cuando hay lavado y todavia no llega a 5 kg, o cuando ya aplica
            if (kilos > 0) {
                val aplica = Descuento.aplica(kilos)
                Text(
                    if (aplica) "¡Descuento por volumen aplicado: 10 % en tu lavado!"
                    else "Más de 5 kg de lavado: 10 % de descuento (llevas ${texto(kilos)} kg)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (aplica) EstadoPedido.LISTO.color else primario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (aplica) EstadoPedido.LISTO.color.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        )
                        .padding(10.dp)
                )
            }

            Text(
                "Entrega estimada: ${fechaEntrega?.fechaCorta() ?: "—"}",
                fontSize = 12.sp,
                color = gris,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            mensaje?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        LavaButton(
            texto = "Confirmar pedido",
            onClick = { vm.confirmar(onPedidoCreado) },
            habilitado = puedeConfirmar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}

@Composable
private fun FilaServicio(servicio: Servicio, cantidad: Double, onMenos: () -> Unit, onMas: () -> Unit) {
    val unidad = Unidad.de(servicio.unidad)
    val elegido = cantidad > 0
    val primario = MaterialTheme.colorScheme.primary

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (elegido) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (elegido) 1.5.dp else 1.dp,
            if (elegido) primario else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                // Foto del servicio con su icono dibujado con Canvas
                FotoServicio(servicio.icono, 60.dp)
                Column {
                    Text(servicio.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "${servicio.textoPrecio()} · ${servicio.horasEntrega} h",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                SelectorCantidad(
                    valor = cantidad,
                    paso = unidad.paso,
                    maximo = unidad.maximo,
                    onMenos = onMenos,
                    onMas = onMas,
                    sufijo = if (unidad == Unidad.KG) "kg" else ""
                )
            }
        }
    }
}

// 3.5 -> "3.5", 5.0 -> "5"
private fun texto(c: Double): String =
    if (c % 1.0 == 0.0) c.toInt().toString() else String.format(Locale.US, "%.1f", c)
