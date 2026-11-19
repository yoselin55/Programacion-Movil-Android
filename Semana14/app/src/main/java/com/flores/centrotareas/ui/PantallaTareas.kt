package com.flores.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flores.centrotareas.R
import com.flores.centrotareas.model.CargaUi
import com.flores.centrotareas.model.PrimosUi
import com.flores.centrotareas.ui.theme.*
import com.flores.centrotareas.viewmodel.TareasViewModel

// Commit 1: pantalla principal. Commit 10 (rediseño): encabezado con imagen y tarjetas con ícono
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Encabezado()
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Tarjeta(R.drawable.ic_tarea, "Tarea con progreso", "Un trabajo de 4 segundos que corre en segundo plano.") {
                ContenidoTarea(vm)
            }
            Tarjeta(R.drawable.ic_primos, "Contar números primos", "Un cálculo pesado que puedes cancelar.") {
                ContenidoPrimos(vm)
            }
            Tarjeta(R.drawable.ic_cargas, "Dos cargas", "Compara pedir los datos uno tras otro o a la vez.") {
                ContenidoCargas(vm)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

// Commit 10 (rediseño): encabezado con la imagen de portada y el nombre de la app
@Composable
fun Encabezado() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        Image(
            painter = painterResource(R.drawable.img_banner),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd
        )
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 24.dp, top = 24.dp, end = 24.dp)
                .fillMaxWidth(0.55f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Centro de Tareas", style = MaterialTheme.typography.headlineMedium, color = Superficie)
            Text("Tu pantalla nunca se congela", style = MaterialTheme.typography.bodyLarge, color = VerdeClaro)
        }
    }
}

// Commit 10 (rediseño): tarjeta con ícono, título y una frase que explica qué hace
@Composable
fun Tarjeta(icono: Int, titulo: String, explicacion: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, VerdeClaro, RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Image(painterResource(icono), contentDescription = null, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)))
            Column {
                Text(titulo, style = MaterialTheme.typography.titleLarge, color = VerdeOscuro)
                Text(explicacion, style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
            }
        }
        contenido()
    }
}

// Commit 10 (rediseño): botones grandes y redondeados
@Composable
fun BotonPrincipal(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = VerdeOscuro, contentColor = Superficie,
            disabledContainerColor = VerdeClaro, disabledContentColor = TintaSuave
        )
    ) { Text(texto, style = MaterialTheme.typography.labelLarge) }
}

@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, if (enabled) VerdeMedio else VerdeClaro),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdeOscuro, disabledContentColor = TintaSuave)
    ) { Text(texto, style = MaterialTheme.typography.labelLarge) }
}

// Recuadro de resultado con el color indicado
@Composable
fun Recuadro(fondo: Color, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(fondo)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = contenido
    )
}

// Commit 10 (rediseño): etiqueta de estado con color según el mensaje
@Composable
fun Estado(mensaje: String) {
    val fondo = when (mensaje) {
        "Trabajando..." -> Amarillo
        "Terminó" -> VerdeClaro
        "Cancelado" -> RojoClaro
        else -> Crema
    }
    Text(
        text = mensaje,
        style = MaterialTheme.typography.labelLarge,
        color = Tinta,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

// Commit 2: tarjeta Tarea con progreso (contador y tarea larga)
@Composable
fun ContenidoTarea(vm: TareasViewModel) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    // Commit 2: prueba de vida: si deja de sumar, la pantalla está congelada
    var toques by rememberSaveable { mutableIntStateOf(0) }

    Button(
        onClick = { toques++ },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Amarillo, contentColor = Tinta)
    ) { Text("Tócame: $toques", style = MaterialTheme.typography.titleMedium) }
    Text(
        "Si este número sigue subiendo mientras la tarea trabaja, la pantalla no está congelada.",
        style = MaterialTheme.typography.bodyMedium, color = TintaSuave
    )

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Commit 5 (Paso 4): Iniciar se deshabilita mientras corre; Cancelar solo mientras corre
        BotonPrincipal("Iniciar", { vm.tareaLarga() }, Modifier.weight(1f), enabled = !corriendo)
        BotonSecundario("Cancelar", { vm.cancelar() }, Modifier.weight(1f), enabled = corriendo)
    }

    // Commit 4 (Paso 3): barra de progreso y porcentaje
    LinearProgressIndicator(
        progress = { progreso },
        modifier = Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(50)),
        color = VerdeOscuro,
        trackColor = VerdeClaro,
        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
        drawStopIndicator = {}
    )
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text("${(progreso * 100).toInt()} %", style = MaterialTheme.typography.titleLarge, color = VerdeOscuro)
        Estado(mensaje)
    }
}

