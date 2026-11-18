package com.flores.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.flores.campusgps.data.LugaresRepositoryLocal
import com.flores.campusgps.data.UbicacionRepositoryGoogle
import com.flores.campusgps.ui.PantallaMapa
import com.flores.campusgps.ui.theme.CampusGPSTheme
import com.flores.campusgps.viewmodel.CampusViewModelFactory
import com.flores.campusgps.viewmodel.MapaViewModel

// Commit 1: arma los repositorios y el ViewModel, y muestra PantallaMapa.
class MainActivity : ComponentActivity() {

    private val vm: MapaViewModel by viewModels {
        CampusViewModelFactory(
            UbicacionRepositoryGoogle(applicationContext),
            LugaresRepositoryLocal()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusGPSTheme {
                PantallaMapa(vm)
            }
        }
    }
}
