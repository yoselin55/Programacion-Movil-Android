package com.flores.lavacampus.ui.estudiante

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.flores.lavacampus.R
import com.flores.lavacampus.data.prefs.FotoPerfil
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.ui.components.BotonMenu
import com.flores.lavacampus.ui.components.FotoConDegradado
import com.flores.lavacampus.ui.components.IconoServicio
import com.flores.lavacampus.ui.components.codigoPedido
import com.flores.lavacampus.ui.components.fechaCorta
import com.flores.lavacampus.viewmodel.InicioViewModel

// Inicio del estudiante: una cabecera con el video y la parte de abajo curva, y encima de ella pocas
// tarjetas blancas: su pedido activo, los servicios y dos fotos
@Composable
fun InicioScreen(
    vm: InicioViewModel,
    onNuevoPedido: () -> Unit,
    onVerPedido: (Int) -> Unit,
    onMisPedidos: () -> Unit,
    onTarifas: () -> Unit,
    foto: FotoPerfil? = null,
    onMenu: () -> Unit = {}
) {
    // La cabecera es oscura y llega hasta arriba: los iconos de la barra del telefono pasan a ser claros
    IconosClarosEnLaBarra()

    // null = todavia cargando: no se muestran pantallas vacias hasta saber si hay pedidos
    val ui by vm.ui.collectAsState()
    val cargado = ui != null
    val activo = ui?.activo
    val servicios = ui?.servicios ?: emptyList()
    val usuario = vm.usuario
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val forma = RoundedCornerShape(28.dp)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Cabecera: video de la lavanderia, curva abajo, con el menu, el saludo y un boton
        FotoConDegradado(
            R.drawable.foto_lavanderia,
            Modifier
                .fillMaxWidth()
                .height(350.dp)
                .clip(RoundedCornerShape(bottomStart = 44.dp, bottomEnd = 44.dp)),
            intensidad = 0.92f,
            veloSuperior = 0.6f,
            video = R.raw.video_lavanderia
        ) {
            Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BotonMenu(onMenu, sobreFoto = true)
                    Column(Modifier.weight(1f).padding(start = 14.dp)) {
                        Text("Hola, ${usuario.nombre.substringBefore(" ")}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(usuario.codigo, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("¿Qué lavamos hoy?", fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(14.dp))
                Text(
                    "Pedir ahora",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable { onNuevoPedido() }
                        .padding(horizontal = 26.dp, vertical = 11.dp)
                )
                Spacer(Modifier.height(48.dp)) // lugar para que las tarjetas se monten sobre la curva
            }
        }

        // Hasta que Room entrega los pedidos solo se ve la cabecera (no depende de ellos).
        // Asi nunca aparece una pantalla intermedia: el resto entra completo de una vez
        if (!cargado) return@Column

        Column(
            Modifier.montarSobre(44.dp).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // Pedido activo: una tarjeta corta con el avance en cuatro barras
            if (activo != null) {
                val estado = EstadoPedido.valueOf(activo.pedido.estado)
                Card(
                    onClick = { onVerPedido(activo.pedido.id) },
                    shape = forma,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.LocalLaundryService, null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Pedido ${activo.pedido.id.codigoPedido()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(estado.texto, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = estado.color)
                            }
                            BarraProgreso(estado, MaterialTheme.colorScheme.primary)
                            Text(
                                if (estado == EstadoPedido.LISTO && activo.pedido.casillero != null)
                                    "Casillero ${activo.pedido.casillero}"
                                else "Entrega ${activo.pedido.fechaEntrega.fechaCorta()}",
                                fontSize = 12.sp,
                                color = gris
                            )
                        }
                    }
                }
            }

            // Servicios: una sola tarjeta con los tres iconos. Al tocar uno se empieza un pedido
            if (servicios.isNotEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, forma)
                        .clip(forma)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), forma)
                        .padding(horizontal = 10.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Servicios", fontWeight = FontWeight.Bold, fontSize = 19.sp, modifier = Modifier.weight(1f))
                        Row(
                            Modifier.clickable { onTarifas() },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Ver tarifas", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        servicios.take(3).forEach { s ->
                            Column(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { onNuevoPedido() }
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    Modifier.size(58.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) { IconoServicio(s.icono, MaterialTheme.colorScheme.primary, 32.dp) }
                                Text(
                                    s.nombre,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    minLines = 2, // los tres quedan alineados aunque el nombre sea corto
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            // Para ti: solo dos fotos, una al lado de la otra
            Text("Para ti", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FotoConDegradado(
                    R.drawable.foto_atencion,
                    Modifier.weight(1.4f).height(200.dp).clip(RoundedCornerShape(26.dp)).clickable { onNuevoPedido() },
                    intensidad = 0.95f,
                    alineacion = BiasAlignment(0f, -0.6f),
                    inicioVelo = 0.55f
                ) {
                    Text(
                        "Atención personalizada",
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    )
                }
                FotoConDegradado(
                    R.drawable.foto_camisas,
                    Modifier.weight(1f).height(200.dp).clip(RoundedCornerShape(26.dp)).clickable { onNuevoPedido() },
                    intensidad = 0.9f
                ) {
                    Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                        Text("-10 %", fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("en +5 kg", fontSize = 12.sp, color = Color.White.copy(alpha = 0.92f))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// Sube un bloque sobre el anterior (las tarjetas se montan sobre la curva de la cabecera) y acorta
// su alto en lo mismo, para que no quede un hueco al final
private fun Modifier.montarSobre(alto: Dp): Modifier = layout { medible, restricciones ->
    val colocable = medible.measure(restricciones)
    val subida = alto.roundToPx()
    layout(colocable.width, colocable.height - subida) { colocable.placeRelative(0, -subida) }
}

// Avance del pedido: cuatro barras (Recibido, En proceso, Listo, Entregado)
@Composable
private fun BarraProgreso(estado: EstadoPedido, color: Color) {
    val avance = estado.ordinal
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(4) { i ->
            Box(
                Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color.copy(alpha = if (i <= avance) 1f else 0.18f))
            )
        }
    }
}

// Pone claros los iconos de la barra de estado mientras esta pantalla esta abierta (la cabecera es oscura)
@Composable
private fun IconosClarosEnLaBarra() {
    val vista = LocalView.current
    DisposableEffect(vista) {
        var contexto = vista.context
        while (contexto is ContextWrapper && contexto !is Activity) contexto = contexto.baseContext
        val control = (contexto as? Activity)?.window?.let { WindowCompat.getInsetsController(it, vista) }
        val anterior = control?.isAppearanceLightStatusBars
        control?.isAppearanceLightStatusBars = false
        onDispose {
            if (control != null && anterior != null) control.isAppearanceLightStatusBars = anterior
        }
    }
}
