package com.flores.saludplus.ui.screens.doctores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.TarjetaDatosMedico
import com.flores.saludplus.ui.theme.TextoSecundario

// Relaciones:
// - La llama AppNavigation en Rutas.DOCTORES_ESPECIALIDAD y recibe especialidadId de la ruta
// - Usa BarraSuperior y TarjetaDatosMedico (Componentes.kt) y Repositorio.medicosPorEspecialidad

// Fase 3: directorio de doctores, paso 2: ficha de cada doctor de la especialidad
// (nombre, especialidad, código, sede, teléfono y horario) con el botón "Agendar cita": al elegir al doctor se
// elige también su sede y sigue Fecha y hora
@Composable
fun DoctoresEspecialidadScreen(especialidadId: Int, onAgendar: (Int) -> Unit, onBack: () -> Unit) {
    val especialidad = Repositorio.obtenerEspecialidad(especialidadId)
    val lista = Repositorio.medicosPorEspecialidad(especialidadId)

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(especialidad?.nombre ?: "Doctores", onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            if (lista.isEmpty()) {
                Text("No hay doctores en esta especialidad", fontSize = 18.sp, color = TextoSecundario)
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(lista, key = { it.id }) { medico -> TarjetaDatosMedico(medico, onAgendar = { onAgendar(medico.id) }) }
            }
        }
    }
}
