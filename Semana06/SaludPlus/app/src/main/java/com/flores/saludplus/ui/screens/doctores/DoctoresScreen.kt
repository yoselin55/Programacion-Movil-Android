package com.flores.saludplus.ui.screens.doctores

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.CampoBusqueda
import com.flores.saludplus.ui.components.EncabezadoConMenu
import com.flores.saludplus.ui.components.ItemEspecialidad
import com.flores.saludplus.ui.theme.Divisor
import com.flores.saludplus.ui.theme.TextoSecundario

// Relaciones:
// - La llama AppNavigation en Rutas.DOCTORES (opción "Doctores" del menú lateral)
// - Usa EncabezadoConMenu (MenuLateral.kt), CampoBusqueda, ItemEspecialidad y BarraInferior (Componentes.kt)
// - Al elegir una especialidad abre DoctoresEspecialidadScreen (onEspecialidad)

// Fase 3: directorio de doctores, paso 1: lista de especialidades (con búsqueda)
@Composable
fun DoctoresScreen(onEspecialidad: (Int) -> Unit, onMenu: () -> Unit, onNavegar: (String) -> Unit) {
    var texto by rememberSaveable { mutableStateOf("") }
    val lista = Repositorio.buscarEspecialidades(texto)

    Scaffold(
        containerColor = Color.White,
        bottomBar = { BarraInferior(Rutas.DOCTORES, onNavegar) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                EncabezadoConMenu("Doctores", onMenu, subtitulo = "Elige una especialidad")
            }
            CampoBusqueda(texto, { texto = it }, "Buscar especialidad...", modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            if (lista.isEmpty()) {
                Text(
                    "No se encontraron especialidades",
                    fontSize = 18.sp, color = TextoSecundario,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 24.dp)) {
                itemsIndexed(lista, key = { _, e -> e.id }) { indice, especialidad ->
                    ItemEspecialidad(especialidad, onClick = { onEspecialidad(especialidad.id) })
                    if (indice < lista.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(start = 118.dp), thickness = 1.dp, color = Divisor)
                    }
                }
            }
        }
    }
}
