package com.flores.lavacampus.ui.estudiante

import com.flores.lavacampus.ui.components.rememberSelectorFoto
import com.flores.lavacampus.ui.components.PieLogo
import com.flores.lavacampus.ui.components.DialogoCambiarPerfil
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
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
    val foto by sesion.foto.collectAsState()
    val errorFoto by sesion.mensajeFoto.collectAsState()
    val errorCambio by sesion.errorCambio.collectAsState()
    val elegirFoto = rememberSelectorFoto(sesion::cambiarFoto)
    var cambiando by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Perfil", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CabeceraPerfil(u.nombre, u.correo, Rol.CLIENTE.texto, foto, elegirFoto, sesion::quitarFoto)
            errorFoto?.let { Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }

            // Contadores
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador(activos.toString(), "Activos", Icons.Outlined.Autorenew, Modifier.weight(1f))
                Contador(totales.toString(), "Pedidos", Icons.Outlined.Inbox, Modifier.weight(1f))
                Contador(
                    // 42.0 se muestra "42 kg" y 7.5 se muestra "7.5 kg"
                    (if (kilos % 1.0 == 0.0) kilos.toInt().toString() else String.format(Locale.US, "%.1f", kilos)) + " kg",
                    "Lavados",
                    Icons.Outlined.LocalLaundryService,
                    Modifier.weight(1f)
                )
            }

            BotonCambiarPerfil("Inicia sesión como Administrador") { sesion.limpiarErrorCambio(); cambiando = true }

            Text("Preferencias", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            GrupoPreferencias {
                FilaSwitch("Modo oscuro", "Cambia el tema de la app", oscuro, sesion::cambiarModoOscuro, Icons.Outlined.DarkMode)
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                FilaSwitch("Avisos de pedido listo", "Notificar cuando puedas recoger", avisos, sesion::cambiarNotificaciones, Icons.Outlined.Notifications)
            }

            NotaDataStore()
            BotonCerrarSesion { sesion.cerrarSesion(onCerrarSesion) }
            PieLogo()
        }
    }

    if (cambiando) {
        DialogoCambiarPerfil(
            destino = "Administrador",
            error = errorCambio,
            onConfirmar = { correo, clave -> sesion.cambiarPerfil(correo, clave, onCambiarPerfil) },
            onCerrar = { cambiando = false }
        )
    }
}

@Composable
private fun Contador(valor: String, titulo: String, icono: ImageVector, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 16.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) { Icon(icono, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(22.dp)) }
        Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(titulo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
    }
}
