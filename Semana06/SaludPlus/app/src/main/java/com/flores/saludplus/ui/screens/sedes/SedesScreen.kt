package com.flores.saludplus.ui.screens.sedes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.clip
import com.flores.saludplus.ui.components.ImagenPorNombre
import com.flores.saludplus.ui.components.nombreRecurso
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.model.Sede
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.EncabezadoConMenu
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.BordeSuave
import com.flores.saludplus.ui.theme.TextoSecundario

// Relaciones:
// - La llama AppNavigation en Rutas.SEDES (opción "Sedes" del menú lateral)
// - Fase 3: FotoSede la usa también SedeDetalleScreen
// - Usa EncabezadoConMenu (MenuLateral.kt) y BarraInferior (Componentes.kt) y Repositorio.sedes
// - Al elegir una sede abre SedeDetalleScreen (onSede)

// Fase 3: primer paso para agendar una cita: elegir la sede
@Composable
fun SedesScreen(onSede: (Int) -> Unit, onMenu: () -> Unit, onNavegar: (String) -> Unit) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = { BarraInferior(Rutas.SEDES, onNavegar) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            EncabezadoConMenu("Sedes", onMenu, subtitulo = "Elige la sede donde quieres atenderte")
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(Repositorio.sedes, key = { it.id }) { sede ->
                    TarjetaSede(sede, onClick = { onSede(sede.id) })
                }
            }
        }
    }
}

// Fase 3: tarjeta blanca de una sede (borde 1dp, esquinas 18dp): foto de la sede (sede_<nombre>) arriba y
// debajo nombre 22sp, dirección y teléfono 16sp, cantidad de doctores y chevron
@Composable
private fun TarjetaSede(sede: Sede, onClick: () -> Unit) {
    val doctores = Repositorio.medicos.count { it.sedeId == sede.id }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BordeSuave, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        FotoSede(sede.nombre, Modifier.fillMaxWidth().height(150.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(sede.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                Text(sede.direccion, fontSize = 16.sp, color = TextoSecundario)
                Text("Tel. ${sede.telefono}", fontSize = 16.sp, color = TextoSecundario)
                Text("$doctores doctores", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AzulPrimario)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(28.dp))
        }
    }
}

// Fase 3: foto de la sede (res/drawable/sede_santa_anita...); si no existe, un recuadro crema con el ícono de ubicación
@Composable
fun FotoSede(nombreSede: String, modifier: Modifier = Modifier) {
    ImagenPorNombre(
        nombre = "sede_" + nombreRecurso(nombreSede),
        descripcion = "Sede $nombreSede",
        modifier = modifier,
        respaldo = {
            Box(modifier = modifier.background(AzulClaro), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(48.dp))
            }
        }
    )
}
