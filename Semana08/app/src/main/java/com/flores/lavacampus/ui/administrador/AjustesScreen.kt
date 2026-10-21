package com.flores.lavacampus.ui.administrador

import com.flores.lavacampus.ui.components.rememberSelectorFoto
import com.flores.lavacampus.ui.components.PieLogo
import com.flores.lavacampus.ui.components.BannerPantalla
import com.flores.lavacampus.R
import com.flores.lavacampus.ui.components.DialogoCambiarPerfil
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.flores.lavacampus.viewmodel.SesionViewModel

// Ajustes del administrador: datos, cambio de perfil y preferencias (DataStore)
@Composable
fun AjustesScreen(
    sesion: SesionViewModel,
    onVolver: () -> Unit,
    onCambiarPerfil: (Rol) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario by sesion.usuarioActual.collectAsState()
    val oscuro by sesion.modoOscuro.collectAsState()
    val notificaciones by sesion.notificaciones.collectAsState()
    val recibir by sesion.recibirPedidos.collectAsState()
    val foto by sesion.foto.collectAsState()
    val errorFoto by sesion.mensajeFoto.collectAsState()
    val errorCambio by sesion.errorCambio.collectAsState()
    val elegirFoto = rememberSelectorFoto(sesion::cambiarFoto)
    var cambiando by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Ajustes", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BannerPantalla(R.drawable.foto_lavanderia, "Ajustes", "Cuenta y preferencias del administrador", alto = 120.dp)
            CabeceraPerfil(usuario?.nombre ?: "", usuario?.correo ?: "", "Perfil activo", foto, elegirFoto, sesion::quitarFoto)
            errorFoto?.let { Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }

            BotonCambiarPerfil("Inicia sesión como Estudiante") { sesion.limpiarErrorCambio(); cambiando = true }

            Text("Preferencias", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            GrupoPreferencias {
                FilaSwitch("Modo oscuro", "Cambia el tema de la app", oscuro, sesion::cambiarModoOscuro)
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                FilaSwitch("Notificaciones", "Avisar pedidos nuevos", notificaciones, sesion::cambiarNotificaciones)
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                FilaSwitch("Recibir pedidos", "Abrir o cerrar la recepción", recibir, sesion::cambiarRecibirPedidos)
            }

            NotaDataStore()
            BotonCerrarSesion { sesion.cerrarSesion(onCerrarSesion) }
            PieLogo()
        }
    }

    if (cambiando) {
        DialogoCambiarPerfil(
            destino = "Estudiante",
            error = errorCambio,
            onConfirmar = { correo, clave -> sesion.cambiarPerfil(correo, clave, onCambiarPerfil) },
            onCerrar = { cambiando = false }
        )
    }
}
