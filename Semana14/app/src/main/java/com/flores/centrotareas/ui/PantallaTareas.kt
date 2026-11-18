package com.flores.centrotareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            Tarjeta("Contar números primos") { Text("Aquí irá el cálculo (paso 5)") }
            Tarjeta("Dos cargas") { Text("Aquí irán las cargas (pasos 6 y 7)") }
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
    // Commit 2: prueba de vida: si deja de sumar, la pantalla está congelada
    var toques by rememberSaveable { mutableIntStateOf(0) }

    Button(onClick = { toques++ }) { Text("Tócame: $toques") }
    // Commit 2 (Paso 1): este botón congela la pantalla 4 segundos
    Button(onClick = { vm.tareaLargaMala() }) { Text("Tarea larga (MAL)") }
    Text(mensaje)
}
