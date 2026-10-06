package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.CampoBusqueda
import com.flores.saludplus.ui.components.ItemEspecialidad
import com.flores.saludplus.ui.theme.Divisor
import com.flores.saludplus.ui.theme.TextoSecundario

// Relaciones:
// - La llama AppNavigation en Rutas.ESPECIALIDADES
// - Usa BarraSuperior, CampoBusqueda e ItemEspecialidad (Componentes.kt)
// - Fase 2: ItemEspecialidad dibuja la imagen nombreRecurso(nombre) con ImagenEspecialidad (ImagenPorNombre.kt)
// - Llama a Repositorio.buscarEspecialidades; al elegir una pasa su id a Médicos (onEspecialidad)

// Commit 6: lista de especialidades con búsqueda en tiempo real
// Fase 2: lista plana sobre fondo blanco, sin tarjetas ni sombras
@Composable
fun EspecialidadesScreen(onEspecialidad: (Int) -> Unit, onBack: () -> Unit) {
    var texto by rememberSaveable { mutableStateOf("") }
    // Se recalcula cada vez que cambia el texto
    val lista = Repositorio.buscarEspecialidades(texto)

    Scaffold(
        // Fase 2: fondo blanco en toda la pantalla
        containerColor = Color.White,
        // Fase 2: BarraSuperior ya trae fondo blanco, flecha de 28dp y título de 26sp negrita
        topBar = { BarraSuperior("Especialidades", onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Fase 2: buscador de ancho completo con margen de 16dp y 12dp de espacio debajo
            CampoBusqueda(
                texto, { texto = it }, "Buscar especialidad...",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))

            if (lista.isEmpty()) {
                Text(
                    "No se encontraron especialidades",
                    fontSize = 18.sp,
                    color = TextoSecundario,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            LazyColumn {
                itemsIndexed(lista, key = { _, especialidad -> especialidad.id }) { indice, especialidad ->
                    // Fase 2: fila plana de 96dp (margen interno de 16dp)
                    ItemEspecialidad(especialidad, onClick = { onEspecialidad(especialidad.id) })
                    // Fase 2: divisor de 1dp entre filas; empieza donde empieza el texto
                    // (16 de margen + 88 de imagen + 14 de espacio = 118dp) y llega al borde derecho
                    if (indice < lista.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 118.dp),
                            thickness = 1.dp,
                            color = Divisor
                        )
                    }
                }
            }
        }
    }
}
