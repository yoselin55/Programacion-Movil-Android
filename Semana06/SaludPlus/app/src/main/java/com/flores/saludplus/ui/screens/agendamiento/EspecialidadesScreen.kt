package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.CampoBusqueda
import com.flores.saludplus.ui.components.ItemEspecialidad

// Relaciones:
// - La llama AppNavigation en Rutas.ESPECIALIDADES
// - Usa BarraSuperior, CampoBusqueda e ItemEspecialidad (Componentes.kt)
// - Llama a Repositorio.buscarEspecialidades; al elegir una pasa su id a Médicos (onEspecialidad)

// Commit 6: lista de especialidades con búsqueda en tiempo real
@Composable
fun EspecialidadesScreen(onEspecialidad: (Int) -> Unit, onBack: () -> Unit) {
    var texto by rememberSaveable { mutableStateOf("") }
    // Se recalcula cada vez que cambia el texto
    val lista = Repositorio.buscarEspecialidades(texto)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { BarraSuperior("Especialidades", onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoBusqueda(texto, { texto = it }, "Buscar especialidad...")

            if (lista.isEmpty()) {
                Text("No se encontraron especialidades", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lista, key = { it.id }) { especialidad ->
                    ItemEspecialidad(especialidad, onClick = { onEspecialidad(especialidad.id) })
                }
            }
        }
    }
}
