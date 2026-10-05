package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.flores.saludplus.ui.components.TarjetaMedico

// Relaciones:
// - La llama AppNavigation en Rutas.MEDICOS y recibe especialidadId de la ruta
// - Usa BarraSuperior, CampoBusqueda y TarjetaMedico (Componentes.kt)
// - Llama a Repositorio.obtenerEspecialidad y buscarMedicos; al elegir uno pasa medicoId a Fecha y hora

// Commit 6: médicos de la especialidad recibida por parámetro, con búsqueda
@Composable
fun MedicosScreen(especialidadId: Int, onMedico: (Int) -> Unit, onBack: () -> Unit) {
    var texto by rememberSaveable { mutableStateOf("") }
    var mostrarBusqueda by rememberSaveable { mutableStateOf(false) }

    val especialidad = Repositorio.obtenerEspecialidad(especialidadId)
    val nombreEspecialidad = especialidad?.nombre ?: ""
    val lista = Repositorio.buscarMedicos(especialidadId, texto)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraSuperior("Médicos de $nombreEspecialidad", onBack, acciones = {
                // La lupa muestra u oculta el campo de búsqueda
                IconButton(onClick = { mostrarBusqueda = !mostrarBusqueda; if (!mostrarBusqueda) texto = "" }) {
                    Icon(
                        if (mostrarBusqueda) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = "Buscar"
                    )
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (mostrarBusqueda) {
                CampoBusqueda(texto, { texto = it }, "Buscar médico...")
            }
            if (lista.isEmpty()) {
                Text("No se encontraron médicos", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(lista, key = { it.id }) { medico ->
                    TarjetaMedico(medico, nombreEspecialidad, onClick = { onMedico(medico.id) })
                }
            }
        }
    }
}
