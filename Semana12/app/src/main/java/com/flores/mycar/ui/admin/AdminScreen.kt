package com.flores.mycar.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Panel del administrador: totales, sincronizar y limpiar cache. Solo lo ve el rol admin. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    correo: String,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Panel Admin") }) },
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Sesión: $correo", style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Total("Marcas guardadas", estado.totalGuardado, Modifier.weight(1f))
                Total("Favoritos", estado.totalFavoritos, Modifier.weight(1f))
            }

            Button(
                onClick = viewModel::sincronizar,
                enabled = !estado.sincronizando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (estado.sincronizando) "Sincronizando..." else "Sincronizar")
            }
            OutlinedButton(
                onClick = viewModel::limpiarCache,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpiar caché")
            }

            estado.mensaje?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun Total(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(valor.toString(), style = MaterialTheme.typography.headlineMedium)
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
