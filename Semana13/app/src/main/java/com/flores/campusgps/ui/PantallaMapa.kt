package com.flores.campusgps.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.flores.campusgps.model.Lugar
import com.flores.campusgps.model.LugarConDistancia
import com.flores.campusgps.model.UbicacionUi
import com.flores.campusgps.ui.theme.Azul
import com.flores.campusgps.ui.theme.AzulClaro
import com.flores.campusgps.ui.theme.Fondo
import com.flores.campusgps.ui.theme.Tinta
import com.flores.campusgps.util.formatearDistancia
import com.flores.campusgps.util.formatearTiempoAPie
import com.flores.campusgps.viewmodel.MapaViewModel

// Mejora 3: abre Google Maps (o el navegador) con indicaciones a pie hacia el lugar elegido.
private fun abrirIndicaciones(contexto: Context, lugar: Lugar) {
    val uri = Uri.parse(
        "https://www.google.com/maps/dir/?api=1&destination=${lugar.latitud},${lugar.longitud}&travelmode=walking"
    )
    try {
        contexto.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: ActivityNotFoundException) {
        // Si no hay ninguna app que abra el enlace, no pasa nada: la app no se cierra.
    }
}

// Altura visible del panel inferior cuando está recogido (incluye el asa y la barra de gestos).
private val AlturaPanel = 192.dp

