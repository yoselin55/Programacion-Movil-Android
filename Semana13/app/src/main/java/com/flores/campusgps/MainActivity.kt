package com.flores.campusgps

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.flores.campusgps.data.LugaresRepositoryLocal
import com.flores.campusgps.data.PreferenciasRepositoryDataStore
import com.flores.campusgps.data.RutasRepositoryOsm
import com.flores.campusgps.data.UbicacionRepositoryGoogle
import com.flores.campusgps.ui.PantallaBienvenida
import com.flores.campusgps.ui.PantallaPrincipal
import com.flores.campusgps.ui.theme.CampusGPSTheme
import com.flores.campusgps.viewmodel.BienvenidaViewModel
import com.flores.campusgps.viewmodel.CampusViewModelFactory
import com.flores.campusgps.viewmodel.MapaViewModel

// Commit 1: arma los repositorios y el ViewModel, y muestra PantallaMapa.
// Mejora 3: primero se muestra la bienvenida y después el mapa.
class MainActivity : ComponentActivity() {

    // Una sola fábrica crea los dos ViewModel con los mismos repositorios.
    private val fabrica by lazy {
        CampusViewModelFactory(
            UbicacionRepositoryGoogle(applicationContext),
            LugaresRepositoryLocal(),
            PreferenciasRepositoryDataStore(applicationContext), // Mejora 2
            RutasRepositoryOsm() // Mejora 3
        )
    }
    private val vm: MapaViewModel by viewModels { fabrica }
    private val vmBienvenida: BienvenidaViewModel by viewModels { fabrica }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mejora 3: la app es clara, así que los íconos de la barra de estado van oscuros.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            CampusGPSTheme {
                var enBienvenida by rememberSaveable { mutableStateOf(true) }
                val nombre by vmBienvenida.nombre.collectAsState()

                Crossfade(targetState = enBienvenida, label = "pantalla") { mostrarBienvenida ->
                    if (mostrarBienvenida) {
                        PantallaBienvenida(nombreGuardado = nombre) { nombreEscrito ->
                            if (nombreEscrito.isNotBlank()) vmBienvenida.guardarNombre(nombreEscrito)
                            enBienvenida = false
                        }
                    } else {
                        // Mejora 3: el mapa y las demás secciones viven dentro del menú lateral.
                        PantallaPrincipal(vm, vmBienvenida)
                    }
                }
            }
        }
    }
}
