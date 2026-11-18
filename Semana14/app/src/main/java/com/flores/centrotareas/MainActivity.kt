package com.flores.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.flores.centrotareas.data.TareasRepositoryLocal
import com.flores.centrotareas.ui.PantallaTareas
import com.flores.centrotareas.ui.theme.CentroTareasTheme
import com.flores.centrotareas.viewmodel.TareasViewModel
import com.flores.centrotareas.viewmodel.TareasViewModelFactory

// Commit 1: actividad principal (proyecto base)
class MainActivity : ComponentActivity() {

    private val viewModel: TareasViewModel by viewModels {
        TareasViewModelFactory(TareasRepositoryLocal())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentroTareasTheme(dynamicColor = false) {
                PantallaTareas(viewModel)
            }
        }
    }
}
