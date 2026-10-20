package com.flores.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.Rol
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.BotonCambiarPerfil
import com.flores.lavacampus.ui.components.BotonCerrarSesion
import com.flores.lavacampus.ui.components.CabeceraPerfil
import com.flores.lavacampus.ui.components.FilaSwitch
import com.flores.lavacampus.ui.components.GrupoPreferencias
import com.flores.lavacampus.ui.components.NotaDataStore
import com.flores.lavacampus.viewmodel.PerfilViewModel
import com.flores.lavacampus.viewmodel.SesionViewModel
import java.util.Locale

// Perfil del estudiante: datos, contadores y preferencias (guardadas en DataStore)
@Composable
fun PerfilScreen(
    vm: PerfilViewModel,
    sesion: SesionViewModel,
    onVolver: () -> Unit,
    onCambiarPerfil: (Rol) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val activos by vm.pedidosActivos.collectAsState()
    val totales by vm.pedidosTotales.collectAsState()
    val kilos by vm.kilosLavados.collectAsState()
    val oscuro by sesion.modoOscuro.collectAsState()
    val avisos by sesion.notificaciones.collectAsState()
    val u = vm.usuario

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Perfil", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CabeceraPerfil(u.nombre, u.correo, Rol.CLIENTE.texto)

            // Contadores
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador(activos.toString(), "Pedidos activos", Modifier.weight(1f))
                Contador(totales.toString(), "Pedidos totales", Modifier.weight(1f))
                Contador(
                    // 42.0 se muestra "42 kg" y 7.5 se muestra "7.5 kg"
                    (if (kilos % 1.0 == 0.0) kilos.toInt().toString() else String.format(Locale.US, "%.1f", kilos)) + " kg",
                    "Lavados",
                    Modifier.weight(1f)
                )
            }

            BotonCambiarPerfil("Modo práctica: ir a vista Administrador") { sesion.cambiarPerfil(onCambiarPerfil) }

            Text("Preferencias", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            GrupoPreferencias {
                FilaSwitch("Modo oscuro", "Cambia el tema de la app", oscuro, sesion::cambiarModoOscuro)
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                FilaSwitch("Avisos de pedido listo", "Notificar cuando puedas recoger", avisos, sesion::cambiarNotificaciones)
            }

            NotaDataStore()
            BotonCerrarSesion { sesion.cerrarSesion(onCerrarSesion) }
        }
    }
}

@Composable
private fun Contador(valor: String, titulo: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 14.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(titulo, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
