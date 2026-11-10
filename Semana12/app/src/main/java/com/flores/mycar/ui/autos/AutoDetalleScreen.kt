package com.flores.mycar.ui.autos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.layout.ContentScale
import com.flores.mycar.ui.components.DialogoCompra
import com.flores.mycar.ui.components.PrecioConDescuento
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import kotlinx.coroutines.launch
import com.flores.mycar.data.autos.MarcasEnVenta
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.Rol
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.domain.model.claveHorario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.components.COLORES_AUTO
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.ErrorView
import com.flores.mycar.ui.components.EtiquetaEstado
import com.flores.mycar.ui.components.FotoAuto
import com.flores.mycar.ui.components.IconoAtras
import com.flores.mycar.ui.components.PuntoColor
import com.flores.mycar.ui.components.precioEnSoles

/**
 * Detalle del auto: que auto es, galeria de fotos, descripcion, color, precio y año,
 * y las acciones: comprar ahora, agregar al carrito y agendar una cita para verlo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoDetalleScreen(
    viewModel: AutoDetalleViewModel,
    sesion: Sesion,
    onVolver: () -> Unit,
    onIrCarrito: () -> Unit,
    onAgendarCita: (String) -> Unit,
    onComprado: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val ficha by viewModel.ficha.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val compras by viewModel.compras.collectAsStateWithLifecycle()
    LaunchedEffect(sesion.uid) { viewModel.usuario(sesion.uid) }

    val esFavorito = (estado as? UiState.Exito)?.datos?.id?.let { it in compras.favoritos } == true

    val autoActual = (estado as? UiState.Exito)?.datos
    val esAdmin = sesion.rol == Rol.ADMIN

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(autoActual?.nombre ?: "Detalle del auto") },
                navigationIcon = { IconoAtras(onVolver) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    if (autoActual != null) {
                        IconButton(onClick = viewModel::alternarFavorito) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (autoActual != null && !esAdmin) {
                Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PrecioConDescuento(autoActual, MaterialTheme.typography.titleLarge)
                        val enCarrito = autoActual.id in compras.carrito
                        Button(
                            onClick = {
                                if (!enCarrito) {
                                    viewModel.alternarCarrito()
                                    Toast.makeText(contexto, "Se agregó al carrito", Toast.LENGTH_SHORT).show()
                                }
                                onIrCarrito()
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(
                                if (enCarrito) Icons.Filled.ShoppingCart else Icons.Filled.AddShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(if (enCarrito) "Ver carrito" else "Agregar al carrito", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val e = estado) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = onVolver)
                is UiState.Exito -> Contenido(
                    auto = e.datos,
                    ficha = ficha,
                    esAdmin = esAdmin,
                    mensaje = mensaje,
                    onComprar = { color, recojo, direccion -> viewModel.comprar(sesion, e.datos, color, recojo, direccion); onComprado() },
                    onAgendarCita = { onAgendarCita(e.datos.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Contenido(
    auto: Auto,
    ficha: FichaEstado,
    esAdmin: Boolean,
    mensaje: String?,
    onComprar: (String, String, String) -> Unit,
    onAgendarCita: () -> Unit
) {
    var confirmarCompra by rememberSaveable { mutableStateOf(false) }
    var descripcionCompleta by rememberSaveable { mutableStateOf(false) }
    var color by rememberSaveable { mutableStateOf(COLORES_AUTO.first()) }
    var pestana by rememberSaveable { mutableIntStateOf(0) }

    if (confirmarCompra) {
        DialogoCompra(
            resumen = auto.nombre,
            total = precioEnSoles(auto.precio),
            onConfirmar = { recojo, direccion -> confirmarCompra = false; onComprar(color, recojo, direccion) },
            onCancelar = { confirmarCompra = false }
        )
        return
    }

    val fotos = (listOf(auto.imagenUrl) + ficha.ficha?.imagenes.orEmpty()).filter { it.isNotBlank() }.distinct()
    val modeloFicha = ficha.ficha?.modelo.orEmpty()
    val descripcion = ficha.ficha?.descripcion.orEmpty()
    val diagonal = MaterialTheme.colorScheme.primary

    val pager = rememberPagerState(pageCount = { fotos.size.coerceAtLeast(1) })
    val alcance = rememberCoroutineScope()
    val esAutoDeApi = 'm' in auto.id.substringAfter('-')

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Foto sobre una franja diagonal de color
        Box(Modifier.fillMaxWidth().height(290.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val forma = Path().apply {
                    moveTo(size.width * 0.38f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(forma, diagonal.copy(alpha = 0.85f))
            }
            Galeria(fotos, buscando = ficha.cargando, pager = pager, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp))
        }

        // Miniaturas de las fotos
        if (fotos.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fotos.forEachIndexed { indice, foto ->
                    FotoAuto(
                        foto,
                        Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                if (pager.currentPage == indice) 2.dp else 1.dp,
                                if (pager.currentPage == indice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { alcance.launch { pager.animateScrollToPage(indice) } }
                    )
                }
            }
        }

        // Hoja inferior
        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                // Titulo: la marca pequeña arriba y el modelo grande
                Text(
                    auto.marca.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    auto.modelo.ifBlank { auto.marca },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                if (esAutoDeApi) {
                    Text("Modelo ${MarcasEnVenta.ANIO}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                mensaje?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text(it, modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                }

                // Etiquetas del auto
                FlowRow(
                    modifier = Modifier.padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Etiqueta(auto.marca)
                    Etiqueta(if (esAutoDeApi) "Modelo ${MarcasEnVenta.ANIO}" else "Clásico")
                    Etiqueta("Color $color")
                    Etiqueta(precioEnSoles(auto.precio))
                }

                // Pestañas
                PrimaryTabRow(
                    selectedTabIndex = pestana,
                    containerColor = Color.Transparent,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    listOf("Acerca de", "Galería", "Especificaciones").forEachIndexed { indice, titulo ->
                        Tab(selected = pestana == indice, onClick = { pestana = indice }, text = { Text(titulo, maxLines = 1) })
                    }
                }

                AnimatedContent(
                    targetState = pestana,
                    transitionSpec = { fadeIn(tween(260)) togetherWith fadeOut(tween(120)) },
                    label = "pestanas",
                    modifier = Modifier.padding(top = 16.dp)
                ) { actual ->
                    when (actual) {
                        0 -> Column {
                            if (descripcion.isNotBlank()) {
                                Text(
                                    descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = if (descripcionCompleta) Int.MAX_VALUE else 6
                                )
                                TextButton(onClick = { descripcionCompleta = !descripcionCompleta }, contentPadding = PaddingValues(0.dp)) {
                                    Text(if (descripcionCompleta) "Ver menos" else "Ver más")
                                }
                            } else {
                                Text(
                                    if (ficha.cargando) "Buscando la descripción..." else "No se encontró una descripción para este auto.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        1 -> FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (fotos.isEmpty()) {
                                Text("Este auto todavía no tiene fotos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            fotos.forEachIndexed { indice, foto ->
                                FotoAuto(
                                    foto,
                                    Modifier
                                        .size(104.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { alcance.launch { pager.animateScrollToPage(indice) } }
                                )
                            }
                        }

                        else -> Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                TarjetaDato("Marca", auto.marca, Icons.Filled.DirectionsCar, Modifier.weight(1f))
                                TarjetaDato("Modelo", auto.modelo.ifBlank { "-" }, Icons.Filled.Style, Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 10.dp)) {
                                TarjetaDato("Año", if (esAutoDeApi) MarcasEnVenta.ANIO.toString() else "Clásico", Icons.Filled.CalendarMonth, Modifier.weight(1f))
                                TarjetaDato("Color", color, Icons.Filled.Palette, Modifier.weight(1f), colorPunto = color)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 10.dp)) {
                                TarjetaDato("Autos de la marca", if (ficha.modelos.isEmpty()) "-" else ficha.modelos.size.toString(), Icons.Filled.Category, Modifier.weight(1f))
                                TarjetaDato("Precio de referencia", precioEnSoles(auto.precio), Icons.Filled.Sell, Modifier.weight(1f))
                            }
                        }
                    }
                }

                if (!esAdmin) {
                    Text("Elige el color", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 22.dp, bottom = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        COLORES_AUTO.forEach { nombre ->
                            FilterChip(
                                selected = nombre == color,
                                onClick = { color = nombre },
                                label = { Text(nombre) },
                                leadingIcon = { PuntoColor(nombre, tamano = 14.dp) }
                            )
                        }
                    }

                    Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { confirmarCompra = true },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) {
                            Icon(Icons.Filled.Payments, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("Comprar ahora", modifier = Modifier.padding(start = 6.dp))
                        }
                        OutlinedButton(
                            onClick = onAgendarCita,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) {
                            Icon(Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("Agendar cita", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

/** Galeria deslizable con contador y puntos. */
@Composable
private fun Galeria(fotos: List<String>, buscando: Boolean, pager: PagerState, modifier: Modifier = Modifier) {
    if (fotos.isEmpty()) {
        Box(modifier.clip(RoundedCornerShape(24.dp))) {
            FotoAuto("", Modifier.fillMaxSize())
            if (buscando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Text("Buscando fotos...", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 12.dp))
                }
            }
        }
        return
    }

    Box(modifier.clip(RoundedCornerShape(24.dp))) {
        HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { pagina ->
            FotoAuto(fotos[pagina], Modifier.fillMaxSize().background(Color.White), ContentScale.Fit)
        }
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.75f),
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
        ) {
            Text(
                "${pager.currentPage + 1} / ${fotos.size}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        if (fotos.size > 1) {
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(fotos.size) { i ->
                    Box(
                        Modifier
                            .size(if (i == pager.currentPage) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == pager.currentPage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun Fila(etiqueta: String, ultimo: Boolean = false, contenido: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically, content = contenido)
    }
    if (!ultimo) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun Dato(etiqueta: String, valor: String, ultimo: Boolean = false) {
    Fila(etiqueta, ultimo) { Text(valor, style = MaterialTheme.typography.titleMedium) }
}

/** Tarjeta pequeña de una especificacion: icono, nombre y valor. */
@Composable
private fun TarjetaDato(titulo: String, valor: String, icono: ImageVector, modifier: Modifier = Modifier, colorPunto: String? = null) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh, modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Text(titulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (colorPunto != null) PuntoColor(colorPunto, tamano = 14.dp, modifier = Modifier.padding(end = 6.dp))
                Text(valor, style = MaterialTheme.typography.titleLarge, maxLines = 1)
            }
        }
    }
}

/** Etiqueta redonda con un dato corto del auto. */
@Composable
private fun Etiqueta(texto: String) {
    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}
