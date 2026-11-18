package com.flores.centrotareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.flores.centrotareas.model.CargaUi
import com.flores.centrotareas.model.PrimosUi
import com.flores.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Commit 1: pantalla con barra superior y tres tarjetas (proyecto base)
fun PantallaTareas(vm: TareasViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Tarjeta("Tarea con progreso") { ContenidoTarea(vm) }
            Tarjeta("Contar números primos") { ContenidoPrimos(vm) }
            Tarjeta("Dos cargas") { ContenidoCargas(vm) }
        }
    }
}

@Composable
fun Tarjeta(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            contenido()
        }
    }
}

@Composable
// Commit 2: tarjeta Tarea con progreso (contador y tarea larga)
fun ContenidoTarea(vm: TareasViewModel) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    // Commit 2: prueba de vida: si deja de sumar, la pantalla está congelada
    var toques by rememberSaveable { mutableIntStateOf(0) }

    Button(onClick = { toques++ }) { Text("Tócame: $toques") }
    // Commit 3 (Paso 2): ahora la tarea corre en segundo plano y el contador sigue sumando
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Commit 5 (Paso 4): Iniciar se deshabilita mientras corre; Cancelar solo mientras corre
        Button(onClick = { vm.tareaLarga() }, enabled = !corriendo) { Text("Iniciar") }
        OutlinedButton(onClick = { vm.cancelar() }, enabled = corriendo) { Text("Cancelar") }
    }

    // Commit 4 (Paso 3): barra de progreso y porcentaje
    LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth())
    Text("${(progreso * 100).toInt()} %")
    Text(mensaje)
}

// Commit 6: recuadro de resultado con el color indicado
@Composable
fun Recuadro(fondo: Color, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = contenido
    )
}

val VerdeExito = Color(0xFFDDF3DF)

@Composable
// Commit 6: tarjeta Contar números primos
fun ContenidoPrimos(vm: TareasViewModel) {
    val estado by vm.primos.collectAsState()
    var texto by rememberSaveable { mutableStateOf("3000000") }
    val calculando = estado is PrimosUi.Calculando

    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text("Contar primos hasta") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                val n = texto.toIntOrNull()
                if (n != null && n >= 2) vm.contar(n)   // ignora texto no numérico o menor que 2
            },
            enabled = !calculando
        ) { Text("Contar") }
        OutlinedButton(onClick = { vm.cancelarPrimos() }, enabled = calculando) { Text("Cancelar") }
    }
    when (val e = estado) {
        PrimosUi.Inicial -> {}
        PrimosUi.Calculando -> Text("Calculando...")
        PrimosUi.Cancelado -> Text("Cancelado")
        is PrimosUi.Listo -> Recuadro(VerdeExito) {
            Text("Primos encontrados: ${e.cantidad}")
            Text("Tiempo: ${e.milisegundos} ms")
        }
    }
}

@Composable
// Commit 7: tarjeta Dos cargas (una por una / a la vez)
fun ContenidoCargas(vm: TareasViewModel) {
    val estado by vm.carga.collectAsState()
    val cargando = estado is CargaUi.Cargando
    val simularError by vm.simularError.collectAsState()

    // Commit 8 (Paso 7): interruptor para provocar el error
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = simularError, onCheckedChange = { vm.cambiarSimularError(it) })
        Spacer(Modifier.width(8.dp))
        Text("Simular error")
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = { vm.cargarUnaPorUna() }, enabled = !cargando) { Text("Una por una") }
        Button(onClick = { vm.cargarALaVez() }, enabled = !cargando) { Text("A la vez") }
    }
    when (val e = estado) {
        CargaUi.Inicial -> {}
        CargaUi.Cargando -> CircularProgressIndicator()
        is CargaUi.Exito -> Recuadro(VerdeExito) {
            Text("Modo: ${e.modo}")
            Text("Perfil: ${e.perfil}")
            Text("Pedidos: ${e.pedidos}")
            Text("Tiempo: ${e.milisegundos} ms")
        }
        is CargaUi.Error -> Recuadro(MaterialTheme.colorScheme.errorContainer) {
            Text(e.mensaje, color = MaterialTheme.colorScheme.onErrorContainer)
            Button(onClick = { vm.cargarALaVez() }) { Text("Reintentar") }
        }
    }
}
