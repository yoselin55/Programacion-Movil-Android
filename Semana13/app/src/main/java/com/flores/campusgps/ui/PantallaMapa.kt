package com.flores.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.flores.campusgps.model.UbicacionUi
import com.flores.campusgps.viewmodel.MapaViewModel

// Commit 1: pantalla simple con el botón y la latitud/longitud; solo dibuja el estado del ViewModel.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val estado by vm.estado.collectAsState()
    // Commit 4: la lista de lugares y el seleccionado se pasan al mapa.
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()

    // Commit 3: el contexto se usa aquí (en la pantalla), no en el ViewModel.
    val contexto = LocalContext.current

    // Commit 3: lanza el cuadro de permiso de Android; si acepta busca, si niega avisa y no se cierra.
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) vm.buscar() else vm.permisoDenegado()
    }

    // Commit 3: si ya tiene permiso (FINE o COARSE) busca directo; si no, lo pide.
    fun alPulsar() {
        val fino = ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION)
        val aprox = ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fino == PackageManager.PERMISSION_GRANTED || aprox == PackageManager.PERMISSION_GRANTED) {
            vm.buscar()
        } else {
            lanzador.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Commit 4: el mapa ocupa todo el espacio libre de arriba (weight 1f).
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )

            // Commit 4: el botón y los mensajes pasan debajo del mapa.
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { alPulsar() }, // Commit 3: antes llamaba directo a vm.buscar()
                    enabled = estado !is UbicacionUi.Buscando
                ) {
                    Text("Obtener mi ubicación")
                }

                when (val e = estado) {
                    UbicacionUi.Inicial -> Text("Pulsa el botón para ver tu ubicación")
                    UbicacionUi.Buscando -> Text("Buscando…")
                    // Commit 7: latitud y longitud en una sola línea para dejar espacio a la lista.
                    is UbicacionUi.Lista ->
                        Text("Latitud: ${e.posicion.latitud}  ·  Longitud: ${e.posicion.longitud}")
                    is UbicacionUi.Error -> Text(e.mensaje, color = MaterialTheme.colorScheme.error)
                }
            }

            // Commit 7: la lista va debajo con altura fija; al tocar una tarjeta se selecciona el lugar.
            ListaLugares(
                lugares = lugares,
                seleccionado = seleccionado,
                onClick = { id -> vm.seleccionar(id) },
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
        }
    }
}