// Commit 6: tarjeta Contar números primos
@Composable
fun ContenidoPrimos(vm: TareasViewModel) {
    val estado by vm.primos.collectAsState()
    val avance by vm.primosProgreso.collectAsState()   // Commit 11 (Mejora 2)
    var texto by rememberSaveable { mutableStateOf("3000000") }
    val calculando = estado is PrimosUi.Calculando

    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text("Contar primos hasta") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth()
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BotonPrincipal(
            "Contar",
            {
                val n = texto.toIntOrNull()
                if (n != null && n >= 2) vm.contar(n)   // ignora texto no numérico o menor que 2
            },
            Modifier.weight(1f),
            enabled = !calculando
        )
        BotonSecundario("Cancelar", { vm.cancelarPrimos() }, Modifier.weight(1f), enabled = calculando)
    }
    when (val e = estado) {
        PrimosUi.Inicial -> {}
        // Commit 11 (Mejora 2): barra y porcentaje mientras cuenta
        PrimosUi.Calculando -> {
            LinearProgressIndicator(
                progress = { avance },
                modifier = Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(50)),
                color = Amarillo,
                trackColor = VerdeClaro,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                drawStopIndicator = {}
            )
            Text("Calculando... ${(avance * 100).toInt()} %", style = MaterialTheme.typography.titleMedium, color = VerdeOscuro)
        }
        PrimosUi.Cancelado -> Estado("Cancelado")
        is PrimosUi.Listo -> Recuadro(VerdeClaro) {
            Text("${e.cantidad} primos", style = MaterialTheme.typography.titleLarge, color = VerdeOscuro)
            Text("encontrados en ${e.milisegundos} ms", style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
        }
    }
}

// Commit 7: tarjeta Dos cargas (una por una / a la vez)
@Composable
fun ContenidoCargas(vm: TareasViewModel) {
    val estado by vm.carga.collectAsState()
    val cargando = estado is CargaUi.Cargando
    val simularError by vm.simularError.collectAsState()

    // Commit 8 (Paso 7): interruptor para provocar el error
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Switch(
            checked = simularError,
            onCheckedChange = { vm.cambiarSimularError(it) },
            colors = SwitchDefaults.colors(
                checkedTrackColor = VerdeOscuro, checkedThumbColor = Superficie,
                uncheckedTrackColor = VerdeClaro, uncheckedThumbColor = VerdeMedio, uncheckedBorderColor = VerdeMedio
            )
        )
        Text("Simular error", style = MaterialTheme.typography.bodyLarge)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BotonSecundario("Una por una", { vm.cargarUnaPorUna() }, Modifier.weight(1f), enabled = !cargando)
        BotonPrincipal("A la vez", { vm.cargarALaVez() }, Modifier.weight(1f), enabled = !cargando)
    }
    when (val e = estado) {
        CargaUi.Inicial -> {}
        CargaUi.Cargando -> CircularProgressIndicator(color = VerdeOscuro, trackColor = VerdeClaro)
        is CargaUi.Exito -> Recuadro(VerdeClaro) {
            Text("Modo: ${e.modo}", style = MaterialTheme.typography.titleMedium, color = VerdeOscuro)
            Text("Perfil: ${e.perfil}", style = MaterialTheme.typography.bodyMedium)
            Text("Pedidos: ${e.pedidos}", style = MaterialTheme.typography.bodyMedium)
            Text("Tiempo: ${e.milisegundos} ms", style = MaterialTheme.typography.bodyMedium)
        }
        is CargaUi.Error -> Recuadro(MaterialTheme.colorScheme.errorContainer) {
            Text(e.mensaje, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onErrorContainer)
            BotonPrincipal("Reintentar", { vm.cargarALaVez() })
        }
    }
}