// Commit 1: pantalla simple con el botón y la latitud/longitud; solo dibuja el estado del ViewModel.
// Mejora 3: pantalla de mapa completa, estilo Google Maps: el buscador y los chips flotan sobre el mapa,
// tu ubicación es el punto azul nativo y un panel deslizable abajo muestra el lugar y la lista.
// La barra superior y el menú lateral viven en PantallaPrincipal.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel,
    nombre: String,
    onAbrirMenu: () -> Unit,
    onAbrirPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.estado.collectAsState()
    // Mejora 2: se usan los lugares visibles (ya filtrados por la categoría y por el texto buscado).
    val lugares by vm.lugaresVisibles.collectAsState()
    val categoria by vm.categoria.collectAsState()
    val consulta by vm.consulta.collectAsState()
    val busquedasRecientes by vm.busquedasRecientes.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    // Commit 8: el lugar más cercano viene del ViewModel (null si todavía no hay ubicación).
    val masCercano by vm.masCercano.collectAsState()
    // Mejora 1: estado del seguimiento en vivo y aviso de llegada.
    val siguiendo by vm.siguiendo.collectAsState()
    val llegada by vm.llegada.collectAsState()
    // Mejora 3: camino a pie por las calles hasta el destino elegido.
    val ruta by vm.ruta.collectAsState()

    // Commit 3: el contexto se usa aquí (en la pantalla), no en el ViewModel.
    val contexto = LocalContext.current

    fun tienePermiso(): Boolean =
        ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    // Mejora 3: el punto azul del mapa solo se activa si hay permiso (se vuelve a revisar al volver a la app).
    var permiso by remember { mutableStateOf(tienePermiso()) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { permiso = tienePermiso() }

    // Mejora 1: si la app se va a segundo plano (por ejemplo, al abrir Cómo llegar) se detiene el seguimiento.
    // Así no se gasta batería pidiendo ubicaciones cuando nadie mira la pantalla.
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { vm.detenerSeguimiento() }

    // Mejora 1: acción que se ejecuta cuando el usuario concede el permiso (buscar o iniciar seguimiento).
    var accionPendiente by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Commit 3: lanza el cuadro de permiso de Android; si acepta ejecuta la acción, si niega avisa y no se cierra.
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        permiso = resultado.values.any { it }
        if (permiso) accionPendiente?.invoke() else vm.permisoDenegado()
        accionPendiente = null
    }

    // Commit 3: si ya tiene permiso (FINE o COARSE) ejecuta la acción directo; si no, lo pide primero.
    // Mejora 1: ahora sirve para cualquier acción que necesite ubicación.
    fun conPermiso(accion: () -> Unit) {
        if (tienePermiso()) {
            accion()
        } else {
            accionPendiente = accion
            lanzador.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    fun alPulsar() = conPermiso { vm.buscar() }

    // Mejora 3: al entrar al mapa se pide el permiso y se busca tu ubicación sola, como hace Google Maps.
    // Ya no hace falta un botón "Obtener mi ubicación": el botón de mi ubicación del mapa hace lo mismo.
    LaunchedEffect(Unit) {
        if (estado is UbicacionUi.Inicial) alPulsar()
    }

    // El lugar al que va el usuario (si eligió uno) o, si no, el más cercano; es el que resume el panel.
    val destino: LugarConDistancia? = lugares.firstOrNull { it.lugar.id == seleccionado }
    val resumen: LugarConDistancia? = destino ?: lugares.firstOrNull { it.lugar.id == masCercano?.id }

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        sheetPeekHeight = AlturaPanel,
        sheetContainerColor = Fondo,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetShadowElevation = 14.dp,
        sheetDragHandle = { BottomSheetDefaults.DragHandle(color = Azul) },
        scaffoldState = rememberBottomSheetScaffoldState(),
        sheetContent = {
            Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Resumen: errores, búsqueda en curso, o el lugar elegido / más cercano.
                    when (val e = estado) {
                        // Commit 9: el error se muestra con su mensaje y su botón (Abrir ajustes o Reintentar).
                        is UbicacionUi.Error -> MensajeError(e, onReintentar = { alPulsar() })
                        UbicacionUi.Buscando -> Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = Tinta)
                            Spacer(Modifier.width(12.dp))
                            Text("Buscando tu ubicación…")
                        }
                        else -> if (resumen != null) {
                            ResumenLugar(resumen, esDestino = destino != null)
                        } else {
                            Text(
                                "Pulsa el botón de mi ubicación del mapa para ver los lugares cerca de ti",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Acciones rápidas, en una fila que se desplaza si no caben todas.
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (destino != null) {
                            Button(
                                onClick = { abrirIndicaciones(contexto, destino.lugar) },
                                shape = RoundedCornerShape(50)
                            ) {
                                Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Cómo llegar")
                            }
                        }

                        // Mejora 1: botón Iniciar/Detener seguimiento; al iniciar pide permiso si hace falta.
                        FilledTonalButton(
                            onClick = {
                                if (siguiendo) vm.detenerSeguimiento() else conPermiso { vm.alternarSeguimiento() }
                            },
                            shape = RoundedCornerShape(50)
                        ) {
                            if (siguiendo) {
                                // Punto rojo que parpadea mientras el seguimiento está activo.
                                val parpadeo by rememberInfiniteTransition(label = "vivo").animateFloat(
                                    initialValue = 0.2f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                                    label = "alfa"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .alpha(parpadeo)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Icon(
                                if (siguiendo) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (siguiendo) "Detener seguimiento" else "Iniciar seguimiento")
                        }

                        // Commit 8: botón con borde para ir al lugar más cercano (si no es ya el elegido).
                        if (masCercano != null && masCercano?.id != destino?.lugar?.id) {
                            OutlinedButton(
                                onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Tinta),
                                border = BorderStroke(1.dp, Tinta),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Ir al más cercano")
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = AzulClaro)

                // Commit 7: la lista va dentro del panel; al deslizarlo hacia arriba se ve completa.
                if (lugares.isEmpty()) {
                    // Mejora 3: si la búsqueda o el filtro no encuentran nada, se avisa en vez de dejar un hueco.
                    Column(
                        modifier = Modifier.fillMaxWidth().height(260.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "No encontramos lugares con esa búsqueda",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    ListaLugares(
                        lugares = lugares,
                        seleccionado = seleccionado,
                        onClick = { id -> vm.seleccionar(id) },
                        masCercanoId = masCercano?.id,
                        onComoLlegar = { lugar -> abrirIndicaciones(contexto, lugar) },
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    )
                }
            }
        }
    ) {
        // Contenido detrás del panel: el mapa a pantalla completa con el buscador y los chips encima.
        Box(modifier = Modifier.fillMaxSize()) {
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercano = masCercano?.id,
                siguiendo = siguiendo,
                ruta = ruta,
                usarPuntoAzul = permiso,
                // Deja libre el espacio del buscador (arriba) y del panel (abajo) para el botón de mi ubicación.
                contentPadding = PaddingValues(top = 150.dp, bottom = AlturaPanel),
                // Mejora 3: el botón de mi ubicación del mapa también actualiza las distancias.
                onMiUbicacion = { alPulsar() },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 8.dp)
            ) {
                // Mejora 3 (búsqueda): escribir filtra los lugares del mapa y de la lista al instante.
                BarraBusqueda(
                    consulta = consulta,
                    onCambio = { vm.cambiarConsulta(it) },
                    onBuscar = { vm.guardarBusqueda() },
                    busquedasRecientes = busquedasRecientes,
                    onElegirBusqueda = { vm.cambiarConsulta(it) },
                    inicial = nombre.take(1).uppercase(),
                    onMenu = onAbrirMenu,
                    onPerfil = onAbrirPerfil,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(Modifier.height(8.dp))

                // Mejora 2: chips para filtrar por categoría (Todos, Estudio, Comida, Eventos, Servicios).
                val coloresChip = FilterChipDefaults.filterChipColors(
                    containerColor = Fondo,
                    labelColor = Tinta,
                    iconColor = Tinta,
                    selectedContainerColor = Azul,
                    selectedLabelColor = Tinta,
                    selectedLeadingIconColor = Tinta
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = categoria == null,
                            onClick = { vm.filtrarPor(null) },
                            label = { Text("Todos") },
                            colors = coloresChip,
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = categoria == null,
                                borderColor = Azul,
                                selectedBorderColor = Azul
                            )
                        )
                    }
                    items(vm.categorias) { cat ->
                        FilterChip(
                            selected = categoria == cat,
                            onClick = { vm.filtrarPor(cat) },
                            label = { Text(cat) },
                            leadingIcon = {
                                Icon(iconoDeCategoria(cat), contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            colors = coloresChip,
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = categoria == cat,
                                borderColor = Azul,
                                selectedBorderColor = Azul
                            )
                        )
                    }
                }
            }

            // Mejora 1: aviso cuando llegas al destino elegido (a menos de 20 m); flota justo sobre el panel.
            llegada?.let { lugar ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 12.dp, end = 12.dp, bottom = AlturaPanel + 12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Fondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 14.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "¡Llegaste a ${lugar.nombre}!",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = { vm.cerrarLlegada() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Tinta)
                        ) { Text("Entendido") }
                    }
                }
            }
        }
    }
}

// Mejora 3: resumen del lugar en la parte recogida del panel: ícono, nombre y una línea con distancia y tiempo.
@Composable
private fun ResumenLugar(item: LugarConDistancia, esDestino: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(46.dp).clip(CircleShape).background(Azul),
            contentAlignment = Alignment.Center
        ) {
            Icon(iconoDeCategoria(item.lugar.categoria), contentDescription = null, tint = Tinta)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(item.lugar.nombre, style = MaterialTheme.typography.titleMedium)
            val detalle = item.metros
                ?.let { "${formatearDistancia(it)} · ${formatearTiempoAPie(it)}" }
                ?: "Sin calcular"
            Text(
                (if (esDestino) "Tu destino · " else "Más cercano · ") + detalle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
